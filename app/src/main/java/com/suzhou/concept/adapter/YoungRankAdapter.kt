package com.suzhou.concept.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.suzhou.concept.R
import com.suzhou.concept.bean.YoungRankItem
import com.suzhou.concept.databinding.SpeakingRankItemBinding

class YoungRankAdapter : PagingDataAdapter<YoungRankItem, YoungRankAdapter.RankHolder>(object :
    DiffUtil.ItemCallback<YoungRankItem>() {
    override fun areItemsTheSame(oldItem: YoungRankItem, newItem: YoungRankItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: YoungRankItem, newItem: YoungRankItem): Boolean {
        return oldItem == newItem
    }
}) {
    private lateinit var videoListener: Consumer<YoungRankItem>

    inner class RankHolder(val bind: SpeakingRankItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onBindViewHolder(holder: RankHolder, position: Int) {
        holder.bind.let {
            it.item = getItem(position)
            it.speakingItemIndex.text = with(StringBuilder()) {
                append(position + 1)
                toString()
            }
            it.root.setOnClickListener {
                //http://iuserspeech.iyuba.cn:9001/video/voa/kouyu/2022/11/13/1668299211034.mp4
                if (::videoListener.isInitialized) {
                    videoListener.accept(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bind = DataBindingUtil.inflate<SpeakingRankItemBinding>(
            inflater,
            R.layout.speaking_rank_item,
            parent,
            false
        )
        return RankHolder(bind)
    }

    fun registerVideoListener(listener: Consumer<YoungRankItem>) {
        videoListener = listener
    }
}