package com.suzhou.concept.adapter

import com.suzhou.concept.bean.EvaluationSentenceItem
import com.suzhou.concept.databinding.FineListenItemLaoutBinding
import com.suzhou.concept.utils.OnWordClickListener
import com.suzhou.concept.utils.SearchWordListener

/**
苏州爱语吧科技有限公司
 */
class FineListenAdapter :BaseAdapter<EvaluationSentenceItem,FineListenItemLaoutBinding>() {

    lateinit var wordListener: SearchWordListener

    override fun FineListenItemLaoutBinding.onBindViewHolder(bean: EvaluationSentenceItem, position: Int) {
        item=bean
        sentence.setOnWordClickListener(object:OnWordClickListener(){
            override fun onNoDoubleClick(str: String) {
                wordListener.searchListener(str,sentence)
            }
        })
    }
}