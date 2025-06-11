package com.suzhou.concept.activity.user

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.text.InputType
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.LoginBean
import com.suzhou.concept.bean.LoginResponse
import com.suzhou.concept.databinding.ActivityModifyUserNameBinding
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.emitFlow
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class ModifyUserNameActivity : BaseActivity<ActivityModifyUserNameBinding>(), View.OnClickListener {

    private val loginBean = LoginBean()
    private lateinit var loginResponse: LoginResponse
    private val action = AppClient.action


    override fun initView() {
        binding.loginBean = loginBean
        binding.verifyPassword.setOnClickListener(this)
        binding.forgotModifyUsername.setOnClickListener(this)
        binding.forgotModifyUsername.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        /**/
        if (AppClient.action == 2) {
            binding.passwordModifyUsername.inputType = InputType.TYPE_CLASS_TEXT
        }

        //hint
        /*binding.passwordInputModifyUsername.hint=when(action){
            1->resources.getString(R.string.hint_password)
            2->resources.getString(R.string.input_new_username)
            3->resources.getString(R.string.hint_password)
            else -> ""
        }*/
        binding.passwordModifyUsername.hint = when (action) {
            1 -> resources.getString(R.string.hint_password)
            2 -> resources.getString(R.string.input_new_username)
            3 -> resources.getString(R.string.hint_password)
            else -> ""
        }
        //button
        binding.verifyPassword.text = when (action) {
            1 -> resources.getString(R.string.verify)
            2 -> resources.getString(R.string.modify_username)
            3 -> resources.getString(R.string.logout)
            else -> ""
        }
    }

    private fun verifyPassWordSuccess() {
        "验证成功".showToast()
        AppClient.action = 2
        startActivity(Intent(this, ModifyUserNameActivity::class.java))
        finish()
    }

    private fun modifyUserName() {
        if (loginBean.isModifyEmpty()) {
//            binding.passwordInputModifyUsername.error = "请填写有效的用户ID"
            ToastUtil.showToast(this, "请填写有效的用户ID")
            return
        }
//        binding.passwordInputModifyUsername.error = ""
        lifecycleScope.launch {
            userAction.modifyUserName(loginResponse, loginBean.password).onStart {
                showLoad()
            }.collect {
                it.onSuccess { result ->
                    if (result.isSuccess()) {
                        GlobalMemory.userInfo.username = result.username
                        userAction.updateLocalName(result.username)
                        finish()
                        "修改成功"
                    } else {
                        when (result.message) {
                            "uid" -> "请检查用户ID是否正确"
                            "username " -> "请检查用户名是否正确"
                            "oldUsername" -> "请输入新的用户名"
                            "10012 sign is error:" -> "sign拼接错误"
                            "username had been used by other user" -> "用户名已被其他用户使用"
                            else -> "修改失败"
                        }
                    }.showToast()
                }.onFailure { e ->
                    e.judgeType().showToast()
                }
            }
        }

    }

    private fun noModifyUserName() {
        if (loginBean.isPasswordEmpty()) {
//            binding.passwordInputModifyUsername.error = "密码为6~12个字符"
            ToastUtil.showToast(this, "密码为6~12个字符")
            return
        }
//        binding.passwordInputModifyUsername.error = ""
        lifecycleScope.launch {
            //将名称和密码设置上
            loginBean.username = GlobalMemory.userInfo.username

            userAction.loginUser(loginBean).onStart {
                showLoad()
            }.collect {
                it.onSuccess { result ->
                    if (!result.isEmpty()) {
                        when (AppClient.action) {
                            1 -> verifyPassWordSuccess()
                            3 -> logoutUser()
                        }
                    } else {
                        dismissLoad()
                        "验证失败，请检查用户名和密码".showToast()
                    }

                }.onFailure { error ->
                    dismissLoad()
                    error.judgeType().showToast()
                }
            }
        }

    }

    private suspend fun logoutUser() {
        userAction.logoutUser(loginBean).onStart {
            showLoad()
        }.flatMapConcat {
            if (it.isSuccess && it.getOrNull()?.isSuccess() == true) {
                userAction.exitLogin()
            } else {
                false.emitFlow()
            }
        }.collect {
            dismissLoad()
            ("注销" + if (it) {
                finish()
                //刷新我的界面-登出操作
                EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_LOGOUT, null))
                "成功"
            } else "失败").showToast()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.forgot_modify_username -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AppClient.forgotPassword)
                )
            )
            R.id.verify_password -> {
                if (action == 2) {
                    modifyUserName()
                } else {
                    noModifyUserName()
                }
            }
        }
    }

    override fun ActivityModifyUserNameBinding.initBinding() {
        setTitleText(
            title = when (action) {
                1 -> "验证密码"
                2 -> "修改用户名"
                3 -> "注销账号"
                else -> ""
            }
        )

        GlobalMemory.userInfo.let {
            loginBean?.username = it.username
            loginResponse = it
            binding.nowUserName = "当前用户名为${it.username}"
        }
    }
}