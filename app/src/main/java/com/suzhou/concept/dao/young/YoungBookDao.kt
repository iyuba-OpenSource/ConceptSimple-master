package com.suzhou.concept.dao.young

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzhou.concept.bean.BookItem

/**
苏州爱语吧科技有限公司
@Date:  2022/11/17
@Author:  han rong cheng
 */
@Dao
interface YoungBookDao {
    @Insert
    fun insertYoungItem(list:List<BookItem>)

    @Query("select * from BookItem where series=:series and userId=:userId")
    fun selectYoungItemList(series:Int,userId:Int):List<BookItem>

    @Query("select * from BookItem where series=:bookId and `index`=:index")
    fun selectSimpleYoungItem(bookId:Int,index:Int):List<BookItem>

    //查询某个数据
    @Query("select * from BookItem where series=:bookId and `index`=:index and userId=:userId")
    fun selectSingleYoungItem(bookId:Int,index:Int,userId: Int):BookItem?

    @Query("update BookItem set isCollect=:isCollect where series=:bookId and `index`=:index and userId=:userId")
    fun updateItemCollect(bookId:Int,index:Int,userId: Int,isCollect:Boolean)

    @Query("update BookItem set isDownload=:isDownload where series=:bookId and `index`=:index and userId=:userId")
    fun updateItemDownload(bookId:Int,index:Int,userId: Int,isDownload:Boolean)

    @Query("select * from BookItem where userId=:userId and isCollect=:isCollect")
    fun selectCollected(userId: Int,isCollect: Boolean=true):List<BookItem>

    @Query("select * from BookItem where userId=:userId and isDownLoad=:isDownLoad")
    fun selectDownLoaded(userId: Int,isDownLoad: Boolean=true):List<BookItem>

    @Query("update BookItem set listenProgress=:progress where series=:bookId and `index`=:index and userId=:userId")
    fun updateItemListen(bookId:Int,index:Int,userId: Int,progress:Int)

    @Query("update BookItem set evalSuccess=:evalSuccess where series=:bookId and `index`=:index and userId=:userId")
    fun updateItemEval(bookId:Int,index:Int,userId: Int,evalSuccess:Int)

    @Query("update BookItem set wordRight=:wordSuccess where series=:bookId and `index`=:index and userId=:userId")
    fun updateItemWord(bookId:Int,index:Int,userId: Int,wordSuccess:Int)
}