package com.suzhou.concept.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.suzhou.concept.lil.util.BigDecimalUtil
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.changeTimeToInt
import java.math.BigDecimal

data class EvaluationSentenceList(
    val size:Int=0,
    val title: Title,
    val data:List<EvaluationSentenceItem>
)

data class Title(val title_cn: String, val title: String)

@Entity
data class EvaluationSentenceItem(
    val voaid:String,
    val EndTiming:Float,
    val Paraid:String,
    val IdIndex:Int,
    val Timing:Float,
    @ColumnInfo(defaultValue = "")
    val Sentence_cn:String="",
    var Sentence:String,
    //以下数据不是json数据，是本地逻辑需要
    var showCn: Boolean=false,
    var currentBlue: Boolean = false,
    var showOperate:Boolean=false,
    var fraction:String="",
    var selfVideoUrl:String="",
    var onlyKay: String="",
    var success:Boolean=false,
    var userId:Int
){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0

    fun inflateEmptyValue(){
        fraction=""
        selfVideoUrl=""
        onlyKay=""
        //Kotlin的默认值插入room时也有点**
        userId= GlobalMemory.userInfo.uid
    }

    fun isCurrentSentence(currentPosition:Int)=currentPosition in (changeTimeToInt(Timing.toString())..changeTimeToInt(EndTiming.toString()))


}

//-----------------------------------------------------------

data class EvaluationSentenceResponse(
    val result:Int,
    val message:String,
    val data: EvaluationSentenceData
)
data class EvaluationSentenceData(
    val sentence:String,
    val total_score:Float,
    val scores:Int,
    val URL:String,
    val filepath:String,
    val words:List<EvaluationSentenceDataItem>
){
    val realScopes get()="${scores}分"
}
@Entity
data class EvaluationSentenceDataItem (
    val content:String="",
    val index:Int=0,
    val score:Float=0f,
    /**
     * 以下为新增字段，再次遇到     编译错误
     * New NOT NULL column 'user_pron2' added with no default value specified. Please specify the default value using @ColumnInfo.
     * 之前怎么解决的我居然忘了，，，
     * 解决方案居然是给新增字段“依次”加ColumnInfo注解
     * */
    @ColumnInfo(name = "delete")
    val delete: String="",
    @ColumnInfo(name = "insert")
    val insert: String="",
    @ColumnInfo(name = "pron")
    val pron: String="",
    @ColumnInfo(name = "pron2")
    val pron2: String="",
    @ColumnInfo(name = "substitute_orgi")
    var substitute_orgi: String="",
    @ColumnInfo(name = "substitute_user")
    val substitute_user: String="",
    @ColumnInfo(name = "user_pron")
    val user_pron: String="",
    @ColumnInfo(name = "user_pron2")
    val user_pron2: String="",
    //以下数据不是json数据，是本地逻辑需要
    var userId:Int=0,
    var voaId:Int= 0,
    var onlyKay:String=""
){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0

    val realScope get()="${score}分"
}
//-----------------------------------------------------------
data class RankResponse(
    var message: String="",
    var mycount: Int=0,
    var myid: Int=-1,
    var myimgSrc: String="",
    var myranking: Int=0,
    var myscores: Int=0,
    var result: Int=-1,
    var vip: String="",
    val data:List<RankItem>,
    val noLogin:String="未登录",
    var myname: String=noLogin,
    val totalTest:Int=0,
    val totalRight:Int=0,
    /**
     * 区别测试与口语
     * */
    var testFlag:Boolean=false
){
    fun copyChange(response:RankResponse){
        message=response.message
        mycount=response.mycount
        myid=response.myid
        myimgSrc=response.myimgSrc
        myranking=response.myranking
        myscores=response.myscores
        result=response.result
        vip=response.vip
        myname=response.myname
    }
    fun myRealCount()= if (testFlag){
        "正确数：${totalRight} \n正确率：${averageScore}"
    }else{
        "句子数:$mycount \n平均分:${if (mycount==0) mycount else myscores/mycount}"
    }
    fun myRealScores()= (if (testFlag) "总题数：${totalTest}" else "${myscores}分")

    fun isNotLogin()=(myid==-1)
    fun clear(){
        myranking=0
        myimgSrc=""
        myname=noLogin
        mycount=0
        myscores=0
        myid=-1
    }
    private val averageScore get() =
        if (testFlag){
            if (totalTest!=0){
                val a=(totalRight.toDouble() / totalTest.toDouble())*100
                val b= BigDecimal(a)
                val c=b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                "$c%"
            }else{
                totalTest.toString()
            }
        }else{
            "平均分:${myscores/ mycount}"
        }
}

data class RankItem(
    val count: Int,
    val imgSrc: String,
    val name: String,
    val ranking: Int=0,
    val scores: Int,
    val sort: Int,
    val uid: Int,
    val vip: String,

    val totalTest:Int,
    val totalRight:Int,
){
    fun realCount()= if (scores==0){
        "正确数：${totalRight} \n正确率：${averageScore}"
    }else{
        "句子数:$count \n$averageScore"
    }
    fun realScores()= with(scores){
        (if (this==0) "总题数：${totalTest}" else "${this}分")
    }
    private val averageScore get() =
        if (scores==0){
            if (totalRight == 0 || totalTest == 0){
                "0%"
            }else{
                val a=(totalRight.toDouble() / totalTest.toDouble())*100
                val b= BigDecimalUtil.trans2Double(a)
                "$b%"
            }
        }else{
            "平均分:${scores/ count}"
        }

}
//-----------------------------------------------------------
data class GroupRankResponse(
    val myid: Int,
    val myimgSrc: String,
    val myranking: Int,
    val result: Int,
    val totalTime:Int,
    val totalWord:Int,
    val totalEssay:Int,
    val myname:String,
    val message:String,
    val data:List<GroupRankItem>
){
    fun realCount()="文章数：${totalEssay} \n单词数：${totalWord}"

    /**
     * 保留两位小数
     * */
    fun hourTime() :String{
        val a=((totalTime).toDouble() / 60 / 60)
        val b=BigDecimal(a)
        val c=b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        return c.toString()+"小时"
    }
}

data class GroupRankItem(
    val uid: Int,
    val totalTime: Int,
    val totalWord: Int,
    val name: String,
    val ranking: Int,
    val sort: Int,
    val totalEssay: Int,
    val imgSrc: String
){
    fun realCount()="文章数：${totalEssay} \n单词数：${totalWord}"

    /**
     * 保留两位小数
     * */
    fun hourTime() :String{
        val a=((totalTime).toDouble() / 60 / 60)
        val b=BigDecimal(a)
        val c=b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        return c.toString()+"小时"
    }
}
//-----------------------------------------------------------
data class MergeResponse(
    var URL: String="",
    var message: String="",
    var result: String=""
    //某些天马行空的接口真是让人诟病
){
    fun isSuccess()=message.contains("success")
}
//-----------------------------------------------------------
data class ReleaseResponse(
    val AddScore: Int=-1,
    val Message: String="",
    val ResultCode: String="",
    val ShuoshuoId: Int=-1
){
    fun isNotEmpty()=(Message.isNotEmpty())
}
//-----------------------------------------------------------

data class RankInfoItem(
    val CreateDate: String,
    val ShuoShuo: String,
    val TopicId: Int,
    val againstCount: Int,
    var agreeCount: Int,
    val id: Int,
    val idIndex: Int,
    val paraid: Int,
    val score: Int,
    val shuoshuotype: Int,
    var sentenceZh:String,
    var sentenceEn:String
){
    val realType:String get() = (if (shuoshuotype==4) "合成" else "单句")
    val showSentence:String get() = (if (shuoshuotype==4) "" else sentenceEn)
    val showSentenceCn:String get() = (if (shuoshuotype==4) "合成音频" else sentenceZh)
}
data class RankInfoResponse(
    val count: Int,
    val message: String,
    val result: Boolean,
    val data:List<RankInfoItem>
)

//-----------------------------------------------------------
@Entity
data class LikeEvaluation(val userId:Int, val itemId:Int){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}
