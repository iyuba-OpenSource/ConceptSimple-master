package com.suzhou.concept.dao.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.StrangenessWordItem
import com.suzhou.concept.utils.GlobalMemory
import com.suzhou.concept.utils.OtherUtils

/**
苏州爱语吧科技有限公司
 */
class DataSource :PagingSource<Int, StrangenessWordItem>() {
    override fun getRefreshKey(state: PagingState<Int, StrangenessWordItem>): Int? =null
    override val keyReuseSupported: Boolean  = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StrangenessWordItem> {
        return  try {
            //页码未定义置为1
            val currentPage = params.key ?: 1
            //仓库层请求数据
            val dataMap= with(mutableMapOf<String,String>()){
                put("u",GlobalMemory.userInfo.uid.toString())
                put("pageNumber",(params.key ?: 1).toString())
                put("pageCounts",params.loadSize.toString())
                this
            }
            val demoReqData = Repository.requestStrangenessWord(OtherUtils.wordPagingUrl,dataMap)
            val nextPage = if (demoReqData.totalPage==currentPage) null else currentPage + 1
            LoadResult.Page(demoReqData.row, null, nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}