package com.suzhou.concept.activity.dollar

import androidx.core.util.Consumer
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.ShowCurrencyAdapter
import com.suzhou.concept.bean.BuyCurrency
import com.suzhou.concept.databinding.ActivityBuyCurrencyBinding
import com.suzhou.concept.lil.ui.my.payNew.PayNewActivity
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.addDefaultDecoration

class BuyCurrencyActivity : BaseActivity<ActivityBuyCurrencyBinding>() ,Consumer<Int>{
    private val list= mutableListOf<BuyCurrency>()
    override fun ActivityBuyCurrencyBinding.initBinding() {
        setTitleText("爱语币充值")
        response=GlobalMemory.userInfo
        list.apply {
            add(BuyCurrency(19.9f, R.drawable.buy_200,210))
            add(BuyCurrency(59.9f, R.drawable.buy_600,650))
            add(BuyCurrency(99.9f, R.drawable.buy_1k,1100))
            add(BuyCurrency(599f, R.drawable.buy_6k,6600))
            add(BuyCurrency(999f, R.drawable.buy_1w,12000))
        }
        val showAdapter= with(ShowCurrencyAdapter()){
            buyListener=this@BuyCurrencyActivity
            changeData(list)
            this
        }
        butList.apply {
            adapter=showAdapter
            addDefaultDecoration()
        }

    }

    override fun accept(t: Int?) {
        t?.let {
            list[it].apply {
//                startPayActivity(orderInfo,price.toString(),true)

                val amount = list[it].iyuCount.toString()
                val price = list[it].price.toString()
                val subject = "爱语币"
                val productId = "1"
                PayNewActivity.start(this@BuyCurrencyActivity,
                    PayNewActivity.PayType_iyuIcon,amount,price,subject,productId)
            }
        }
    }

}