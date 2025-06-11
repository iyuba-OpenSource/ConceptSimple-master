package com.suzhou.concept.activity.word

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.WordSentenceAdapter
import com.suzhou.concept.bean.LocalCollect
import com.suzhou.concept.bean.WordRemoveEvent
import com.suzhou.concept.databinding.ActivityShowWordBinding
import com.suzhou.concept.utils.*
import com.suzhou.concept.utils.logic.GlobalPlayManager
import com.suzhou.concept.utils.logic.VoiceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import personal.iyuba.personalhomelibrary.utils.ToastFactory
import kotlin.concurrent.thread

class ShowWordActivity : BaseActivity<ActivityShowWordBinding>(), View.OnClickListener {
    private var isDelete =false
    private var word=""
    private var audio=""

    override fun ActivityShowWordBinding.initBinding() {
        word = intent.getStringExtra(ExtraKeysFactory.definitionWord).toString()
        isDelete=intent.getBooleanExtra(ExtraKeysFactory.listWord,false)
        if (isDelete){
            changeStar(isDelete)
        }else{
            thread {
                val result = conceptViewModel.selectCollectByWord(word)
                if (result != null) {
                    isDelete = result.isCollect
                    changeStar(isDelete)
                }else{
                    changeStar(false)
                }
            }
        }
        wordPlay.setOnClickListener(this@ShowWordActivity)
        collect.setOnClickListener(this@ShowWordActivity)
        lifecycleScope.launch {
            try {
                conceptViewModel.requestPickWord(word).flowOn(Dispatchers.IO).collect{
                    item = it
                    setTitleText(it.key)
                    audio=it.audio
                    binding.wordSentenceList.adapter = with(WordSentenceAdapter()){
                        changeData(it.sent)
                        this
                    }
                }
            }catch (e:Exception){
                ToastFactory.showShort(this@ShowWordActivity,"未查询到该单词内容")
            }
        }
    }

    private fun changeStar(flag: Boolean) {
        if (flag) {
            binding.collect.setBackgroundResource(R.drawable.star)
        } else {
            binding.collect.setBackgroundResource(R.drawable.un_star)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.standard_left -> finish()
            R.id.word_play -> GlobalPlayManager.addUrl(Pair(VoiceStatus.WORD_BOOK,audio))
            R.id.collect -> {
                if (!GlobalMemory.isLogin()){
                    showGoLoginDialog()
                }else{
                    collectWord()
                }
            }
        }
    }

    private fun collectWord(){
        lifecycleScope.launch {
            conceptViewModel.changeCollectStatus(word.changeEncode(),  isDelete)
                .flowOn(Dispatchers.IO)
                .collect{
                    if (it.result==1){
                        val result = conceptViewModel.selectCollectByWord(it.word)
                        if (result != null) {
                            conceptViewModel.updateWord(LocalCollect(it.word, !result.isCollect))
                            //换状态
                        } else {
                            //插入
                            conceptViewModel.insertWord(LocalCollect(it.word, !isDelete))
                        }
                        isDelete=!isDelete
                        changeStar(isDelete)
                        EventBus.getDefault().post(WordRemoveEvent())
                        if (isDelete) {"收藏成功"} else {"取消收藏成功"}.showToast()
                    }else{
                        "收藏失败".showToast()
                    }
                }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        GlobalPlayManager.executeDestroy()
    }
}