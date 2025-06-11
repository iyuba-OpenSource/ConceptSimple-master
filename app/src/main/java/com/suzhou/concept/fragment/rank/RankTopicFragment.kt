package com.suzhou.concept.fragment.rank

import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.suzhou.concept.adapter.RankPagingAdapter
import com.suzhou.concept.databinding.RankChildLayoutBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OnClickRankItemListener
import com.suzhou.concept.utils.addDefaultDecoration
import com.suzhou.concept.utils.visibilityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
@Date:  2023/1/13
@Author:  han rong cheng
 */
class RankTopicFragment(private val flag:Boolean): BaseFragment<RankChildLayoutBinding>() {
    override fun RankChildLayoutBinding.initBinding() {
        val rankAdapter= with(RankPagingAdapter()){
            itemListener=itemClickListener
            this
        }
        rankGroup.apply {
            adapter=rankAdapter
            addDefaultDecoration()
        }
        userAction.loadTopicRank(flag)
        lifecycleScope.launch {
            userAction.judgeTopicRankIn(flag).first().onError {
                rankGroup.visibilityState(true)
            }.onSuccess {
                topicRankEmpty.visibilityState(true)
                rankAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            userAction.judgeTopicErrorIn(flag).collect{
                it.testFlag=!flag
                item=it
            }
        }
        rankAdapter.addLoadStateListener { state->
            val notLoading=state.refresh is LoadState.NotLoading
            if (notLoading){
                lifecycleScope.launch {
                    GlobalMemory.rankTopicResponse.apply {
                        testFlag=!flag
                        item= this
                    }
                }
            }
        }
    }

    private val itemClickListener= object: OnClickRankItemListener {
        override fun listenItem(userId: Int, userName: String) {

        }
    }

}