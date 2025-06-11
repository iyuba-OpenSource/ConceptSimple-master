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
public interface WordJuniorDao {

    //保存数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveData(List<WordEntity_Junior> list);

    //保存单个数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveSingleData(WordEntity_Junior concept);

    //查询本书籍下的单词数据
    @Query("select * from WordEntity_Junior where book_id=:bookId order by position asc")
    List<WordEntity_Junior> searchWordByBookId(int bookId);

    //查询本书籍下当前单元的单词数据
    @Query("select * from WordEntity_Junior where book_id=:bookId and unit_id=:unitId order by position asc")
    List<WordEntity_Junior> searchWordByBookIdAndUnitId(int bookId,int unitId);

    //查询本单元下的单词数据
    @Query("select * from WordEntity_Junior where unit_id=:unitId order by position asc")
    List<WordEntity_Junior> searchWordByUnitId(int unitId);

    //查询本课程下的单词数据
    @Query("select * from WordEntity_Junior where voaId=:voaId order by position asc")
    List<WordEntity_Junior> searchWordByVoaId(int voaId);

    //分类查询每个单元的数据
    @Query("select book_id as bookId,unit_id as id,COUNT(*) as wordCount from WordEntity_Junior where bookId=:bookId group by unit_id order by unit_id asc")
    List<WordClassifyBean> searchUnitCountData(int bookId);

    //查询当前书籍的随机100个单词数据
    @Query("select * from WordEntity_Junior where book_id=:bookId order by RANDOM() limit 100")
    List<WordEntity_Junior> searchRandomWordByBookId(int bookId);
}
