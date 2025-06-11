package com.suzhou.concept.adapter

import android.graphics.Color
import com.suzhou.concept.R
import com.suzhou.concept.bean.RankInfoItem
import com.suzhou.concept.databinding.EvaluationInfoItemBinding
import com.suzhou.concept.utils.OnEvaluationInfoOperateListener
import com.suzhou.concept.utils.changeVideoUrl

/**
苏州爱语吧科技有限公司
 */
class EvaluationInfoAdapter :BaseAdapter<RankInfoItem,EvaluationInfoItemBinding>(){
    lateinit var itemListener: OnEvaluationInfoOperateListener
    override fun EvaluationInfoItemBinding.onBindViewHolder(bean: RankInfoItem, position: Int) {
        item=bean
        evaluationType.apply {
            when(bean.shuoshuotype){
                2-> {
                    setBackgroundResource(R.drawable.rank_border)
                    setTextColor(Color.WHITE)
                }
                4-> {
                    setBackgroundResource(R.drawable.border)
                    setTextColor(Color.BLACK)
                }
            }
        }

        //播放按钮
        playEvaluation.setOnClickListener {
            itemListener.playVideo(bean.ShuoShuo.changeVideoUrl())
        }

        //点赞按钮
        likeImgEvaluation.setOnClickListener {
            itemListener.likeItem(bean)
        }
    }
}