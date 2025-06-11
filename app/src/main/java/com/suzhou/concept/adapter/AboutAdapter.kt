package com.suzhou.concept.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.suzhou.concept.R
import com.suzhou.concept.databinding.AboutItemLayoutBinding


/**
苏州爱语吧科技有限公司
 */
class AboutAdapter (val list:List<String>): RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
    lateinit var itemListener: Consumer<Int>
    inner class ViewHolder(val bind:AboutItemLayoutBinding):RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bind=DataBindingUtil.inflate<AboutItemLayoutBinding>(inflater, R.layout.about_item_layout,parent,false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind.item=list[position]
        holder.bind.aboutItem.setOnClickListener { itemListener.accept(position) }
    }

    override fun getItemCount(): Int =list.size
}