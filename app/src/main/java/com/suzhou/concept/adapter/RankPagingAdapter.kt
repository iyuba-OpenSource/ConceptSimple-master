package com.suzhou.concept.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.suzhou.concept.R
import com.suzhou.concept.bean.RankItem
import com.suzhou.concept.databinding.RankItemLayoutBinding
import com.suzhou.concept.utils.OnClickRankItemListener

/**
苏州爱语吧科技有限公司
 */
class RankPagingAdapter : PagingDataAdapter<RankItem, RankPagingAdapter.RankHolder>(object : DiffUtil.ItemCallback<RankItem>() {
    override fun areItemsTheSame(oldItem: RankItem, newItem: RankItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: RankItem, newItem: RankItem): Boolean {
        return oldItem == newItem
    }
}) {
    lateinit var itemListener: OnClickRankItemListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankPagingAdapter.RankHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bind = DataBindingUtil.inflate<RankItemLayoutBinding>(inflater, R.layout.rank_item_layout, parent, false)
        return RankHolder(bind)
    }

    override fun onBindViewHolder(holder: RankHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind.item = it
            holder.bind.rankIndex.apply {
                if (it.ranking <= 3) {
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.rank_border)
                } else {
                    setTextColor(Color.BLACK)
                    setBackgroundResource(R.drawable.rank_border_white)
                }
            }
            holder.bind.root.setOnClickListener { _ ->
                itemListener.listenItem(it.uid,it.name)
            }
        }
    }

    inner class RankHolder(val bind: RankItemLayoutBinding) : RecyclerView.ViewHolder(bind.root)
}