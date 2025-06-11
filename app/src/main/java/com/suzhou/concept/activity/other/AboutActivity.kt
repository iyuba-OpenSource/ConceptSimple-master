package com.suzhou.concept.activity.other

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.AppClient
import com.suzhou.concept.BuildConfig
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.activity.user.ModifyUserNameActivity
import com.suzhou.concept.adapter.AboutAdapter
import com.suzhou.concept.bean.CustomerResponse
import com.suzhou.concept.bean.CustomerServiceItem
import com.suzhou.concept.databinding.ActivityAboutBinding
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.requestPermission
import com.suzhou.concept.utils.showDialog
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startQQ
import com.tencent.vasdolly.helper.ChannelReaderUtil
import com.youdao.sdk.common.OAIDHelper
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import personal.iyuba.personalhomelibrary.utils.ToastFactory

class AboutActivity : BaseActivity<ActivityAboutBinding>() ,Consumer<Int>{
    //kotlin版回调地狱？？？

    private val list= mutableListOf<String>()
    private val phone="4008881905"
    private var item= CustomerServiceItem()
    private val requestPhone=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            startPhone()
        }else{
            "申请权限被拒绝".showToast()
        }
    }
    override fun ActivityAboutBinding.initBinding() {
        setTitleText("关于")
        lifecycleScope.launch{
            conceptViewModel.requestCustomerService()
                .onStart {
                    showLoad()
                }.collect{
                    dismissLoad()
                    it.onSuccess {result->
                        addData(result)
                    }.onFailure {e->
                        e.judgeType().showToast()
                    }
            }
        }

        showChannel()
    }
    private fun addData(response: CustomerResponse){
        if (response.data.isNotEmpty()){
            item= response.data[0]
            list.apply {
                add("编辑QQ:${item.editor}")
                add("技术QQ:${item.technician}")
                add("投诉QQ:${item.manager}")
                add("客服电话：$phone")
                add("送考试用书")
                add("送英文名著")
                if (GlobalMemory.isLogin())add(resources.getString(R.string.logout))
            }
            binding.aboutList.adapter= with(AboutAdapter(list)){
                itemListener=this@AboutActivity
                this
            }
        }
    }

    override fun accept(t: Int?) {
        t?.let {
            when (it) {
                0 -> startQQ(item.editor)
                1 -> startQQ(item.technician)
                2 -> startQQ(item.manager)
                3 -> {
                    val permission= Manifest.permission.READ_CONTACTS
                    requestPermission(permission,{
                        startPhone()
                    },{
                        requestPhone.launch(permission)
                    })
                }
                4->{
                    SendBookActivity.sendType=1
                    startActivity(Intent(this, SendBookActivity::class.java))
                }
                5->{
                    SendBookActivity.sendType=0
                    startActivity(Intent(this, SendBookActivity::class.java))
                }
                list.size - 1 -> {
                    val message = "账号一旦注销,该账号下的信息将不再保留,且不能再继续使用该账号进行登录学习,确定注销?"
                    message.showDialog(this) {
                        AppClient.action = 3
                        startActivity(Intent(this, ModifyUserNameActivity::class.java))
                    }
                }
            }
        }
    }
    private fun  startPhone(){
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    }


    //测试：显示渠道号
    private var channelCount = 0
    private fun showChannel(){
        var tvTitle = binding.toolbar.root.findViewById<TextView>(R.id.standard_title)
        tvTitle.setOnClickListener{
            if (BuildConfig.DEBUG) {
                ToastUtil.showToast(this, OAIDHelper.getInstance().oaid)
            }

            channelCount++

            if (channelCount>=6){
                var channel = ChannelReaderUtil.getChannel(this@AboutActivity)
                ToastFactory.showShort(this@AboutActivity, "渠道--$channel")
            }
        }
    }
}