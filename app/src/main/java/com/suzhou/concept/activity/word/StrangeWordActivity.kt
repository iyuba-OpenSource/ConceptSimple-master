//package com.suzhou.concept.activity.word
//
//import androidx.lifecycle.lifecycleScope
//import com.suzhou.concept.R
//import com.suzhou.concept.activity.BaseActivity
//import com.suzhou.concept.adapter.DataRecycleViewAdapter
//import com.suzhou.concept.bean.WordRemoveEvent
//import com.suzhou.concept.databinding.ActivityStrangeWordBinding
//import com.suzhou.concept.utils.StrangeListener
//import com.suzhou.concept.utils.logic.GlobalPlayManager
//import com.suzhou.concept.utils.logic.VoiceStatus
//import com.suzhou.concept.utils.startShowWordActivity
//import com.suzhou.concept.utils.visibilityState
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flatMapConcat
//import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//
//class StrangeWordActivity : BaseActivity<ActivityStrangeWordBinding>() , StrangeListener {
//    private lateinit var dataAdapter:DataRecycleViewAdapter
//
//    override fun ActivityStrangeWordBinding.initBinding() {
//        setTitleText("生词本")
//        requestPagingWord()
//    }
//
//    /**
//     * 下下下下策
//     * */
//    private fun requestPagingWord(){
//        lifecycleScope.launch {
//            conceptViewModel.requestStrangenessConcat().catch {
//                changeVisibility(getString(R.string.empty_data), false)
//            }.flatMapConcat {
//                conceptViewModel.requestStrangenessWord()
//            }.collect{
//                changeVisibility()
//                dataAdapter = with(DataRecycleViewAdapter()) {
//                    listener = this@StrangeWordActivity
//                    this
//                }
//                binding.strangeList.adapter = dataAdapter
//                dataAdapter.submitData(it)
//            }
//        }
//    }
//
//    private fun changeVisibility(message:String?="",flag:Boolean=true){
//        binding.strangeEmpty.visibilityState(flag)
//        binding.strangeList.visibilityState(!flag)
//        binding.strangeEmpty.text=message
//    }
//
//    override fun wordDetailed(word:String) {
//        startShowWordActivity(word,true)
//    }
//
//
//    override fun playVideo(url: String) {
//        GlobalPlayManager.addUrl(Pair(VoiceStatus.WORD_BOOK,url))
//    }
//
//    @Subscribe(threadMode = ThreadMode.ASYNC)
//    fun event(event: WordRemoveEvent){
//        requestPagingWord()
//    }
//
//
//    override fun initEventBus(): Boolean =true
//
//    override fun onDestroy() {
//        super.onDestroy()
//        GlobalPlayManager.executeDestroy()
//    }
//}