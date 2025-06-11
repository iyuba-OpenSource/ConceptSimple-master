package com.suzhou.concept.activity.article

import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.AnalysisInfoAdapter
import com.suzhou.concept.bean.ExerciseRecord
import com.suzhou.concept.databinding.ActivitySeekAnalysisBinding
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.addDefaultDecoration

class SeekAnalysisActivity : BaseActivity<ActivitySeekAnalysisBinding>() {

    override fun ActivitySeekAnalysisBinding.initBinding() {
        setTitleText(getString(R.string.verify_result))
        val list= intent.getParcelableArrayListExtra<ExerciseRecord>(ExtraKeysFactory.viewAnalysisList) ?: return
        val dataAdapter= with(AnalysisInfoAdapter()){
            changeData(list)
            this
        }
        analysisList.apply {
            addDefaultDecoration()
            adapter=dataAdapter
        }
    }
}