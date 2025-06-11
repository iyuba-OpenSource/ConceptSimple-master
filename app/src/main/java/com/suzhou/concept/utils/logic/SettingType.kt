package com.suzhou.concept.utils.logic

import com.suzhou.concept.utils.GlobalMemory

/**
苏州爱语吧科技有限公司
@Date:  2023/2/10
@Author:  han rong cheng
 */
enum class SettingType(val type:String) {
    TiredSdkInfo("第三方信息共享清单"),
    PersonalInfo("个人信息收集清单"),
    PRIVACY("隐私政策"),
    PROTOCOL("使用协议"),
    QQ_GROUP("QQ交流群"),
    CACHE("清除缓存"),
    MINE_DUB("我的配音"),
    SHOP_MARK("购买记录"),
    STUDY_REPORT("学习报告"),
    ABOUT("关于"),
    OFFICIAL_GROUP(GlobalMemory.groupName),
}