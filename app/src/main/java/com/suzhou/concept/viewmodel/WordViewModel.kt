package com.suzhou.concept.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.WordItem
import com.suzhou.concept.bean.WordOptions
import com.suzhou.concept.utils.FlowResult
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.toWordMap
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
 */
class WordViewModel:BaseViewModel() {

    fun saveThroughWordId (wordId:Int)=Repository.saveThroughWordId(wordId)
    fun getThroughWordId ()=Repository.getThroughWordId()
    fun updateRightStatus (item:WordItem,flag:Boolean){
        Log.d("11111111111111", "bookId: __________${item.bookId}")
        Log.d("11111111111111", "position: __________${item.position}")
        Log.d("11111111111111", "GlobalMemory.userInfo.uid: __________${GlobalMemory.userInfo.uid}")
        Log.d("11111111111111", "item.unitId+1: __________${item.unitId+1}")
        Repository.updateRightStatus(item.bookId,item.position,flag, GlobalMemory.userInfo.uid,item.unitId+1)
    }

    //区分类型进行数据插入
    fun updateRightStatus (item:WordItem,flag:Boolean,language:String){
        Repository.updateRightStatus(item.bookId,item.position,flag, GlobalMemory.userInfo.uid,item.unitId+1)
    }

    //查询全四册的正确单词数量
    fun selectRightWordCount(voaId:Int,userId:Int,correct:Boolean):Int{
        return Repository.selectRightWordCount(voaId, userId,correct)
    }
    //查询全四册的单词数量
    fun selectWordCount(voaId:Int):Int{
        return Repository.selectWordCount(voaId)
    }

    //查询单词闯关的正确单词数量
    fun selectWordBreakRightCount(type:String,bookId: Int,unitId:Int,userId:Int):Int{
        return Repository.selectWordBreakRightCount(type, bookId, unitId, userId)
    }

    /**
     * 获取青少版的单词Item
     * */
    private fun requestYoungWordItem(bookId: Int) = Repository.selectYoungWordById(bookId)
        .flatMapConcat {
            if (it.isEmpty()) {
                Repository.getYoungWord(bookId, youngWordUrl).flatMapConcat { result ->
                    result.data.map { item ->
                        item.inflateAudio()
                        item.userId = GlobalMemory.userInfo.uid
                        item
                    }.let { list ->
                        Repository.insertYoungWordItem(list)
                        flow { emit(list) }
                    }
                }
            } else {
                flow { emit(it) }
            }
        }

    /**
     * 获取青少版的单词
     * */
    private fun requestYoungWord(bookId: Int) =
        requestYoungWordItem(bookId).map {
            val itemList = it.map { item -> item.toWordItem() }
            breakInfoResult.emit(itemList)
            itemList.toWordMap(judgeIndex(bookId), true)
                .map { entry ->
                    entry.value
                }
        }

    /**
     * 部分青少本课本分上(A)下(B)册
     * */
    private fun judgeIndex(bookId: Int) = when (bookId) {
        //StarterA
        278 ,
        //StarterB
        279 ,
        //青少版1A
        280 ,
        //青少版2A
        282 ,
        //青少版1B
        //青少版3A
        284 ,
        //青少版4A
        286 ,
        //青少版5A
        288 -> 0
        281 ,
        //青少版2B
        283 ,
        //青少版3B
        285 -> 15
        //青少版4B
        287 ,
        //青少版5B
        289 -> 24
        else -> 0
    }

    /**
     * 请求全四册的内容(这里不需要根据uid查询，直接传0即可)
     * */
    private fun requestFourClassItem(bookId: Int = 1)
        =Repository.selectByBookIdAndUserId(bookId, GlobalMemory.userInfo.uid).flatMapConcat {
            if (it.isEmpty()) {
                Repository.getConceptWord(bookId).flatMapConcat { netResult ->
                    netResult.data.map { item ->
                        item.bookId = bookId
                        item.userId=GlobalMemory.userInfo.uid
                        item.unitId=item.voa_id-1000
                        item
                    }.let { list->
                        Repository.insertWordList(list)
                        flow { emit(list) }
                    }
                }
            } else {
                flow { emit(it) }
            }
        }


    /**
     * 请求全四册的内容
     * */
    private fun requestFourClass(bookId: Int = 1) =
        requestFourClassItem(bookId).map {
            it.forEach { item->
                item.unitId=item.voa_id-1000-1
            }
            Log.d("111111111111111111", "requestFourClass: _______________${it.last()}")
            breakInfoResult.emit(it)
            it.toWordMap().map { item->item.value }
        }

    /**
     * 通过过滤获取某个关卡的具体信息
     * */
    val breakInfoResult=MutableSharedFlow<List<WordItem>>()
    /**
     * 默认显示全四册的第一册
     * */
    val wordOptionsResult= MutableSharedFlow<FlowResult<List<WordOptions>>>()

    fun requestMergeWord() {
        //可以像插入数据库一样，先insert，再返回原来的集合？？？
        //list->(全四册区别处理)map(全四册区别处理)->list
        //理想状态下是否应该再两条主flow里分别进行数据转换？？？这也与第一条相契合
        viewModelScope.launch {
            //为什么一开始要写个defaultBookId？？？
            //写个获取bookId的流？？？
            getThroughWordId().flatMapConcat {bookId->
                saveThroughWordId(bookId)
                if (bookId>4) {
                    GlobalMemory.wordYoung=true
                    requestYoungWord(bookId)
                } else {
                    GlobalMemory.wordYoung=false
                    requestFourClass(bookId)
                }
            }.onStart {
                wordOptionsResult.emit(FlowResult.Loading())
            }.catch {
                wordOptionsResult.emit(FlowResult.Error(it))
            }.collect{
                wordOptionsResult.emit(FlowResult.Success(it))
            }
        }
    }
    fun updateWordRightStatus(item:WordItem,flag:Boolean)=Repository.updateWordRightStatus(item.bookId, item.position, flag, item.unitId+1)
    //查询青少版的正确单词数量
    fun selectYoungRightWordCount(voaId: Int,userId: Int,correct: Boolean):Int = Repository.selectYoungRightWordCount(voaId, userId, correct)
    //查询青少版的单词数量
    fun selectYoungWordCount(voaId: Int):Int = Repository.selectYoungWordCount(voaId)

    /**
     * -----------------------------------------------------------------------------
     * */
    private val knowledge=MutableSharedFlow<FlowResult<List<WordItem>>>(6,6)
    val lastKnowledge=knowledge.asSharedFlow()
    fun requestKnowledgeWord(){
        val bookId=AppClient.conceptItem.bookId
        viewModelScope.launch {
            if (GlobalMemory.currentYoung){
                requestYoungWordItem(bookId).map {
                    it.map { item->item.toWordItem() }
                }
            }else{
                requestFourClassItem(bookId)
            }.map {
                it.filter { item->

                    if (AppClient.conceptItem.language=="UK"){
                        var voaId:Int = AppClient.conceptItem.voa_id.toInt()
                        item.voa_id.toString()==(voaId/10).toString()
                    }else{
                        item.voa_id.toString()==AppClient.conceptItem.voa_id
                    }
                }
            }.catch {
                knowledge.emit(FlowResult.Error(it))
            }.collect{
                knowledge.emit(FlowResult.Success(it))
            }
        }
    }
}