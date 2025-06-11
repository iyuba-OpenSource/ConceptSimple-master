package com.suzhou.concept.activity.user

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.LoginBean
import com.suzhou.concept.bean.UpdateLocalWordEvent
import com.suzhou.concept.databinding.ActivityFastRegisterBinding
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import kotlin.random.Random

class FastRegisterActivity : BaseActivity<ActivityFastRegisterBinding>() {
    private var phone=""

    override fun ActivityFastRegisterBinding.initBinding() {
        setTitleText("快捷注册")
        //19861978744
        phone=intent.getStringExtra(ExtraKeysFactory.fastRegisterPhone).toString()
        val username="user_${getRandomFour()}${phone.substring(7,phone.length)}"
        val password=phone.substring(5,phone.length)
        item= LoginBean(
            username = username,
            password = password,
            protocol = "11002"
        )
        defaultPassword.text= getDefaultNameOrPass(false,"手机号后六位")
        defaultUsername.text= getDefaultNameOrPass(true,username)
        submitRegister.setOnClickListener { submitRegister() }
    }

    private fun getDefaultNameOrPass(isName:Boolean,value:String): SpannableStringBuilder {
        val type=if (isName) R.string.default_username else R.string.default_password
        val content=resources.getString(type)+value
        val symbol="："
        val builder= SpannableStringBuilder(content)
        if (content.contains(symbol)){
            val symbolIndex=content.indexOf(symbol)
            val beforeSpan= ForegroundColorSpan(Color.BLACK)
            val afterSpan= ForegroundColorSpan(ContextCompat.getColor(this,R.color.modify_head))
            builder.apply {
                setSpan(beforeSpan,0,symbolIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                setSpan(afterSpan,symbolIndex+1,content.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
        }
        return builder
    }

    /**
     * 获取随机4位数字
     * */
    private fun getRandomFour()= with(StringBuilder()){
        repeat(4) {
            val ran = Random.nextInt(9)
            append(ran)
        }
        toString()
    }

    private fun submitRegister(){
        binding.apply {
            if(item!!.isUserNameEmpty()){
//                fastNameLayout.error="用户名长度为3~15个字符"
                ToastUtil.showToast(this@FastRegisterActivity,"用户名长度为3~15个字符")
                return
            }
//            fastNameLayout.error=""
            if (item!!.isPasswordEmpty()){
//                fastWordLayout.error="密码为6~15个字符"
                ToastUtil.showToast(this@FastRegisterActivity,"密码为6~15个字符")
                return
            }
//            fastWordLayout.error=""
            lifecycleScope.launch {
                userAction.fastRegister(phone,item!!).onStart {
                    showLoad()
                }.collect{
                    dismissLoad()
                    it.onSuccess { result ->
                        when (result.result) {
                            "111" -> {
                                EventBus.getDefault().post(UpdateLocalWordEvent())
                                GlobalMemory.inflateLoginInfo(result)
                                userAction.saveLogin(result)
                                finish()
                                "注册成功"
                            }
                            "112" -> "账号已存在"
                            else -> result.message
                        }.showToast()
                    }.onFailure { e ->
                        e.judgeType().showToast()
                    }
                }
            }
        }

    }
}