package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PronBean {


    @SerializedName("result")
    private int result;
    @SerializedName("msg")
    private String msg;
    @SerializedName("vowel")
    private List<VowelDTO> vowel;
    @SerializedName("consonant")
    private List<VowelDTO> consonant;


    public List<VowelDTO> getConsonant() {
        return consonant;
    }

    public void setConsonant(List<VowelDTO> consonant) {
        this.consonant = consonant;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<VowelDTO> getVowel() {
        return vowel;
    }

    public void setVowel(List<VowelDTO> vowel) {
        this.vowel = vowel;
    }


    public static class VowelDTO {
        @SerializedName("id")
        private int id;
        @SerializedName("pron")
        private String pron;
        @SerializedName("sound")
        private String sound;
        @SerializedName("category1")
        private String category1;
        @SerializedName("category2")
        private String category2;
        @SerializedName("category3")
        private String category3;
        @SerializedName("word")
        private String word;
        @SerializedName("wordAudio")
        private String wordAudio;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPron() {
            return pron;
        }

        public void setPron(String pron) {
            this.pron = pron;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getCategory1() {
            return category1;
        }

        public void setCategory1(String category1) {
            this.category1 = category1;
        }

        public String getCategory2() {
            return category2;
        }

        public void setCategory2(String category2) {
            this.category2 = category2;
        }

        public String getCategory3() {
            return category3;
        }

        public void setCategory3(String category3) {
            this.category3 = category3;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getWordAudio() {
            return wordAudio;
        }

        public void setWordAudio(String wordAudio) {
            this.wordAudio = wordAudio;
        }
    }
}
