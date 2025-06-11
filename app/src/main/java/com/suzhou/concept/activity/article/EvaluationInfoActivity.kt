package com.suzhou.concept.activity.article

import androidx.lifecycle.lifecycleScope
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.adapter.EvaluationInfoAdapter
import com.suzhou.concept.bean.LikeEvaluation
import com.suzhou.concept.bean.RankInfoItem
import com.suzhou.concept.databinding.ActivityEvaluationInfoBinding
import com.suzhou.concept.utils.*
import com.suzhou.concept.utils.logic.GlobalPlayManager
import com.suzhou.concept.utils.logic.VoiceStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 排行榜详情
 */
class EvaluationInfoActivity : BaseActivity<ActivityEvaluationInfoBinding>(), OnEvaluationInfoOperateListener {
    private val list= mutableListOf<RankInfoItem>()
    private lateinit var infoAdapter:EvaluationInfoAdapter

    override fun ActivityEvaluationInfoBinding.initBinding() {
        val userId=intent.getIntExtra(ExtraKeysFactory.userId,0)
        val userName=intent.getStringExtra(ExtraKeysFactory.userName)
        infoAdapter= with(EvaluationInfoAdapter()){
            itemListener=this@EvaluationInfoActivity
            this
        }
        evaluationInfo.apply {
            adapter=infoAdapter
            addDefaultDecoration()
        }
        setTitleText("\"${userName}\"的评测")
        evaluation.getWorksByUserId(userId)
        lifecycleScope.launch {
            evaluation.evalInfoResult.collect{result->
                result.onSuccess {
                    dismissLoad()
                    list.addAll(it)
                    infoAdapter.changeData(it)
                }.onError {
                    it.judgeType().showToast()
                    dismissLoad()
                }.onLoading {
                    showLoad()
                }
            }
        }
    }


    override fun playVideo(url: String) {
        GlobalPlayManager.addUrl(Pair(VoiceStatus.EVAL,url))
    }

    override fun likeItem(item: RankInfoItem) {
        //不宜协程套协程
        if (!GlobalMemory.isLogin()){
            showGoLoginDialog()
            return
        }
        lifecycleScope.launch {
            val local=evaluation.selectSimpleLikeEvaluation(item.id).first()
            if (local.isEmpty()){
                val netResult=evaluation.likeEvaluation(item.id).first()
                ("点赞"+if (netResult.isNotEmpty()) {
                    val index=list.indexOfFirst { it==item }
                    list[index].agreeCount=list[index].agreeCount.inc()
                    infoAdapter.notifyItemChanged(index)
                    val like=LikeEvaluation(GlobalMemory.userInfo.uid,item.id)
                    evaluation.insertSimpleLikeEvaluation(like).first()
                    "成功"
                } else "失败").showToast()
            }else{
                "不能重复点赞".showToast()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        GlobalPlayManager.executeDestroy()
    }
}