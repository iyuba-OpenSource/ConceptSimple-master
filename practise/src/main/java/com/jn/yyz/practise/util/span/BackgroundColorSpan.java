package com.jn.yyz.practise.util.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

public class BackgroundColorSpan extends ReplacementSpan {

    private int backgroundColor;
    private float radius;
    private int textColor;

    public BackgroundColorSpan(int backgroundColor, int textColor, float radius) {

        this.backgroundColor = backgroundColor;
        this.radius = radius;
        this.textColor = textColor;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        Paint.FontMetrics fm = paint.getFontMetrics();
        // fm.bottom - fm.top 解决设置行距（android:lineSpacingMultiplier="1.2"）时背景色高度问题
        RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rect, radius, radius, paint);
        paint.setColor(textColor);
        paint.setUnderlineText(true);
        canvas.drawText(text, start, end, x, y, paint);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }

}