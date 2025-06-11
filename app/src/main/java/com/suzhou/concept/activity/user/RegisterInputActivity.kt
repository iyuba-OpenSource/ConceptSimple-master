package com.suzhou.concept.activity.user

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.LoginBean
import com.suzhou.concept.bean.UpdateLocalWordEvent
import com.suzhou.concept.databinding.ActivityRegisterInputBinding
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class RegisterInputActivity : BaseActivity<ActivityRegisterInputBinding>() ,View.OnClickListener{
    private val loginBean=LoginBean(protocol = "11002")
    override fun ActivityRegisterInputBinding.initBinding() {
        setTitleText("用户注册")
        login=loginBean
        submitRegister.setOnClickListener (this@RegisterInputActivity)
    }


    private fun submitRegister(){
        if (loginBean.isUserNameEmpty()) {
//            binding.usernameRegister.error = "用户ID长度为3~15个字符"
            ToastUtil.showToast(this, "用户ID长度为3~15个字符")
            return
        }
//        binding.usernameRegister.error = ""
        if (loginBean.isPasswordEmpty()) {
//            binding.passwordRegister.error = "密码为6~15个字符"
            ToastUtil.showToast(this, "密码为6~15个字符")
            return
        }
//        binding.passwordRegister.error = ""
        val registerPhone = intent.getStringExtra(ExtraKeysFactory.registerPhone).toString()
        lifecycleScope.launch {
            userAction.fastRegister(registerPhone, loginBean).onStart {
                showLoad()
            }.collect{
                dismissLoad()
                it.onSuccess {result->
                    result.apply {
                        if (isSuccess()){
                            EventBus.getDefault().post(UpdateLocalWordEvent())
                            GlobalMemory.inflateLoginInfo(this)
                            "注册成功".showToast()
                            userAction.saveLogin(this)
                            finish()
                        }else{
                            getErrorType().showToast()
                        }
                    }

                }.onFailure { error->
                    error.judgeType().showToast()
                }
            }
        }


    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.submit_register-> submitRegister()
        }
    }


}