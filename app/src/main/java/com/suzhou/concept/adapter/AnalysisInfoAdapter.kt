package com.suzhou.concept.adapter

import com.suzhou.concept.bean.ExerciseRecord
import com.suzhou.concept.databinding.SeekAnalysisItemBinding

/**
苏州爱语吧科技有限公司
@Date:  2023/2/7
@Author:  han rong cheng
 */
class AnalysisInfoAdapter :BaseAdapter<ExerciseRecord, SeekAnalysisItemBinding>() {
    override fun SeekAnalysisItemBinding.onBindViewHolder(bean: ExerciseRecord, position: Int) {
        item=bean
    }
}