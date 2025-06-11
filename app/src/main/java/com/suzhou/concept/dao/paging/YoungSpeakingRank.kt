package com.suzhou.concept.dao.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.YoungRankItem
import com.suzhou.concept.utils.putAppName
import com.suzhou.concept.utils.putFormat
import com.suzhou.concept.utils.putPlatform
import com.suzhou.concept.utils.putProtocol

class YoungSpeakingRank (val url:String,val voaId:String): PagingSource<Int, YoungRankItem>() {
    override fun getRefreshKey(state: PagingState<Int, YoungRankItem>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, YoungRankItem> {
        return  try {
            //页码未定义置为1
            val currentPage = params.key ?: 1
            //仓库层请求数据
            val dataMap= with(mutableMapOf<String,String>()){
                putPlatform()
                putFormat()
                putProtocol("60001")
                put("voaid","321001")
                put("pageNumber",currentPage.toString())
                put("pageCounts",params.loadSize.toString())
                put("sort","2")
                putAppName("topic")
                put("selectType","3")
                this
            }
            val rankData = Repository.getYoungSpeakingRank(url,dataMap)
            val nextPage = if (rankData.TotalPage==currentPage) null else currentPage + 1
            val lastResult=rankData.data.map {
                it.score="${it.score}分"
                it
            }
            LoadResult.Page(lastResult, null, nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}