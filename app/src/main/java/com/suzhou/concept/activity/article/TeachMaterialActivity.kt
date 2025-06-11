package com.suzhou.concept.activity.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.suzhou.concept.AppClient
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.databinding.ActivityTeachMaterialBinding
import com.suzhou.concept.lil.data.library.TypeLibrary
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.manager.StudyDataManager
import com.suzhou.concept.lil.service.data.ListenPlayEvent
import com.suzhou.concept.lil.ui.study.eval_new.EvalFixFragment
import com.suzhou.concept.lil.ui.study.exercise.ExerciseNewFragment
import com.suzhou.concept.lil.ui.study.listen.ListenNewFragment
import com.suzhou.concept.lil.ui.study.practise.PractiseShowFragment
import com.suzhou.concept.lil.ui.study.rank.RankNewFragment
import com.suzhou.concept.lil.ui.study.read.ReadFragment
import com.suzhou.concept.lil.ui.study.word.KnowledgeNewFragment
import com.suzhou.concept.lil.view.dialog.LoadingDialog
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.judgeVip
import com.suzhou.concept.utils.showPrivacyDialog
import com.suzhou.concept.utils.showToast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.utils.ToastFactory


/**
 * 学习界面
 */
@RequiresApi(Build.VERSION_CODES.N)
class TeachMaterialActivity : BaseActivity<ActivityTeachMaterialBinding>(), PlatformActionListener {
    //修改成list的形式
    /*private val titleArray by lazy {
        if (GlobalMemory.currentYoung) {
            resources.getStringArray(R.array.teach_material_array_young)
        } else {
            resources.getStringArray(R.array.teach_material_array_four)
        }
    }*/

    private lateinit var mediator: TabLayoutMediator
    private lateinit var popWindow: PopupWindow
    private lateinit var oks: OnekeyShare

    //切换中英文
    private lateinit var readLanguage: TextView

    //更新习题
    private lateinit var refreshExercise: TextView

    //单词界面
//    private lateinit var knowledgeFragment: KnowledgeFragment
    private lateinit var knowledgeFragment: KnowledgeNewFragment

    //新的原文界面
    private lateinit var listenNewFragment: ListenNewFragment

    //新的习题界面
    private lateinit var exerciseNewFragment: ExerciseNewFragment

    //是否已经跳转
    private var isJumpPage = false

    private var pdfType = -1

    //标题显示
    private lateinit var titleList:MutableList<String>
    //界面显示
    private lateinit var fragmentList: MutableList<Fragment>

    override fun ActivityTeachMaterialBinding.initBinding() {
        installStandRight { showPop(it) }
        setTitleText(AppClient.conceptItem.realTitle())

        //请求数据
        requestSentenceList()

        //回调
        lifecycleScope.launch {
            //pdf生成
            conceptViewModel.pdfResult.collect { result ->
                result.onError {
                    it.judgeType().showToast()
                }.onSuccess {
                    if (it.isEmpty()) {
                        "生成失败".showToast()
                        return@onSuccess
                    }
                    val url = it.realPath(pdfType == 1)
                    val clipBoardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newRawUri("concept_pdf", Uri.parse(url))
                    clipBoardManager.setPrimaryClip(clipData)
                    "${url}链接已复制".showPrivacyDialog(
                        this@TeachMaterialActivity,
                        "PDF链接生成成功", "下载", "取消", {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }, {})
                }
            }
        }
        lifecycleScope.launch {
            //分享
            conceptViewModel.shareResult.collect { result ->
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
        lifecycleScope.launch {
            val voaId = AppClient.conceptItem.voa_id
            if (exercise.selectByNumber(voaId).first()) {
                exercise.requestTestRecord(voaId)
            }
        }
    }

    fun requestSentenceList() {
        startLoading()

        //数据请求
        evaluation.requestSentenceList()
    }

    //填充布局样式
    fun setLayout() {
        //重置标题数据
        if (::titleList.isInitialized){
            titleList.clear()
        }else{
            titleList = mutableListOf()
        }
        //重制数据
        if (::fragmentList.isInitialized) {
            fragmentList.clear()
        } else {
            fragmentList = mutableListOf()
        }

        //获取数据进行填充
        val position = AppClient.curListIndex

        val bundle = Bundle()
        bundle.putInt(ExtraKeysFactory.position, position)
        //设置原文界面
        titleList.add("原文")
        listenNewFragment = ListenNewFragment.getInstance(position)
        fragmentList.add(listenNewFragment)
        //设置评测界面
        titleList.add("评测")
        fragmentList.add(EvalFixFragment())
        //设置排行界面
        titleList.add("排行")
        fragmentList.add(RankNewFragment.getInstance(AppClient.conceptItem.voa_id.toInt()))
        //设置知识界面
        var showType = TypeLibrary.BookType.conceptFour
        if (GlobalMemory.currentYoung){
            showType = TypeLibrary.BookType.conceptJunior
        }
        titleList.add("知识")
        knowledgeFragment = KnowledgeNewFragment.getInstance(showType,AppClient.conceptItem.bookId,AppClient.conceptItem.voa_id.toInt(),position)
        fragmentList.add(knowledgeFragment)
        //设置练习界面
        if (!GlobalMemory.currentYoung) {
            //更换为新的
            titleList.add("练习")
            exerciseNewFragment = ExerciseNewFragment.getInstance()
            fragmentList.add(exerciseNewFragment)
        }
        //设置阅读界面
        titleList.add("阅读")
        fragmentList.add(ReadFragment())
        // TODO: 设置新版本的练习题界面(判断第一册只有奇数课程存在，第二册都有习题)
        if ((AppClient.conceptItem.bookId==1&&position%2==0) || AppClient.conceptItem.bookId==2){
            titleList.add("测试")
            fragmentList.add(PractiseShowFragment.getInstance("concept",AppClient.conceptItem.voa_id.toInt(),position))
        }

        binding.materialPager.isSaveEnabled = false

        val adapter = TeachMaterialAdapter(this@TeachMaterialActivity, fragmentList)
        binding.materialPager.adapter = adapter
        binding.materialPager.offscreenPageLimit = fragmentList.size
        binding.materialPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //根据名称判断显示
                val showTitle = titleList[position]
                if (showTitle.equals("原文")) {
                    listenNewFragment.switchOtherPage(false)
                } else {
                    listenNewFragment.switchOtherPage(true)
                }
            }
        })
        binding.materialTab.tabGravity = TabLayout.GRAVITY_FILL
        if (::mediator.isInitialized) {
            mediator.detach()
        }

        binding.materialTab.tabMode = TabLayout.MODE_AUTO
        mediator = TabLayoutMediator(
            binding.materialTab,
            binding.materialPager,
            false,
            true
        ) { tab, position ->
            run {
                tab.text = titleList[position]
            }
        }
        mediator.attach()
    }

    override fun onDestroy() {
        if (::mediator.isInitialized) {
            mediator.detach()
        }

        super.onDestroy()
    }

    override fun initEventBus(): Boolean = true

    private fun outPdf() {
        judgeVip("导出PDF") {
            "请选择需要导出的PDF的形式".showPrivacyDialog(
                this,
                "提示",
                "导出英文",
                "导出中英双语",
                {
                    pdfType = 1
                    conceptViewModel.requestPdf(pdfType)
                },
                {
                    pdfType = 0
                    conceptViewModel.requestPdf(pdfType)
                })
        }
    }

    private fun showPop(group: View) {
        if (!::popWindow.isInitialized) {
            val view = View.inflate(this, R.layout.pop_item_content, null)
            //下载pdf
            val downloadPdf = view.findViewById<TextView>(R.id.download_pdf)
            //分享文章
            val shareContent = view.findViewById<TextView>(R.id.share_content)
            //切换中英文
            readLanguage = view.findViewById<TextView>(R.id.change_language)
            //更新习题
            refreshExercise = view.findViewById(R.id.refresh_exercise)
            val color = ContextCompat.getColor(this, R.color.half_black)
            popWindow = with(
                PopupWindow(
                    view,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
                )
            ) {
                isTouchable = true
                setBackgroundDrawable(ColorDrawable(color))
                this
            }
            downloadPdf.setOnClickListener {
                listenNewFragment.switchOtherPage(true)

                popWindow.dismiss()
                outPdf()
            }
            shareContent.setOnClickListener {
                listenNewFragment.switchOtherPage(true)

                popWindow.dismiss()
                startShare()
            }

            //切换中英文
            readLanguage.setOnClickListener {
                listenNewFragment.switchOtherPage(true)

                popWindow.dismiss()
                changeReadLanguage()
            }

            //更新习题
            refreshExercise.setOnClickListener {
                //这里的调用习题界面操作
                exerciseNewFragment.refreshData()
                popWindow.dismiss()
            }
        }

        //根据位置切换
        /*val curIndex = binding.materialPager.currentItem
        if (curIndex == fragmentList.size - 1) {
            readLanguage.visibility = View.VISIBLE
        } else {
            readLanguage.visibility = View.GONE
        }*/

        val showTitle = titleList[binding.materialPager.currentItem]
        if (showTitle.equals("阅读")) {
            readLanguage.visibility = View.VISIBLE
        } else {
            readLanguage.visibility = View.GONE
        }

        if (showTitle.equals("练习") || showTitle.equals("习题")) {
            refreshExercise.visibility = View.VISIBLE
        } else {
            refreshExercise.visibility = View.GONE
        }

        popWindow.showAsDropDown(group, 50, 0)
    }

    private fun startShare() {
        val shareUrl = GlobalMemory.currentLanguage.getShareUrl()
        val shareTitle =
            "[我正在新概念中读:${AppClient.conceptItem.title}  ${AppClient.conceptItem.title_cn}这篇课文,非常有意思，大家快来读吧！] "
        val imageUrl =
            "http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/074c570a80edc4f4c8433839f44a92dbe26c9b570"
        ShareSDK.removeCookieOnAuthorize(true)
        oks = with(OnekeyShare()) {
            disableSSOWhenAuthorize()
            setTitle(shareTitle)
            setTitleUrl(shareUrl)
            text = shareTitle
            setImageUrl(imageUrl)
            setUrl(shareUrl)
            setSite(resources.getString(R.string.app_name))
            setSiteUrl(shareUrl)
            callback = this@TeachMaterialActivity
            this
        }

        //先判断是否存在微信或者qq
        val platform: Platform = ShareSDK.getPlatform(Wechat.NAME)
        platform.isClientValid {
            if (!it) {
                oks.addHiddenPlatform(Wechat.NAME)
                oks.addHiddenPlatform(WechatMoments.NAME)
            }
        }

        oks.show(this)
    }

    private fun changeReadLanguage() {
        val isShowCn = StudyDataManager.getInstance().readShowCn
        StudyDataManager.getInstance().readShowCn = !isShowCn
        //刷新显示
        EventBus.getDefault().post(RefreshEvent(RefreshEvent.READ_LANGUAGE, null))
    }

    override fun onComplete(
        platform: Platform?,
        paramAnonymousInt: Int,
        paramAnonymousHashMap: HashMap<String, Any>?
    ) {
        if (!GlobalMemory.isLogin()) {
            "分享成功".showToast()
            return
        }

        val typeArray = arrayOf("Wechat")
        val srid = if (typeArray.contains(platform?.name)) 19 else 49
        conceptViewModel.shareContent(srid)
    }

    override fun onError(
        paramAnonymousPlatform: Platform?,
        paramAnonymousInt: Int,
        paramAnonymousThrowable: Throwable?
    ) {
        "分享失败".showToast()
    }

    override fun onCancel(paramAnonymousPlatform: Platform?, paramAnonymousInt: Int) {
        "分享取消".showToast()
    }


    //回调数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {

        //toast显示
        if (event.type.equals(RefreshEvent.SHOW_TOAST)) {
            if (!TextUtils.isEmpty(event.msg)) {
                ToastFactory.showShort(this@TeachMaterialActivity, event.msg)
            }

            //刷新用户信息
            userAction.refreshUserInfo()
        }

        //dialog显示
        if (event.type.equals(RefreshEvent.SHOW_DIALOG)) {
            if (!TextUtils.isEmpty(event.msg)) {
                AlertDialog.Builder(this@TeachMaterialActivity)
                    .setTitle("奖励信息")
                    .setMessage(event.msg)
                    .setPositiveButton("确定", null)
                    .show()
            }

            //刷新用户信息
            userAction.refreshUserInfo()
        }

        //数据加载完成
        if (event.type.equals(RefreshEvent.STUDY_FINISH)) {
            showLayout(event.msg)
        }
    }

    //加载弹窗
    private lateinit var loadingdialog: LoadingDialog

    private fun startLoading() {
        if (!::loadingdialog.isInitialized) {
            loadingdialog = LoadingDialog(this)
            loadingdialog.create()
        }
        loadingdialog.show()
    }

    private fun stopLoading() {
        if (::loadingdialog.isInitialized) {
            loadingdialog.dismiss()
        }
    }

    //界面显示
    private fun showLayout(showMsg: String?) {
        stopLoading()
        setLayout()

        if (!TextUtils.isEmpty(showMsg)) {
            ToastFactory.showShort(this, showMsg)
        }

        //设置跳转的位置
        if (!isJumpPage) {
            if (listenNewFragment==null){
                return
            }

            isJumpPage = true
            val jumpPage = intent.getStringExtra(ExtraKeysFactory.teachKey)
            if (jumpPage.equals(ExtraKeysFactory.exerciseFlag)) {
                listenNewFragment.switchOtherPage(true)
//                val exercisePage = fragmentList.indexOfFirst { item -> item is ExerciseFragment }
                val exercisePage = fragmentList.indexOfFirst { item -> item is ExerciseNewFragment }
                binding.materialPager.setCurrentItem(exercisePage,false)
            }

            if (jumpPage.equals(ExtraKeysFactory.evalFlag)) {
                listenNewFragment.switchOtherPage(true)
                val evalPage = fragmentList.indexOfFirst { item -> item is EvalFixFragment }
                binding.materialPager.setCurrentItem(evalPage,false)
            }
        }
    }

    //音频操作回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayEvent(event: ListenPlayEvent) {
        if (event.showType.equals(ListenPlayEvent.PLAY_switch)) {
            if (event.showMsg.equals("1")) {
                //旧的操作：随机获取一个课程（这里需要根据总的列表数据，随机获取一个并设置才行）
                //新的操作：顺序播放
                setTitleText(AppClient.conceptItem.realTitle())
                if (::mediator.isInitialized) {
                    mediator.detach()
                }

                requestSentenceList()
            }
        }
    }
}