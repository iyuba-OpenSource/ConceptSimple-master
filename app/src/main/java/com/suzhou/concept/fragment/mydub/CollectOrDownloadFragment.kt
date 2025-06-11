package com.suzhou.concept.fragment.mydub

import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.activity.speaking.MineDubActivity
import com.suzhou.concept.activity.speaking.PlaySpeakingActivity
import com.suzhou.concept.adapter.SentenceAdapter
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.databinding.CollectOrDownloadFragmentBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.addDefaultDecoration
import com.suzhou.concept.utils.startActivity
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
@Date:  2022/12/12
@Author:  han rong cheng
isCollect:区分是收藏还是下载列表
 */
class CollectOrDownloadFragment(private val isCollect: Boolean = true) :
    BaseFragment<CollectOrDownloadFragmentBinding>(), Consumer<Pair<Int,ConceptItem>> {
    override fun CollectOrDownloadFragmentBinding.initBinding() {
        young.selectCollectOrDownload(isCollect)
        lifecycleScope.launch {
            young.judgeFlow(isCollect).collect { result ->
                result.onSuccess {
                    collectOrDownloadList.apply {
                        addDefaultDecoration()
                        adapter = with(SentenceAdapter()) {
                            itemListener = this@CollectOrDownloadFragment
                            changeData(it)
                            this
                        }
                    }
                    dismissActivityLoad<MineDubActivity>()
                }.onError {
                    dismissActivityLoad<MineDubActivity>()
                }.onLoading {
                    showActivityLoad<MineDubActivity>()
                }
            }
        }
    }

    override fun accept(t: Pair<Int,ConceptItem>) {
        requireActivity().startActivity<PlaySpeakingActivity> {
            GlobalMemory.speakingItem=t.second
        }
    }
}