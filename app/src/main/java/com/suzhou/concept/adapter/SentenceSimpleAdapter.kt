package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.databinding.SentenceItemLayoutHuaweiBinding

/**
苏州爱语吧科技有限公司
 */
class SentenceSimpleAdapter :BaseAdapter<ConceptItem, SentenceItemLayoutHuaweiBinding>() {
    lateinit var itemListener:Consumer<ConceptItem>

    override fun SentenceItemLayoutHuaweiBinding.onBindViewHolder(bean: ConceptItem, position: Int) {
        item=bean
        root.setOnClickListener {
            itemListener.accept(bean)
        }


        //区分数据类型，然后分别赋值
        if (bean.title.lowercase().contains("unit")){
            var strArray = bean.title.split(" ")
            if (strArray.size>2){
                var unitName = strArray[0]+" "+strArray[1]
                sentenceIndex.text = unitName
            }else{
                sentenceIndex.text = "Unit ${position+1}"
            }
        }else{
            sentenceIndex.text = "Lesson ${position+1}"
        }
    }
}