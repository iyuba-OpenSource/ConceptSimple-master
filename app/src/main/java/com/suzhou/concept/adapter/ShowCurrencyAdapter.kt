package com.suzhou.concept.adapter

import androidx.core.util.Consumer
import com.suzhou.concept.bean.BuyCurrency
import com.suzhou.concept.databinding.BuyCurrencyItemBinding

/**
苏州爱语吧科技有限公司
@Date:  2022/8/30
@Author:  han rong cheng
 */
class ShowCurrencyAdapter :BaseAdapter<BuyCurrency,BuyCurrencyItemBinding>() {
    lateinit var buyListener:Consumer<Int>
    override fun BuyCurrencyItemBinding.onBindViewHolder(bean: BuyCurrency, position: Int) {
        item=bean
        buyNow.setOnClickListener {
            if (::buyListener.isInitialized){
                buyListener.accept(position)
            }
        }
    }
}