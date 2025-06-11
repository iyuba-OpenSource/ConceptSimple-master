package com.suzhou.concept.activity.user

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.RegisterBean
import com.suzhou.concept.databinding.ActivityRegisterBinding
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.checkPhone
import com.suzhou.concept.utils.getPolicyUrl
import com.suzhou.concept.utils.getProtocolUrl
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import com.suzhou.concept.utils.startWeb
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


/**
 * 手机号注册界面
 */
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(), View.OnClickListener {
    private lateinit var eventHandler: EventHandler
    private val register = RegisterBean()
    private val submitSuccess=0
    private val getSuccess=1
    private val timing=2
    private val error=3
    private var count=60

    private val handler=Handler(Looper.myLooper()!!){
        when(it.what){
            submitSuccess->{
                //提交验证码成功
                "提交验证码成功".showToast()
                dismissLoad()
                startActivity<RegisterInputActivity> {
                    putExtra(ExtraKeysFactory.registerPhone,register.phone)
                }
                finish()
            }
            getSuccess-> "获取验证码成功".showToast()
            error-> {
                it.obj.toString().showToast()
                dismissLoad()
            }
            timing->{
                val text="重新发送($count S)"
                binding.requestVerify.text=text
                if (count==0){
                    switchSendSMSView(true)
                    binding.requestVerify.text=resources.getString(R.string.get_verify)
                }
            }
        }
        true
    }
    override fun ActivityRegisterBinding.initBinding() {
        //刚想起来似乎有部分没双向绑定上？？？
        register=this@RegisterActivity.register
        init()
        setTitleText("用户注册")
        siteSuperLink()
        eventHandler = object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    when (event) {
                        SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE -> handler.sendEmptyMessage(submitSuccess)
                        SMSSDK.EVENT_GET_VERIFICATION_CODE -> handler.sendEmptyMessage(getSuccess)
                    }
                }else{
                    val msg= with(Message()){
                        what=error
                        obj=data
                        this
                    }
                    handler.sendMessage(msg)
                }
            }
        }
        SMSSDK.registerEventHandler(eventHandler)

        //关闭邮箱注册
        binding.webRegister.visibility = View.INVISIBLE
    }

    private fun init() {
        binding.apply {
            register = register
            webRegister.setOnClickListener(this@RegisterActivity)
            webRegister.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            requestVerify.setOnClickListener(this@RegisterActivity)
            next.setOnClickListener(this@RegisterActivity)
        }
    }

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

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.standard_left -> finish()
            R.id.web_register -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppClient.webRegister)))
            R.id.request_verify -> requestVerify()
            R.id.next -> {
                if (register.isEmpty()){
                    "手机号和验证码不能为空！".showToast()
                    return
                }
                if (!register.agree) {
                    "请同意《使用协议》和《隐私政策》".showToast()
                    return
                }
                SMSSDK.submitVerificationCode("86",register.phone,register.verifyCode)
                showLoad()
            }
        }
    }

    private fun requestVerify(){
        /*lifecycleScope.launch {
            userAction.getSendSmsStatus().collect{
                if (!it){
                    "获取发送以及接受短信权限".showPrivacyDialog(this@RegisterActivity,"提示","确定","取消",{
                        judgeCurr()
                        launch { userAction.saveSendSms().first()}
                        UserDao.saveSendSms()
                    },{
                        "申请权限失败".showToast()
                    })
                    return@collect
                }
                judgeCurr()
            }
        }*/

        //发送短信
        judgeCurr()
    }

    private fun judgeCurr(){
        when{
            !register.phone.checkPhone()->"请输入正确的手机号码".showToast()
            !register.agree->"请同意《使用协议》和《隐私政策》".showToast()
            else->judgePhoneStatus()
        }
    }

    private fun judgePhoneStatus(){
        lifecycleScope.launch {
            userAction.getRegisterStatus(register.phone).onStart {
                showLoad()
            }.collect{
                dismissLoad()
                it.onSuccess {result->
                    if (result.isNotRegistered()) {
                        SMSSDK.getVerificationCode("86", register.phone)
                        switchSendSMSView(false)
                        //重新发送倒计时
                        count=60
                        thread {
                            while (count>0){
                                count--
                                Thread.sleep(1000)
                                handler.sendEmptyMessage(timing)
                            }
                        }
                    } else {
                        "手机号已注册，请换个手机号试试".showToast()
                    }
                }.onFailure { e->
                    e.judgeType().showToast()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    //设置发送短信按钮操作
    private fun switchSendSMSView(isEnable:Boolean) {
        binding.requestVerify.isEnabled = isEnable

        if (isEnable){
            binding.requestVerify.setBackgroundResource(R.drawable.shape_corner_theme_10dp)
        }else{
            binding.requestVerify.setBackgroundResource(R.drawable.shape_corner_gray_10dp)
        }
    }
}