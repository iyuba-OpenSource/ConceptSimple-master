package com.jn.yyz.practise.entity;

public class Pair {

    private String name;

    private String answer;

    private boolean isPair = false;

    //错误状态
    private boolean isError = false;

    private boolean isPlaying = false;

    /**
     * 206 音频地址
     */
    private String sound;

    public Pair(String name) {
        this.name = name;
    }


    public Pair(String name, String answer) {
        this.name = name;
        this.answer = answer;
    }

    public Pair(String name, String answer, String sound) {
        this.name = name;
        this.answer = answer;
        this.sound = sound;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPair() {
        return isPair;
    }

    public void setPair(boolean pair) {
        isPair = pair;
    }
}
