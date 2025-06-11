//package com.suzhou.concept.activity.through
//
//import android.content.Intent
//import android.os.Bundle
//import com.suzhou.concept.AppClient
//import com.suzhou.concept.activity.BaseActivity
//import com.suzhou.concept.activity.user.LoginActivity
//import com.suzhou.concept.adapter.WordLevelAdapter
//import com.suzhou.concept.bean.WordItem
//import com.suzhou.concept.databinding.ActivityShowWordLevelBinding
//import com.suzhou.concept.lil.data.newDB.RoomDBManager
//import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean
//import com.suzhou.concept.lil.data.remote.RetrofitUtil
//import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_result
//import com.suzhou.concept.lil.data.remote.bean.Report_wordBreak_submit
//import com.suzhou.concept.lil.event.WordBreakEvent
//import com.suzhou.concept.lil.ui.study.eval.util.RxTimer
//import com.suzhou.concept.lil.ui.word_note.WordDeleteBean
//import com.suzhou.concept.lil.util.ToastUtil
//import com.suzhou.concept.lil.view.dialog.LoadingDialog
//import com.suzhou.concept.utils.ExtraKeysFactory
//import com.suzhou.concept.utils.GlobalMemory
//import com.suzhou.concept.utils.OnWordListItemListener
//import com.suzhou.concept.utils.addDefaultDecoration
//import com.suzhou.concept.utils.logic.GlobalPlayManager
//import com.suzhou.concept.utils.logic.VoiceStatus
//import com.suzhou.concept.utils.startActivity
//import io.reactivex.Observer
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import personal.iyuba.personalhomelibrary.utils.ToastFactory
//
///**
// * 单词列表界面
// */
//class ShowWordLevelActivity : BaseActivity<ActivityShowWordLevelBinding>() {
//    private val result = mutableListOf<WordItem>()
//    private lateinit var wordAdapter: WordLevelAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        EventBus.getDefault().register(this)
//    }
//
//    override fun ActivityShowWordLevelBinding.initBinding() {
//        result.addAll(AppClient.wordList)
////        setTitleText(result.first().readUnit())
//        //显示标题内容
//        val unitIndex = intent.getIntExtra(ExtraKeysFactory.data, result.first().unitId)
//        if (GlobalMemory.wordYoung) {
//            setTitleText("Unit $unitIndex")
//        } else {
//            setTitleText("Lesson $unitIndex")
//        }
//        wordAdapter = with(WordLevelAdapter()) {
//            setListener(onWordListener)
//
//            changeData(result)
//            this
//        }
//        wordLevelList.apply {
//            adapter = wordAdapter
//            addDefaultDecoration()
//        }
//        startBreakThrough.setOnClickListener {
//            //先消除数据
//            uploadList.clear()
//
//            startActivity<BreakThroughActivity> {
//                putExtra(ExtraKeysFactory.data,unitIndex)
//            }
//        }
//    }
//
//    private val onWordListener = object : OnWordListItemListener {
//        override fun onPlay(position: Int) {
//            GlobalPlayManager.addUrl(Pair(VoiceStatus.BREAK_WORD, result[position].audio))
//        }
//
//        override fun onCollect(item: WordItem) {
//            if (!GlobalMemory.isLogin()) {
//                startActivity(Intent(this@ShowWordLevelActivity, LoginActivity::class.java))
//                return
//            }
//
//            //根据数据库的数据来判断
//            val dbData: WordCollectBean? = RoomDBManager.getInstance()
//                .getSingleWordCollectData(GlobalMemory.userInfo.uid, item.word)
//            if (dbData == null) {
//                collectWord(item, "insert")
//            } else {
//                collectWord(item, "delete")
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        EventBus.getDefault().unregister(this)
//
//        GlobalPlayManager.executeDestroy()
//
//        if (::collectWordDis.isInitialized) {
//            RxTimer.unDisposable(collectWordDis)
//        }
//        stopLoading()
//    }
//
//    //显示加载弹窗
//    private lateinit var loadingDialog: LoadingDialog
//
//    private fun startLoading() {
//        if (!::loadingDialog.isInitialized) {
//            loadingDialog = LoadingDialog(this)
//            loadingDialog.create()
//        }
//        loadingDialog.show()
//    }
//
//    private fun stopLoading() {
//        if (::loadingDialog.isInitialized) {
//            loadingDialog.dismiss()
//        }
//    }
//
//    //收藏/取消收藏单词
//    private lateinit var collectWordDis: Disposable
//
//    private fun collectWord(item: WordItem, mode: String) {
//        startLoading()
//
//        //合并数据
//        var list = ArrayList<WordCollectBean>()
//        var collectBean =
//            WordCollectBean()
//        collectBean.word = item.word
//        collectBean.pron = item.pron
//        collectBean.audio = item.audio
//        collectBean.def = item.def
//        collectBean.userId = GlobalMemory.userInfo.uid
//        list.add(collectBean)
//
//        RetrofitUtil.getInstance().collectWord(list, mode)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : Observer<WordDeleteBean> {
//                override fun onSubscribe(d: Disposable) {
//                    collectWordDis = d
//                }
//
//                override fun onError(e: Throwable) {
//                    var showStr: String = "收藏"
//                    if (mode == "delete") {
//                        showStr = "取消收藏"
//                    }
//                    ToastFactory.showShort(this@ShowWordLevelActivity, "$showStr 单词异常，请重试～")
//                }
//
//                override fun onComplete() {
//                    stopLoading()
//                    RxTimer.unDisposable(collectWordDis)
//                }
//
//                override fun onNext(bean: WordDeleteBean) {
//                    var showStr: String = "收藏"
//                    if (mode == "delete") {
//                        showStr = "取消收藏"
//                    }
//
//                    if (bean.result == 1) {
//                        //判断后进行操作
//                        if (mode == "delete") {
//                            RoomDBManager.getInstance().deleteMultiWordCollectData(list)
//                        } else {
//                            RoomDBManager.getInstance().saveMultiWordCollectData(list)
//                        }
//                        //提示显示
//                        ToastFactory.showShort(this@ShowWordLevelActivity, "$showStr 单词成功")
//                        //刷新显示
//                        wordAdapter.notifyDataSetChanged()
//                    } else {
//                        ToastFactory.showShort(
//                            this@ShowWordLevelActivity,
//                            "$showStr 单词失败，请重试～"
//                        )
//                    }
//                }
//            })
//    }
//
//    /******************接收单词闯关的回调*************************/
//    private var uploadList = mutableListOf<Report_wordBreak_submit.TestListBean>()
//    private lateinit var submitWordBreakDis:Disposable
//
//    //因为在设计闯关操作时，设计的太离谱了，每次下一组都是直接跳转到新的界面，导致数据是不正确的，因此将数据返回来使用这个进行处理
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public fun onEvent(event:WordBreakEvent) {
//        val breakStatus = event.isBreakStatus
//        val list = event.list
//        if (list!=null&&list.size>0){
//            uploadList.addAll(list)
//        }
//
//        if (breakStatus){
//            //获取第一个数据即可
//            val wordData = result[0]
//            //将数据上传到学习报告中
//            RetrofitUtil.getInstance().submitWordBreakReport(wordData.bookId,uploadList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object:Observer<Report_wordBreak_result>{
//                    override fun onSubscribe(d: Disposable) {
//                        submitWordBreakDis = d
//                    }
//
//                    override fun onError(e: Throwable) {
//                        ToastUtil.showToast(this@ShowWordLevelActivity,"提交单词闯关进度异常")
//                    }
//
//                    override fun onComplete() {
//
//                    }
//
//                    override fun onNext(bean: Report_wordBreak_result) {
//                        if (bean!=null&&bean.result.equals("1")){
//                            ToastUtil.showToast(this@ShowWordLevelActivity,"提交单词闯关进度成功")
//                        }else{
//                            ToastUtil.showToast(this@ShowWordLevelActivity,"提交单词闯关进度失败")
//                        }
//                    }
//                })
//        }
//    }
//}