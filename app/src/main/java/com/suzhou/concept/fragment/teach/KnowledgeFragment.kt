//package com.suzhou.concept.fragment.teach
//
//import android.content.Intent
//import android.text.TextUtils
//import android.view.View
//import androidx.appcompat.app.AlertDialog
//import androidx.core.util.Consumer
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.GridLayoutManager
//import com.suzhou.concept.R
//import com.suzhou.concept.activity.dollar.MemberCentreActivity
//import com.suzhou.concept.adapter.KnowledgeAdapter
//import com.suzhou.concept.adapter.WordShowBottomAdapter
//import com.suzhou.concept.databinding.KnowledgeFragmentBinding
//import com.suzhou.concept.fragment.BaseFragment
//import com.suzhou.concept.lil.ui.study.word.train.WordTrainActivity
//import com.suzhou.concept.utils.ExtraKeysFactory
//import com.suzhou.concept.utils.GlobalMemory
//import com.suzhou.concept.utils.GlobalMemory.userInfo
//import com.suzhou.concept.utils.addDefaultDecoration
//import com.suzhou.concept.utils.judgeType
//import com.suzhou.concept.utils.logic.GlobalPlayManager
//import com.suzhou.concept.utils.logic.VoiceStatus
//import com.suzhou.concept.utils.showGoLoginDialog
//import com.suzhou.concept.utils.showToast
//import kotlinx.coroutines.launch
//
//
///**
// * 知识界面
// */
//class KnowledgeFragment:BaseFragment<KnowledgeFragmentBinding>() {
//
//    //是否第一次加载
//    private var isFirstLoading:Boolean = true
//
//    override fun KnowledgeFragmentBinding.initBinding() {
//        updateLoadingStyle(true,"")
//
//        val dataAdapter= with(KnowledgeAdapter()){
//            inflate(listener)
//            this
//        }
//        knowledgeList.apply {
//            adapter=dataAdapter
//            addDefaultDecoration()
//        }
//
//        //增加训练功能
//        showTrain()
//
//        lifecycleScope.launch {
//            wordViewModel.lastKnowledge.collect{result->
//                result.onError {
//                    updateLoadingStyle(false,"加载单词数据失败，请重试")
//                    it.judgeType().showToast()
//                }.onSuccess {
//                    dataAdapter.changeData(it)
//
//                    //如果存在单词数据，则显示训练功能
//                    if (it!=null&& it.isNotEmpty()){
//                        updateLoadingStyle(false,"")
//                        bind.knowledgeTrain.visibility = View.VISIBLE
//                    }else{
//                        updateLoadingStyle(false,"暂无单词数据")
//                        bind.knowledgeTrain.visibility = View.GONE
//                    }
//                }
//            }
//        }
//
//        //设置刷新
//        bind.knowledgeButton.setOnClickListener {
//            updateLoadingStyle(true,"")
//            wordViewModel.requestKnowledgeWord()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        if (isFirstLoading){
//            wordViewModel.requestKnowledgeWord()
//            isFirstLoading = false
//        }
//    }
//
//    private val listener=Consumer<String>{
//        GlobalPlayManager.addUrl(Pair(VoiceStatus.WORD_KNOWLEDGE,it))
//    }
//
//    private fun showTrain(){
//        val pairList:MutableList<Pair<String,Pair<Int,String>>> = ArrayList()
//        pairList.add(Pair(WordTrainActivity.Train_enToCn, Pair(R.drawable.vector_en2cn,"英汉训练")))
//        pairList.add(Pair(WordTrainActivity.Train_cnToEn, Pair(R.drawable.vector_cn2en,"汉英训练")))
//        pairList.add(Pair(WordTrainActivity.Word_spell, Pair(R.drawable.vector_spelling,"单词拼写")))
//        pairList.add(Pair(WordTrainActivity.Train_listen, Pair(R.drawable.vector_listen,"听力训练")))
//
//        val showAdapter:WordShowBottomAdapter = WordShowBottomAdapter(requireActivity(),pairList)
//        val gridManager:GridLayoutManager = GridLayoutManager(requireActivity(),pairList.size)
//        bind.knowledgeTrain.layoutManager = gridManager
//        bind.knowledgeTrain.adapter = showAdapter
//        showAdapter.setListener {
//            //判断用户登录信息
//            if (!GlobalMemory.isLogin()){
//                requireActivity().showGoLoginDialog()
//                return@setListener
//            }
//
//            //判断当前是否<3或者购买会员
//            val position = arguments?.getInt(ExtraKeysFactory.position,-1)
//            if (position!! >2 && !userInfo.isVip()){
//                AlertDialog.Builder(requireActivity())
//                    .setTitle("会员购买")
//                    .setMessage("单词训练仅限前三课，是否开通会员继续使用？")
//                    .setPositiveButton("开通会员") { dialog, which ->
//                        requireActivity().startActivity(
//                            Intent(
//                                context,
//                                MemberCentreActivity::class.java
//                            )
//                        )
//                    }
//                    .setNegativeButton("取消", null)
//                    .setCancelable(false)
//                    .show()
//                return@setListener
//            }
//
//            //之后进行跳转
//            WordTrainActivity.start(requireActivity(),it)
//        }
//    }
//
//    //样式切换
//    private fun  updateLoadingStyle(loading:Boolean,showMsg:String){
//        if (loading){
//            bind.knowledgeProgress.visibility = View.VISIBLE
//            bind.knowledgeMsg.visibility = View.VISIBLE
//            bind.knowledgeButton.visibility = View.INVISIBLE
//
//            bind.knowledgeMsg.text = "正在加载单词数据"
//        }else{
//            if (TextUtils.isEmpty(showMsg)){
//                bind.knowledgeLoading.visibility = View.GONE
//            }else{
//                bind.knowledgeLoading.visibility = View.VISIBLE
//                bind.knowledgeProgress.visibility = View.GONE
//                bind.knowledgeMsg.visibility = View.VISIBLE
//                bind.knowledgeButton.visibility = View.VISIBLE
//
//                bind.knowledgeMsg.text = showMsg
//            }
//        }
//    }
//}