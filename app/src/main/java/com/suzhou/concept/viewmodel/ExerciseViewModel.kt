package com.suzhou.concept.viewmodel

import androidx.lifecycle.viewModelScope
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.*
import com.suzhou.concept.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
苏州爱语吧科技有限公司
@Date:  2023/2/7
@Author:  han rong cheng
 */
class ExerciseViewModel:BaseViewModel() {
    /**
     * --------------------------获取单选题---------------------------------------------
     * */
    private val multiple=MutableSharedFlow<FlowResult<List<MultipleItem>>>()

    val lastMultiple=multiple.asSharedFlow()

    fun getConceptExercise(){
        viewModelScope.launch {
            Repository.getConceptExercise(getExerciseUrl,AppClient.conceptItem.voa_id).catch {
                multiple.emit(FlowResult.Error(it))
                structure.emit(FlowResult.Error(it))
            }.collect{
                multiple.emit(FlowResult.Success(it.MultipleChoice))
                structure.emit(FlowResult.Success(it.VoaStructureExercise))
            }
        }
    }

    //判断本地是否有的时候可以把两个查询本地的流组合起来
    private val structure=MutableSharedFlow<FlowResult<List<StructureItem>>>(6,6)
    val lastStructure=structure.asSharedFlow()
    /**
     * -------------------------------- --------------------------------
     * */
    private val submitExerciseResult=MutableSharedFlow<FlowResult<StudyRecordResponse>>()
    val lastSubmitExerciseResult=submitExerciseResult.asSharedFlow()

    fun submitExerciseRecord(list:List<ExerciseRecord>){
        val array= JSONArray()
        list.forEach {
            array.put(it.toJsonObject())
        }
        val obj= JSONObject().put("datalist",array)
        val sign="${GlobalMemory.userInfo.uid}iyubaTest${nowTime()}".toMd5()
        dataMap.apply {
            clear()
            putFormat()
            putUserId("uid")
            putAppId("appId")
            putDeviceId()
            putAppName("appName")
            put("sign",sign)
            put("jsonStr",obj.toString().changeEncode())
        }
        viewModelScope.launch {
            Repository.submitExerciseRecord(exerciseUrl, dataMap).onStart {
                submitExerciseResult.emit(FlowResult.Loading())
            }.catch {
                submitExerciseResult.emit(FlowResult.Error(it))
            }.flowOn(Dispatchers.IO)
                .collect {
                    submitExerciseResult.emit(FlowResult.Success(it))
                }
        }
    }

    /**
     * -------------------------------- --------------------------------
     * */
    private val testRecordFlow=MutableSharedFlow<FlowResult<List<TestRecordItem>>>(10,10)
    val lastTestRecord=testRecordFlow.asSharedFlow()

    fun requestTestRecord(lessonId:String){
        reDoFlag=true
        val sign= with(StringBuilder()){
            append(GlobalMemory.userInfo.uid)
            append(signDate())
            toString().toMd5()
        }
        dataMap.apply {
            clear()
            putAppId("appId")
            putUserId("uid")
            put("TestMode","10")
            put("sign",sign)
            putFormat()
            put("Pageth","1")
            put("NumPerPage","1000")
        }
        viewModelScope.launch {
            Repository.requestTestRecord(testRecordUrl,dataMap).onStart {
                testRecordFlow.emit(FlowResult.Loading())
            }.map {
                it.data.filter { item->
                    (item.LessonId==lessonId)
                }
            }.catch {
                testRecordFlow.emit(FlowResult.Error(it))
            }.collect{
                testRecordFlow.emit(FlowResult.Success(it.sortedBy {item-> item.UpdateTime }))
            }
        }
    }
    private var reDoFlag=false
    fun getReDidFlag()=reDoFlag

    fun operateRedo(number:String,flag:Boolean,isMultiple:Boolean=true){
        viewModelScope.launch {
            Repository.selectByNumber(number).map {
                if (it.isNotEmpty()){
                    if (isMultiple){
                        Repository.updateRedoMultipleStatus(number, flag)
                    }else{
                        Repository.updateRedoStructureStatus(number, flag)
                    }
                }else{
                    Repository.insertRedo(ReDoBean(number,flag,false))
                }
            }.first()
        }
    }

    fun selectByNumber(number:String,flag: Boolean=true)=Repository.selectByNumber(number).map {
        if (it.isEmpty()){
            false
        }else{
            with(it.first()){
                if (flag) reDidMultiple else reDidStructure
            }
        }
    }
}