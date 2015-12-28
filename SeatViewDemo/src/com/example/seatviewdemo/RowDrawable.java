
package com.example.seatviewdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class RowDrawable extends Drawable {

    private Drawable mBkgDraw;
    private String[] mRowData;
    private int mOffset;

    public RowDrawable(Drawable draw, String[] data) {
        mBkgDraw = draw;
        setRowName(data);
    }

    public void setRowName(String[] array) {
        final int length = array.length;
        final int nullCount = getNullCount(array) - SeatDrawable.SCREEN_HEIGHT_ROW;
        mRowData = new String[length - nullCount];

        for (int i = 0; i < mRowData.length; i++) {
            if (i < SeatDrawable.SCREEN_HEIGHT_ROW) {
                mRowData[i] = null;
            } else {
                mRowData[i] = array[i + nullCount];
            }
        }
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mBkgDraw.setBounds(bounds);
        super.onBoundsChange(bounds);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBkgDraw.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBkgDraw.getIntrinsicHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height() - mOffset * 2;
        int oneDataHeight = height / mRowData.length;

        int offsetY = getBounds().top + mOffset + (height - oneDataHeight * mRowData.length) / 2;
        int textCenterX = getBounds().left + width / 2;
        int textCenterY = offsetY + oneDataHeight / 3;

        adjustBounds();

        mBkgDraw.draw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setTextSize(20f);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < mRowData.length; i++) {
            if (mRowData[i] != null) {
                canvas.drawText(mRowData[i], textCenterX, textCenterY + (paint.descent() - paint.ascent()) / 2, paint);
            }
            textCenterY += oneDataHeight;
        }

    }

    private void adjustBounds() {
        int height = getBounds().height() - mOffset * 2;
        int oneDataHeight = height / mRowData.length;
        int index = 0;
        while (mRowData[index] == null) {
            if (mRowData[index] == null || mRowData[index].equals("")) {
                mBkgDraw.getBounds().top += oneDataHeight;
            }
            index++;
        }

        index = mRowData.length - 1;
        while (mRowData[index] == null) {
            if (mRowData[index] == null || mRowData[index].equals("")) {
                mBkgDraw.getBounds().bottom -= oneDataHeight;
            }
            index--;
        }
    }

    private int getNullCount(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

}
