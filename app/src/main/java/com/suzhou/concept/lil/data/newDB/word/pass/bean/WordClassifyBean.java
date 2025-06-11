package com.suzhou.concept.lil.data.newDB.word.pass.bean;

/**
 * 单词分类数量显示
 */
public class WordClassifyBean {

    private int bookId;
    private int id;
    private int wordCount;

    public WordClassifyBean(int bookId, int id, int wordCount) {
        this.bookId = bookId;
        this.id = id;
        this.wordCount = wordCount;
    }

    public int getBookId() {
        return bookId;
    }

    public int getId() {
        return id;
    }

    public int getWordCount() {
        return wordCount;
    }
}
