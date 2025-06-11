package com.suzhou.concept.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.suzhou.concept.R
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.databinding.SelectBookItemBinding
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.SelectBookListener

/**
苏州爱语吧科技有限公司
 */
class SelectBookAdapter(private val list: List<LanguageType>):RecyclerView.Adapter<SelectBookAdapter.ViewHolder>() {
    lateinit var selectBookListener: SelectBookListener
    inner class ViewHolder(val bind: SelectBookItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val binding = DataBindingUtil.inflate<SelectBookItemBinding>(LayoutInflater.from(context), R.layout.select_book_item, parent, false)
        val viewHolder = ViewHolder(binding)
        viewHolder.bind.root.setOnClickListener {
            selectBookListener.listener(list[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind.item = OtherUtils.convertInt(list[holder.adapterPosition].bookId)
    }

    override fun getItemCount() = list.size
}