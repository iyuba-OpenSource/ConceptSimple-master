package com.suzhou.concept.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import com.suzhou.concept.R
import com.suzhou.concept.adapter.TitlePopAdapter
import com.suzhou.concept.bean.TitlePopBean

/**
苏州爱语吧科技有限公司
@Date:  2022/12/2
@Author:  han rong cheng
 */
class TitlePopupWindow(
    context: Context,
    width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) : PopupWindow(context)  {
    private val adapter = TitlePopAdapter()

    init {
        isTouchable = true
        isFocusable = true
        this.width = width
        this.height = height
        contentView = View.inflate(context, R.layout.title_popup_content, null)
        val popList = contentView.findViewById<RecyclerView>(R.id.pop_list)
        popList.adapter=adapter
    }

    fun addItem(list: List<TitlePopBean>) {
        adapter.changeData(list)
    }

    fun registerItemListener(itemListener: Consumer<Int>){
        adapter.itemListener=itemListener
    }
}