package com.jn.yyz.practise.entity;

public class SoundFillIn {

    private String path;

    private boolean isPlaying = false;


    public SoundFillIn(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
