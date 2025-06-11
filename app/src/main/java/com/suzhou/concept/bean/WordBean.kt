package com.suzhou.concept.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.suzhou.concept.utils.GlobalMemory
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
苏州爱语吧科技有限公司
 */
data class WordOptions(
    var index:Int=0,
    var total:Int=1,
    var finished:Int=0,
    var voaId: Int=0
){
//    fun realUnitId()="Unit $index"
    fun realUnitId() = if (GlobalMemory.wordYoung){
        "Unit $index"
    }else{
        "Lesson $index"
    }
    fun getSchedule()="($finished/$total)"
    fun isComplete()=(total==(finished))
}
//--------------------------------------
// TODO: 这里增加一个单词的参数，避免上传单词学习报告时无法找到对应的内容
//data class LinkDataBean (val content:String, val num:Int, val type:String, val col:Int, val row:Int)
data class LinkDataBean (val content:String, val num:Int, val type:String, val col:Int, val row:Int,val word: String)
//--------------------------------------
@Parcelize
@Entity
data class WordItem(
    val audio: String="",
    val def: String="",
    val end_timing: String="",
    val examples: String="",
    val position: Int=-1,
    val pron: String="",
    val sentence: String="",
    val sentence_audio: String="",
    val sentence_cn: String="",
    val timing: String="",
    val voa_id: Int=-1,
    val word: String="",
    //以下数据不是json数据，是本地逻辑需要
    var correct:Boolean=false,
    var userId:Int= GlobalMemory.userInfo.uid,
    var bookId:Int=1,
    var unitId:Int=-1
): Parcelable{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id:Long=0

    fun realPron()="/$pron/"
    fun readUnit()="Unit ${unitId+1}"
}
//--------------------------------------
data class ConceptWordResponse(val size:Int, val data:List<WordItem>)
