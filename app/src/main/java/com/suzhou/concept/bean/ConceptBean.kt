package com.suzhou.concept.bean

import android.os.Parcelable
import androidx.annotation.IntRange
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.suzhou.concept.AppClient
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.logic.SettingType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable
import java.text.DecimalFormat

/**
苏州爱语吧科技有限公司
 */
data class ConceptList(val data: List<ConceptItem>, val size: Int)

@Parcelize
@Entity
data class ConceptItem(
    val text_num: String="",
    val totalTime: Int=-1,
    val titleid: String="",
    val end_time: String="",
    val packageid: Int=-1,
    var title: String="",
    val ownerid: String="",
    val price: String="",
    var voa_id: String="",
    val percentage: String="",
    val title_cn: String="",
    val choice_num: String="",
    var name: String="",
    val categoryid: Int=-1,
    val desc: String="",
    var lesson: String="",
    //以下数据不是json数据，是本地逻辑需要
    @ColumnInfo(name="bookId")
    var bookId:Int=-1,
    @ColumnInfo(name="language")
    var language:String="US",
    @ColumnInfo(name="index", defaultValue = "1")
    var index:Int=1,
    /**
     * 听力进度
     * */
    @IntRange(from = 0, to = 100)
    @ColumnInfo(name="listenProgress", defaultValue = "1")
    var listenProgress:Int=0,
    /**
     * 评测数量
     * */
    @ColumnInfo(name="evalSuccess", defaultValue = "1")
    @IntRange(from = 0, to = 100)
    var evalSuccess:Int=0,

    /**
     * 单选题的成功数（全四册特有，青少版null）
     * */
    @ColumnInfo(name="exerciseRight", defaultValue = "0")
    var exerciseRight:Int=0,

    /**
     * 单词闯关的正确数
     * */
    @ColumnInfo(name="wordRight", defaultValue = "0")
    var wordRight:Int=0,
    /**
     * 单词闯关的总数
     * 奇怪，此列分明没有赋值，但是却有值，莫非是我之前少写了个字段？
     * */
    @ColumnInfo(name="wordNum", defaultValue = "0")
    var wordNum:Int=0
    /**
     * 再次遇到room please specify the default value using @ColumnInfo.
     * 使用@ColumnInfo后额外设置defaultValue才可以，有时候难以捉摸
     * */
) : Parcelable {
    //英文标题显示
    fun showTitleEn():String{
        return title
    }

    fun changeLesson(l: String) {
        lesson = l
    }

    fun realTitle()=(if(GlobalMemory.currentYoung) "" else lesson)+title

    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id:Long=0

    fun isEmpty()=(totalTime==-1||categoryid==-1||text_num.isEmpty()||title_cn.isEmpty())

    fun getShareVoaId() =if (language=="US"||GlobalMemory.currentYoung) voa_id else (voa_id.toInt()/10).toString()

    val lessonId get()=(voa_id.toInt()*(if (language!="US")10 else 1)).toString()


    fun getYoungVideo()="http://${OtherUtils.staticStr}${OtherUtils.iyuba_cn}/sounds/voa/sentence/202005/${voa_id}/${voa_id}.mp3"


    /**
     * bookId对应青少版的series
     * */
    fun judgeYoungSeries() = when (bookId) {
        278 -> 1
        279 -> 2
        280 -> 3
        281 -> 4
        282, 283 -> 5
        else -> 5
    }.toFloat()

    /**
     *
     * */
    @IgnoredOnParcel
    @Ignore
    var youngChild=BookItemChild()
}


data class LanguageType(val language: String="US", val bookId: Int=1) : Serializable {
    fun isUS() = (language == "US")
    fun isUK() = (language == "UK")

    fun convertLanguage()= if (bookId<=4){
        OtherUtils.convertInt(bookId) + "(" + (if (isUK()) "英音" else "美音")+ ")"
    }else{
        language
    }

//    fun getShareUrl()=if (GlobalMemory.currentYoung){
//        "http://m.${OtherUtils.iyuba_cn}/voaS/playPY.jsp?apptype=${OtherUtils.appType}&id=${AppClient.conceptItem.voa_id}"
//    }else{
//        "http://${OtherUtils.i_user_speech}${OtherUtils.appType}/course?language=${language}&unit=${bookId}&lesson=${AppClient.conceptItem.getShareVoaId()}"
//    }

    // TODO: 2023/10/17 上边的分享链接，这里要求进行修改（李涛-新概念群）
    //https://www.aienglish.com/
    fun getShareUrl()=if (GlobalMemory.currentYoung){
        "http://m.${OtherUtils.iyuba_cn}/voaS/playPY.jsp?apptype=${OtherUtils.appType}&id=${AppClient.conceptItem.voa_id}"
    }else{
        "http://www.aienglish.com/${OtherUtils.appType}/course?language=${language}&unit=${bookId}&lesson=${AppClient.conceptItem.getShareVoaId()}"
    }
}
data class QqResponse(val message:String,val QQ:String,val key:String)

data class SettingItem(val item:SettingType, val cache:Boolean, var value:String,val icon:Int)

data class PdfResponse(val exists:String="",var path:String=""){
    fun realPath(flag:Boolean):String{
        if (flag){
            path=path.replace("ceptpdf","ceptpdf_eg")
        }
        return "http://apps.${OtherUtils.iyuba_cn}/iyuba$path"
    }
    fun isEmpty()=(exists.isEmpty()||path.isEmpty())
}
//--------------------------------------
@Root(name = "data" ,strict = false)
data class PickWord @JvmOverloads constructor(
    @field:Element(name = "result",required = false)
    var result:Int=0,
    @field:Element(name = "key",required = false)
    var key:String="",
    @field:Element(name = "audio",required = false)
    var audio:String="",
    @field:Element(name = "pron",required = false)
    var pron:String="",
    @field:Element(name = "proncode",required = false)
    var proncode:String="",
    @field:Element(name = "def",required = false)
    var def:String="",
    @field:ElementList(entry = "sent",required = false,inline = true)
    @param:ElementList(entry = "sent",required = false,inline = true)
    val sent:List<PickWordItem>,
){
    fun realPron()="[$pron]"
}

@Root(name = "sent" )
data class PickWordItem @JvmOverloads constructor(
    @field:Element(name = "number")
    var number:Int=0,
    @field:Element(name = "orig")
    var orig:String="",
    @field:Element(name = "trans")
    var trans:String=""
){
    fun getRealOrig()=orig.replace("<em>","'").replace("</em>","'")
}
//-------------------------------------------
@Root(name = "response" ,strict = false)
data class StrangenessWord @JvmOverloads constructor(
    @field:Element(name = "counts")
    var counts:Int=0,
    @field:Element(name = "pageNumber")
    var pageNumber:Int=0,
    @field:Element(name = "totalPage")
    var totalPage:Int=0,
    @field:Element(name = "firstPage")
    var firstPage:Int=0,
    @field:Element(name = "prevPage")
    var prevPage:Int=0,
    @field:Element(name = "nextPage")
    var nextPage:Int=0,
    @field:Element(name = "lastPage")
    var lastPage:Int=0,
    @field:ElementList(entry = "row",inline = true,required = false)
    @param:ElementList(entry = "row",inline = true,required = false)
    val row:List<StrangenessWordItem>,
)
@Root(name = "row",strict = false)
data class StrangenessWordItem @JvmOverloads constructor(
    //可能为空值------------>required = false
    @field:Element(name = "Word")
    var Word:String="",
    @field:Element(name = "createDate")
    var createDate:String="",
    @field:Element(name = "Audio",required = false)
    var Audio:String="",
    @field:Element(name = "Pron",required = false)
    var Pron:String="",
    @field:Element(name = "Def")
    var Def:String=""
){
    fun realPron()="[$Pron]"
}

//-------------------------------------------
@Root(name = "response" ,strict = false)
data class WordStatus @JvmOverloads constructor(
    @field:Element(name = "result",required = false)
    var result:Int=1,
    @field:Element(name = "word",required = false)
    var word:String=""
)
//-------------------------------------------
@Entity
data class LocalCollect(var word:String="",var isCollect:Boolean=false){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}
//-------------------------------------------
data class CustomerServiceItem(
    val editor: Int=0,
    val manager: Int=0,
    val technician: Int=0
)
data class CustomerResponse(
    val data: List<CustomerServiceItem>,
    val result: Int
)
//-------------------------------------------
data class CorrectSoundResponse(
    val audio: String="",
    val def: String="",
    val delete_id: List<List<Int>>,
    val insert_id: List<List<Int>>,
    val key: String,
    val match_idx: List<List<Int>>,
    val ori_pron: String="",
    val pron: String,
    val proncode: String,
    val result: Int,
    val sent: List<PickWordItem>,
    val substitute_id: List<List<Int>>,
    val user_pron: String=""
){
    val realOri get() = "[$ori_pron]"
    val realUserPron get() = "[$user_pron]"
}


data class ShareContentResponse(
    val addcredit: String="",
    val result: Int=0,
    val totalcredit: String="",
    val message:String=""
)

data class StudyRecordResponse(
    val result: String,
    @SerializedName(value = "jifen")
    val scores: String,
    val message: String,

    //新增的奖励信息
    val reward:String,
    val rewardMessage:String
)


data class SignResponse(
    val ranking: String,
    val result: String,
    val sentence: String,
    val shareId: String,
    val totalDays: String,
    val totalDaysTime: String,
    val totalTime: String,
    val totalUser: String,
    val totalWord: String,
    val totalWords: String
): Serializable {
    val qrIconUrl get()="http://app.${OtherUtils.iyuba_cn}/share.jsp?uid=${GlobalMemory.userInfo.uid}&appId=${AppClient.appId}&shareId=${shareId}"

    fun getOverPercent(): String = "超越了" + if (totalUser.isNotEmpty() && ranking.isNotEmpty()) {
        val carry = 1 - ranking.toDouble() / totalUser.toDouble()
        DecimalFormat("0.00").format(carry).replace("0.", "")
    } else {
        "0"
    } + "%的同学"

    fun getTodayWords()="今日学习单词${totalWord}个"
    fun getStudyDays()="已累计学习${totalDays}天"
}

