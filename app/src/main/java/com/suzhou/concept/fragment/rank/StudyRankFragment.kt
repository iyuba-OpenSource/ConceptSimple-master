package com.suzhou.concept.fragment.rank

import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.adapter.StudyRankAdapter
import com.suzhou.concept.bean.GroupRankItem
import com.suzhou.concept.bean.StudyRankEvent
import com.suzhou.concept.databinding.StudyRankLayoutBinding

import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.addDefaultDecoration
import com.suzhou.concept.utils.visibilityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
苏州爱语吧科技有限公司
@Date:  2023/1/16
@Author:  han rong cheng
 */
class StudyRankFragment(private val flag :Boolean=false): BaseFragment<StudyRankLayoutBinding>() {
    override fun StudyRankLayoutBinding.initBinding() {

        username=GlobalMemory.userInfo.username
        val studyAdapter= with(StudyRankAdapter()){
            itemListener=listener
            this
        }
        studyRank.apply {
            adapter=studyAdapter
            addDefaultDecoration()
        }
        userAction.loadStudyListenRank(flag)
        lifecycleScope.launch {
            userAction.judgeErrorFlow(flag).collect{
                item=it
            }
        }
        lifecycleScope.launch {
            val result=userAction.judgeRankFlow(flag).first()
            result.onError {
                studyRank.visibilityState(true)
            }.onSuccess {
                studyEmpty.visibilityState(true)
                studyAdapter.submitData(it)
            }
        }
    }
    private val  listener=Consumer<GroupRankItem>{
//        requireActivity().gotoEvaluationInfo(it.uid,it.name,it.imgSrc)
    }

    override fun initEventBus(): Boolean =true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: StudyRankEvent){
        event.groupResponse.apply {
            bind.item=this
        }
    }
}