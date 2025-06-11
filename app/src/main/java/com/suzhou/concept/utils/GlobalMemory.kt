package com.suzhou.concept.utils

import com.iyuba.module.user.IyuUserManager
import com.suzhou.concept.AppClient
import com.suzhou.concept.bean.ConceptItem
import com.suzhou.concept.bean.LanguageType
import com.suzhou.concept.bean.LoginResponse
import com.suzhou.concept.bean.RankResponse

/**
苏州爱语吧科技有限公司
@Date:  2022/8/30
@Author:  han rong cheng

全局内存数据的单例类
 */
object GlobalMemory {

    /**
     * 本地用户信息
     * */
    var userInfo = LoginResponse()

    /**
     * 秒验登录注册/普通登录注册时调用
     * */
    fun inflateLoginInfo(info: LoginResponse) {
        userInfo = info
        IyuUserManager.getInstance().currentUser = info.convertOtherUser()
    }

    //    fun isLogin() = userInfo.isSuccess()
    fun isLogin():Boolean{
        return userInfo != null && userInfo.uid > 0
    }

    /**
     * 退出登录/注销时调用
     * */
    fun clearUserInfo() {
        IyuUserManager.getInstance().logout()
        userInfo = LoginResponse()
    }

    /**
     * 美/英音 第X册
     * */
    var currentLanguage = LanguageType()


    /**
     * 当播放音频的fragment被Destroy时，记录下currentPosition
     * */
    var currentPosition = 0

    /**
     * FineListenFragment的是否在播放
     * */
    var fineListenIsPlay = false

    /**
     * FineListenFragment的是否被创建
     * */
    var fineListenCreate = false

    /**
     * 提交听力记录时的后台播放开始时间
     * */
    var submitStartTime = ""

    /**
     * 当前是否为青少版(课文)
     * */
    var currentYoung = false

    /**
     * 当前是否为青少版(单词)
     * */
    var wordYoung = false

    /**
     * 下下下下策，临时性的增加一个是否在评测界面播放原句子的变量
     * */
    var isPlayingOriginal = false
    var isRecording = false
    var isPlayingSelf = false

    fun getVideoUrl(voaId: Int) = when {
        currentLanguage.isUK() -> "http://static2.${OtherUtils.iyuba_cn}/${OtherUtils.appType}/british/${AppClient.curShowBookId}/${AppClient.curShowBookId}_${(voaId / 10) % 1000}.mp3"
        currentLanguage.isUS() -> "http://static2.${OtherUtils.iyuba_cn}/${OtherUtils.appType}/${voaId / 1000}_${voaId - voaId / 1000 * 1000}.mp3"
//        currentYoung -> AppClient.conceptItem.getYoungVideo()
        currentYoung -> "http://${OtherUtils.staticStr}${OtherUtils.iyuba_cn}/sounds/voa/sentence/202005/${voaId}/${voaId}.mp3"
        else -> ""
    }

    var startRankInfo = false


    /**
     * 口语秀部分多个activity需要的XX?
     * */
    var speakingItem = ConceptItem()

    /**
     * 区分华为与小米渠道
     */
    var huaweiChannelFlag = false

    val topicRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTopicRanking.jsp"
    val testRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTestRanking.jsp"
    val studyRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getStudyRanking.jsp"


    var rankTopicResponse = RankResponse(data = emptyList())

    var wordPagingEmpty = false

    const val groupId = 10113
    const val groupName = "新概念英语官方群"


    //当前记录的选中的类型和书籍id
    var curSelectLanguage: String = ""
    var curSelectBookId: Int = 0

    //包名信息
    val package_suzhou = "com.suzhou.concept"
    val package_concept2 = "com.iyuba.concept2"
}