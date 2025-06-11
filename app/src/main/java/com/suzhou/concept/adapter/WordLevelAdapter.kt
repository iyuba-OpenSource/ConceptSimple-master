package com.suzhou.concept.adapter

import com.suzhou.concept.R
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.databinding.WordLevelItemBinding
import com.suzhou.concept.lil.data.newDB.RoomDBManager
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OnWordListItemListener

/**
苏州爱语吧科技有限公司
 */
class WordLevelAdapter:BaseAdapter<WordItem,WordLevelItemBinding>() {

    override fun WordLevelItemBinding.onBindViewHolder(bean: WordItem, position: Int) {
        this.item=bean
        this.playWord.setOnClickListener {
            onWordListItemListener.onPlay(position)
        }

        this.collect.setOnClickListener {
            onWordListItemListener.onCollect(bean)
        }

        //根据数据库数据显示
        val collectBean: WordCollectBean? = RoomDBManager.getInstance().getSingleWordCollectData(GlobalMemory.userInfo.uid,bean.word)
        this.collect.apply {
            if (collectBean==null){
                setImageResource(R.drawable.ic_collect_no)
            }else{
                setImageResource(R.drawable.ic_collected)
            }
        }
    }

    //接口
    private lateinit var onWordListItemListener: OnWordListItemListener

    public fun setListener(listener:OnWordListItemListener){
        this.onWordListItemListener = listener
    }
}