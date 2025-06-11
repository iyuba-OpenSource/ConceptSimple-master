package com.suzhou.concept.lil.data.newDB.word.pass;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordClassifyBean;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/11 16:54
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Dao
public interface WordFourDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<WordEntity_Four> list);

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(WordEntity_Four concept);

    //查询本书籍下的单词数据
    @Query("select * from WordEntity_Four where bookId=:bookId order by voaId asc")
    List<WordEntity_Four> searchWordByBookId(int bookId);

    //查询本章节下的单词数据
    @Query("select * from WordEntity_Four where voaId=:voaId order by position asc")
    List<WordEntity_Four> searchWordByVoaId(int voaId);

    //分类查询每个单元的数据
    @Query("select bookId,voaId as id,COUNT(*) as wordCount from WordEntity_Four where bookId=:bookId group by voaId order by voaId asc")
    List<WordClassifyBean> searchUnitCountData(int bookId);

    //查询当前书籍下随机的100个单词数据
    @Query("select * from WordEntity_Four where bookId=:bookId order by RANDOM() limit 100")
    List<WordEntity_Four> searchRandomDataByBookId(int bookId);
}
