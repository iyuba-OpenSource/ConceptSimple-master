//package com.suzhou.concept.fragment.teach
//
//import android.view.View
//import androidx.lifecycle.lifecycleScope
//import androidx.paging.LoadState
//import com.bumptech.glide.Glide
//import com.suzhou.concept.AppClient
//import com.suzhou.concept.R
//import com.suzhou.concept.activity.article.EvaluationInfoActivity
//import com.suzhou.concept.adapter.RankPagingAdapter
//import com.suzhou.concept.databinding.RankFragmentBinding
//import com.suzhou.concept.fragment.BaseFragment
//import com.suzhou.concept.utils.ExtraKeysFactory
//import com.suzhou.concept.utils.GlobalMemory
//import com.suzhou.concept.utils.OnClickRankItemListener
//import com.suzhou.concept.utils.addDefaultDecoration
//import com.suzhou.concept.utils.showGoLoginDialog
//import com.suzhou.concept.utils.startActivity
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//
///**
// * 排行界面
// */
//class RankFragment: BaseFragment<RankFragmentBinding>(),View.OnClickListener, OnClickRankItemListener {
//
//    override fun RankFragmentBinding.initBinding() {
//        rankLayout.visibility=View.GONE
//        rankProgress.visibility=View.VISIBLE
//        val adapter= with(RankPagingAdapter()){
//            itemListener=this@RankFragment
//            this
//        }
//        rankHead.setOnClickListener(this@RankFragment)
//        rankPager.apply {
//            rankPager.adapter=adapter
//            addDefaultDecoration()
//        }
//        lifecycleScope.launch {
//            val result=evaluation.getRankData().first()
//            adapter.submitData(result)
//        }
//        adapter.addLoadStateListener { state->
//            val notLoading=state.refresh is LoadState.NotLoading
//            val showProgress=(rankProgress.visibility==View.VISIBLE)
//            if (notLoading&&showProgress){
//                item= AppClient.rankResponse
//                rankProgress.visibility=View.GONE
//                rankLayout.visibility=View.VISIBLE
//            }
//        }
//    }
//
//    private fun gotoEvaluationInfo(userId:Int= GlobalMemory.userInfo.uid, userName:String=GlobalMemory.userInfo.username){
//        GlobalMemory.startRankInfo=true
//        requireActivity().startActivity<EvaluationInfoActivity> {
//            putExtra(ExtraKeysFactory.userId,userId)
//            putExtra(ExtraKeysFactory.userName,userName)
//        }
//    }
//    override fun onClick(p0: View?) {
//        if (GlobalMemory.isLogin()){
//            gotoEvaluationInfo()
//        }else{
//            activity?.showGoLoginDialog()
//        }
//    }
//
//
//    override fun listenItem(userId: Int, userName: String) {
//        gotoEvaluationInfo(userId, userName)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (GlobalMemory.isLogin()){
//            lifecycleScope.launch {
//                bind.item=evaluation.getRankDataUser()
//            }
//
//            Glide.with(requireActivity()).load(GlobalMemory.userInfo.imgSrc).into(bind.myHead)
//            bind.myName.text = GlobalMemory.userInfo.username
//        }else{
//            Glide.with(requireActivity()).load(R.drawable.head_small).into(bind.myHead)
//            bind.myName.text = "未登录"
//        }
//    }
//
//}