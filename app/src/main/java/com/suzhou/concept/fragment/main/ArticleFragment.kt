package com.suzhou.concept.fragment.main

import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iyuba.imooclib.ui.content.ContentActivity
import com.suzhou.concept.AppClient
import com.suzhou.concept.AppClient.Companion.conceptItem
import com.suzhou.concept.R
import com.suzhou.concept.activity.MainActivity
import com.suzhou.concept.activity.article.SelectBookActivity
import com.suzhou.concept.activity.article.TeachMaterialActivity
import com.suzhou.concept.adapter.SentenceAdapter
import com.suzhou.concept.bean.ChangeBookEvent
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.ExerciseEvent
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.WordEvent
import com.suzhou.concept.dao.AppDatabase.Companion.getDatabase
import com.suzhou.concept.databinding.ArticleFragmentLayoutBinding
import com.suzhou.concept.fragment.BaseFragment
import com.suzhou.concept.lil.event.LocalEvalDataRefreshEvent
import com.suzhou.concept.lil.event.PageJumpEvent
import com.suzhou.concept.lil.event.RefreshEvent
import com.suzhou.concept.lil.service.ListenPlayManager
import com.suzhou.concept.lil.service.data.ListenPlayEvent
import com.suzhou.concept.lil.service.temp.ListenPlaySession
import com.suzhou.concept.lil.ui.ad.util.show.AdShowUtil
import com.suzhou.concept.lil.ui.ad.util.show.template.AdTemplateShowManager
import com.suzhou.concept.lil.ui.ad.util.show.template.AdTemplateViewBean
import com.suzhou.concept.lil.ui.ad.util.show.template.OnAdTemplateShowListener
import com.suzhou.concept.utils.ExtraKeysFactory
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.GlobalMemory.currentYoung
import com.suzhou.concept.utils.GlobalMemory.userInfo
import com.suzhou.concept.utils.OnStatisticsListener
import com.suzhou.concept.utils.addDefaultDecoration
import com.suzhou.concept.utils.findIndexSuccess
import com.suzhou.concept.utils.judgeType
import com.suzhou.concept.utils.showToast
import com.suzhou.concept.utils.startActivity
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.utils.ToastFactory

/**
 * 首页
 */
@RequiresApi(Build.VERSION_CODES.N)
class ArticleFragment : BaseFragment<ArticleFragmentLayoutBinding>() {
    private val list = mutableListOf<ConceptItem>()
    private val sentenceAdapter = SentenceAdapter()

    //是否去了其他界面
    private var isToOtherPage: Boolean = false

    override fun ArticleFragmentLayoutBinding.initBinding() {
        sentenceAdapter.apply {
            itemListener = listener
            inflateStatisticsListener(statisticsListener)
        }
        newConceptList.apply {
            addDefaultDecoration()
            adapter = sentenceAdapter
            itemAnimator = null
        }
        lifecycleScope.launch {
            userAction.fetchLanguageType().collect {
                refreshType(it)
            }
        }

        setBackPlayLayout()

        lifecycleScope.launch {
            conceptViewModel.articleListResult.collect { result ->
                result.onError {
                    it.judgeType().showToast()
                    dismissActivityLoad<MainActivity>()
                }.onSuccess {
                    list.clear()
                    list.addAll(it)
                    sentenceAdapter.changeData(it)
                    dismissActivityLoad<MainActivity>()
                    //设置界面缓存
                    bind.newConceptList.setItemViewCacheSize(list.size)

                    //这里保存下当前的数据
                    ListenPlaySession.getInstance().transDataToTemp(list)

                    //刷新广告
                    refreshTemplateAd()
                }.onLoading {
                    showActivityLoad<MainActivity>()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                conceptViewModel.lastUpdateListen.collect { result ->
                    result.onSuccess {
                        changeProgressStatus() { index ->
                            list[index].listenProgress = it
                        }
                    }.onError {
                        it.judgeType().showToast()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                conceptViewModel.lastUpdateEval.collect { result ->
                    result.onSuccess {
                        changeProgressStatus() { index ->
                            val evalInc = AppClient.conceptItem.evalSuccess + 1
                            list[index].evalSuccess = evalInc
                        }
                    }.onError {
                        it.judgeType().showToast()
                    }
                }
            }
        }
        lifecycleScope.launch {
            conceptViewModel.lastUpdateWord.collect { result ->
                result.onSuccess {
                    changeProgressStatus { index ->
                        list[index].wordRight = it.second
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.RESUMED){
            conceptViewModel.lastUpdateExercise.collect { result ->
                result.onSuccess {
                    changeProgressStatus { index ->
                        list[index].exerciseRight = it.second
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
//            }
        }
    }

    override fun onResume() {
        super.onResume()

        isToOtherPage = false
    }

    override fun onPause() {
        super.onPause()

        isToOtherPage = true
    }

    override fun onDestroy() {
        super.onDestroy()
        //关闭广告
        AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey)
    }

    private val statisticsListener = object : OnStatisticsListener {

        override fun onListen(position: Int, item: ConceptItem) {
            startTeach(position, item)
        }

        override fun onEval(position: Int, item: ConceptItem) {
            EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))
            startTeach(position, item) {
                putExtra(ExtraKeysFactory.teachKey, ExtraKeysFactory.evalFlag)
            }
        }

        override fun onWord() {
            EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))
//            conceptViewModel.changeGoWord()
            //跳转到单词闯关界面
            EventBus.getDefault().post(PageJumpEvent(PageJumpEvent.page_wordPass))
        }

        override fun onExercise(position: Int, item: ConceptItem) {
            EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))
            startTeach(position, item) {
                putExtra(ExtraKeysFactory.teachKey, ExtraKeysFactory.exerciseFlag)
            }
        }

        override fun onMooc(item: ConceptItem) {
            if (!"0".equals(item.titleid, true)) {
                EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))

                startActivity(
                    ContentActivity.buildIntent(
                        requireContext(),
                        item.categoryid,
                        "class.jichu"
                    )
                )
            }
        }
    }

    private fun changeProgressStatus(method: (index: Int) -> Unit) {
        if (list.isEmpty()) {
            return
        }

        val index = list.indexOfFirst { item ->
            val bookId = AppClient.conceptItem.bookId
            val language = AppClient.conceptItem.language
            val index = AppClient.conceptItem.index
            val bookIdEqual = item.bookId == bookId
            val languageEqual = item.language == language
            val indexEqual = item.index == index
            bookIdEqual && languageEqual && indexEqual
        }
        if (!index.findIndexSuccess()) {
            return
        }
        method.invoke(index)
        sentenceAdapter.notifyItemChanged(index)
    }

    private fun refreshType(type: LanguageType) {
        if (TextUtils.isEmpty(GlobalMemory.curSelectLanguage)) {
            GlobalMemory.curSelectLanguage = type.language
        }
        if (GlobalMemory.curSelectBookId == 0) {
            GlobalMemory.curSelectBookId = type.bookId
        }

        AppClient.curShowBookId = type.bookId
        GlobalMemory.currentLanguage = type
        setTitleText(type.convertLanguage(), true) {
            requireActivity().startActivity<SelectBookActivity> {

            }
        }
        GlobalMemory.currentYoung = type.bookId > 4
        conceptViewModel.requestArticleList(type)
    }

    override fun initEventBus(): Boolean = true

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: ChangeBookEvent) {
        //先清除广告
        AdTemplateShowManager.getInstance().clearAd(adTemplateKey)
        //刷新数据
        refreshType(event.type)
    }

    private val listener = Consumer<Pair<Int, ConceptItem>> {
        startTeach(it.first, it.second)
    }

    private fun startTeach(position: Int, item: ConceptItem, block: Intent.() -> Unit = {}) {
        AppClient.conceptItem = item
        requireActivity().startActivity<TeachMaterialActivity> {
            block().apply {
                AppClient.curListIndex = position
                putExtra(ExtraKeysFactory.position, position)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: ExerciseEvent) {
        changeProgressStatus { index ->
            list[index].exerciseRight = event.exerciseNum
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: WordEvent) {
        if (list.isEmpty()) {
            return
        }

        var index = event.index;
        if (GlobalMemory.currentYoung) {
            index = list.indexOfFirst { item ->
                val bookIdEqual = item.bookId == event.bookId
                val indexEqual = item.index == event.index
                bookIdEqual && indexEqual
            }
        }

        if (GlobalMemory.currentYoung && !index.findIndexSuccess()) {
            return
        }

        list[index].wordNum = event.wordNum
        list[index].wordRight = event.wordRight
        sentenceAdapter.notifyItemChanged(index)
    }

    //直接刷新本地数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: LocalEvalDataRefreshEvent) {
        sentenceAdapter.notifyDataSetChanged()
    }

    //回调操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayEvent(event: ListenPlayEvent) {
        if (event.showType.equals(ListenPlayEvent.PLAY_start)) {
            //正在播放
            bind.backstageLayout.visibility = View.VISIBLE
            bind.backstageControl.setImageResource(R.drawable.pause)

            //设置不同类型的显示
            bind.backstageText.text = showLessonTitle(ListenPlaySession.getInstance().tempBean.showIndex,ListenPlaySession.getInstance().tempBean.title)
            ListenPlayManager.getInstance().playService.updateNotification(
                ListenPlaySession.getInstance().tempBean.title,
                null,
                true
            )
            ListenPlayManager.getInstance().playService.continuePlay(true)
        }

        if (event.showType.equals(ListenPlayEvent.PLAY_pause)) {
            //已经暂停
            if (bind.backstageLayout.visibility == View.VISIBLE) {
                bind.backstageLayout.visibility = View.VISIBLE
            } else {
                bind.backstageLayout.visibility = View.GONE
            }

            bind.backstageControl.setImageResource(R.drawable.start)
            if (ListenPlaySession.getInstance().tempBean != null) {
                bind.backstageText.text = showLessonTitle(ListenPlaySession.getInstance().tempBean.showIndex,ListenPlaySession.getInstance().tempBean.title)
                ListenPlayManager.getInstance().playService.updateNotification(
                    ListenPlaySession.getInstance().tempBean.title,
                    null,
                    true
                )
            }
            ListenPlayManager.getInstance().playService.pauseAudio()

            //如果正在播放，则刷新进度；不播放不刷新进度
            //刷新进度
            val curProgress = ListenPlayManager.getInstance().playService.playerProgress
            val totalTime = ListenPlayManager.getInstance().playService.playerDuration
            val showProgress = (curProgress*100L/totalTime).toInt()
            updateListenProgress(showProgress)
        }

        if (event.showType.equals(ListenPlayEvent.PLAY_ui_hide)) {
            //暂停并且隐藏底部控制
            bind.backstageLayout.visibility = View.GONE
            ListenPlayManager.getInstance().playService.pauseAudio()
            ListenPlayManager.getInstance().playService.updateNotification(
                ListenPlaySession.getInstance().tempBean.title,
                null,
                true
            )
        }

        if (event.showType.equals(ListenPlayEvent.PLAY_switch)) {
            //播放完成后切换其他播放
            updateListenProgress(100)

            if (event.showMsg.isEmpty()) {
                /*//随机获取一个数据进行保存
                var randomInt:Int = (Math.random()*list.size).toInt()

                //如果随机数和现在一样，则+1或者-1处理
                if (randomInt == ListenPlaySession.getInstance().tempBean.showIndex){
                    if (randomInt<list.size-1){
                        randomInt+=1
                    }else{
                        randomInt-=1
                    }
                }*/

                //这里根据要求，设置成顺序播放
                val curPosition = ListenPlaySession.getInstance().tempBean.showIndex
                val totalCount = ListenPlaySession.getInstance().tempList.size
                var nextPosition = 0;
                if (curPosition == totalCount - 1) {
                    nextPosition = 0
                } else {
                    //其他的话直接+1
                    nextPosition = curPosition + 1
                }


                //设置当前列表选中的位置
                AppClient.curListIndex = nextPosition
                //这里将选中的item数据一并赋值过来
                AppClient.conceptItem = list[nextPosition]

                //如果没有跳转到其他界面，则直接播放即可
                if (!isToOtherPage) {
                    //把数据塞进去
                    ListenPlaySession.getInstance().setSelectData(nextPosition)
                    //加载播放
                    ListenPlayManager.getInstance().playService.playAudio()
                } else {
                    //传递给下一个界面
                    EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_switch, "1"))
                }
            }
        }

        if (event.showType.equals(ListenPlayEvent.PLAY_prepare_finish)) {
            //加载完成
            if (!isToOtherPage) {
                EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_start))
            }
        }

        if (event.showType.equals(ListenPlayEvent.PLAY_complete_finish)) {
            //播放完成
            bind.backstageControl.setImageResource(R.drawable.start)

            //刷新进度显示

            if (!isToOtherPage) {
                //下一个
                EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_switch, ""))
            }
        }

        if (event.showType.equals(ListenPlayEvent.PLAY_bg_text)) {
            //下边控制条的数据刷新显示
            bind.backstageText.text = showLessonTitle(ListenPlaySession.getInstance().tempBean.showIndex,ListenPlaySession.getInstance().tempBean.title)
        }
    }

    //刷新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent){
        //登录或者会员操作
        if (event.type == RefreshEvent.USER_VIP){
            refreshTemplateAd()
        }

        //登出操作
        if (event.type == RefreshEvent.USER_LOGOUT){
            refreshTemplateAd()
        }
    }

    //设置底部的播放功能
    private fun setBackPlayLayout() {
        bind.backstageLayout.visibility = View.GONE
        bind.backstageControl.setOnClickListener {
            if (!ListenPlayManager.getInstance().playService.isPrepare) {
                ToastFactory.showShort(requireActivity(), "音频文件加载错误~")
                return@setOnClickListener
            }

            //判断播放状态
            var isPlay = ListenPlayManager.getInstance().playService.player.isPlaying
            if (isPlay) {
                EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_pause))
            } else {
                EventBus.getDefault().post(ListenPlayEvent(ListenPlayEvent.PLAY_start))
            }
        }
        bind.backstageLayout.setOnClickListener {
            startTeach(ListenPlaySession.getInstance().tempBean.showIndex, AppClient.conceptItem)
        }
    }

    /*************************当前界面的辅助功能**************************/
    //显示文本信息
    private fun showLessonTitle(showIndex:Int,showTitle:String):String{
        var showText = ""
        if (GlobalMemory.currentYoung){
            showText = showTitle
        }else{
            showText = "Lesson "+(showIndex+1)+" "+showTitle
        }

        return showText
    }

    //更新播放进度
    private fun updateListenProgress(progress: Int) {
        val bookId = conceptItem.bookId
        val index = conceptItem.index
        val userId = userInfo.uid
        if (currentYoung) {
            getDatabase(requireActivity()).youngBookDao().updateItemListen(bookId, index, userId, progress)
        } else {
            val language = conceptItem.language
            getDatabase(requireActivity()).conceptDao().updateListenConceptItem(bookId, language, index, progress)
        }

        //刷新数据显示
        sentenceAdapter.notifyDataSetChanged()
    }

    /*****************************设置新的信息流广告******************/
    //当前信息流广告的key
    private val adTemplateKey: String = ArticleFragment::class.java.getName()

    //模版广告数据
    private var templateViewBean: AdTemplateViewBean? = null

    //显示广告
    private fun showTemplateAd() {
        if (templateViewBean == null) {
            templateViewBean = AdTemplateViewBean(
                    R.layout.item_ad_mix,
                    R.id.template_container,
                    R.id.ad_whole_body,
                    R.id.native_main_image,
                    R.id.native_title,
                    bind.newConceptList,
                    sentenceAdapter,
                    object :
                        OnAdTemplateShowListener {
                        override fun onLoadFinishAd() {
                        }

                        override fun onAdShow(showAdMsg: String?) {
                        }

                        override fun onAdClick() {
                        }
                    })
            AdTemplateShowManager.getInstance().setShowData(adTemplateKey, templateViewBean)
        }
        AdTemplateShowManager.getInstance().showTemplateAd(adTemplateKey, activity)
    }

    //刷新广告操作[根据类型判断刷新还是隐藏]
    private fun refreshTemplateAd() {
//        if (!AdShowUtil.Util.isADBlock() && !userInfo.isVip()) {
//            showTemplateAd()
//        } else {
//            AdTemplateShowManager.getInstance().stopTemplateAd(adTemplateKey)
//        }

        //更新广告逻辑
        if (!AdShowUtil.Util.isADBlock()){
            showTemplateAd()
        }
    }
}