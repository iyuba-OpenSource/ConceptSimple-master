package com.jn.yyz.practise.entity;

public class WordFillIn {


    //单词
    private String word;

    //选择的单词
    private Translate translate;


    public WordFillIn(String word, Translate translate) {
        this.word = word;
        this.translate = translate;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Translate getTranslate() {
        return translate;
    }

    public void setTranslate(Translate translate) {
        this.translate = translate;
    }
}
