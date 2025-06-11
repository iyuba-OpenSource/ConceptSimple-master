package com.suzhou.concept.bean

import androidx.annotation.DrawableRes
import com.iyuba.module.user.User
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.changeString
import com.suzhou.concept.utils.logic.OperateType
import com.suzhou.concept.utils.safeToFloat
import com.suzhou.concept.utils.safeToInt
import com.suzhou.concept.utils.safeToLong
import com.suzhou.concept.utils.timeStampDate
import com.suzhou.concept.utils.toMd5

/**
苏州爱语吧科技有限公司
 */
data class LoginBean(
    var username: String = "",
    var password: String = "",
    val token: String = "",
    val format: String = "json",
    var protocol: String = "11001",
    var isAuto: Boolean = false
) {
    fun getSign() = (protocol + username + password.toMd5() + "iyubaV2").toMd5()
    fun isUserNameEmpty() = ((username.isEmpty()) || username.length !in (3..15))
    fun isModifyEmpty() = ((password.isEmpty()) || password.length !in (6..15))
    fun isPasswordEmpty() = ((password.isEmpty()) || password.length !in (6..15))
}

data class LoginResponse(
    //爱语币
    val Amount: String = "0",
    val mobile: String = "",
    val message: String = "",
    val result: String = "",
    val uid: Int = 0,
    val isteacher: String = "",
    var expireTime: String = "0",
    //钱包单位（分）
    val money: String = "",
    //积分
    val credits: Int = 0,
    val jiFen: Int = 0,
    val nickname: String = "",
    var vipStatus: Int = -1,
    var imgSrc: String = "",
    val email: String = "",
    var username: String = "未登录",
    var self: SelfResponse= SelfResponse()
) {
    //显示名称(有昵称显示昵称，没有昵称显示名称)
    fun showName():String{
        return username
    }
    fun isNotRegistered()=(result!="101")
    fun isEmpty() = (uid==0)
    fun isSuccess()=(message=="success")
    fun getSign() = ("10012" + uid + "iyubaV2").toMd5()
    fun isVip()=(getUserType()!=self.generalUser)
    fun getUserType() = if (self.isVip()) {
        self.getUserType()
    }else if (isEmpty()||!self.judgeStatus(vipStatus)){
        self.generalUser
    }else {
        val time = "${expireTime}000".safeToLong()
        if (System.currentTimeMillis() > time) {
            self.generalUser
        } else {
            "会员到期:${time.timeStampDate("yyyy-MM-dd")}"
        }
    }

    //设置头像的url
    fun getUserPic():String = if (isEmpty()){
        ""
    }else{
        "http://api."+OtherUtils.iyuba_com+"/v2/api.iyuba?protocol=10005&size=middle&timestamp="+ System.currentTimeMillis()+"&uid="+uid
    }

//    val showMoney: String get()="钱包：${money.safeToFloat().changeString()}元"
//    val showCredits: String get() {
//        return "积分：$credits"
//    }

    val realCredits get()= if (money.isNotEmpty()) (money.toInt()/100).toString() else "0.00"

    fun convertOtherUser()= with(User()){
        this.uid=this@LoginResponse.uid
        this.name=this@LoginResponse.username
        this.nickname=this@LoginResponse.nickname
        this.email = this@LoginResponse.email
        this.imgUrl = this@LoginResponse.imgSrc
        this.mobile = this@LoginResponse.mobile
        this.credit = this@LoginResponse.credits
        this.vipStatus = this@LoginResponse.vipStatus.toString()
        this.vipExpireTime = this@LoginResponse.expireTime.safeToLong()
        this.iyubiAmount = this@LoginResponse.Amount.safeToInt()
        this.money = this@LoginResponse.money.safeToInt()
        this
    }

    fun getErrorType() = when (result) {
        "0" -> "服务器异常"
        "00" -> "sign拼接错误"
        "000" -> "用户名或密码或电子邮件丢失"
        "102" -> "用户名不存在"
        "103" -> "密码不正确"
        "104" -> "请输入正确的电子邮件"
        "110" -> "服务器错误"
        "112" -> "用户名已存在"
        "113" -> "邮箱已被注册"
        "114" -> "用户名长度错误"
        "115" -> "手机号已被注册"
        else -> message
    }


}

data class ModifyUserNameResponse(
    val result: String = "",
    val uid: Int = 0,
    val message: String = "",
    val username: String = "",
) {
    fun isSuccess() = (message == "OK")
}

data class LogoutUserResponse(val result: String, val message: String) {
    fun isSuccess() = (result == "101" && message == "success")
}

data class UploadPhotoResponse(
    val status: Int,
    val jiFen: Int,
    val middleUrl: String="",
    val smallUrl: String="",
    val bigUrl: String="",
    val message: String=""
) {
    fun isSuccess() = (middleUrl.isNotEmpty() && smallUrl.isNotEmpty() && bigUrl.isNotEmpty())
}
data class RegisterBean(var phone:String="",var verifyCode:String="",var agree:Boolean=false){
    fun isEmpty()=(phone.isEmpty()||verifyCode.isEmpty())
}
//----------------------------------------------
data class SelfResponse(
    val albums:String="",
    val gender:String="",
    val distance:String="",
    val blogs:String="",
    val middle_url:String="",
    val contribute:String="",
    val shengwang:String="",
    val bio:String="",
    val posts:String="",
    val relation:String="",
    val result:String="",
    val isteacher:String="",
    val credits:String="0",
    val nickname:String="",
    val email:String="",
    val views:String="",
    val amount:String="",
    val follower:String="",
    val mobile:String="",
    val allThumbUp:String="",
    val icoins:String="",
    val message:String="",
    val friends:String="",
    val doings:String="",
    val expireTime:String="",
    val money:String="",
    val following:String="",
    val sharings:String="",
    var vipStatus:Int=-1,
    val username:String="",
    val generalUser:String="普通用户"
){
    fun realMiddleUrl()="http://static1.${OtherUtils.iyuba_cn}/uc_server/$middle_url"
    fun isVip()=(getUserType()!=generalUser)
    fun judgeStatus(vip:Int)=(vip>0)
    fun getUserType() = if (judgeStatus(vipStatus)) {
        val time = "${expireTime}000".toLong()
        if (System.currentTimeMillis() > time) {
            generalUser
        } else {
            "会员到期:${time.timeStampDate("yyyy-MM-dd")}"
        }
    } else {
        generalUser
    }

    val realDollar: String get()="钱包：${money.safeToFloat().changeString()}元"
    val realCredits: String get() {
        return "积分：$credits"
    }
}
//----------------------------------------------
data class VipBean(val id: Int, val price: Int, val description: String) {
    fun realDescription() = "$description:￥$price"
}

//----------------------------------------------
data class RequestPayResponse(
    val alipayTradeStr:String,
    val result:Int,
    val message:String
){
    fun isSuccess()=(result==200&&message=="Success"&&alipayTradeStr.isNotEmpty())
}
//----------------------------------------------

data class PayResponse(val msg:String="", val code:Int=0){
    fun isSuccess()=(msg=="Success"&&code==200)
}
//----------------------------------------------

data class SelectTitle(val position:Int=0,val type:String="人教版")

data class BuyCurrency(
    val price: Float,
    @DrawableRes
    val image: Int,
    val iyuCount:Int
){
    val orderInfo get() = "购买${iyuCount}爱语币"
}

data class AllVipItem(
    val desc:String,
    @DrawableRes
    val resource:Int
)



data class SecondVerifyResponse(
    val isLogin:Int,
    val userinfo:LoginResponse,
    val res:SecondVerifyChild
){

    /**
     * 进入快捷注册
     * */
    fun isNoResisterOrError() = (isLogin == 0 && res != null)

    /**
     * 手动登录
     * */
    fun goHandLogin() = (isLogin == 0 && (userinfo == null || res == null))
}

data class SecondVerifyChild(val valid: Boolean, var phone: String, val isValid: String)

data class AddScoreResponse(
    //此次获得积分
    val addcredit: String,
    //连续打卡天数
    val days: String,
    //此次得到红包的数量 ,单位是分
    val money: String,
    //200算成功,
    val result: String,
    //当money>0返回的是总金额,money=0返回的是总积分;
    val totalcredit: String
)

data class HorizontalOperateBean(
    @DrawableRes
    val icon:Int,
    val type:OperateType
)
