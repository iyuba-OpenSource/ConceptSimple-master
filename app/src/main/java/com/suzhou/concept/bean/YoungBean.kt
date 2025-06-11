package com.suzhou.concept.bean

import android.os.Parcelable
import androidx.annotation.IntRange
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.suzhou.concept.AppClient
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.changeTimeToLong
import kotlinx.parcelize.Parcelize
import okhttp3.MediaType
import okhttp3.RequestBody
import java.math.RoundingMode
import java.text.NumberFormat

/**
苏州爱语吧科技有限公司
@Date:  2022/10/15
@Author:  han rong cheng
 */

/**
 * 青少版列表
 * */
@Entity
data class YoungItem(
    //系列ID
    val Category: String,
    val CreateTime: String,
    val DescCn: String,
    val HotFlg: String,
    //书本id,即bookid，对应全四册的1，2，3，4
    val Id: String,
    val KeyWords: String,
    //对应书本的课文数量
    val SeriesCount: String,
    val SeriesName: String,
    val UpdateTime: String,
    //是否有对应微课
    val haveMicro: String,
    //是否有对应视频
    val isVideo: String,
    //封面图
    val pic: String,
    val version: String
){
    fun toLanguage()=LanguageType(SeriesName,Id.toInt())

    @PrimaryKey(autoGenerate = true)
    var tableId:Long=0

    fun showBookName():String{
        if (SeriesName.startsWith("新概念英语")){
            return SeriesName.replace("新概念英语","")
        }else{
            return SeriesName
        }
    }
}

data class YoungList<T>(
    val result:Int,
    val total:Int,
    val data:List<T>
)

@Entity
data class BookItem(
    //系列Id
    val Category: String,
    val CategoryName: String,
    val CreatTime: String,
    val DescCn: String,
    val Flag: String,
    val HotFlg: String,
    //对应voaid
    val Id: String,
    val IntroDesc: String,
    val Keyword: String,
    val Pagetitle: String,
    //封面图
    val Pic: String,
    val PublishTime: String,
    //浏览量
    val ReadCount: String,
    //返回的配音口语秀的纯背景音
    val Sound: String,
    //总句子数
    val Texts: String,
    //英文标题
    val Title: String,
    //中文标题
    val Title_cn: String,
    val Url: String,
    val categoryid: String,
    val classid: String,
    //是否有点读
    val clickRead: String,
    val desc: String,
    val havePractice: String,
    val listenPercentage: String,
    val name: String,
    val outlineid: String,
    val ownerid: String,
    val packageid: String,
    val percentage: String,
    val price: String,
    //书本id,即bookid，对应全四册的1，2，3，4
    val series: String,
    val totalTime: String,
    //口语秀视频的后半部分
    val video: String,
    /**
     * 单词闯关的总数
     * */
    val wordNum: Int,
    //
    var index:Int,

    //以下数据不是json数据，是本地逻辑需要
    var isRelease:Boolean=false,
    var isCollect:Boolean=false,
    var isDownload:Boolean=false,
    /**
     * 听力进度
     * */
    @IntRange(from = 0, to = 100)
    var listenProgress:Int=0,
    /**
     * 评测数量
     * */
    @IntRange(from = 0, to = 100)
    var evalSuccess:Int=0,
    /**
     * 单词闯关的正确数
     * */
    var wordRight:Int=0,
    var userId:Int
){
    fun toConceptItem()= with(ConceptItem(
        title = Title,
        title_cn = Title_cn,
        lesson = index.toString(),
        voa_id = Id,
        bookId =series.toInt(),
        index = index,
        name = DescCn,
        listenProgress = listenProgress,
        text_num = Texts,
        evalSuccess = evalSuccess,
        wordRight = wordRight,
        wordNum = wordNum
    )){
//        youngPic=Pic
//        youngVideoPath=video
        //是否可以再手动写个类中类？
        youngChild= toBookItemChild()
        this
    }

    @PrimaryKey(autoGenerate = true)
    var tableId:Long=0

    private fun toBookItemChild()=BookItemChild(Pic,video,Sound,isRelease,isCollect, isDownload, userId)
}

/**
 * 青少版的是否发布,是否收藏,是否下载,用户id
 * */
@Parcelize
data class BookItemChild(
    var youngPic:String="",
    //  /video/voa/321/321001.mp4
    var youngVideoPath:String="",
    //http://staticvip.iyuba.cn/sounds/voa/202005/321003.mp3
    var youngBackVoice:String="",
    var isRelease:Boolean=false,
    var isCollect:Boolean=false,
    var isDownload:Boolean=false,
    var userId:Int=0
): Parcelable{
    val youngBackVoiceEndPath get() =with(youngBackVoice){
        val start=indexOf(OtherUtils.iyuba_cn)+ OtherUtils.iyuba_cn.length
        substring(start,length)
    }
}


@Entity
data class YoungSentenceItem(
    val EndTiming: String="0",
    val End_x: String="",
    val End_y: String="",
    val IdIndex: String="",
    val ImgPath: String="",
    val ImgWords: String="",
    val ParaId: String="",
    var Sentence: String="",
    val Start_x: String="",
    val Start_y: String="",
    val Timing: String="0",
    var sentence_cn: String="",
    @ColumnInfo(defaultValue = "1")
    var voaId:String="",
    //以下数据不是json数据，是本地逻辑需要
    var showCn: Boolean=false,
    var currentBlue: Boolean = false,
    var showOperate:Boolean=false,
    var fraction:String="",
    var selfVideoUrl:String="",
    var onlyKay: String="",
    var success:Boolean=false,
    var userId:Int=0
){
    /**
     * 此处注意转换规则
     * */
    fun toEvaluationSentenceItem() = EvaluationSentenceItem(
        voaId,
        EndTiming.toFloat(),
        ParaId,
        IdIndex.toInt(),
        Timing.toFloat(),
        sentence_cn,
        Sentence,
        userId = GlobalMemory.userInfo.uid,
        showCn = showCn,
        currentBlue = currentBlue,
        showOperate = showOperate,
        fraction = fraction,
        selfVideoUrl = selfVideoUrl,
        onlyKay = onlyKay,
        success = success
    )

    fun inflateEmptyValue(){
        fraction=""
        selfVideoUrl=""
        onlyKay=""
        //Kotlin的默认值插入room时也有点**
        userId= GlobalMemory.userInfo.uid
    }

    @PrimaryKey(autoGenerate = true)
    var id:Long=0

    fun sentenceDuration() = with(NumberFormat.getNumberInstance()){
        maximumFractionDigits=2
        roundingMode=RoundingMode.UP
        StringBuilder().let {
            it.append("时间：")
            it.append(format(EndTiming.toDouble()-Timing.toDouble()))
            it.append(" S")
            it.toString()
        }
    }

    fun inflateDefault(desc:String){
        Sentence=desc
        sentence_cn=desc
    }

    @Ignore
    val wordList= mutableListOf<EvaluationSentenceDataItem>()

    fun getStart()= changeTimeToLong(Timing)
    fun getEnd()= changeTimeToLong(EndTiming)
    fun getDuration()=getEnd()-getStart()
}

data class YoungSentenceList(
    val Images: String,
    val words:List<String>,
    val total: String,
    val voatext:List<YoungSentenceItem>
)

@Entity
data class YoungWordItem(
    val Sentence: String,
    val Sentence_audio: String,
    val Sentence_cn: String,
    @ColumnInfo(name = "audio", defaultValue = "1")
    var audio: String,

    val book_id: String,
    val def: String,
    val examples: String,
    val idindex: String,
    val pic_url: String,
    val position: String,
    val pron: String,
    //终于有把单词分成若干组的unit_id了！！！
    val unit_id: String,
    val updateTime: String,
    val version: String,
    val videoUrl: String,
    val voa_id: String,
    val word: String,
    //以下数据不是json数据，是本地逻辑需要
    var correct:Boolean=false,
    var userId:Int= GlobalMemory.userInfo.uid,

    /**
     * 此列无用
     * */
    @Deprecated("和接口返回的book_id功能重复，room删除列又不是很方便，所以废弃", replaceWith = ReplaceWith("book_id"))
    @ColumnInfo(name = "bookId", defaultValue = "")
    val bookId:String=""
){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0

    fun toWordItem()=WordItem(
        audio = audio,
        def = def,
        examples = examples,
        correct = correct,
        userId = userId,
        bookId = book_id.toInt(),
        unitId = unit_id.toInt(),
        position = position.toInt(),
        pron = pron,
        sentence = Sentence,
        sentence_audio = Sentence_audio,
        sentence_cn = Sentence_cn,
        word = word,
        voa_id = voa_id.toInt()
    )

    fun inflateAudio(){
        if (audio==null){
            audio=""
        }
    }
}

data class YoungWordList(
    val result:Int,
    val data:List<YoungWordItem>
)

@kotlinx.android.parcel.Parcelize
data class YoungRankItem(
    val CreateDate: String,
    val ImgSrc: String,
    val ShuoShuo: String,
    val ShuoShuoType: String,
    val TopicCategory: String,
    val UserName: String,
    val Userid: String,
    val againstCount: String,
    var agreeCount: String,
    val backId: Int,
    val backList: String,
    val id: String,
    val idIndex: String,
    val image: String,
    val paraid: String,
    var score: String,
    val title: String,
    val topicid: String,
    val videoUrl: String,
    val vip: String
): Parcelable
{
    val realVideoUrl get() = with(StringBuilder()){
        append("http://")
//         TODO: 这里貌似存在问题，使用链接替换下
        append(OtherUtils.i_user_speech)
//        append(OtherUtils.user_speech)
        append(videoUrl)
        toString()
    }

}

data class YoungRankResponse(
    val AddScore: Int,
    val Counts: Int,
    val FirstPage: Int,
    val LastPage: Int,
    val Message: String,
    val NextPage: Int,
    val PageNumber: Int,
    val PrevPage: Int,
    val ResultCode: String,
    val TotalPage: Int,
    val data:List<YoungRankItem>
)

@Entity
data class LikeYoung(val userId:Int, val otherId:Int){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}

data class LikeYoungResponse(val ResultCode:String="",val Message:String=""){
    fun isEmpty()=(ResultCode.isEmpty()||Message.isEmpty())
}


data class MineReleaseItem(
    val CreateDate: String,
    val ImgSrc: String,
    val Pic: String,
    val Title: String,
    val Title_cn: String,
    val TopicId: Int,
    val UserId: Int,
    val UserName: String,
    val agreeCount: Int,
    val id: Int,
    val score: Int,
    val type: String,
    val videoUrl: String,
){
    fun toYoungRankItem() = YoungRankItem(
        CreateDate,
        ImgSrc,
        "",
        "",
        "",
        UserName,
        UserId.toString(),
        "0",
        agreeCount.toString(),
        1,
        "1",
        id.toString(),
        "1",
        "",
        "",
        score.toString(),
        Title,
        "1",
        videoUrl,
        "0"
    )
}

data class MineReleaseResponse(
    val result:Boolean,
    val data:List<MineReleaseItem>,
    val count:Int,
    val message:String
)

data class PostItem(
    val score: Int,
    val sound: String,
    val username: String,
    val voaid: Int,
    val wavList: List<WavListItem>,
    //新概念青少 固定 321
    val category: Int =321,
    val appName: String=AppClient.appName,
    val flag: Int = 1,
    val format: String="json",
    val paraId: Int = 0,
    val idIndex: Int = 0,
    val platform: String="android",
    val shuoshuotype: Int = 3,
    val topic: String=AppClient.appName
){
    fun toBody(): RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(this))
}

@Parcelize
data class WavListItem(
    var URL: String,
    val beginTime: Float,
    val duration: Float,
    val endTime: Float,
    val index: Int
):Parcelable

data class MergeDubResponse(
    val AddScore:Int,
    val ShuoShuoId:Int,
    val Message:String,
    val ResultCode:String
){
    fun isError()=(Message.isEmpty()||ResultCode.isEmpty())
}