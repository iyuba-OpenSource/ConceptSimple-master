package com.suzhou.concept.activity.speaking

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.SentenceAdapter
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.UpdateSpeakListEvent
import com.suzhou.concept.databinding.ActivitySpeakingShowBinding
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.addDefaultDecoration
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 口语秀列表界面
 */
class SpeakingShowActivity : BaseActivity<ActivitySpeakingShowBinding>() , Consumer<Pair<Int,ConceptItem>> {
    private val youngSpeakingSelect=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== RESULT_OK){
            (it.data?.getSerializableExtra(ExtraKeysFactory.youngSpeaking) as LanguageType).let {type->
                conceptViewModel.apply {
                    requestYoungBookList(type.bookId)
                    saveSpeakShow(type)
                }
                setTitleText(type.language)
            }
        }
    }

    override fun ActivitySpeakingShowBinding.initBinding() {
        lifecycleScope.launch {
            conceptViewModel.speakTitle.map {
                val oldValue="新概念英语"
                if (it.contains(oldValue)){
                    it.replace(oldValue,"")
                }else{
                    it
                }
            }.collect{
                setTitleText(it)
            }
        }
        installStandRight {
            youngSpeakingSelect.launch(Intent(this@SpeakingShowActivity, SelectYoungActivity::class.java))
        }
        val sentenceAdapter= with(SentenceAdapter()){
            itemListener = this@SpeakingShowActivity
            this
        }
        speckList.apply {
            adapter=sentenceAdapter
            addDefaultDecoration()
        }
        conceptViewModel.requestYoungBookList()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                conceptViewModel.speakList.collect{result->
                    result.onSuccess {
                        sentenceAdapter.changeData(it)
                        dismissLoad()
                    }.onLoading {
                        showLoad()
                    }.onError {
                        it.judgeType().showToast()
                        dismissLoad()
                    }
                }
            }
        }
    }

    /**
     * 跳转播放视频activity(简介，排行，2个fragment)->开始配音activity
     * */
    override fun accept(t: Pair<Int,ConceptItem>) {
        startActivity<PlaySpeakingActivity> {
            GlobalMemory.speakingItem=t.second
        }
    }

    override fun initEventBus(): Boolean =true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: UpdateSpeakListEvent){
        conceptViewModel.requestYoungBookList()
    }
}