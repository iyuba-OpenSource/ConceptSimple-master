package com.jn.yyz.practise.util.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.digitalink.Ink;

public class HandwritingView extends View {

    private Ink.Builder inkBuilder;

    private Ink.Stroke.Builder strokeBuilder;

    private Paint linePaint;

    private Bitmap canvasBitmap;

    private Canvas drawCanvas;

    private Path currentStrokePath;

    public HandwritingView(@NonNull Context context) {
        super(context);
        init();
    }

    public HandwritingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandwritingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HandwritingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        inkBuilder = Ink.builder();
//        strokeBuilder = Ink.Stroke.builder();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(3.5f);

        currentStrokePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0) {

            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (isEnabled()) {

            float x = event.getX();
            float y = event.getY();
            long t = System.currentTimeMillis();

            // If your setup does not provide timing information, you can omit the
            // third paramater (t) in the calls to Ink.Point.create
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    currentStrokePath.moveTo(x, y);
                    strokeBuilder = Ink.Stroke.builder();
                    strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentStrokePath.lineTo(x, y);
                    strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                    break;
                case MotionEvent.ACTION_UP:
                    currentStrokePath.lineTo(x, y);
                    drawCanvas.drawPath(currentStrokePath, linePaint);
                    currentStrokePath.reset();

                    strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                    inkBuilder.addStroke(strokeBuilder.build());
                    strokeBuilder = null;
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, linePaint);
//        canvas.drawPath(currentStroke, currentStrokePaint);
        canvas.drawPath(currentStrokePath, linePaint);
    }

    public Ink getInk() {

        if (inkBuilder == null) {

            return null;
        } else {

            Ink ink = inkBuilder.build();
            return ink;
        }
    }


    public void clear() {

        if (drawCanvas != null) {

            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        if (currentStrokePath != null) {

            currentStrokePath.reset();
        }
        inkBuilder = Ink.builder();
        invalidate();
    }
}
