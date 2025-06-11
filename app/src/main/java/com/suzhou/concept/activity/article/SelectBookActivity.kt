package com.suzhou.concept.activity.article

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.iyuba.module.toolbox.GsonUtils
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.activity.article.data.ConceptJuniorBookBean
import com.suzhou.concept.activity.article.data.ConceptJuniorBookData
import com.suzhou.concept.adapter.SelectBookAdapter
import com.suzhou.concept.adapter.YoungBookAdapter
import com.suzhou.concept.bean.ChangeBookEvent
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.databinding.ActivitySelectBookBinding
import com.suzhou.concept.lil.service.data.ListenPlayEvent
import com.suzhou.concept.lil.view.NoScrollGridLayoutManager
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.SelectBookListener
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
@Date:  2022/9/20
@Author:  han rong cheng
 */
class SelectBookActivity: BaseActivity<ActivitySelectBookBinding>(), SelectBookListener {
    override fun ActivitySelectBookBinding.initBinding() {
        setTitleText("选书")
        newUkList.addData("UK")
        newUsList.addData("US")
        val youngAdapter= with(YoungBookAdapter()){
            selectBookListener=this@SelectBookActivity
            this
        }
        newYoungList.apply {
//            layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            layoutManager = NoScrollGridLayoutManager(this@SelectBookActivity,2,false)
            adapter=youngAdapter
        }

        // TODO: 这里李涛要求暂时固定青少版的数据，因为数据比较少，直接使用静态文本数据处理
        if (ConceptJuniorBookData.bookData.isNotEmpty()){
            //从静态文件的中获取数据
            val juniorBookData = GsonUtils.toObject(ConceptJuniorBookData.bookData,ConceptJuniorBookBean::class.java)
            if (juniorBookData!=null && juniorBookData.data.size>0){
                youngAdapter.changeData(juniorBookData.data)
            }else{
                young.requestBookList()
            }
        }else{
            //请求青少版的数据
            young.requestBookList()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                young.bookList.collect{result->
                    result.onError {
                        it.judgeType().showToast()
                    }.onLoading {
                        showLoad()
                    }.onSuccess {
                        dismissLoad()
                        youngAdapter.changeData(it)
                    }
                }
            }
        }
    }

    override fun listener(languageType: LanguageType) {
        lifecycleScope.launch {
            userAction.saveLanguage(languageType).collect{
                if (GlobalMemory.curSelectLanguage != languageType.language
                    ||GlobalMemory.curSelectBookId != languageType.bookId){

                    //隐藏首页的播放
                    EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_ui_hide))
                }

                //这里如果类型和书籍id和之前的不一样，则隐藏下首页的后台播放
                GlobalMemory.curSelectLanguage = languageType.language
                GlobalMemory.curSelectBookId = languageType.bookId

                if (it)finish()
            }
        }

        GlobalMemory.currentYoung=languageType.bookId>4
        EventBus.getDefault().post(ChangeBookEvent(languageType))
    }

    private fun RecyclerView.addData(language:String){
        val count=4
//        layoutManager=StaggeredGridLayoutManager(count,StaggeredGridLayoutManager.VERTICAL)
        layoutManager = NoScrollGridLayoutManager(this@SelectBookActivity,count,false)
        val list= mutableListOf<LanguageType>()
        for (i in 1..4){
            list.add(LanguageType(language,i))
        }
        adapter=with(SelectBookAdapter(list)){
            selectBookListener=this@SelectBookActivity
            this
        }
    }
}