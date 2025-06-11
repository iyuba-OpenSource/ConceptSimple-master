package com.suzhou.concept.activity.user

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.telephony.TelephonyManager
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.iyuba.sdk.other.NetworkUtil
import com.mob.secverify.SecVerify
import com.mob.secverify.VerifyCallback
import com.mob.secverify.common.exception.VerifyException
import com.mob.secverify.datatype.VerifyResult
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.LoginBean
import com.suzhou.concept.bean.UpdateLocalWordEvent
import com.suzhou.concept.databinding.ActivityLoginBinding
import com.suzhou.concept.lil.event.LocalEvalDataRefreshEvent
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.manager.TempDataManager
import com.suzhou.concept.lil.util.LibRxTimer
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.getPolicyUrl
import com.suzhou.concept.utils.getProtocolUrl
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import com.suzhou.concept.utils.startWeb
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import personal.iyuba.personalhomelibrary.utils.ToastFactory

class LoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {
    private val loginBean = LoginBean()

    //计时器tag
    //用户信息延迟获取计时器
    private val timer_userInfoDelay = "timer_userInfoDelay"
    //用户登录延迟获取计时器
    private val timer_loginDelay = "timer_loginDelay"

    override fun ActivityLoginBinding.initBinding() {
        binding.loginItem = loginBean
        setTitleText("用户登录")

        //判断是否显示秒验登录
        val secondVerifyEnabled=(SecVerify.isVerifySupport() && hasSimCard() && isNetEnabled() && TempDataManager.getInstance().mobVerify)
        updateUi(secondVerifyEnabled,"正在加载登录信息～")
        if (secondVerifyEnabled){
            startSecondVerify()
        }

        listenLogin()
        siteSuperLink()
    }

    private fun listenLogin(){
        lifecycleScope.launch {
            userAction.loginResult.collect{result->
                result.onLoading {
                    updateUi(true,"正在更新用户信息～")
                }.onSuccess {
                    if (it.isSuccess()){
                        //刷新单词
                        EventBus.getDefault().post(RefreshEvent(RefreshEvent.WORD_PASS_REFRESH,null))
                        //刷新首页
                        EventBus.getDefault().post(LocalEvalDataRefreshEvent())

                        GlobalMemory.inflateLoginInfo(it)

                        //这里有个问题，直接退出会无法显示钱包和积分数据，延迟一下再退出
                        LibRxTimer.getInstance().timerInMain(timer_loginDelay,1500,object :
                            LibRxTimer.RxActionListener{
                            override fun onAction(number: Long) {
                                LibRxTimer.getInstance().cancelTimer(timer_loginDelay)
                                finish()
                                ToastFactory.showShort(this@LoginActivity,"登录成功")
                            }
                        })
                    }else{
                        updateUi(false,"")
                        it.getErrorType().showToast()
                    }
                }.onError {
                    updateUi(false,"")
                    it.judgeType().showToast()
                }
            }
        }
    }

    override fun initView() {
        binding.forgotPassword.apply {
            setOnClickListener(this@LoginActivity)
            paint.flags = Paint.UNDERLINE_TEXT_FLAG
        }
        binding.register.setOnClickListener(this)
        binding.login.setOnClickListener(this)
        binding.readCheck.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.forgot_password -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppClient.forgotPassword)))
            R.id.register -> {
                startActivity<RegisterActivity> {}
                finish()
            }
            R.id.login -> {
                val isCheck = binding.readCheck.isChecked
                if (!isCheck){
                    ToastUtil.showToast(this,"请阅读并同意《使用协议》和《隐私政策》后使用")
                    return
                }

                if (loginBean.isUserNameEmpty()) {
                    ToastUtil.showToast(this,"请填写有效的用户ID")
//                    binding.usernameTextInput.error = "请填写有效的用户ID"
                } else {
//                    binding.usernameTextInput.error = ""
                    if (loginBean.isPasswordEmpty()) {
                        ToastUtil.showToast(this,"密码为6~12个字符")
//                        binding.passwordTextInput.error = "密码为6~12个字符"
                    } else {
//                        binding.passwordTextInput.error = ""
                        userAction.loginUser1(loginBean)
                    }
                }
            }
        }
    }


    /**
     * 判断是否包含SIM卡
     * */
    private fun hasSimCard(): Boolean {
        val  telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telMgr.simState) {
            TelephonyManager.SIM_STATE_ABSENT-> false; // 没有SIM卡
            TelephonyManager.SIM_STATE_UNKNOWN-> false
            else-> true
        }
    }


    private fun startSecondVerify(){
        SecVerify.verify(object: VerifyCallback() {
            override fun onComplete(p0: VerifyResult?) {
                p0?.let {
                    notificationService(it)
                }
            }

            override fun onFailure(p0: VerifyException?) {
                updateUi(false,"")
            }

            override fun onOtherLogin() {
                SecVerify.finishOAuthPage()
                updateUi(false,"")
            }

            override fun onUserCanceled() {
                SecVerify.finishOAuthPage()
                finish()
            }

        })
    }

    private fun isNetEnabled(): Boolean {
        //这个好像有点不对啊，暂时屏蔽
        /*val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.allNetworkInfo.forEach {
            return it.state == NetworkInfo.State.CONNECTED
        }
        return false*/

        return NetworkUtil.isConnected(this@LoginActivity)
    }

    private fun notificationService(verify: VerifyResult){
        lifecycleScope.launch {
            userAction.secondVerify(verify).catch {
                it.judgeType().showToast()
                updateUi(false,"")

            }.collect {

                if (it.isNoResisterOrError()){
                    startActivity<FastRegisterActivity> {
                        putExtra(ExtraKeysFactory.fastRegisterPhone,it.res.phone)
                    }
                    finish()
                    return@collect
                }

                if (it.goHandLogin()){
                    updateUi(false,"")
                    "登录失败，请手动登录".showToast()
                    return@collect
                }

                if (it.isLogin==1) {
                    it.userinfo.apply {
                        updateUi(true,"正在更新用户信息～")
                        EventBus.getDefault().post(UpdateLocalWordEvent())
                        GlobalMemory.inflateLoginInfo(it.userinfo)
                        userAction.saveLogin(this)

                        //使用20001接口重新获取数据
                        userAction.refreshUserInfo()

                        //延迟退出
                        LibRxTimer.getInstance().timerInMain(timer_userInfoDelay,1500L,object:
                            LibRxTimer.RxActionListener{
                            override fun onAction(number: Long) {
                                LibRxTimer.getInstance().cancelTimer(timer_userInfoDelay)
                                finish()
                            }
                        })
                    }
                    return@collect
                }

                updateUi(false,"")
                "登录失败，请手动登录".showToast()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //更新界面显示
    private fun updateUi(isLoading:Boolean,showMsg:String) {
        if (isLoading){
            binding.loadingLayout.visibility = View.VISIBLE
            binding.loginLayout.visibility = View.GONE

            binding.showMsg.text = showMsg
        }else{
            binding.loadingLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
        }
    }

    //设置链接
    private fun siteSuperLink() {
        val content = resources.getString(R.string.read_agree)
        val builder = SpannableStringBuilder()
        builder.append(content)
        val start = content.indexOf("《")
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                startWeb(getPolicyUrl())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.main)
                ds.isUnderlineText = false
            }
        }, start, start + 6, 0)
        val end = content.lastIndexOf("《")
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                startWeb(getProtocolUrl())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.main)
                ds.isUnderlineText = false
            }
        }, end, end + 6, 0)
        binding.readAgree.movementMethod = LinkMovementMethod.getInstance()
        binding.readAgree.setText(builder, TextView.BufferType.SPANNABLE)
    }
}