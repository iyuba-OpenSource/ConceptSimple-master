package com.suzhou.concept.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.suzhou.concept.AppClient
import com.suzhou.concept.utils.GlobalMemory
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.*

/**
苏州爱语吧科技有限公司
@Date:  2023/2/7
@Author:  han rong cheng
 */
data class MultipleItem(
    val answer: String,
    val choice_A: String,
    val choice_B: String,
    val choice_C: String,
    val choice_D: String,
    val index_id: String,
    val question: String,
    val voa_id: String
){
    fun toSelectArray()= arrayOf(choice_A,choice_B,choice_C,choice_D)
}

data class StructureItem(
    val answer: String,
    val column: String,
    val desc_CH: String,
    val desc_EN: String,
    val id: String,
    val note: String,
    val number: String,
    val ques_num: String,
    val type: String,
    var userAnswer:String=""
){
    fun realAnswer() =(if (userAnswer==null) "" else userAnswer)
}

data class ExerciseResponse(
    val MultipleChoice:List<MultipleItem>,
    val VoaStructureExercise:List<StructureItem>,
    val SizeVoaDiffcultyExercise:Int,
    val SizeVoaStructureExercise:Int,
    val SizeMultipleChoice:Int,
//    val VoaDiffcultyExercise:List<?>,
)

@Parcelize
data class ExerciseRecord(
    //登录用户id
    val uid:Int=GlobalMemory.userInfo.uid,
    //voaid
    var LessonId:String="",
    //题号
    var TestNumber:String="",
    //这道题做题开始时间
    var BeginTime:String="",
    //用户的答案
    var UserAnswer:String="",
    //正确答案
    var RightAnswer:String="",
    //答对了1,答错了0
    var AnswerResut:String="",
    //这道题做题结束时间
    var TestTime:String="",
    val AppName:String= AppClient.appName,
    /**
     * 问题
     * */
    var title:String="",
): Parcelable {
    fun toJsonObject()= with(JSONObject()){
        put("uid",uid)
        put("LessonId",LessonId)
        put("TestNumber",TestNumber)
        put("BeginTime",BeginTime)
        put("UserAnswer",UserAnswer)
        put("RightAnswer",RightAnswer)
        put("AnswerResut",AnswerResut)
        put("TestTime",TestTime)
        put("AppName",AppName)
        this
    }

    fun inflateRight(){
        AnswerResut=(if (UserAnswer==RightAnswer) "1" else "0")
    }
}

data class SomeResponse<T>(
    val result:Int,
    val message:String,
    val data:List<T>
)

data class TestRecordItem(
    val AppId: String,
    val AppName: String,
    val BeginTime: String,
    val LessonId: String,
    var RightAnswer: String,
    val Score: Int,
    val TestNumber: String,
    val TestTime: String,
    val TestWords: String,
    val UpdateTime: String,
    var UserAnswer: String,
    val testindex: String
){
    fun changeUpperCase(flag:Boolean=false){
        RightAnswer= (if (flag) RightAnswer.lowercase() else RightAnswer.uppercase())
        UserAnswer= (if (flag) UserAnswer.lowercase() else UserAnswer.uppercase())

    }
}

/**
 * 本地存储重做标记
 * */
@Entity
data class ReDoBean(
    val testNumber:String,
    val reDidMultiple:Boolean,
    val reDidStructure:Boolean
){
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}