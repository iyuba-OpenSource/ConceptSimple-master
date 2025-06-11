package com.suzhou.concept.fragment.main

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.MainActivity
import com.suzhou.concept.activity.article.SelectBookActivity
import com.suzhou.concept.activity.article.TeachMaterialActivity
import com.suzhou.concept.adapter.SentenceSimpleAdapter
import com.suzhou.concept.bean.*
import com.suzhou.concept.databinding.ArticleFragmentLayoutHuaweiBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.utils.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 首页
 */
@RequiresApi(Build.VERSION_CODES.N)
class ArticleSimpleFragment : BaseFragment<ArticleFragmentLayoutHuaweiBinding>() {
    private val list= mutableListOf<ConceptItem>()
    private val sentenceAdapter= SentenceSimpleAdapter()

    override fun ArticleFragmentLayoutHuaweiBinding.initBinding() {
        sentenceAdapter.apply {
            itemListener=listener
        }
        newConceptList.apply {
            adapter = sentenceAdapter
            itemAnimator=null
        }
        lifecycleScope.launch {
            userAction.fetchLanguageType().collect{
                refreshType(it)
            }
        }
        userAction.controlResponse.observe(this@ArticleSimpleFragment){changeBackstageLayout(it)}
        lifecycleScope.launch {
            conceptViewModel.articleListResult.collect{result->
                result.onError {
                    it.judgeType().showToast()
                    dismissActivityLoad<MainActivity>()
                }.onSuccess {
                    list.addAll(it)
                    sentenceAdapter.changeData(it)
                    dismissActivityLoad<MainActivity>()
                }.onLoading {
                    showActivityLoad<MainActivity>()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                conceptViewModel.lastUpdateListen.collect{result->
                    result.onSuccess {
                        changeProgressStatus(){index->
                            list[index].listenProgress = it
                        }
                    }.onError {
                        it.judgeType().showToast()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                conceptViewModel.lastUpdateEval.collect{result->
                    result.onSuccess {
                        changeProgressStatus(){index->
                            val evalInc=AppClient.conceptItem.evalSuccess+1
                            list[index].evalSuccess = evalInc
                        }
                    }.onError {
                        it.judgeType().showToast()
                    }
                }
            }
        }
        lifecycleScope.launch {
            conceptViewModel.lastUpdateWord.collect{result->
                result.onSuccess {
                    changeProgressStatus { index ->
                        list[index].wordRight=it.second
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED){
                conceptViewModel.lastUpdateExercise.collect{result->
                    result.onSuccess {
                        changeProgressStatus { index ->
                            list[index].exerciseRight=it.second
                        }
                    }.onError {
                        it.judgeType().showToast()
                    }
                }
//            }
        }
    }

    private fun changeProgressStatus(method:(index:Int)->Unit) {
        if (list.isEmpty()) {
            return
        }
        val index = list.indexOfFirst { item ->
            val bookId = AppClient.conceptItem.bookId
            val language = AppClient.conceptItem.language
            val index = AppClient.conceptItem.index
            val bookIdEqual = item.bookId == bookId
            val languageEqual = item.language == language
            val indexEqual = item.index == index
            bookIdEqual && languageEqual && indexEqual
        }
        if (!index.findIndexSuccess()) {
            return
        }
        method.invoke(index)
        sentenceAdapter.notifyItemChanged(index)
    }


    private fun refreshType(type: LanguageType) {
        GlobalMemory.currentLanguage = type
        setTitleText(type.convertLanguage(), true) {
            requireActivity().startActivity<SelectBookActivity> {  }
        }
        GlobalMemory.currentYoung=type.bookId>4
        conceptViewModel.requestArticleList(type)
    }

    override fun initEventBus(): Boolean =true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:ChangeBookEvent){
        refreshType(event.type)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:CloseBottomEvent){
        bind.backstageLayout.visibilityState(true)
        EventBus.getDefault().post(ControlVideoEvent())
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun event(event:ListenPlayEvent){
        bind.backstageControl.setImageResource((event.play).getServiceImage())
    }


    private fun changeBackstageLayout(isPlaying:Boolean){
        var localFlag=isPlaying
        bind.backstageLayout.visibility=View.VISIBLE
        bind.backstageControl.apply {
            setImageResource(R.drawable.pause)
            setOnClickListener {
                localFlag=!localFlag
                setImageResource(localFlag.getServiceImage())
                EventBus.getDefault().post(ControlVideoEvent())
            }
        }
        bind.backstageText.apply {
            text=AppClient.conceptItem.title
            //如果不设置点击事件就会点穿到下面的RecyclerView.item
            setOnClickListener {
                requireActivity().startActivity<TeachMaterialActivity> {  }
            }
        }
    }

    private val listener= Consumer<ConceptItem> { startTeach(it) }

    private fun startTeach(item:ConceptItem,block: Intent.()->Unit={}){
        AppClient.conceptItem=item
        requireActivity().startActivity<TeachMaterialActivity> { block() }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:ExerciseEvent){
        changeProgressStatus { index ->
            list[index].exerciseRight=event.exerciseNum
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event:WordEvent){
        if (list.isEmpty()) {
            return
        }
        val index = list.indexOfFirst { item ->
            val bookIdEqual = item.bookId == event.bookId
            val indexEqual = item.index == event.index
            bookIdEqual && indexEqual
        }
        if (!index.findIndexSuccess()) {
            return
        }
        list[index].wordRight=event.wordRight
        sentenceAdapter.notifyItemChanged(index)
    }

}