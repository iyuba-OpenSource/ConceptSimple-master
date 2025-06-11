//package com.suzhou.concept.fragment.main
//
//import android.content.Intent
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import androidx.core.util.Consumer
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.recyclerview.widget.GridLayoutManager
//import com.suzhou.concept.AppClient
//import com.suzhou.concept.R
//import com.suzhou.concept.activity.through.ShowWordLevelActivity
//import com.suzhou.concept.adapter.WordOptionAdapter
//import com.suzhou.concept.bean.*
//import com.suzhou.concept.databinding.BreakThroughFragmentBinding
//import com.suzhou.concept.fragment.BaseFragment
//import com.suzhou.concept.utils.*
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flatMapConcat
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//
///**
//苏州爱语吧科技有限公司
// */
//@RequiresApi(Build.VERSION_CODES.N)
//class BreakThroughFragment : BaseFragment<BreakThroughFragmentBinding>(), Consumer<Int> {
//    private val adapter = WordOptionAdapter()
//
//    private val nativeList = mutableListOf<WordItem>()
//    private val optionList= mutableListOf<WordOptions>()
//    private var indexSelected=1
//
//    private lateinit var dialog:AlertDialog
//    private val itemList= with(mutableListOf<Pair<String,Int>>()){
//        add(Pair("第一册（1062个单词）",1))
//        add(Pair("第二册（858个单词）",2))
//        add(Pair("第三册（1038个单词）",3))
//        add(Pair("第四册（788个单词）",4))
//        this
//    }
//
//    //初始化操作
//    override fun BreakThroughFragmentBinding.initBinding() {
//        setTitleText("单词闯关",true){
//            dialog.showNow(resources.displayMetrics)
//        }
//        young.requestBookList()
//        collectDialog()
//        wordLongList.apply {
//            layoutManager = GridLayoutManager(requireContext(), 3)
//            adapter = this@BreakThroughFragment.adapter
//        }
//        adapter.clickListener = this@BreakThroughFragment
//        listenWordOption()
//        listenWordInfo()
//    }
//
//    private fun listenWordInfo(){
//        lifecycleScope.launch {
//            wordViewModel.breakInfoResult.collect{
//                nativeList.apply {
//                    clear()
//                    addAll(it)
//                }
//            }
//        }
//    }
//
//    private fun listenWordOption(){
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED){
//                wordViewModel.wordOptionsResult.collect{result->
//                    result.onError {
//                        it.judgeType().showToast()
//                        changeStatus(false)
//                    }.onSuccess {
//                        changeStatus()
//                        optionList.apply {
//                            clear()
//                            addAll(it)
//                        }
//                        adapter.changeData(it)
//                    }.onLoading {
//                        changeStatus(false)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun collectDialog(){
//        lifecycleScope.launch {
//            young.bookList.flatMapConcat {
//                wordViewModel.getThroughWordId().flatMapConcat { i ->
//                    flow { emit(Pair(it, i)) }
//                }
//            }.first().run {
//                first.onError {
//                    it.judgeType().showToast()
//                }.onSuccess {
//                    initDialog(it, second)
//                    requestMergeWord()
//                }
//            }
//        }
//    }
//
//    private fun requestMergeWord(){
//        wordViewModel.requestMergeWord()
//    }
//
//    private fun initDialog(list:List<YoungItem>,historyId:Int){
//        val descArray = arrayOf(
//            "StarterA（94个单词）", "StarterB（130个单词）",
//            "青少版1A（368个单词）", "青少版1B（346个单词）",
//            "青少版2A（308个单词）", "青少版2B（276个单词）",
//            "青少版3A（320个单词）", "青少版3B（197个单词）",
//            "青少版4A（493个单词）", "青少版4B（433个单词）",
//            "青少版5A（378个单词）", "青少版5B（315个单词）"
//        )
//        if (descArray.size==list.size){
//            for (i in list.indices){
//                itemList.add(Pair(descArray[i],list[i].Id.toInt()))
//            }
//        }
//        val items=itemList.map { it.first }.toTypedArray()
//        val checkIndex = with(itemList.map {
//            it.second
//        }.indexOfFirst {
//            it == historyId
//        }) {
//            (if (this < 0) 0 else this)
//        }
//        bind.bookDesc=itemList[checkIndex].first
//        dialog=AlertDialog.Builder(requireContext())
//            .setSingleChoiceItems(items,checkIndex) { _, i ->
//                itemList[i].apply {
//                    bind.bookDesc=first
//                    wordViewModel.saveThroughWordId(second)
//                    requestMergeWord()
//                    dialog.dismiss()
//                }
//            }
//            .setTitle("选择课本")
//            .setPositiveButton(getString(R.string.cancel),null)
//            .create()
//    }
//
//    /**
//     * 为什么不能只存id？2022-11-11 14：53
//     *
//     * 还得根据Pairs中的某个元素确定选择对话框的角标 2022-11-11 14：55
//     * */
//
//
//    /**
//     * 跳转到列表界面
//     * index--位置，wordItem--单词数据
//     */
//    private fun startShowWordLevelActivity(index: Int,option: WordOptions) {
//        //再三权衡下，还是觉得过滤List比room查询要好
//        val young=with(bind.bookDesc!!){
//            contains("A")||contains("B")
//        }
//        val resultList = nativeList.filter { i ->
//            optionList[index].voaId== with(i){
//                //区别青少版
//                (if (young) unitId else voa_id)
//            }
//        }
//        AppClient.resultWordList.clear()
//        EventBus.getDefault().post(PauseServiceVideoEvent())
//        //保存单词数据
//        AppClient.addWordData(resultList)
//        //全局的Operation的WordItem，然后遍历
//        var intent = Intent(requireContext(), ShowWordLevelActivity::class.java)
//        intent.putExtra(ExtraKeysFactory.data,option.index)
//        startActivity(intent)
//    }
//
//    override fun accept(t: Int) {
//        indexSelected=t
//        activity?.judgeLogin {
//            if (t == 0) {
//                startShowWordLevelActivity(t,optionList[t])
//                return@judgeLogin
//            }
//            activity?.judgeVip("单词闯关") {
//                if (t > 0 && optionList[t - 1].isComplete()) {
//                    startShowWordLevelActivity(t,optionList[t])
//                } else {
//                    "通关上个单元后解锁此单元内容".showToast()
//                }
//            }
//        }
//    }
//
//    private fun changeStatus(flag:Boolean=true){
//        bind.apply {
//            loadLayout.visibilityState(flag)
//            wordLongList.visibilityState(!flag)
//        }
//    }
//
//    /**
//     * 最后一个明显的问题，做完之后如何以一个合适的方式刷新做题结果？？？只刷新对应position为最佳（反正本地数据已经更改，再次启动app后会看到数据更改）
//     *
//     * 还有课文页面的青少版选择性展示图片，与全四册区别
//     *
//     * 需要参数：index,正确数量、错误数量
//     */
//    @Subscribe
//    fun event(event: OverBreakEvent){
//        optionList[indexSelected].finished=event.rightNum
//        adapter.notifyItemChanged(indexSelected)
//    }
//
//    @Subscribe
//    fun event(event: UpdateLocalWordEvent){
//        requestMergeWord()
//    }
//
//    //单词刷新操作
//    /*@Subscribe(threadMode = ThreadMode.MAIN)
//    fun event(event:RefreshEvent){
//        if (event.type.equals(RefreshEvent.WORD_REFRESH)){
//            young.requestBookList()
//            collectDialog()
//            adapter.notifyDataSetChanged()
//            listenWordOption()
//            listenWordInfo()
//            setTitleText("单词闯关",true){
//                dialog.showNow(resources.displayMetrics)
//            }
//        }
//    }*/
//
//    override fun initEventBus(): Boolean =true
//}