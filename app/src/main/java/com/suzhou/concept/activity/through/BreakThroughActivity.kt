//package com.suzhou.concept.activity.through
//
//import android.graphics.Color
//import android.util.Log
//import android.view.View
//import androidx.lifecycle.lifecycleScope
//import com.suzhou.concept.AppClient
//import com.suzhou.concept.R
//import com.suzhou.concept.activity.BaseActivity
//import com.suzhou.concept.bean.LinkDataBean
//import com.suzhou.concept.bean.LinkLineBean
//import com.suzhou.concept.bean.OverBreakEvent
//import com.suzhou.concept.bean.WordItem
//import com.suzhou.concept.databinding.ActivityBreakThroughBinding
//import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_submit
//import com.suzhou.concept.lil.event.WordBreakEvent
//import com.suzhou.concept.lil.util.DateUtil
//import com.suzhou.concept.utils.ExtraKeysFactory
//import com.suzhou.concept.utils.GlobalMemory
//import com.suzhou.concept.utils.startActivity
//import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.EventBus
//
///**
// *  单词闯关界面
// */
//class BreakThroughActivity : BaseActivity<ActivityBreakThroughBinding>(),View.OnClickListener {
//    private val result= mutableListOf<WordItem>()
//    private val newResult=mutableListOf<WordItem>()
//    private val linkLineResult= mutableListOf<LinkLineBean>()
//    private val remainder=2
//    private val groupCount=6
//    private val flagStr="new_result"
//    private val rightFlag="rightNum"
//    private var rightNum=0
//    private var wordNum=0
//    override fun ActivityBreakThroughBinding.initBinding() {
//        result.addAll(AppClient.wordList)
//        intent.getParcelableArrayListExtra<WordItem>(flagStr)?.let {
//            result.clear()
//            result.addAll(it.toMutableList())
//        }
//        wordNum=result.size
//        rightNum=intent.getIntExtra(rightFlag,0)
//
////        setTitleText(result[0].readUnit())
//        //显示标题内容
//        val unitIndex = intent.getIntExtra(ExtraKeysFactory.data, result.first().unitId)
//        if (GlobalMemory.wordYoung) {
//            setTitleText("Unit $unitIndex")
//        } else {
//            setTitleText("Lesson $unitIndex")
//        }
//
//        changeData()
//        controlButton.setOnClickListener(this@BreakThroughActivity)
//        linkLine.setOnChoiceResultListener { correct, result ->
//            linkLineResult.addAll(result)
//            resultHint.text=if (correct){
//                resultHint.setTextColor(Color.GREEN)
//                "您全部回答正确"
//            }else{
//                val mistakeCount=result.filter { !it.isRight }.size
//                resultHint.setTextColor(Color.RED)
//                "您有${mistakeCount}个单词连线错误，继续加油吧!"
//            }
//            resultHint.visibility=View.VISIBLE
//            controlButton.visibility = View.VISIBLE
//            judgeGroupCount({
//                controlButton.text = resources.getString(R.string.next_group)
//            }, {
//                controlButton.text = resources.getString(R.string.submit)
//            })
//        }
//    }
//
//    private fun changeLocalDataBase(isFinish:Boolean){
//        lifecycleScope.launch {
//            if (newResult.size==linkLineResult.size){
//                for (i in linkLineResult.indices){
//                    newResult[i].correct=linkLineResult[i].isRight
//                    AppClient.addWordResult(newResult[i])
//                }
//                for (i in linkLineResult.indices){
//                    //IO操作较为耗时
//                     if (GlobalMemory.wordYoung){
//                         wordViewModel.updateWordRightStatus(newResult[i],linkLineResult[i].isRight)
//                     }else{
//                         //这里插入单词数据后，下面请区分类型处理（英音、美音、青少版）
//                         wordViewModel.updateRightStatus(newResult[i],linkLineResult[i].isRight,AppClient.conceptItem.language)
//                     }
//                }
//            }
//        }
//
//        if (isFinish){
//            //获取数据库内容，然后发送新的数据
//            val item=result.first()
//            var rightNum = if (GlobalMemory.wordYoung){
//                wordViewModel.selectYoungRightWordCount(item.voa_id,GlobalMemory.userInfo.uid,true)
//            }else{
//                wordViewModel.selectRightWordCount(item.voa_id,GlobalMemory.userInfo.uid,true)
//            }
//
//            wordNum = if (GlobalMemory.wordYoung){
//                wordViewModel.selectYoungWordCount(item.voa_id)
//            }else{
//                wordViewModel.selectWordCount(item.voa_id)
//            }
//
//            var index:Int = item.voa_id
//            when (GlobalMemory.currentLanguage.language) {
//                "US" -> {
//                    index = item.voa_id
//                    index = index%1000-1
//                }
//                "UK" -> {
//                    index = item.voa_id/10
//                    index = index%1000-1
//                }
//                else -> {
//                    index = item.unitId
//                }
//            }
//
//            conceptViewModel.updateWordItem(rightNum,wordNum,item.bookId,index)
//        }
////        val rightNum=linkLineResult.filter { it.isRight }.size
////        val item=result.first()
////        //index为ConceptItem的角标，从0开始
////        conceptViewModel.updateWordItem(rightNum,wordNum,item.bookId,item.unitId)
//    }
//
//    private fun changeData(fromIndex:Int=0,toIndex:Int=groupCount){
//        val list= mutableListOf<LinkDataBean>()
//        newResult.clear()
//        judgeGroupCount({
//            newResult.addAll(result.subList(fromIndex, toIndex))
//        },{
//            newResult.addAll(result)
//        })
//        //判断num相同
//        for (i in newResult.indices){
//            val item=newResult[i]
//            list.add(LinkDataBean(item.word,i,"0",0,i,item.word))
//            list.add(LinkDataBean(item.def,i,"0",1,newResult.size-i,item.word))
//        }
//        binding.linkLine.setData(list)
//    }
//
//    override fun onClick(p0: View?) {
//        when(p0?.id){
//            R.id.control_button-> {
//                var isFinish = false
//
//                val right=linkLineResult.filter { it.isRight }.size
//                judgeGroupCount({
//                    val temporaryList=mutableListOf<WordItem>()
//                    temporaryList.addAll(result.subList(groupCount, result.size))
//                    temporaryList as ArrayList
//                    startActivity<BreakThroughActivity> {
//                        putParcelableArrayListExtra(flagStr, temporaryList)
//                        putExtra(rightFlag,rightNum+right)
//                        putExtra(ExtraKeysFactory.data,intent.getIntExtra(ExtraKeysFactory.data,0))
//                    }
//                    //下一批
//                    isFinish = false;
//                },{
//                    //闯关完成
//                    EventBus.getDefault().post(OverBreakEvent(rightNum+right))
//                    isFinish = true
//                })
//
//                //这里通过获取左右两侧的数据和连线数据，确定正确和错误信息，同时计算出需要上传的数据，之后进行处理
//                buildBreakData(isFinish,binding.linkLine.leftData,binding.linkLine.rightData,linkLineResult)
//                //设置下一组的开始时间
//                startTestTime = System.currentTimeMillis()
//                //将数据保存到数据库中
//                changeLocalDataBase(isFinish)
//                finish()
//            }
//        }
//    }
//
//    private inline fun judgeGroupCount(trueMethod:()->Unit,falseMethod:()->Unit={}){
//        if (result.size-groupCount>remainder){
//            trueMethod()
//        }else{
//            falseMethod()
//        }
//    }
//
//    //将数据转换成需要上传数据的操作(这里是把左侧数据和右侧数据、连线数据合并后显示的)
//    //这里的uploadList有问题，因为每次下一组的时候都会跳转到新的界面，导致数据丢失，因此直接回调到其他界面处理
//    private var uploadList = mutableListOf<Report_wordBreak_submit.TestListBean>()
//    private var startTestTime = System.currentTimeMillis()
//    private var showId = 0
//
//    private fun buildBreakData(hasFinish:Boolean,leftList:List<LinkDataBean>,rightList:List<LinkDataBean>,lineList:List<LinkLineBean>){
//        if (leftList.size != rightList.size
//            ||leftList.size != lineList.size
//            ||rightList.size != lineList.size){
//            return
//        }
//
//        //获取第一个数据，拿到其中的数据信息
//        val wordData = result[0]
//
//        val category = "单词闯关"
//        val testMode = "W"
//        val startTime = DateUtil.toDateStr(startTestTime,DateUtil.YMD)
//        val endTime = DateUtil.toDateStr(System.currentTimeMillis(),DateUtil.YMD)
//
//        for (i in lineList.indices){
//            //先添加固定的数据
//            var testBean:Report_wordBreak_submit.TestListBean = Report_wordBreak_submit.TestListBean()
//            testBean.category = category
//            testBean.testMode = testMode
//            testBean.beginTime = startTime
//            testBean.testTime = endTime
//
//            //连线数据
//            val lineData = lineList[i]
//            //连线数据对应的左右两侧数据
//            val leftData = leftList[lineData.leftIndex]
//            val rightData = rightList[lineData.rightIndex]
//
//            //添加合并的数据
//            testBean.lessonId = wordData.unitId.toString()
//            testBean.testId = showId
//
//            testBean.rightAnswer = leftData.word
//            testBean.userAnswer = rightData.word
//            if (lineData.isRight){
//                testBean.answerResut = 1
//            }else{
//                testBean.answerResut = 0
//            }
//
//            //添加到提交数据中
//            uploadList.add(testBean)
//
//            //自动增加id数据
//            showId++
//
//            //写出内容
//            Log.d("上传数据显示", "连线位置:--"+lineData.leftIndex+"---"+lineData.rightIndex)
//            Log.d("上传数据显示", "左侧单词:--"+leftData.word)
//            Log.d("上传数据显示", "右侧单词:--"+rightData.word)
//            Log.d("上传数据显示", "数据数量:--"+uploadList.size)
//            Log.d("上传数据显示", "数据显示:--"+testBean.toString())
//            Log.d("上传数据显示", "分割线:-----------------------------------------")
//        }
//
//        //将数据发送到回调中
//        EventBus.getDefault().post(WordBreakEvent(hasFinish,uploadList))
//    }
//}