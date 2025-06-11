package com.jn.yyz.practise.util.span;

import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.jn.yyz.practise.entity.Translate;

public class WordClickSpan extends ClickableSpan {


    private Translate translate;

    private ClickCallback clickCallback;

    public WordClickSpan(Translate translate) {
        this.translate = translate;
    }

    @Override
    public void onClick(@NonNull View widget) {

        if (clickCallback != null) {

            clickCallback.click(translate);
        }
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public interface ClickCallback {


        void click(Translate translate);
    }
}
