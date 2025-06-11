package com.suzhou.concept.lil.data.newDB.word.pass;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 单词表-新概念-全四册
 * @date: 2023/5/11 16:47
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"voaId","position"})
public class WordEntity_Four {

    @NonNull
    public int voaId;
    public String word;
    public String def;
    public String pron;
    public String examples;
    public String audio;
    @NonNull
    public String position;
    public String sentence;
    public String sentence_cn;
    public String timing;
    public String end_timing;
    public String sentence_audio;
    public String sentence_single_audio;

    //本地数据
    public String bookId;

    public WordEntity_Four() {
    }

    @Ignore
    public WordEntity_Four(int voaId, String word, String def, String pron, String examples, String audio, @NonNull String position, String sentence, String sentence_cn, String timing, String end_timing, String sentence_audio, String sentence_single_audio, String bookId) {
        this.voaId = voaId;
        this.word = word;
        this.def = def;
        this.pron = pron;
        this.examples = examples;
        this.audio = audio;
        this.position = position;
        this.sentence = sentence;
        this.sentence_cn = sentence_cn;
        this.timing = timing;
        this.end_timing = end_timing;
        this.sentence_audio = sentence_audio;
        this.sentence_single_audio = sentence_single_audio;
        this.bookId = bookId;
    }
}
