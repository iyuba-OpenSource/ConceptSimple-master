package com.suzhou.concept.viewmodel

import androidx.lifecycle.viewModelScope
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.AdItem
import com.suzhou.concept.utils.FlowResult
import com.suzhou.concept.utils.OtherUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AdvertiseViewModel:BaseViewModel() {

    val adResult= MutableSharedFlow<FlowResult<AdItem>>(5,5)

    fun requestAdType(flag:String="1"){
        viewModelScope.launch {
            Repository.getLoginResponse().flatMapMerge {
                dataMap.apply {
                    clear()
                    put("uid",it.uid.toString())
//                    put("appId","148")
                    putAppId("appId")
                    put("flag",flag)
                }
                Repository.requestAdType(adUrl,dataMap)
            }.onStart {
                adResult.emit(FlowResult.Loading())
            }.catch {
                adResult.emit(FlowResult.Error(it))
            }.collect{
                if (it.isEmpty()){
                    adResult.emit(FlowResult.Error(Throwable()))
                }else{
                    it[0].data.apply {
//                        Repository.saveSplash(OtherUtils.splashHead+startuppic_Url)
                        adResult.emit(FlowResult.Success(this))
                    }
                }
            }
        }
    }
    fun getSplash()=Repository.getSplash()

}