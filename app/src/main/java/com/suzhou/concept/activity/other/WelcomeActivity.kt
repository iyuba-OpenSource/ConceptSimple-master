package com.suzhou.concept.activity.other

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.iyuba.module.privacy.PrivacyInfoHelper
import com.mob.MobSDK
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.activity.MainActivity
import com.suzhou.concept.databinding.ActivityWelcomeBinding
import com.suzhou.concept.dialog.PrivacyAgreeDialog
import com.suzhou.concept.lil.data.remote.RetrofitUtil
import com.suzhou.concept.lil.data.remote.bean.App_check
import com.suzhou.concept.lil.event.UserinfoRefreshEvent
import com.suzhou.concept.lil.manager.AbilityControlManager
import com.suzhou.concept.lil.ui.ad.util.AdLogUtil
import com.suzhou.concept.lil.ui.ad.util.show.AdShowUtil
import com.suzhou.concept.lil.ui.ad.util.show.spread.AdSpreadShowManager
import com.suzhou.concept.lil.ui.ad.util.show.spread.AdSpreadViewBean
import com.suzhou.concept.lil.ui.ad.util.upload.AdUploadManager
import com.suzhou.concept.lil.util.LibRxUtil
import com.suzhou.concept.lil.util.OAIDNewHelper
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.OnAgreePrivacyListener
import com.suzhou.concept.utils.getPolicyUrl
import com.suzhou.concept.utils.getProtocolUrl
import com.suzhou.concept.utils.startActivity
import com.suzhou.concept.utils.startWeb
import com.tencent.vasdolly.helper.ChannelReaderUtil
import com.umeng.commonsdk.UMConfigure
import com.yd.saas.ydsdk.manager.YdConfig
import com.youdao.sdk.common.OAIDHelper
import com.youdao.sdk.common.YouDaoAd
import com.youdao.sdk.common.YoudaoSDK
import data.ConfigData
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService

/**
 * 开屏界面
 */
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(), OnAgreePrivacyListener {
    private lateinit var fiveJob: Job
    private var agreeFlag = false

    override fun ActivityWelcomeBinding.initBinding() {
        userAction.initSomeThing()

        lifecycleScope.launch {
            userAction.initResult.collect {
                if (it.first) {
                    agreeFlag = true
                    PrivacyAgreeDialog(this@WelcomeActivity, this@WelcomeActivity)
                } else {
                    initYouDao()
                }

                if (!it.first) {

                    //加载广告
                    if (!AdShowUtil.Util.isADBlock()){
                        showSpreadAd()
                    }else{
                        binding.adSkip.visibility = View.VISIBLE
                        binding.adSkip.setOnClickListener {
                            closeTimer()
                            startMain()
                        }
                        startTimer()
                    }
                } else if (!it.second) {
                    startActivity<MainActivity> {
                        finish()
                    }
                }
            }
        }

        /****************************审核处理*************************/
        //增加视频的审核处理
        if(ConfigData.videoVerifyCheck){
            verifyVideo()
        }else{
            AbilityControlManager.getInstance().isLimitVideo = false
        }

        //增加单词的审核处理
        if (ConfigData.wordVerifyCheck){
            verifyWord()
        }else{
            AbilityControlManager.getInstance().isLimitWord = false
        }

        //增加微课的审核处理
        if (ConfigData.mocVerifyCheck){
            verifyMoc()
        }else{
            AbilityControlManager.getInstance().isLimitMoc = false
        }
    }

    private fun initYouDao() {
        try {
            //先初始化oaid
            OAIDHelper.getInstance().init(this)
            //oaid升级版本
            val oaidNewHelper = OAIDNewHelper(object : OAIDNewHelper.AppIdsUpdater {
                override fun onIdData(isSupported: Boolean, isLimited: Boolean, oaid: String?, vaid: String?, aaid: String?) {
                    if (isSupported && !isLimited) {
                        OAIDHelper.getInstance().oaid = oaid
                    }
                }
            }, "msaoaidsec", ConfigData.oaid_pem)
            oaidNewHelper.getDeviceIds(this, true, false, false)
        }catch (e:Exception){

        }

        //关闭有道的获取应用列表的操作
        YouDaoAd.getYouDaoOptions().isDebugMode = false
        YouDaoAd.getNativeDownloadOptions().isConfirmDialogEnabled = true
        YouDaoAd.getYouDaoOptions().isAppListEnabled = false
        YouDaoAd.getYouDaoOptions().isPositionEnabled = false
        YouDaoAd.getYouDaoOptions().isSdkDownloadApkEnabled = true
        YouDaoAd.getYouDaoOptions().isDeviceParamsEnabled = false
        YouDaoAd.getYouDaoOptions().isWifiEnabled = false
        YouDaoAd.getYouDaoOptions().isCanObtainAndroidId = false
        YoudaoSDK.init(this)
        //初始化爱语吧广告sdk
        YdConfig.getInstance().init(AppClient.context, AppClient.appId.toString())
        //设置广告限制
        AdShowUtil.Util.setADBlock()
        //初始化数据库链接(用于测试，可删除)
        SQLiteStudioService.instance().start(this)
    }

    override fun seekPrivacy() {
        startWeb(getPolicyUrl())
    }

    override fun seekProtocol() {
        startWeb(getProtocolUrl())
    }

    override fun agree() {
        // 初始化MobSDK
        MobSDK.submitPolicyGrantResult(true)
        // 初始化友盟SDK (需用户同意隐私政策后调用)
        var channel = ChannelReaderUtil.getChannel(this);
        UMConfigure.submitPolicyGrantResult(this,true)
        UMConfigure.init(this, "5d831637570df34afd00091d", channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        UMConfigure.setLogEnabled(true)

        lifecycleScope.launch {
            userAction.saveIsFirstLogin(false).first()
        }
        PrivacyInfoHelper.init(this)
        PrivacyInfoHelper.getInstance().putApproved(true)

        initYouDao()
        startMain()
    }

    override fun noAgree() {
        finish()
    }

    //初始化部分操作
    private fun initSdk(){
        // 初始化MobSDK
        MobSDK.submitPolicyGrantResult(true)
        MobSDK.init(this)
        // 初始化友盟SDK (需用户同意隐私政策后调用)
        var channel = ChannelReaderUtil.getChannel(this);
        UMConfigure.submitPolicyGrantResult(this,true)
        UMConfigure.init(this, "5d831637570df34afd00091d", channel, UMConfigure.DEVICE_TYPE_PHONE, "")
        UMConfigure.setLogEnabled(true)

        lifecycleScope.launch {
            userAction.saveIsFirstLogin(false).first()
        }
        PrivacyInfoHelper.init(this)
        PrivacyInfoHelper.getInstance().putApproved(true)
    }

    override fun onResume() {
        super.onResume()

        //跳转到主界面
        if (isClickAd){
            isClickAd = false
            startMain()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //关闭计时器
        closeTimer()
        //关闭广告
        AdSpreadShowManager.getInstance().stopSpreadAd()
    }

    /*********************视频审核处理***********************/
    private lateinit var videoDis:Disposable

    private fun  verifyVideo(){
        val channel = ChannelReaderUtil.getChannel(this)
        val verifyId = ConfigData.getVideoLimitChannelId(channel)
        RetrofitUtil.getInstance().verify(verifyId)
            .subscribe(object:Observer<App_check>{
                override fun onSubscribe(d: Disposable) {
                    videoDis = d
                }

                override fun onNext(response: App_check) {
                    AbilityControlManager.getInstance().isLimitVideo = response.result.equals("1")
                }

                override fun onError(e: Throwable) {
                    AbilityControlManager.getInstance().isLimitVideo = false
                }

                override fun onComplete() {
                    LibRxUtil.unDisposable(videoDis)
                }
            })
    }

    /*********************微课审核处理***********************/
    private lateinit var mocDis:Disposable

    private fun  verifyMoc(){
        val channel = ChannelReaderUtil.getChannel(this)
        val verifyId = ConfigData.getMocLimitChannelId(channel)

        RetrofitUtil.getInstance().verify(verifyId)
            .subscribe(object:Observer<App_check>{
                override fun onSubscribe(d: Disposable) {
                    mocDis = d
                }

                override fun onNext(response: App_check) {
                    AbilityControlManager.getInstance().isLimitMoc = response.result.equals("1")
                }

                override fun onError(e: Throwable) {
                    AbilityControlManager.getInstance().isLimitMoc = false
                }

                override fun onComplete() {
                    LibRxUtil.unDisposable(mocDis)
                }
            })
    }

    /*********************单词审核处理***********************/
    private lateinit var wordDis:Disposable

    private fun  verifyWord(){
        val channel = ChannelReaderUtil.getChannel(this)
        val verifyId = ConfigData.getWordLimitChannelId(channel)

        RetrofitUtil.getInstance().verify(verifyId)
            .subscribe(object:Observer<App_check>{
                override fun onSubscribe(d: Disposable) {
                    wordDis = d
                }

                override fun onNext(response: App_check) {
                    AbilityControlManager.getInstance().isLimitWord = response.result.equals("1")
                }

                override fun onError(e: Throwable) {
                    AbilityControlManager.getInstance().isLimitWord = false
                }

                override fun onComplete() {
                    LibRxUtil.unDisposable(wordDis)
                }
            })
    }

    /*********************广告模式数据************************/
    //开启倒计时
    private fun startTimer() {
        if (!::fiveJob.isInitialized){
            fiveJob=countDownFive()
        }
    }

    //关闭倒计时
    private fun closeTimer(){
        if (::fiveJob.isInitialized) {
            fiveJob.cancel()
        }
    }

    //跳转到主界面
    private fun startMain(){
        closeTimer()
        startActivity<MainActivity>{
            finish()
        }
    }

    //5秒倒计时
    private fun countDownFive() = lifecycleScope.launch {
        flow {
            val range = 5 downTo 1
            for (i in range) {
                emit(i)
                delay(1000)
            }
        }.onCompletion {
            if (!agreeFlag) {
                startMain()
            }
        }.collect {
            binding.adSkip.text = with(StringBuilder()) {
                Log.d("广告显示", "开屏--广告倒计时--$it")

                append(getString(R.string.skip))
                append("(")
                append(it)
                append("S")
                append(")")
                toString()
            }
        }
    }

    /*****************************新的开屏广告操作****************************/
    //开屏广告接口是否完成
    private var isSplashAdLoaded = false

    //是否已经点击了广告
    private var isClickAd = false

    //是否已经获取了奖励
    private var isGetRewardByClickAd = false
    //广告倒计时时间
    private val AdDownTime: Int = 5
    //操作倒计时时间
    private val OperateTime: Int = 5

    //界面数据
    private var spreadViewBean: AdSpreadViewBean? = null

    //展示广告
    private fun showSpreadAd() {
        if (spreadViewBean == null) {
            spreadViewBean =
                AdSpreadViewBean(
                    binding.adImage,
                    binding.adSkip,
                    binding.adTips,
                    binding.adLayout,
                    object :
                        AdSpreadShowManager.OnAdSpreadShowListener {
                        override fun onLoadFinishAd() {
                            isSplashAdLoaded = true
                            AdSpreadShowManager.getInstance()
                                .stopOperateTimer()
                        }

                        override fun onAdShow(adType: String?) {
                        }

                        override fun onAdClick(
                            adType: String,
                            isJumpByUserClick: Boolean,
                            jumpUrl: String?
                        ) {
                            if (isJumpByUserClick) {
                                if (TextUtils.isEmpty(jumpUrl)) {
                                    ToastUtil.showToast(this@WelcomeActivity, "暂无内容")
                                    return
                                }

                                //设置点击
                                isClickAd = true
                                //关闭计时器
                                AdSpreadShowManager.getInstance()
                                    .stopAdTimer()
                                //跳转界面
                                val intent = Intent()
                                intent.setClass(
                                    this@WelcomeActivity,
                                    UseInstructionsActivity::class.java
                                )
                                intent.putExtra(ExtraKeysFactory.webUrlOut, jumpUrl)
                                startActivity(intent)
                            }

                            //点击广告获取奖励
                            if (!isGetRewardByClickAd) {
                                isGetRewardByClickAd = true


                                val fixShowType: String =
                                    AdShowUtil.NetParam.AdShowPosition.show_spread
                                val fixAdType = adType
                                AdUploadManager.getInstance()
                                    .clickAdForReward(
                                        fixShowType,
                                        fixAdType,
                                        object :
                                            AdUploadManager.OnAdClickCallBackListener {
                                            override fun showClickAdResult(
                                                isSuccess: Boolean,
                                                showMsg: String?
                                            ) {
                                                //显示奖励信息
                                                ToastUtil.showToast(this@WelcomeActivity, showMsg)
                                                //刷新用户信息
                                                if (isSuccess) {
                                                    EventBus.getDefault()
                                                        .post(UserinfoRefreshEvent())
                                                }
                                            }
                                        })
                            }
                        }

                        override fun onAdClose(adType: String?) {
                            //关闭广告
                            AdSpreadShowManager.getInstance()
                                .stopSpreadAd()
                            //跳出
                            startMain()
                        }

                        override fun onAdError(adType: String?) {
                        }

                        override fun onAdShowTime(isEnd: Boolean, lastTime: Int) {
                            if (isEnd) {
                                //跳转
                                startMain()
                            } else {
                                //开启广告计时器
                                binding.adSkip.setText("跳过(" + lastTime + "s)")
                            }
                        }

                        override fun onOperateTime(isEnd: Boolean, lastTime: Int) {
                            if (isEnd) {
                                //跳转到下一个
                                startMain()
                                return
                            }

                            if (isSplashAdLoaded) {
                                AdSpreadShowManager.getInstance()
                                    .stopOperateTimer()
                                return
                            }

                            AdLogUtil.showDebug(
                                AdSpreadShowManager.TAG,
                                "操作定时器时间--$lastTime"
                            )
                        }
                    },
                    AdDownTime,
                    OperateTime
                )
            AdSpreadShowManager.getInstance().setShowData(this, spreadViewBean)
        }
        AdSpreadShowManager.getInstance().showSpreadAd()
    }
}