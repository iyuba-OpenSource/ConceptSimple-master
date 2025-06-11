//package com.suzhou.concept.fragment.speaking
//
//import androidx.core.util.Consumer
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.recyclerview.widget.DividerItemDecoration
//import com.iyuba.module.toolbox.GsonUtils
//import com.suzhou.concept.activity.speaking.OtherOneVideoActivity
//import com.suzhou.concept.activity.speaking.PlaySpeakingActivity
//import com.suzhou.concept.adapter.YoungRankAdapter
//import com.suzhou.concept.bean.YoungRankItem
//import com.suzhou.concept.databinding.SpeakingRankLayoutBinding
//import com.suzhou.concept.fragment.BaseFragment
//import com.suzhou.concept.utils.ExtraKeysFactory
//import com.suzhou.concept.utils.judgeType
//import com.suzhou.concept.utils.showToast
//import com.suzhou.concept.utils.startActivity
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flatMapConcat
//import kotlinx.coroutines.flow.onStart
//import kotlinx.coroutines.launch
//
//class SpeakingRankFragment : BaseFragment<SpeakingRankLayoutBinding>() , Consumer<YoungRankItem> {
//
//    private lateinit var adapter:YoungRankAdapter
//
//    override fun SpeakingRankLayoutBinding.initBinding() {
//        adapter = with(YoungRankAdapter()) {
//            registerVideoListener(this@SpeakingRankFragment)
//            this
//        }
//        speakingRankList.adapter = adapter
//        speakingRankList.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                young.youngItem.flatMapConcat {
//                    young.requestRankList(it.voa_id)
//                }.onStart {
//                    showActivityLoad<PlaySpeakingActivity>()
//                }.catch {
//                    dismissActivityLoad<PlaySpeakingActivity>()
//                    it.judgeType().showToast()
//                }.collect {
//                    dismissActivityLoad<PlaySpeakingActivity>()
//                    adapter.submitData(it)
//                }
//            }
//        }
//    }
//
//    override fun accept(item: YoungRankItem) {
//        val rankData = GsonUtils.toJson(item)
//        requireActivity().startActivity<OtherOneVideoActivity> {
//            putExtra(ExtraKeysFactory.youngRankItem,rankData)
//        }
//    }
//}