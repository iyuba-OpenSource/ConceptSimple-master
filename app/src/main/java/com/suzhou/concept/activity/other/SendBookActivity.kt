package com.suzhou.concept.activity.other

import android.content.Intent
import android.net.Uri
import android.view.View
import com.suzhou.concept.R
import com.suzhou.concept.activity.BaseActivity
import com.suzhou.concept.databinding.ActivitySendBookBinding
import com.suzhou.concept.utils.judgeBookType
import com.suzhou.concept.utils.loadLargeImage

class SendBookActivity : BaseActivity<ActivitySendBookBinding>() ,View.OnClickListener{
    companion object{
        const val qqCode=2111356785
        var sendType=0
    }
    override fun ActivitySendBookBinding.initBinding() {
        binding.goEvaluate.setOnClickListener(this@SendBookActivity)
        binding.notNeededYet.setOnClickListener(this@SendBookActivity)
        when(sendType){
            0->{
                binding.longImg.loadLargeImage(R.drawable.bg_send_book_image_novel)
                setTitleText("送英文名著啦")
                binding.sendBookDesc.judgeBookType(activity = this@SendBookActivity)
            }
            else->{
                setTitleText("送考试用书啦")
                binding.longImg.loadLargeImage(R.drawable.bg_send_book_image)
                binding.sendBookDesc.judgeBookType("书","一本由爱语吧名师团队编写的电子书",this@SendBookActivity)
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.go_evaluate->{
                val i = Intent().apply {
                    data = Uri.parse("market://details?id=$packageName")
                    action = Intent.ACTION_VIEW
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(i)
            }
            R.id.not_needed_yet->finish()
        }
    }

}