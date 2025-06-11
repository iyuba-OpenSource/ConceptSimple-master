package com.suzhou.concept.activity.dollar

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.alipay.sdk.app.PayTask
import com.iyuba.imooclib.IMooc
import com.suzhou.concept.BuildConfig
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.databinding.ActivityPayBinding
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.util.LibRxTimer
import com.suzhou.concept.lil.view.dialog.LoadingMsgDialog
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showDialog
import com.suzhou.concept.utils.showPrivacyDialog
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.utils.ToastFactory

/**
 * 被替换了，替换的为PayNewActivity
 *
 * 当前界面已经无用了
 */
class PayActivity : BaseActivity<ActivityPayBinding>() {
    private lateinit var price: String
    private lateinit var amount: String
    /**
     * 是否为购买爱语币
     * */
    private var buyAiDollar=false
    //用户信息延迟加载计时器
    private val timer_userInfoDelay = "timer_userInfoDelay"

    companion object{
        fun toPayActivity(context: Context,isIyuBi:Boolean,payType:String,price:String,productId:String,amount:String){
            var intent:Intent = Intent()
            intent.setClass(context,PayActivity::class.java)
            intent.putExtra(ExtraKeysFactory.buyType,isIyuBi)//是否是爱语币
            intent.putExtra(ExtraKeysFactory.payPrice,price)//价格
            intent.putExtra(ExtraKeysFactory.payType,payType)//类型名称
            intent.putExtra(ExtraKeysFactory.productId,productId)//类型id
            intent.putExtra(ExtraKeysFactory.amount,amount)//数量
            context.startActivity(intent)
        }
    }

    override fun ActivityPayBinding.initBinding() {
        setTitleText("支付")
        price = intent?.getStringExtra(ExtraKeysFactory.payPrice).toString()
        if (BuildConfig.DEBUG){
            price = "0.01"
        }
        //需要区分购买爱语币还是会员
        buyAiDollar=intent.getBooleanExtra(ExtraKeysFactory.buyType,false)
        payChinaDollar = price + "元"
        welcome = GlobalMemory.userInfo.username
        val payType = intent.getStringExtra(ExtraKeysFactory.payType).toString()
        order = payType
        //这里进行区分处理，增加微课购买逻辑
        if (order.toString().contains("微课课程")){
            amount = intent.getStringExtra(ExtraKeysFactory.amount).toString()
        }else{
            payType.let {
                amount =if (buyAiDollar){
                    val buy=it.indexOf("买")+1
                    val love=it.indexOf("爱")
                    it.substring(buy,love)
                }else {
                    val startIndex = it.indexOf("员")+ 1
                    val endIndex = it.indexOf("个")
                    it.substring(startIndex, endIndex)
                }
            }
        }
        diyText()
        var markTime = 0L
        verifyPay.setOnClickListener {
            if (System.currentTimeMillis() - markTime > 3000) {
                markTime = System.currentTimeMillis()
                "是否去支付？".showDialog(this@PayActivity, "去支付") {
                    alipay()
                }
            } else {
                "您点击的太快了。。。".showToast()
            }
        }
    }

    private fun diyText() {
        val zfbDesc = resources.getString(R.string.zfb_desc)
        val span = SpannableString(zfbDesc)
        val fore = ForegroundColorSpan(ContextCompat.getColor(this, R.color.text_gray))
        span.setSpan(fore, zfbDesc.indexOf("\n"), zfbDesc.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.zfbPay.text = span
    }

    /**
     * flowOn(Dispatchers.IO)指定上游操作的执行线程
     * */
    @OptIn(FlowPreview::class)
    private fun alipay() {
        val cate = if (buyAiDollar){
            "爱语币"
        }else{
            if (binding.order.toString().contains("员")){
                //会员操作
                binding.order.toString().let { it.substring(0,it.indexOf("员")+1) }
            }else{
                //其他操作
                binding.order.toString()
            }
        }
        val body=if (cate=="黄金会员"){
            val appName=getString(R.string.app_name)
            "${appName}-花费${price}元购买${appName}${cate}"
        }else{
            "花费${price}元购买${cate}"
        }
        var productId = getProductId(cate)
        if (intent.getStringExtra(ExtraKeysFactory.productId)!=null){
            productId = intent.getStringExtra(ExtraKeysFactory.productId).toString()
        }
        lifecycleScope.launch {
            userAction.requestPayVip(price, amount, productId, cate, body).flatMapConcat {
                val alipay = PayTask(this@PayActivity)
                val ailiPayResult = alipay.payV2(it.alipayTradeStr, true).toString()
                userAction.payVip(ailiPayResult)
            }.flowOn(Dispatchers.IO).catch {
                it.judgeType().showToast()
            }.collect {
                "是否开通成功？".showPrivacyDialog(this@PayActivity, "提示", "确定", "取消", {
                    if (it.isSuccess()){
                        startLoading("正在加载用户信息")
                        LibRxTimer.getInstance().timerInMain(timer_userInfoDelay,1000L,object:
                            LibRxTimer.RxActionListener{
                            override fun onAction(number: Long) {
                                LibRxTimer.getInstance().cancelTimer(timer_userInfoDelay)
                                userAction.refreshUserInfo()
                            }
                        })
                    }else{
                        "开通失败".showToast()
                    }
                }, {
                    "开通失败".showToast()
                })
            }
        }
    }

    private fun getProductId(subject: String) = when (subject) {
        "本应用会员" -> "10"
        "全站会员" -> "0"
        "黄金会员" -> "21"
        //爱语币为1
        else -> "1"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event:RefreshEvent){
        if (event.type.equals(RefreshEvent.USER_VIP)){
            stopLoading()
            if (!TextUtils.isEmpty(event.msg)){
                ToastFactory.showShort(this@PayActivity,event.msg)
                return
            }

            //微课直购进行刷新
            val payType = intent.getStringExtra(ExtraKeysFactory.payType).toString()
            if (payType.contains("微课课程")){
                IMooc.notifyCoursePurchased()
            }
            finish()
            "开通成功！若未生效重新登录即可".showToast()
        }
    }

    override fun initEventBus(): Boolean {
        return true
    }

    /**************************新的操作*********************/
    //显示支付未知状态弹窗
    private fun showPayUnknownDialog(){
        AlertDialog.Builder(this)
            .setMessage("是否支付完成\n\n(如会员、课程未生效，请退出后重新登录)")
            .setPositiveButton("已完成",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    //刷新数据
                    getUserInfo()
                }
            }).setNegativeButton("未完成",object :DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    //刷新数据
                    getUserInfo()
                }
            }).setCancelable(false)
            .create().show()
    }

    //显示自定义内容弹窗
    private fun showPayCustomsDialog(showMsg:String){
        AlertDialog.Builder(this)
            .setMessage(showMsg)
            .setPositiveButton("确定",object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    finish()
                }
            }).setCancelable(false)
            .create().show()
    }

    //加载弹窗
    private var loadingDialog:LoadingMsgDialog? = null

    private fun startLoading(showMsg:String){
        if (loadingDialog==null){
            loadingDialog = LoadingMsgDialog(this)
        }
        loadingDialog!!.setMsg(showMsg)
        if (loadingDialog!!.isShowing){
            loadingDialog!!.show()
        }
    }

    private fun stopLoading(){
        if (loadingDialog!=null&&loadingDialog!!.isShowing){
            loadingDialog!!.dismiss()
        }
    }

    //加载用户数据
    private fun getUserInfo(){

    }
}