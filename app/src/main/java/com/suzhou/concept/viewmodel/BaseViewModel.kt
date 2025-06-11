package com.suzhou.concept.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import com.suzhou.concept.AppClient
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils
import java.util.*

/**
苏州爱语吧科技有限公司
@Date:  2022/9/20
@Author:  han rong cheng
 */
abstract class BaseViewModel :ViewModel(){
    protected val dataMap= mutableMapOf<String,String>()
    protected val deviceName= Build.MANUFACTURER.trim().lowercase(Locale.getDefault())



    protected fun MutableMap<String,String>.putAppId(key:String="appid")=put(key,AppClient.appId.toString())
    protected fun MutableMap<String,String>.putUserId(key:String="userId")=put(key,GlobalMemory.userInfo.uid.toString())
    protected fun MutableMap<String,String>.putDeviceId(key:String="DeviceId")=put(key,deviceName)
    //ConceptViewModel
    protected val wordUrl="http://word.${OtherUtils.iyuba_cn}/words/"
    protected val customerUrl="http://${OtherUtils.i_user_speech}japanapi/getJpQQ.jsp"
    protected val qqUrl= "http://m.${OtherUtils.iyuba_cn}/m_login/getQQGroup.jsp"
    protected fun getPdfUrl(isYoung:Boolean)=if (isYoung){
        "https://apps.${OtherUtils.iyuba_cn}/iyuba/getVoapdfFile_new.jsp"
    }else{
        "http://app.${OtherUtils.iyuba_cn}/iyuba/getConceptPdfFile.jsp"
    }
    protected val shareContentUrl="http://api.${OtherUtils.iyuba_cn}/credits/updateScore.jsp"
    protected val submitRecordUrl="http://daxue.${OtherUtils.iyuba_cn}/ecollege/updateStudyRecordNew.jsp"
    //EvaluationViewModel
    protected val evaluationUrl="http://${OtherUtils.i_user_speech}test/ai/"
    protected val pagingEvalUrl="http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTopicRanking.jsp"
    protected val releaseEvalUrl="http://voa.${OtherUtils.iyuba_cn}/voa/"
    protected val releaseSimple="http://voa.${OtherUtils.iyuba_cn}/voa/UnicomApi"
    protected val correctSoundUrl="http://word.${OtherUtils.iyuba_cn}/words/apiWordAi.jsp"
    protected val mergeUrl="http://${OtherUtils.i_user_speech}test/merge/"
    //UserActionViewModel
    protected val uploadPhotoUrl="http://api.${OtherUtils.iyuba_com}/v2/avatar?uid=${GlobalMemory.userInfo.uid}"
    protected val payUrl = "http://vip.${OtherUtils.iyuba_cn}/"
    protected val operateUserUrl="http://api.${OtherUtils.iyuba_com}/v2/api.iyuba"
    protected val secondVerifyUrl="http://api.${OtherUtils.iyuba_com}/v2/api.iyuba"
    protected val signUrl="http://daxue.${OtherUtils.iyuba_cn}/ecollege/getMyTime.jsp"
    //YoungViewModel
    protected val youngListUrl="http://apps.${OtherUtils.iyuba_cn}/iyuba/getTitleBySeries.jsp"
    protected val sentenceUrl="http://apps.${OtherUtils.iyuba_cn}/iyuba/textExamApi.jsp"
    protected val speakingSentenceUrl="http://${OtherUtils.i_user_speech}test/eval/"
    protected val mineReleasedUrl="http://voa.${OtherUtils.iyuba_cn}/voa/getTalkShowOtherWorks.jsp"
    //WordViewModel
    protected val youngWordUrl="http://apps.${OtherUtils.iyuba_cn}/iyuba/getWordByUnit.jsp"
    protected val adUrl="http://app.${OtherUtils.iyuba_cn}/dev/getAdEntryAll.jsp"
    //ExerciseViewModel
    protected val getExerciseUrl="http://apps.${OtherUtils.iyuba_cn}/concept/getConceptExercise.jsp"
    protected val exerciseUrl="http://daxue.${OtherUtils.iyuba_cn}/ecollege/updateTestRecordNew.jsp"
    protected val testRecordUrl="http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTestRecordDetail.jsp"


    /**
     * 东八区
     * */
    fun getDayDistance():Long{
        val old=Calendar.getInstance(Locale.CHINA)
        old.set(1970,0,1,0,0,0)
        val now=Calendar.getInstance(Locale.CHINA)
        val intervalMilli=now.timeInMillis-old.timeInMillis
        return intervalMilli/(24*60*60*1000)
    }
}