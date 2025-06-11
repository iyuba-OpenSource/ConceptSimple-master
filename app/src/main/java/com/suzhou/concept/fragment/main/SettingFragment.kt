package com.suzhou.concept.fragment.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.iyuba.headlinelibrary.HeadlineType
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew
import com.iyuba.headlinelibrary.ui.content.TextContentActivity
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity
import com.iyuba.imooclib.ui.record.PurchaseRecordActivity
import com.iyuba.module.favor.BasicFavor
import com.iyuba.module.favor.event.FavorItemEvent
import com.iyuba.module.favor.ui.BasicFavorActivity
import com.iyuba.module.headlinetalk.ui.mytalk.MyTalkActivity
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.MainActivity
import com.suzhou.concept.activity.dollar.BuyCurrencyActivity
import com.suzhou.concept.activity.dollar.MemberCentreActivity
import com.suzhou.concept.activity.other.AboutActivity
import com.suzhou.concept.activity.other.SignActivity
import com.suzhou.concept.activity.speaking.MineDubActivity
import com.suzhou.concept.activity.speaking.SpeakingShowActivity
import com.suzhou.concept.adapter.OperateHorizontalAdapter
import com.suzhou.concept.adapter.SettingAdapter
import com.suzhou.concept.bean.HorizontalOperateBean
import com.suzhou.concept.bean.SettingItem
import com.suzhou.concept.databinding.SettingLayoutBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.lil.data.remote.RetrofitUtil
import com.suzhou.concept.lil.data.remote.bean.User_info
import com.suzhou.concept.lil.event.LocalEvalDataRefreshEvent
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.event.UserinfoRefreshEvent
import com.suzhou.concept.lil.ui.my.payNew.PayUserInfoHelpUtil
import com.suzhou.concept.lil.ui.my.rank.RankNewActivity
import com.suzhou.concept.lil.ui.my.walletList.RewardMarkActivity
import com.suzhou.concept.lil.ui.my.wordNote.WordNoteActivity
import com.suzhou.concept.lil.util.EncodeUtil
import com.suzhou.concept.lil.util.ToastUtil
import com.suzhou.concept.lil.view.NoScrollGridLayoutManager
import com.suzhou.concept.lil.view.NoScrollLinearLayoutManager
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.addDefaultDecoration
import com.suzhou.concept.utils.changeHeadline
import com.suzhou.concept.utils.getPersonalInfoUrl
import com.suzhou.concept.utils.getPolicyUrl
import com.suzhou.concept.utils.getProtocolUrl
import com.suzhou.concept.utils.getTiredSdkInfoUrl
import com.suzhou.concept.utils.judgeLogin
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.logic.DataCleanManager
import com.suzhou.concept.utils.logic.OperateType
import com.suzhou.concept.utils.logic.SettingType
import com.suzhou.concept.utils.showDialog
import com.suzhou.concept.utils.showGoLoginDialog
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import com.suzhou.concept.utils.startWeb
import com.suzhou.concept.utils.visibilityState
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.PersonalHome
import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity
import personal.iyuba.personalhomelibrary.ui.my.MySpeechActivity
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType
import java.io.File


/**
 * 我的界面
 */
class SettingFragment : BaseFragment<SettingLayoutBinding>(), View.OnClickListener {
    private val settingAdapter by lazy { SettingAdapter() }
    private val setList = mutableListOf<SettingItem>()
    private val handler = Handler(Looper.myLooper()!!) {
        DataCleanManager.cleanCustomCache(requireActivity().cacheDir.path)
        for (i in setList.indices) {
            setList[i].value = "0.00KB"
            settingAdapter.notifyItemChanged(i)
        }
        dismissActivityLoad<MainActivity>()
        true
    }

    // TODO: 不知道为啥这里请求存储权限，暂时关闭
//    private val requestPermissionWrite=registerForActivityResult(ActivityResultContracts.RequestPermission()){
//
//    }

    override fun SettingLayoutBinding.initBinding() {
        setTitleText("我的")
        setList.apply {
            //我的配音
            add(SettingItem(SettingType.MINE_DUB, false, "", R.drawable.mine_dub))
            //购买记录
            add(SettingItem(SettingType.SHOP_MARK, false, "", R.drawable.shop_mark))
            //学习报告
            add(SettingItem(SettingType.STUDY_REPORT, false, "", R.drawable.study_report))
        }
        addData()

        head.setOnClickListener(this@SettingFragment)
        settingDollar.setOnClickListener(this@SettingFragment)
        settingCredits.setOnClickListener(this@SettingFragment)
        headLayout.setOnClickListener(this@SettingFragment)
        buyIyu.setOnClickListener(this@SettingFragment)
        signIn.apply {
            visibilityState(!GlobalMemory.isLogin())
            setOnClickListener(this@SettingFragment)
        }

        //下边的竖向排列
        settingList.apply {
            adapter = settingAdapter
            addDefaultDecoration()
            layoutManager =
                NoScrollLinearLayoutManager(
                    requireContext(),
                    false
                )
        }

        refreshUser()

        //请求qq信息
        conceptViewModel.requestQQGroup()
        lifecycleScope.launch {
            conceptViewModel.lastQQ.collect { result ->
                result.onSuccess {
                    //qq交流群
                    setList.add(0, SettingItem(SettingType.QQ_GROUP, false, it.key, R.drawable.qq))
                    settingAdapter.apply {
                        changeData(setList)
                        positionListener = itemListener
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
        // TODO: 不知道为啥这里请求存储权限，暂时关闭
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
//            //此处判断加的莫名其妙？？？
//        }else{
//            requestPermissionWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
        inflateStaggerFour()
    }

    private fun inflateStaggerFour() {
        val operateList = with(mutableListOf<HorizontalOperateBean>()) {
            //会员中心
            add(HorizontalOperateBean(R.drawable.vip, OperateType.VIP_CENTER))
            //生词本
            add(HorizontalOperateBean(R.drawable.strange, OperateType.WORD_BOOK))
            //视频收藏
            add(HorizontalOperateBean(R.drawable.small_video, OperateType.COLLECT_VIDEO))
            //视频配音
            add(HorizontalOperateBean(R.drawable.video_dubbing, OperateType.VIDEO_DUBBING))


            //消息中心
            add(HorizontalOperateBean(R.drawable.message, OperateType.MESSAGE_CENTER))//这里要求关闭消息中心
            //口语圈
            add(HorizontalOperateBean(R.drawable.speak_cycle, OperateType.SPEAK_CYCLE))
            //排行榜
            add(HorizontalOperateBean(R.drawable.rank_me, OperateType.RANK))
            //口语秀
            add(HorizontalOperateBean(R.drawable.speaking_show, OperateType.SPEAK_SHOW))

            //移到下边
//            add(HorizontalOperateBean(R.drawable.about,OperateType.CUSTOMER_SERVICE))
            this
        }
        val operateAdapter = with(OperateHorizontalAdapter()) {
            changeData(operateList)
            inflateListener(horizontalListener)
            this
        }

        //中间的横向排列
        bind.otherOperate.apply {
            adapter = operateAdapter
//            addDefaultDecoration()
//            layoutManager= StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL)
            layoutManager =
                NoScrollGridLayoutManager(
                    requireContext(),
                    4,
                    false
                )
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUser()
    }

    private val horizontalListener = Consumer<OperateType> {
        when (it) {
            //会员中心
            OperateType.VIP_CENTER -> requireActivity().startActivity<MemberCentreActivity> { }
//            OperateType.CUSTOMER_SERVICE->requireActivity().startActivity<AboutActivity> {  }

            //视频配音
            OperateType.VIDEO_DUBBING -> requireActivity().judgeLogin {
                val types = ArrayList<String>()
                types.add(HeadlineType.SMALLVIDEO)
                startActivity(MyTalkActivity.buildIntent(activity, types))
            }

            //排行榜
            OperateType.RANK -> {
                //原来的界面：RankActivity

                requireActivity().judgeLogin {
                    RankNewActivity.start(activity)
                }
            }

            //口语圈
            OperateType.SPEAK_CYCLE -> requireActivity().judgeLogin<MySpeechActivity>()

            //生词本
            OperateType.WORD_BOOK -> {
                //原来的界面：StrangeWordActivity
                requireActivity().judgeLogin<WordNoteActivity>()
            }

            //视频收藏
            OperateType.COLLECT_VIDEO -> {
                //原来的界面：CollectVideoActivity

                if (!GlobalMemory.isLogin()) {
                    requireActivity().showGoLoginDialog()
                } else {
                    val types: MutableList<String> = java.util.ArrayList()
                    types.add(HeadlineType.SMALLVIDEO)
                    types.add(HeadlineType.HEADLINE)
                    types.add(HeadlineType.VOAVIDEO)
                    types.add(HeadlineType.MEIYU)
                    types.add(HeadlineType.TED)

                    BasicFavor.setTypeFilter(types)

                    startActivity(BasicFavorActivity.buildIntent(activity))
                }
            }

            //消息中心
            OperateType.MESSAGE_CENTER -> requireActivity().judgeLogin<MessageActivity>()

            //口语秀
            OperateType.SPEAK_SHOW -> requireActivity().judgeLogin<SpeakingShowActivity>()
            else -> {}
        }
    }

    private fun refreshUser() {
        bind.signIn.visibilityState(!GlobalMemory.isLogin())
        GlobalMemory.userInfo.apply {
            bind.response = this

            if (!isSuccess()) {
                bind.exit.visibility = View.GONE
            } else {
                bind.usernameShow.setOnClickListener(this@SettingFragment)
                bind.exit.setOnClickListener(this@SettingFragment)
                bind.exit.visibility = View.VISIBLE
            }
        }
        dismissActivityLoad<MainActivity>()
    }

    private fun signSingleDay() {
        lifecycleScope.launch {
            userAction.signEveryDay().onStart {
                showActivityLoad<MainActivity>()
            }.collect {
                dismissActivityLoad<MainActivity>()
                it.onSuccess { result ->
                    if (result.result == "1") {
                        val signStudyTime = 3 * 60
                        val time = result.totalTime.toInt()
                        if (time < signStudyTime) {
                            getStudyTime(time).showToast()
                        } else {
                            requireActivity().startActivity<SignActivity> {
                                putExtra(ExtraKeysFactory.signResult, result)
                            }
                            //打卡成功后刷新本地
                        }
                    } else {
                        "打卡加载失败".showToast()
                    }
                }.onFailure { e ->
                    e.judgeType().showToast()
                }
            }
        }
    }

    private fun addData() {
        setList.apply {
            //清除缓存
            add(SettingItem(SettingType.CACHE, true, loadCache(), R.drawable.cache))
            //隐私政策
            add(SettingItem(SettingType.PRIVACY, false, "", R.drawable.privacy))
            //用户协议
            add(SettingItem(SettingType.PROTOCOL, false, "", R.drawable.protocol))
            //第三方信息共享清单
            add(SettingItem(SettingType.TiredSdkInfo,false,"",R.drawable.ic_report_tiredsdkinfo))
            //个人信息收集清单
            add(SettingItem(SettingType.PersonalInfo,false,"",R.drawable.ic_report_personalinfo))
            //关于
            add(SettingItem(SettingType.ABOUT, false, "", R.drawable.about))
        }
        settingAdapter.apply {
            changeData(setList)
            positionListener = itemListener
        }
    }

    private fun loadCache(): String {
        val file = File(requireActivity().cacheDir.path)
        return DataCleanManager.getCacheSize(file)
    }

    private fun joinQQGroup(key: String): Boolean = try {
        //此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val intent = with(Intent()) {
            val qqKey = resources.getString(R.string.qq_key)
            data = Uri.parse(qqKey + key)
            this
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        // 未安装手Q或安装的版本不支持
        false
    }


    private val itemListener = Consumer<SettingItem> {
        when (it.item) {
            //隐私政策
            SettingType.PRIVACY -> {
                //不知道为啥非要设置成需要撤回的操作，感觉没必要，直接删除这个逻辑
                /*"撤回已同意的授权".showPrivacyDialog(
                    requireContext(),
                    "隐私协议",
                    "撤回已同意的授权",
                    "去阅读",
                    {
                        "撤回后，APP将不可使用，确认撤回授权？".showDialog(
                            requireContext(),
                            "继续撤回"
                        ) {
                            userAction.saveIsFirstLogin(true)
                            requireActivity().finish()
                        }
                    },
                    {
                        requireActivity().apply {
                            requireActivity().startWeb(getProtocolUrl())
                        }
                    })*/

                requireActivity().apply {
                    requireActivity().startWeb(getProtocolUrl())
                }
            }
            //用户协议
            SettingType.PROTOCOL -> {
                requireActivity().apply {
                    requireActivity().startWeb(getPolicyUrl())
                }
            }
            //第三方信息共享清单
            SettingType.TiredSdkInfo -> {
                requireActivity().apply {
                    requireActivity().startWeb(getTiredSdkInfoUrl())
                }
            }
            //个人信息收集清单
            SettingType.PersonalInfo -> {
                requireActivity().apply {
                    requireActivity().startWeb(getPersonalInfoUrl())
                }
            }
            //qq群
            SettingType.QQ_GROUP -> {
                /*if (!joinQQGroup(it.value)) {
                    "请安装QQ或这检查QQ安装版本".showToast()
                }*/

                var intent = Intent()
                val jumpUri = Uri.parse(resources.getString(R.string.qq_key)+it.value)
                intent.setData(jumpUri)
                try {
                    startActivity(intent)
                }catch (e:Exception){
                    ToastUtil.showToast(context, "您的设备尚未安装QQ客户端")
                }
            }
            //清除缓存
            SettingType.CACHE -> {
                "是否清除全部缓存？".showDialog(requireContext()) {
                    showActivityLoad<MainActivity>()
                    handler.sendEmptyMessageAtTime(0, 500)
                }
            }
            //我的配音
            SettingType.MINE_DUB -> requireActivity().judgeLogin<MineDubActivity>()
            //官方群
            SettingType.OFFICIAL_GROUP -> requireActivity().judgeLogin {
                GroupChatManageActivity.start(
                    requireContext(),
                    GlobalMemory.groupId,
                    GlobalMemory.groupName,
                    true
                )
            }
            //关于
            SettingType.ABOUT -> requireActivity().startActivity<AboutActivity>()
            //购买记录
            SettingType.SHOP_MARK -> requireActivity().judgeLogin<PurchaseRecordActivity>()
            //学习报告
            SettingType.STUDY_REPORT -> requireActivity().judgeLogin {
                val type = "all"
                val showTypes = arrayOf(
                    SummaryType.LISTEN,
                    SummaryType.WORD,
                    SummaryType.EVALUATE,
                    SummaryType.MOOC,
                    SummaryType.READ
                )
                startActivity(SummaryActivity.getIntent(requireActivity(), type, showTypes, 0))
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(p0: View?) {
        when (p0?.id) {

            //修改昵称（暂时关闭）
//            R.id.username_show -> {
//                requireActivity().judgeLogin(userAction) {
//                    startActivity(Intent(requireContext(), ModifyUserNameActivity::class.java))
//                    AppClient.action = 1
//                }
//            }

            //退出登录
            R.id.exit -> {
                "是否退出登录？".showDialog(requireContext()) {
                    lifecycleScope.launch {
                        userAction.exitLogin().onStart {
                            showActivityLoad<MainActivity>()
                        }.collect {
                            dismissActivityLoad<MainActivity>()
                            GlobalMemory.clearUserInfo()
                            AppClient.rankResponse.clear()
                            refreshUser()

                            //刷新登出操作
                            EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_LOGOUT,null))
                            //刷新首页
                            EventBus.getDefault().post(LocalEvalDataRefreshEvent())
                        }
                    }
                }
            }

            //登录
            R.id.sign_in -> {
                signSingleDay()
            }

            //个人中心
            R.id.head_layout -> {
                //原来的方法
//                requireActivity().judgeLogin(userAction) { }

                requireActivity().judgeLogin {
                    //设置文件的输出路径
                    PersonalHome.setFileProviderAuthority(getString(R.string.authorities))
                    startActivity(
                        PersonalHomeActivity.buildIntent(
                            requireActivity(),
                            GlobalMemory.userInfo.uid,
                            GlobalMemory.userInfo.username,
                            0
                        )
                    )
                }
            }

            R.id.head -> {
                requireActivity().judgeLogin {
                    //设置文件的输出路径
                    PersonalHome.setFileProviderAuthority(getString(R.string.authorities))
                    startActivity(
                        PersonalHomeActivity.buildIntent(
                            requireActivity(),
                            GlobalMemory.userInfo.uid,
                            GlobalMemory.userInfo.username,
                            0
                        )
                    )
                }
            }

            R.id.username_show -> {
                requireActivity().judgeLogin {
                    //设置文件的输出路径
                    PersonalHome.setFileProviderAuthority(getString(R.string.authorities))
                    startActivity(
                        PersonalHomeActivity.buildIntent(
                            requireActivity(),
                            GlobalMemory.userInfo.uid,
                            GlobalMemory.userInfo.username,
                            0
                        )
                    )
                }
            }

            //购买爱语币
            R.id.buy_iyu -> {
                requireActivity().judgeLogin() {
                    requireContext().startActivity<BuyCurrencyActivity> {}
                }
            }

            //钱包
            R.id.setting_dollar -> {
                requireActivity().judgeLogin() {

                    //原来的显示
//                    val desc="当前钱包金额:${GlobalMemory.userInfo.self.realDollar},满10元可在[爱语吧]微信公众号提现(关注绑定爱语吧账号),每天坚持打卡分享,获得更多红包吧!"
//                    desc.showPositiveDialog(requireContext()){}

                    //新的跳转
                    RewardMarkActivity.start(requireActivity())
                }
            }

            //积分
            R.id.setting_credits -> {
                requireActivity().judgeLogin() {
                    val sign = EncodeUtil.md5("iyuba"+GlobalMemory.userInfo.uid+"camstory")
                    val showUrl = "http://m."+ OtherUtils.iyuba_cn+"/mall/index.jsp?&uid="+GlobalMemory.userInfo.uid+"&sign="+sign+"&username="+GlobalMemory.userInfo.username+"&platform=android&appid="+AppClient.appId;
                    requireActivity().startWeb(showUrl)
                }
            }

            //因为此功能存在bug，暂时屏蔽
            //            R.id.head -> requireActivity().judgeLogin(userAction){
            //                startActivity(Intent(requireContext(), ModifyUserHeadActivity::class.java))
            //            }
        }
    }

    private fun getStudyTime(time: Int) = with(StringBuilder("当前已学习")) {
        if (time > 60) {
            val minute = time / 60
            append(minute).append("分").append(time - minute * 60).append("秒")
        } else {
            append(time).append("秒")
        }
        append("\n满3分钟可打卡")
        toString()
    }

    /**
     * 视频收藏跳转
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: FavorItemEvent) {
        val part = event.items[event.position]
        when (part.type) {
            HeadlineType.NEWS, HeadlineType.VOA, HeadlineType.CSVOA -> {
                val intent = with(part) {
                    TextContentActivity.buildIntent(
                        requireContext(),
                        id,
                        title,
                        titleCn,
                        type,
                        categoryName,
                        createTime,
                        pic,
                        source
                    )
                }
                startActivity(intent)
            }

            HeadlineType.BBC -> startActivity(
                AudioContentActivityNew.buildIntent(
                    requireContext(),
                    part.categoryName,
                    part.title,
                    part.titleCn,
                    part.pic,
                    part.type,
                    part.id,
                    part.sound
                )
            )

            HeadlineType.SONG -> startActivity(
                AudioContentActivity.buildIntent(
                    requireContext(),
                    part.categoryName,
                    part.title,
                    part.titleCn,
                    part.pic,
                    part.type,
                    part.id,
                    part.sound
                )
            )

            HeadlineType.VOAVIDEO, HeadlineType.MEIYU, HeadlineType.TED, HeadlineType.BBCWORDVIDEO, HeadlineType.TOPVIDEOS, HeadlineType.JAPANVIDEOS -> {
                startActivity(
                    VideoContentActivityNew.buildIntent(
                        requireContext(),
                        part.categoryName,
                        part.title,
                        part.titleCn,
                        part.pic,
                        part.type,
                        part.id,
                        part.sound
                    )
                )
            }

            HeadlineType.SMALLVIDEO -> startActivity(
                VideoMiniContentActivity.buildIntentForOne(
                    requireContext(),
                    part.id,
                    0,
                    1,
                    1
                )
            )

            HeadlineType.HEADLINE -> startActivity(
                VideoContentActivity.buildIntent(
                    requireContext(),
                    part.changeHeadline()
                )
            )
        }
    }

    override fun initEventBus(): Boolean = true

    //设置其他界面刷新登出操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(event: RefreshEvent) {

        //用户登出
        if (event.type == RefreshEvent.USER_LOGOUT) {
            GlobalMemory.clearUserInfo()
            AppClient.rankResponse.clear()
            refreshUser()
        }

        //用户数据刷新
        if (event.type == RefreshEvent.USER_VIP) {
            refreshUser()
        }
    }

    //设置用户数据刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onEvent(event: UserinfoRefreshEvent) {
        //加载用户信息
        RetrofitUtil.getInstance().userInfo
            .subscribe(object : Observer<User_info?> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(userInfo: User_info) {
                    //刷新数据并推出
                    PayUserInfoHelpUtil.saveUserinfo(GlobalMemory.userInfo.uid, userInfo)
                    //刷新用户信息显示
                    EventBus.getDefault().post(RefreshEvent(RefreshEvent.USER_VIP,null))
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {}
            })
    }
}