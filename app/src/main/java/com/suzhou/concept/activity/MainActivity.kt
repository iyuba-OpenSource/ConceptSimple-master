package com.suzhou.concept.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.iyuba.dlex.bizs.DLManager
import com.iyuba.headlinelibrary.HeadlineType
import com.iyuba.headlinelibrary.IHeadline
import com.iyuba.headlinelibrary.data.local.HeadlineInfoHelper
import com.iyuba.headlinelibrary.data.local.db.HLDBManager
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew
import com.iyuba.headlinelibrary.ui.content.TextContentActivity
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity
import com.iyuba.imooclib.IMooc
import com.iyuba.imooclib.ImoocManager
import com.iyuba.imooclib.data.local.IMoocDBManager
import com.iyuba.imooclib.event.ImoocBuyIyubiEvent
import com.iyuba.imooclib.event.ImoocBuyVIPEvent
import com.iyuba.imooclib.event.ImoocPayCourseEvent
import com.iyuba.imooclib.ui.mobclass.MobClassFragment
import com.iyuba.module.dl.BasicDLDBManager
import com.iyuba.module.dl.DLItemEvent
import com.iyuba.module.favor.BasicFavorManager
import com.iyuba.module.favor.data.local.BasicFavorDBManager
import com.iyuba.module.favor.data.local.BasicFavorInfoHelper
import com.iyuba.module.favor.data.model.BasicFavorPart
import com.iyuba.module.privacy.PrivacyInfoHelper
import com.iyuba.module.user.IyuUserManager
import com.iyuba.mse.BuildConfig
import com.iyuba.share.ShareExecutor
import com.iyuba.share.mob.MobShareExecutor
import com.iyuba.widget.unipicker.IUniversityPicker
import com.mob.secverify.PreVerifyCallback
import com.mob.secverify.SecVerify
import com.mob.secverify.common.exception.VerifyException
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.dollar.BuyCurrencyActivity
import com.suzhou.concept.activity.dollar.MemberCentreActivity
import com.suzhou.concept.activity.user.LoginActivity
import com.suzhou.concept.adapter.PagerAdapter
import com.suzhou.concept.databinding.ActivityMainBinding
import com.suzhou.concept.fragment.main.ArticleFragment
import com.suzhou.concept.fragment.main.SettingFragment
import com.suzhou.concept.lil.event.PageJumpEvent
import com.suzhou.concept.lil.manager.AbilityControlManager
import com.suzhou.concept.lil.manager.TempDataManager
import com.suzhou.concept.lil.service.ListenPlayManager
import com.suzhou.concept.lil.service.ListenPlayService
import com.suzhou.concept.lil.service.data.ListenPlayEvent
import com.suzhou.concept.lil.ui.ad.util.show.AdShowUtil
import com.suzhou.concept.lil.ui.my.payNew.PayNewActivity
import com.suzhou.concept.lil.ui.wordPass.WordPassFragment
import com.suzhou.concept.receiver.NetChangeReceiver
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.GlobalMemory.userInfo
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.changeHeadline
import com.suzhou.concept.utils.logic.GlobalPlayManager
import com.suzhou.concept.utils.startActivity
import data.AdTestKeyData
import data.App
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.PersonalHome
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent


/**
 * 主界面
 */
@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : BaseActivity<ActivityMainBinding>(){
    private lateinit var receiver: NetChangeReceiver
    //底部菜单
    private var bottomList = mutableListOf<Pair<Int,String>>()
    //界面样式
    private var fragmentList = mutableListOf<Fragment>()
    //底部的适配器
    private lateinit var bottomAdapter:MainBottomAdapter

    override fun ActivityMainBinding.initBinding() {
        //设置底部数据和界面数据

        //主页
        bottomList.add(Pair(R.drawable.home,"首页"))
        fragmentList.add(ArticleFragment())
        //单词
        if (!AbilityControlManager.getInstance().isLimitWord){
            bottomList.add(Pair(R.drawable.break_through,"单词"))
            fragmentList.add(WordPassFragment.getInstance())
        }
        //微课
        if (!AbilityControlManager.getInstance().isLimitMoc){
            bottomList.add(Pair(R.drawable.micro_lesson,"微课"))
            fragmentList.add(inflateMicroLesson())
        }
        //视频
        if (!AbilityControlManager.getInstance().isLimitVideo){
            bottomList.add(Pair(R.drawable.small_video,"视频"))
            fragmentList.add(getWatchVideo())
        }
        //我的
        bottomList.add(Pair(R.drawable.setting,"我的"))
        fragmentList.add(SettingFragment())

        IMoocDBManager.init(this@MainActivity)
        BasicFavorInfoHelper.init(this@MainActivity)
        initHeadline()
        initSecVerify()
        initPersonalHome()
        GlobalPlayManager.preparePlayer(this@MainActivity)


        /*lifecycleScope.launch {
            conceptViewModel.lastGoWord.collect {
                val index = fragmentList.indexOfFirst { item -> item is BreakThroughFragment }
                if (index.findIndexSuccess()) {
//                    navigationBar.selectedItemId = bottomList[index]
                    bottomAdapter.setSelectPosition(index)
                }
            }
        }*/

        //开启后台服务
        startListenBgService()
    }

    private fun initPersonalHome() {
//        IUniversityPicker.init(this)
        personal.iyuba.personalhomelibrary.data.local.HLDBManager.init(this@MainActivity)
        val appId = AppClient.appId.toString()
        val appName = getString(R.string.app_name)
        PersonalHome.init(this, appId, appName)
        PersonalHome.setIsCompress(true)
        PersonalHome.setAppInfo(appId, appName)
        PersonalHome.setCategoryType(AppClient.appName)
        PersonalHome.setMainPath(javaClass.name)

        //个人中心的分享
        PersonalHome.setEnableShare(App.isOpenShare)
        //开启昵称修改
        PersonalHome.setEnableEditNickname(true)
    }

    override fun initEventBus(): Boolean = true

    //秒验初始化
    private fun initSecVerify() {
        SecVerify.preVerify(object : PreVerifyCallback() {
            override fun onComplete(p0: Void?) {
                TempDataManager.getInstance().mobVerify = true
            }

            override fun onFailure(p0: VerifyException?) {
                TempDataManager.getInstance().mobVerify = false
            }
        })
    }


    override fun initView() {
        /*val menu = if (GlobalMemory.huaweiChannelFlag){
            if (AbilityControlManager.getInstance().isLimitVideo){
                R.menu.frame_menu_limitv
            }else{
                R.menu.frame_menu
            }
        }else{
            if (AbilityControlManager.getInstance().isLimitVideo){
                R.menu.frame_xiaomi_menu_limitv
            }else{
                R.menu.frame_xiaomi_menu
            }
        }
        binding.navigationBar.setOnItemSelectedListener(this)
        binding.navigationBar.inflateMenu(menu)*/

        //设置viewPager2
        binding.navigationPager.apply {
            currentItem = 0
            isUserInputEnabled = false
            offscreenPageLimit = fragmentList.size
            adapter = PagerAdapter(this@MainActivity, fragmentList)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    binding.navigationBar.selectedItemId = bottomList[position]
                    bottomAdapter.setSelectPosition(position)
                }
            })
        }

        //设置适配器
        bottomAdapter = MainBottomAdapter(this,bottomList)
        binding.recyclerView.layoutManager = GridLayoutManager(this,bottomList.size)
        binding.recyclerView.adapter = bottomAdapter
        bottomAdapter.setOnItemClickListener {
            //取消滑动动画
            binding.navigationPager.setCurrentItem(it, false)
            //设置播放切换
            if (it != 0) {
                EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))
            }
        }

        if (bottomList.size>1){
            binding.recyclerView.visibility == View.VISIBLE
        }else{
            binding.recyclerView.visibility == View.INVISIBLE
        }

        receiver = NetChangeReceiver()
        val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(receiver, filter)
    }

    private fun initHeadline() {
        HLDBManager.init(this)
//        DBManager.init(this)
        IUniversityPicker.init(this)

        PrivacyInfoHelper.init(this)
        DLManager.init(this, 5)
        BasicFavorDBManager.init(this)
        BasicDLDBManager.init(this)
        BasicFavorManager.appId = AppClient.appId.toString()
        ShareExecutor.getInstance().realExecutor = MobShareExecutor()

        /******************微课模块******************/
        IMooc.init(this,AppClient.appId.toString(), AppClient.appName)
        IMooc.setYoudaoId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao)
        ImoocManager.youdaoId = AdTestKeyData.KeyData.TemplateAdKey.template_youdao

        /*******************视频模块*****************/
        IHeadline.setDebug(BuildConfig.DEBUG)
        IHeadline.init(this, AppClient.appId.toString(), AppClient.appName, true)
        //设置分享功能开启
        IHeadline.setEnableShare(true)
        //设置口语圈开启
        IHeadline.setEnableGoStore(true)
        HeadlineInfoHelper.init(this)
        //增加视频模块的配音功能
        IHeadline.setEnableSmallVideoTalk(true)
        //设置广告显示
        IHeadline.setEnableAd(!AdShowUtil.Util.isADBlock() && !userInfo.isVip())
        //设置信息流广告内容
        IHeadline.setAdAppId(AdShowUtil.NetParam.AppId.toString())
        IHeadline.setYoudaoStreamId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao)
        IHeadline.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,"")
        //设置banner广告内容
        IHeadline.setYoudaoBannerId(AdTestKeyData.KeyData.BannerAdKey.banner_youdao)

        //个人中心和微课模块的登录信息
        if (!GlobalMemory.isLogin()) {
            IyuUserManager.getInstance().logout()
        }
    }

    //微课模块
    private fun inflateMicroLesson(): MobClassFragment {
        val newConceptOwnerId = 21
        val list = with(ArrayList<Int>()) {
            add(-2) //全部课程
            add(-1) //最新课程
            add(2) //BBE英语
            add(3) //英语四级
            add(4) //VOA英语
            add(7) //英语六级
            add(8) //托福
            add(9) //考研英语一
            add(newConceptOwnerId) //新概念英语
            add(22) //走遍美国
            add(28) //学位英语
            add(52) //考研英语二
            add(91) //中职英语
            this
        }
        val args = MobClassFragment.buildArguments(newConceptOwnerId, false, list)
        return MobClassFragment.newInstance(args)
    }

    //视频模块
    private fun getWatchVideo(): DropdownTitleFragmentNew {
        IHeadline.resetMseUrl()
        val extraMergeUrl = "http://${OtherUtils.i_user_speech}test/merge/"
        val extraUrl = "http://${OtherUtils.i_user_speech}test/ai/"
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl)
        IHeadline.setExtraMseUrl(extraUrl)

        val types = arrayOf(
            HeadlineType.SMALLVIDEO,
            HeadlineType.HEADLINE,
            HeadlineType.VOAVIDEO,
            HeadlineType.MEIYU,
            HeadlineType.TED
        )
        val bundle = DropdownTitleFragmentNew.buildArguments(10, types, false)
        return DropdownTitleFragmentNew.newInstance(bundle)
    }

    /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val index = bottomList.indexOfFirst { item.itemId == it }
        //取消滑动动画
        binding.navigationPager.setCurrentItem(index, false)
        //设置播放切换
        if (index != 0) {
            EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))
        }
        return true
    }*/

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
        EventBus.getDefault().unregister(this@MainActivity)

        //停止后台服务
        stopListenBgService()
    }

    /**
     * 视频下载后点击
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DLItemEvent) {
        val item = event.items[event.position]
        when (item.type) {
            HeadlineType.VOA,
            HeadlineType.CSVOA,
            HeadlineType.BBC,
            HeadlineType.SONG -> {
                startActivity(
                    AudioContentActivity.buildIntent(
                        this,
                        item.categoryName,
                        item.title,
                        item.titleCn,
                        item.pic,
                        item.type,
                        item.id
                    )
                )
            }
            HeadlineType.VOAVIDEO,
            HeadlineType.MEIYU,
            HeadlineType.TED,
            HeadlineType.BBCWORDVIDEO,
            HeadlineType.TOPVIDEOS,
            HeadlineType.JAPANVIDEOS -> {
                startActivity(
                    VideoContentActivity.buildIntent(
                        this,
                        item.categoryName,
                        item.title,
                        item.titleCn,
                        item.pic,
                        item.type,
                        item.id
                    )
                )
            }
            HeadlineType.HEADLINE -> {
                startActivity(VideoContentActivity.buildIntent(this, item.changeHeadline()))
            }
        }
    }

    /**
     * 获取视频模块“现在升级的点击”
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(headlineGoVIPEvent: HeadlineGoVIPEvent?) {
        val intent = Intent(this, MemberCentreActivity::class.java)
        startActivity(intent)
    }

    /***************************回调操作*************************/
    /***********微课*************/
    //微课跳转开通黄金会员
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: ImoocBuyVIPEvent) {
        //判断是否登录
        if (GlobalMemory.isLogin()) {
            startActivity<MemberCentreActivity> {
                putExtra(ExtraKeysFactory.buyGoldVip, true)
            }
        } else {
            startActivity<LoginActivity> { }
        }
    }

    //微课跳转爱语币购买
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ImoocBuyIyubiEvent) {
        if (GlobalMemory.isLogin()) {
            startActivity<BuyCurrencyActivity> {}
        } else {
            startActivity<LoginActivity> { }
        }
    }

    //微课跳转直购
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ImoocPayCourseEvent) {
        if (GlobalMemory.isLogin()) {
            //这里需要仿着已经存在的数据处理
            val payType = "微课课程"
            val subject = event.body.toString()
            val amount = event.courseId.toString()
            val productId = event.productId.toString()
            val price = event.price.toString()

            PayNewActivity.start(
                this,
                PayNewActivity.PayType_moc,
                amount,
                price,
                subject,
                productId
            )
        } else {
            startActivity<LoginActivity> { }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: ArtDataSkipEvent) {
        //新版个人中心点击列表项
        val fPart = BasicFavorPart()
        if ("news" == event.type) {
            fPart.setTitle(event.headline.Title)
            fPart.setTitleCn(event.headline.TitleCn)
            fPart.setPic(event.headline.pic)
            fPart.setType(event.type)
            fPart.setId(event.headline.id + "")
            goFavorItem(fPart)
        }
        /*else if (AppClient.appName.equals(event.type)) {
            var topicId = event.exam.topicId.toInt()
            if (topicId > 10000) {
                topicId = topicId / 10
            }

            //本地跳转-不好弄，暂时不弄了
        }*/
        else {
            fPart.setTitle(event.voa.title)
            fPart.setTitleCn(event.voa.title_cn)
            fPart.setPic(event.voa.pic)
            fPart.setType(event.type)
            fPart.setId(event.voa.voaid.toString() + "")
            fPart.setSound(event.voa.sound)
            goFavorItem(fPart)
        }
    }

    private fun goFavorItem(part: BasicFavorPart) {
        when (part.getType()) {
            "news" -> startActivity(
                TextContentActivity.getIntent2Me(
                    this,
                    part.getId(),
                    part.getTitle(),
                    part.getTitleCn(),
                    part.getType(),
                    part.getCategoryName(),
                    part.getCreateTime(),
                    part.getPic(),
                    part.getSource()
                )
            )
            "voa", "csvoa", "bbc" -> startActivity(
                AudioContentActivityNew.getIntent2Me(
                    this,
                    part.getCategoryName(), part.getTitle(), part.getTitleCn(),
                    part.getPic(), part.getType(), part.getId(), part.getSound()
                )
            )
            "song" -> startActivity(
                AudioContentActivity.getIntent2Me(
                    this,
                    part.getCategoryName(), part.getTitle(), part.getTitleCn(),
                    part.getPic(), part.getType(), part.getId(), part.getSound()
                )
            )
            "voavideo", "meiyu", "ted", "bbcwordvideo", "topvideos", "japanvideos" -> startActivity(
                VideoContentActivityNew.getIntent2Me(
                    this,
                    part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(),
                    part.getType(), part.getId(), part.getSound()
                )
            )
            /*"series" -> {
                val intent: Intent =
                    SeriesActivity.buildIntent(theme, part.getSeriesId(), part.getId())
                startActivity(intent)
            }*/
            HeadlineType.SMALLVIDEO -> {
                val code = 1
                val pageCount = 1
                val dataPage = 0
                val forOne = VideoMiniContentActivity.buildIntentForOne(
                    this,
                    part.getId(),
                    dataPage,
                    pageCount,
                    code
                )
                startActivity(forOne)
            }
        }
    }

    /****************界面跳转******************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event:PageJumpEvent){
        if (event.toPage.equals(PageJumpEvent.page_wordPass)){
            //跳转到单词通关界面
            binding.navigationPager.currentItem = 1
        }
    }

    /****************后台播放相关*****************/
    //开启后台服务
    private fun startListenBgService() {
        var listenBgService = Intent()
        listenBgService.setClass(this, ListenPlayService::class.java)
        //这里使用绑定服务
        bindService(listenBgService, ListenPlayManager.getInstance().conn, Context.BIND_AUTO_CREATE)
    }

    //停止后台服务
    private fun stopListenBgService() {
        unbindService(ListenPlayManager.getInstance().conn)
    }
}