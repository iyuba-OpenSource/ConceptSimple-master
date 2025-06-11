package com.suzhou.concept.fragment.exercise

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.suzhou.concept.R
import com.suzhou.concept.bean.ExerciseRecord
import com.suzhou.concept.bean.StructureItem
import com.suzhou.concept.bean.TestRecordItem
import com.suzhou.concept.databinding.StructureExerciseFragmentBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.utils.*
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
@Date:  2023/2/8
@Author:  han rong cheng
 */
class StructureExerciseFragment : BaseFragment<StructureExerciseFragmentBinding>() {
    private val structureList = mutableListOf<StructureItem>()
    private val recordList = mutableListOf<ExerciseRecord>()
    private val testRecord = mutableListOf<TestRecordItem>()

    override fun StructureExerciseFragmentBinding.initBinding() {
        structureEdit.addTextChangedListener(watcher)
        submitTestRecordStructure.setOnClickListener(clickListener)
        redoStructure.setOnClickListener(clickListener)
        structureItem.addOnTabSelectedListener(tabSelectedListener)
        changeClickVisibility()
        lifecycleScope.launch {
            exercise.lastStructure.collect { result ->
                result.onSuccess {
                    structureList.addAll(it)
                    it.forEach { item ->
                        val text = "第${item.number}题"
                        structureItem.apply {
                            addTab(newTab().setText(text))
                        }
                    }
                    bind.titleDesc = if (it.isNotEmpty()) {
                        with(it.first()) { "$desc_EN $desc_CH" }
                    } else {
                        getString(R.string.empty_data)
                    }
                    changeVisibility(it.isNotEmpty())
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                exercise.lastSubmitExerciseResult.collect { result ->
                    result.onError {
                        it.judgeType().showToast()
                    }.onSuccess {
                        "做题记录提交成功".showToast()
                        changeClickVisibility(false)
                        //对应EditText需要变颜色
                        exercise.operateRedo(
                            structureList.first().id,
                            flag = true,
                            isMultiple = false
                        )
                    }
                }
            }
        }
        lifecycleScope.launch {
            exercise.lastTestRecord.collect { result ->
                result.onError {
                    it.judgeType().showToast()
                }.onSuccess {
                    val map = mutableMapOf<String, TestRecordItem>()
                    it.filterStructure(true)
                        .forEach { item ->
                            item.changeUpperCase(true)
                            map[item.TestNumber] = item
                        }
                    testRecord.addAll(map.map { item -> item.value })
//                    val testCompleted=(structureList.size==testRecord.size)
//                    changeClickVisibility(!testCompleted)
                    structureItem.addOnTabSelectedListener(tabSelectedListener)
                }
            }
        }
    }

    /**
     * 重做
     * */
    private fun startReDo() {
        recordList.clear()
        testRecord.clear()
        structureList.forEach {
            it.userAnswer = ""
        }
        changeClickVisibility()
        bind.structureItem.getTabAt(0)?.select()
        if (structureList.isNotEmpty()) {
            inflateData(0)
        }
        exercise.operateRedo(structureList.first().id, false, isMultiple = false)
    }

    private fun changeClickVisibility(flag: Boolean = true) {
        bind.apply {
            redoStructure.visibilityState(flag)
            structureAnswer.visibilityState(flag)
            submitTestRecordStructure.visibilityState(!flag)
        }
    }

    private fun submitExerciseRecord() {
        if (!GlobalMemory.isLogin()) {
            requireActivity().showGoLoginDialog()
            return
        }
        if (recordList.filter { it.UserAnswer.isNotEmpty() }.size != structureList.size) {
            "还有题目未完成,请先答题".showToast()
            return
        }
        recordList.forEach {
            if (it.TestTime.isEmpty()) {
                it.TestTime = System.currentTimeMillis().timeStampDate()
            }
            it.inflateRight()
        }
        exercise.submitExerciseRecord(recordList)
    }

    private val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.submit_test_record_structure -> submitExerciseRecord()
            R.id.redo_structure -> startReDo()
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val position = getCurrPosition()
            if (position.findIndexSuccess()) {

                //判断下数量再显示
                if (recordList!=null&&recordList.size>position){
                    recordList[position].apply {
                        UserAnswer = s.toString()
                        TestTime = System.currentTimeMillis().timeStampDate()
                        inflateRight()
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    private fun inflateData(position: Int) {
        val item = structureList[position]
        if (testRecord.isNotEmpty()) {
            item.userAnswer = testRecord[position].UserAnswer
        }
        bind.item = item
        val addFlag = recordList.indexOfFirst { it.TestNumber == item.number }
        if (!addFlag.findIndexSuccess()) {
            val exerciseItem = ExerciseRecord(
                LessonId = item.id,
                TestNumber = item.number,
                BeginTime = System.currentTimeMillis().timeStampDate(),
                RightAnswer = item.answer
            ).let {
                it.title = item.note
                it
            }
            recordList.add(exerciseItem)
        } else {
            if (recordList.isNotEmpty()) {
                recordList[addFlag].apply {
                    UserAnswer = item.realAnswer()
                    TestTime = System.currentTimeMillis().timeStampDate()
                }
            }
        }
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            inflateData(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {

        }

        override fun onTabReselected(tab: TabLayout.Tab) {

        }
    }

    private fun getCurrPosition() = with(bind.item!!.number) {
        if (isEmpty() || structureList.isEmpty()) {
            -1
        } else {
            structureList.indexOfFirst { it.number == this }
        }
    }

    private fun changeVisibility(isEmpty: Boolean) {
        bind.apply {
            structureEmpty.visibilityState(isEmpty)
            structureItem.visibilityState(!isEmpty)
            structureInput.visibilityState(!isEmpty)
            structureOperate.visibilityState(!isEmpty)
        }
    }

}