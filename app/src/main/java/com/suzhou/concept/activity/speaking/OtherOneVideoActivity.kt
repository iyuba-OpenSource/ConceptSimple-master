package com.suzhou.concept.activity.speaking

import android.Manifest
import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.lifecycleScope
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.iyuba.module.toolbox.GsonUtils
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.bean.YoungRankItem
import com.suzhou.concept.databinding.ActivityOtherOneVideoBinding
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.util.PermissionDialogUtil
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startShareWorld
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * 口语配音后的详情界面
 */
class OtherOneVideoActivity : BaseActivity<ActivityOtherOneVideoBinding>() ,PlatformActionListener{
    private lateinit var player: ExoPlayer
    private lateinit var oks: OnekeyShare
    override fun ActivityOtherOneVideoBinding.initBinding() {
        val youngItem = intent.getStringExtra(ExtraKeysFactory.youngRankItem)
        item = GsonUtils.toObject(youngItem,YoungRankItem::class.java)

        GlobalMemory.speakingItem.name.apply {
            desc = this
            setTitleText(this)
        }

        youngItem?.let {
            with(ExoPlayer.Builder(this@OtherOneVideoActivity).build()) {
                val data = GsonUtils.toObject(youngItem,YoungRankItem::class.java)
                setMediaItem(MediaItem.fromUri(data.realVideoUrl))

                prepare()
                play()
                this
            }.also {
                player=it
                otherOnePlayer.player =it
            }
        }
        wantSpeaking.setOnClickListener (object:OnClickListener{
            override fun onClick(v: View?) {
                //显示权限的提示信息
                //增加权限弹窗显示
                val pairList: MutableList<android.util.Pair<String, android.util.Pair<String, String>>> = ArrayList()
                pairList.add(android.util.Pair(Manifest.permission.RECORD_AUDIO, android.util.Pair("麦克风权限", "录制评测时朗读的音频，用于评测打分使用")))
                pairList.add(android.util.Pair(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.util.Pair("存储权限", "保存下载和评测的音视频文件，用于评测打分使用")))

                PermissionDialogUtil.getInstance().showMsgDialog(this@OtherOneVideoActivity,pairList,object:PermissionDialogUtil.OnPermissionResultListener{
                    override fun onGranted(isSuccess: Boolean) {
                        if (isSuccess){
                            startActivity(Intent(this@OtherOneVideoActivity,DubSpeakingActivity::class.java))
                            finish()
                        }
                    }
                })
            }
        })
        agreeImg.setOnClickListener (object:OnClickListener{
            override fun onClick(v: View?) {
                young.likeOtherOneYoung(binding.item!!.id.toInt())
            }
        })
        shareOther.setOnClickListener (object:OnClickListener{
            override fun onClick(v: View?) {
                startShare()
            }
        })

        listenShare()
        lifecycleScope.launch {
            young.likeYoungResult.collect{result->
                result.onSuccess {
                    if (it.isEmpty()){
                        "您已经点过赞了".showToast()
                    }else{
                        binding.item=with(binding.item!!){
                            agreeCount=(agreeCount.toInt()+1).toString()

                            binding.agreeImg.setImageResource(R.drawable.agree_theme)
                            //刷新外面的点赞数据显示
                            EventBus.getDefault().post(RefreshEvent(RefreshEvent.KOUYU_AGREE,""))

                            this
                        }
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }

        //显示是否点赞的图标
        val agreeList = young.getSimpleYoungAgreeData(binding.item!!.id.toInt())
        if (agreeList.isNotEmpty()){
            binding.agreeImg.setImageResource(R.drawable.agree_theme)
        }else{
            binding.agreeImg.setImageResource(R.drawable.awesome)
        }

        //增加口语秀判定操作
        showTalkBtn()
    }

    private fun startShare(){
        if (!::oks.isInitialized){
            val space=" "
            val url="http://voa.${OtherUtils.iyuba_cn}/voa/talkShowShare.jsp?shuoshuoId=${binding.item!!.id}&apptype=newConceptTalk"
            val titleEnd=GlobalMemory.speakingItem.name.let {
                it.substring(it.indexOf(space)+1,it.length)
            }
            val title= with(StringBuilder()){
                append("播音员：")
                append(binding.item!!.UserName)
                append(space)
                append(titleEnd)
                toString()
            }
            val icon=GlobalMemory.speakingItem.youngChild.youngPic
            oks=startShareWorld(title,url,this,icon)
        }

        //判断微信是否存在，不存在则不显示
        val platform:Platform = ShareSDK.getPlatform(Wechat.NAME)
        platform.isClientValid {
            if (!it){
                oks.addHiddenPlatform(Wechat.NAME)
                oks.addHiddenPlatform(WechatMoments.NAME)
            }
        }

        oks.show(this)
    }
    private fun listenShare(){
        lifecycleScope.launch {
            conceptViewModel.shareResult.collect{result->
                result.onSuccess {
                    if (it.result == 200) {
                        "分享成功，增加了${it.addcredit}积分，共有${it.totalcredit}积分"
                    } else {
                        it.message
                    }.showToast()
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized){
            player.stop()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::player.isInitialized){
            player.pause()
        }
    }

    override fun onComplete(p0: Platform, p1: Int, p2: HashMap<String, Any>?) {
        val srid=when(p0.name){
            "QQ","Wechat","WechatFavorite"->7
            "QZone","WechatMoments","SinaWeibo","TencentWeibo"->19
            else ->0
        }
        conceptViewModel.shareContent(srid)
    }

    override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {

    }

    override fun onCancel(p0: Platform?, p1: Int) {

    }

    //这里根据本地数据查询出当前的数据内容，并且赋值给相关的内容中(没有的话就关闭去配音按钮)
    //当前暂时直接关闭去配音的按钮，后面再进行处理
    private fun showTalkBtn(){
        binding.wantSpeaking.visibility = View.INVISIBLE
    }
}