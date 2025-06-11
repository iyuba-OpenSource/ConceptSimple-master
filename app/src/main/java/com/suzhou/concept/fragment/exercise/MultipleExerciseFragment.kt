package com.suzhou.concept.fragment.exercise

import android.view.View
import android.widget.RadioButton
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.suzhou.concept.R
import com.suzhou.concept.activity.article.SeekAnalysisActivity
import com.suzhou.concept.bean.ExerciseRecord
import com.suzhou.concept.bean.MultipleItem
import com.suzhou.concept.bean.TestRecordItem
import com.suzhou.concept.databinding.MultipleExerciseFragmentBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil
import com.suzhou.concept.utils.*
import kotlinx.coroutines.launch

/**
 * 选择题
 */
class MultipleExerciseFragment:BaseFragment<MultipleExerciseFragmentBinding>() {
    private val list= mutableListOf<MultipleItem>()
    private val recordList= mutableListOf<ExerciseRecord>()
    private val testRecord= mutableListOf<TestRecordItem>()
    private val secondTestRecord= mutableListOf<TestRecordItem>()

    override fun MultipleExerciseFragmentBinding.initBinding() {
        verifyResult.setOnClickListener(clickListener)
        redo.setOnClickListener(clickListener)
        submitTestRecord.setOnClickListener(clickListener)
        selectItem.addOnTabSelectedListener(tabSelectedListener)

        lifecycleScope.launch {
            exercise.lastMultiple.collect{result->
                result.onSuccess {
                    list.addAll(it)
                    it.forEach {item->
                        val text="第${item.index_id}题"
                        selectItem.apply {
                            addTab(newTab().setText(text))
                        }
                    }
                    val testCompleted=(list.size==testRecord.size)
                    changeClickVisibility(!testCompleted)
                    changeVisibility(it.isNotEmpty())
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                exercise.lastSubmitExerciseResult.collect{result->
                    result.onError {
                        it.judgeType().showToast()
                    }.onSuccess {
                        val rightNum=recordList.filter {item-> item.AnswerResut=="1" }.size
                        conceptViewModel.updateExerciseItem(rightNum)
                        "做题记录提交成功".showToast()
                        changeClickVisibility(false)
                        exercise.requestTestRecord(list.first().voa_id)
                        exercise.operateRedo(list.first().voa_id,true)
                    }
                }
            }
        }
        lifecycleScope.launch {
            exercise.lastTestRecord.collect{result->
                result.onSuccess {
                    //根据TestNumber去重
                    val map= mutableMapOf<String, TestRecordItem>()
                    it.filterStructure(false)
                        .forEach { item ->
                            item.changeUpperCase()
                            map[item.TestNumber] = item
                        }
                    val list=map.map { item-> item.value }
                    secondTestRecord.apply {
                        clear()
                        addAll(list)
                        sortBy {item-> item.TestNumber }
                    }
                    testRecord.apply {
                        clear()
                        addAll(list)
                        sortBy {item-> item.TestNumber }
                    }
                    if (list.isEmpty()){
                        exercise.getConceptExercise()
                    }else{
                        seekVerifyResult()
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
    }

    /**
     * 查看做题结果
     * */
    private fun seekVerifyResult(){
        if (recordList.isEmpty()){
            return
        }

        recordList.last().apply {
            if (UserAnswer.isEmpty()){
                UserAnswer=secondTestRecord.last().UserAnswer
                recordList[recordList.lastIndex] = this
            }
        }
        val lastList= ArrayList<ExerciseRecord>()
        secondTestRecord.forEach {
            val item=ExerciseRecord(
                TestNumber = it.TestNumber,
                UserAnswer = it.UserAnswer,
                RightAnswer = it.RightAnswer
            )
            lastList.add(item)
        }
        for (i in list.indices){
            lastList[i].title=list[i].question
        }
        requireActivity().startActivity<SeekAnalysisActivity> {
            putParcelableArrayListExtra(ExtraKeysFactory.viewAnalysisList,lastList)
        }
    }

    /**
     * 重做
     * */
    private fun startReDo(){
        recordList.clear()
        testRecord.clear()
        changeClickVisibility()
        bind.selectItem.getTabAt(0)?.select()
        bind.questionOptions.clearCheck()
        if (list.isNotEmpty()){
            inflateData(list.first())
        }
        exercise.operateRedo(list.first().voa_id,false)
    }

    private fun changeClickVisibility(flag:Boolean=true){
        bind.apply {
            verifyResult.visibilityState(flag)
            redo.visibilityState(flag)
            submitTestRecord.visibilityState(!flag)
        }
    }

    private val clickListener= View.OnClickListener {
        when(it.id){
            R.id.verify_result->seekVerifyResult()
            R.id.redo->startReDo()
            R.id.submit_test_record->submitExerciseRecord()
        }
    }

    private fun submitExerciseRecord(){
        if (!GlobalMemory.isLogin()){
            requireActivity().showGoLoginDialog()
            return
        }
        if (recordList.filter { it.UserAnswer.isNotEmpty() }.size!=list.size){
            "还有题目未完成,请先答题".showToast()
            return
        }
        recordList.forEach {
            if (it.TestTime.isEmpty()){
                it.TestTime=System.currentTimeMillis().timeStampDate()
            }
            it.inflateRight()
        }
        exercise.submitExerciseRecord(recordList)
    }

    private fun addRecordList(position:Int, currAnswer:String){
        val index=recordList.indexOfFirst {
            it.TestNumber == list[position].index_id
        }
        if (index.findIndexSuccess()){
            recordList[index].apply {
                UserAnswer=currAnswer
                TestTime=System.currentTimeMillis().timeStampDate()
                RightAnswer = HelpUtil.transCharAnswer(list[index].answer)
            }
        }
    }

    private fun inflateData(item:MultipleItem){
        bind.apply {
            questionOptions.removeAllViews()
            question="（单选）Q${item.index_id}:${item.question}"
            questionOptions.addRadioButton(item.toSelectArray())
        }
        testRecord.indexOfFirst {
            it.TestNumber==item.index_id
        }.let {
            if (it.findIndexSuccess()){
                val index=OtherUtils.selectArray.indexOfFirst { index-> index== testRecord[it].UserAnswer}
                if (index.findIndexSuccess()){
                    bind.questionOptions.apply {
                        val id = getSingleRadio(true)[index].id
                        check(id)
                    }
                }
            }
        }
        val addFlag=recordList.indexOfFirst { it.TestNumber== item.index_id}
        if (!addFlag.findIndexSuccess()){
            val right=if (item.answer.isNotEmpty()){
                try {
                    val index = with(item.answer) {
                        if (toInt() < 0) 0 else toInt()
                    }
                    OtherUtils.selectArray[index]
                } catch (e: Exception) { "" }
            }else ""
            val exerciseItem=ExerciseRecord(
                LessonId = item.voa_id,
                TestNumber = item.index_id,
                BeginTime = System.currentTimeMillis().timeStampDate(),
                RightAnswer = right
            ).let {
                it.title=item.question
                it
            }
            recordList.add(exerciseItem)
        }
    }
    private val tabSelectedListener=object : TabLayout.OnTabSelectedListener{
        override fun onTabSelected(tab: TabLayout.Tab) {
            //将所有的选中按钮设置为未选中(服了，写的什么烂代码)
            for (i in 0 until bind.questionOptions.childCount){
                val checkView:RadioButton = bind.questionOptions.get(i) as RadioButton
                checkView.isChecked = false
            }

            //设置当前选中的位置


            val position=tab.position
            val item=list[position]
            inflateData(item)
            bind.questionOptions.setOnCheckedChangeListener { _, i ->
                if (i.findIndexSuccess()){
                    addRecordList(position, OtherUtils.selectArray[i])
                }
            }
            val testNumber=recordList.indexOfFirst { it.TestNumber==list[position].index_id }
            val simpleIndex = OtherUtils.selectArray.indexOfFirst { it == recordList[testNumber].UserAnswer }
            if (simpleIndex.findIndexSuccess()) {
                bind.questionOptions.apply {
                    val id = getSingleRadio(true)[simpleIndex].id
                    check(id)
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {

        }

        override fun onTabReselected(tab: TabLayout.Tab) {

        }
    }

    private fun changeVisibility(isEmpty:Boolean){
        bind.apply {
            multipleEmpty.visibilityState(isEmpty)
            multipleQuestion.visibilityState(!isEmpty)
            selectItem.visibilityState(!isEmpty)
            multipleInput.visibilityState(!isEmpty)
            multipleOperate.visibilityState(!isEmpty)
        }
    }
}