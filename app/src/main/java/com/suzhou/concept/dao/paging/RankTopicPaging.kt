package com.suzhou.concept.dao.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.RankItem
import com.suzhou.concept.bean.RankResponse
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.putUserId
import com.suzhou.concept.utils.signDate
import com.suzhou.concept.utils.toMd5

/**
苏州爱语吧科技有限公司
@Date:  2023/1/12
@Author:  han rong cheng
 */
class RankTopicPaging(private val flag:Boolean) :PagingSource<Int, RankItem>() {
    private val dataMap= mutableMapOf<String,String>()
    private val type="D"
    override fun getRefreshKey(state: PagingState<Int, RankItem>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RankItem> {
        return try {
            val start = (params.key ?: 0)
            val total= (params.loadSize)
            val date= signDate()
            val response=getTopicRanking(start.toString(), total.toString(), date)
            val nextPage=(if (response.data.size< total) null else (start +1)*total)
            GlobalMemory.rankTopicResponse.copyChange(response)
            LoadResult.Page(response.data,null,nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    /**
     * 获取口语排行
     * */
    private suspend fun getTopicRanking(start:String,total:String,date: String): RankResponse {
        val topicId="0"
        val sign=with(StringBuilder()){
            append(GlobalMemory.userInfo.uid)
            append(AppClient.appName)
            append(topicId)
            append(start)
            append(total)
            append(date)
            toString().toMd5()
        }
        dataMap.apply {
            clear()
            putUserId("uid")
            put("type",type)
            put("start",start)
            put("total",total)
            put("sign",sign)
            put("topic", AppClient.appName)
            put("topicid",topicId)
            put("shuoshuotype","4")
        }
        val url=if (flag) GlobalMemory.topicRankUrl else GlobalMemory.testRankUrl
        return Repository.getTopicRanking(url,dataMap)
    }


}