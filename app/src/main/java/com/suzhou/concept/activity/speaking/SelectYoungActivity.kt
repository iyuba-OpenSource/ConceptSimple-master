package com.suzhou.concept.activity.speaking

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.YoungBookAdapter
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.databinding.ActivitySelectYoungBinding
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.SelectBookListener
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.launch

class SelectYoungActivity : BaseActivity<ActivitySelectYoungBinding>(), SelectBookListener {


    override fun ActivitySelectYoungBinding.initBinding() {
        setTitleText("选书")
        val youngAdapter = with(YoungBookAdapter()) {
            selectBookListener = this@SelectYoungActivity
            this
        }
        selectYoungList.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = youngAdapter
        }
        young.requestBookList()
        lifecycleScope.launch {
            young.bookList.collect { result ->
                result.onError {
                    dismissLoad()
                    it.judgeType().showToast()
                }.onSuccess {
                    dismissLoad()
                    youngAdapter.changeData(it)
                }.onLoading {
                    dismissLoad()
                }
            }
        }
    }

    /**
     * 相邻的activity不要滥用EventBus
     * */
    override fun listener(languageType: LanguageType) {
        val intent=Intent().putExtra(ExtraKeysFactory.youngSpeaking,languageType)
        setResult(RESULT_OK,intent)
        finish()
    }
}