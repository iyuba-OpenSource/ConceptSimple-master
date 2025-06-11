package com.suzhou.concept.lil.data.newDB.word.collect;

import androidx.annotation.NonNull;
import androidx.room.Entity;


/**
 * @title: 单词收藏数据
 * @date: 2023/10/8 17:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(tableName = "WordCollect",primaryKeys = "word")
public class WordCollectBean {

    @NonNull
    public String word;
    public String pron;
    public String def;
    public String audio;
    @NonNull
    public int userId;
}
