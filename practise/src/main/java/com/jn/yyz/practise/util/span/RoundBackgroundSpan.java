package com.jn.yyz.practise.util.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

public class RoundBackgroundSpan extends ReplacementSpan {

    private int backgroundColor;
    private float radius;
    private int textColor;
    private int padding;

    public RoundBackgroundSpan(int backgroundColor, int textColor, float radius, int padding) {

        this.backgroundColor = backgroundColor;
        this.radius = radius;
        this.textColor = textColor;
        this.padding = padding;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        float width = paint.measureText(text.subSequence(start, end).toString());
        RectF rectF = new RectF(x, top, x + width + padding * 2, bottom);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setColor(textColor);
        canvas.drawText(text, start, end, x + padding, y, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        canvas.drawLine(x, bottom, x + width + padding * 2, bottom, paint);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {

        return padding + Math.round(paint.measureText(text, start, end)) + padding;
    }
}