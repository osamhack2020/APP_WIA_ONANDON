package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class CustomSpan extends DotSpan {
    private int radius;
    private int color;
    private int eventNum;
    private int eventNth;

    CustomSpan(int radius, int color, int eventNum, int eventNth){
        this.radius = radius;
        this.color = color;
        this.eventNum = eventNum;
        this.eventNth = eventNth;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }
        int offset = eventNth * ((left + right) / (eventNum + 1));
        canvas.drawCircle(left + offset, bottom + radius, radius, paint);
        paint.setColor(oldColor);
    }
}
