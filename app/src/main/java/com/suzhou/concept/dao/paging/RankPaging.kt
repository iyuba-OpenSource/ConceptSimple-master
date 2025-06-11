package com.suzhou.concept.dao.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.suzhou.concept.AppClient
import com.suzhou.concept.Repository
import com.suzhou.concept.bean.RankItem
import com.suzhou.concept.utils.OtherUtils
import com.suzhou.concept.utils.toMd5
import java.text.SimpleDateFormat
import java.util.*

/**
苏州爱语吧科技有限公司
 */
class RankPaging(val url:String):PagingSource<Int, RankItem>() {

    override fun getRefreshKey(state: PagingState<Int, RankItem>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RankItem> {
        return  try {
            val currentPage = params.key ?: 0
            val pageSize= params.loadSize
            val newData=Repository.getRankData(url,currentPage, pageSize)
            if (!newData.isNotLogin()){
                AppClient.rankResponse.copyChange(newData)
            }
            val nextPage = if (newData.data.isEmpty()) null else currentPage + 1
            LoadResult.Page(newData.data, null, nextPage)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}