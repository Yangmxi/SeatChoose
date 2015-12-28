
package com.example.seatviewdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Map;

/**
 * This Drawable designed for cinema seat choose.
 * It could draw the seats from the array of seat data,
 * And the {{@link #setSeatDate(int[][])} MUST run before seatView.setImageDrawable(seatDrawable). 
 * @author ymx
 *
 */
public class SeatDrawable extends Drawable {

    private static final String TAG = "SeatView";

    // This is the space between seats, for example, the road in seats.
    public static final int TYPE_SPACE = -1;

    // Seat is inexistent, No info about this seat.
    public static final int TYPE_INVISIBLE = 0;

    // Seat is available, user can select it.
    public static final int TYPE_AVAIL = 1;

    // Seat is unavailable, it selected by others.
    public static final int TYPE_UNAVAIL = 2;

    // Seat selected by user.
    public static final int TYPE_SELECTED = 3;

    // Seat is just for lover & Left, the status is available
    public static final int TYPE_LOVER_AVAIL_LEFT = 4;

    // Seat is just for lover unavailable & left, it selected by others.
    public static final int TYPE_LOVER_UNAVAIL_LEFT = 5;

    // Seat is just for lover & left, the status is selected
    public static final int TYPE_LOVER_SELECTED_LEFT = 6;

    // Seat is just for lover & right, the status is available
    public static final int TYPE_LOVER_AVAIL_RIGHT = 7;

    // Seat is just for lover unavailable & right, it selected by others.
    public static final int TYPE_LOVER_UNAVAIL_RIGHT = 8;

    // Seat is just for lover & right, the status is selected
    public static final int TYPE_LOVER_SELECTED_RIGHT = 9;

    public static final int TYPE_MAX = 10;

    // The max size of seats could be selected.
    public static final int MAX_SELECTED_SEAT = 4;

    public static final int MAX_ROW_SEAT = 10;
    // The number of lover left and lover right type
    private static final int TYPE_NUM_LOVER_LEFT_RIGHT = 3;

    private static final int MAX_ROW = 50;
    private static final int MAX_COLUMN = 100;
    private static final int NO_ROW_COLU_STATUS = 0xffff;

    /**
     * Seat drawable occupancy of Seat Size, others is space
     * Which between Seats.
     */
    private static final float SEAT_RATIO = 0.9f;

    /**
     * The screen in the top & center of the seat view.
     * Height of screen equals SCREEN_HEIGHT_ROW * mSeatHeight
     * @hide
     */
    static final int SCREEN_HEIGHT_ROW = 1;
    private Drawable mSeatAvail, mSeatUnavail, mSeatSelected;
    private Drawable mSeatAvailLeft, mSeatUnavailLeft, mSeatSelectedLeft;
    private Drawable mSeatAvailRight, mSeatUnavailRight, mSeatSelectedRight;
    private Drawable mScreen;
    private int[][] mSeatDateArray;

    /**
     *  The left matrix of seat data array which status is {{@link #TYPE_SPACE}
     *  User Can not See the seats in this matrix.
     *  mSpaceRow : the row number of matrix
     *  mSpaceColumn : the column number of matrix
     */
    private int mSpaceColumn, mSpaceRow;

    // seat touch width = height, not the seat drawable width/height
    private int mSeatWidth, mSeatHeight;

    private Context mContext;

    /**
     * In order to set the drawable in the center of bounds.
     * When drawable zoom and bounds changed, canvas should change the origin point.
     */
    private int mOffsetX, mOffsetY;

    public SeatDrawable(Context context) {
        initRes(context);
        mSeatDateArray = new int[MAX_ROW][MAX_COLUMN];
    }

    private void initRes(Context context) {
        mContext = context;
        Resources res = context.getResources();
        mSeatAvail = res.getDrawable(R.drawable.seat_avail);
        mSeatUnavail = res.getDrawable(R.drawable.seat_unavail);
        mSeatSelected = res.getDrawable(R.drawable.seat_selected);
        mSeatAvailLeft = res.getDrawable(R.drawable.seat_lover_avail_left);
        mSeatUnavailLeft = res.getDrawable(R.drawable.seat_lover_unavail_left);
        mSeatSelectedLeft = res.getDrawable(R.drawable.seat_lover_selected_left);
        mSeatAvailRight = res.getDrawable(R.drawable.seat_lover_avail_right);
        mSeatUnavailRight = res.getDrawable(R.drawable.seat_lover_unavail_right);
        mSeatSelectedRight = res.getDrawable(R.drawable.seat_lover_selected_right);
        mScreen = res.getDrawable(R.drawable.round_corner_bkg);
    }

    /**
     * Setting the seat date.
     * @param array the seat date array
     */
    public void setSeatDate(int[][] array) {
        final int rowLength = array.length;
        final int coluLength = array[0].length;
        mSeatDateArray = array;
        mSpaceColumn = getLeftSpaceColumn(array);
        mSpaceRow = getTopSpaceRow(array) - SCREEN_HEIGHT_ROW;
        mSeatDateArray = new int[rowLength - mSpaceRow][coluLength - mSpaceColumn];
        for (int row = 0; row < rowLength - mSpaceRow; row++) {
            for (int colu = 0; colu < coluLength - mSpaceColumn; colu++) {
                if (row < SCREEN_HEIGHT_ROW) {
                    mSeatDateArray[row][colu] = TYPE_SPACE;
                } else {
                    mSeatDateArray[row][colu] = array[row + mSpaceRow][colu + mSpaceColumn];
                }
            }
        }
    }

    /**
     * Setting the Seat status.
     * {@link#TYPE_INVISIBLE}, {@link#TYPE_AVAIL},
     * {@link#TYPE_UNAVAIL}, {@link#TYPE_SELECTED},
     * {@link#TYPE_SPACE}, {@link#TYPE_LOVER_AVAIL},
     * {@link#TYPE_LOVER_SELECTED}
     * @param loca the row and column of seat.
     * @param status the status of seat setting.
     * @param isLover Is the seat belong lover seat.
     * @return true setting successful, false is failed.
     */
    public boolean setSeatStatus(int[] loca, int status, boolean isLover) {
        int row = loca[0];
        int column = loca[1];
        if (row < getSeatArrayRow() && column < getSeatArrayColumn()) {
            if (isLover) {
                // Lover seat
                mSeatDateArray[row][column] = status;
                column = getNearestLoverColumn(loca);
                if (column < getSeatArrayColumn() && column >= 0) {
                    if (status >= TYPE_LOVER_AVAIL_LEFT && status <= TYPE_LOVER_SELECTED_LEFT) {
                        mSeatDateArray[row][column] = status + TYPE_NUM_LOVER_LEFT_RIGHT;
                    } else if (status >= TYPE_LOVER_AVAIL_RIGHT && status <= TYPE_LOVER_SELECTED_RIGHT) {
                        mSeatDateArray[row][column] = status - TYPE_NUM_LOVER_LEFT_RIGHT;
                    }
                    invalidateSelf();
                    return true;
                }
            } else {
                // Not lover seat
                mSeatDateArray[row][column] = status;
                invalidateSelf();
                return true;
            }
            return false;
        } else {
            Log.e(TAG, "Set Seat Status fail!");
            return false;
        }
    }

    /**
     * Get the seat status.
     * {@link#TYPE_INVISIBLE}, {@link#TYPE_AVAIL},
     * {@link#TYPE_UNAVAIL}, {@link#TYPE_SELECTED},
     * {@link#TYPE_SPACE}, {@link#TYPE_LOVER_AVAIL},
     * {@link#TYPE_LOVER_SELECTED}
     * @param loca the location of seat.
     * loca[0]: the row number
     * loca[1]: the column number
     * @return the status of seat.
     */
    public int getSeatStatus(int[] loca) {
        if (loca[0] < getSeatArrayRow() && loca[1] < getSeatArrayColumn()
                && loca[0] >= 0 && loca[1] >= 0) {
            return mSeatDateArray[loca[0]][loca[1]];
        }
        return NO_ROW_COLU_STATUS;
    }

    public int getSeatStatus(int row, int colu) {
        int[] loca = {
                row, colu
        };
        return getSeatStatus(loca);
    }

    /**
     * Get the seat which the row and column information.
     * @param location the location[0] is row, location[1] is column
     * @param ratioX the ratio x of bounds
     * @param ratioY the ratio y of bounds
     */
    public void getSeatRowColumn(int[] location, float ratioX, float ratioY) {
        location[1] = (int) Math.floor(ratioX * getSeatArrayColumn());
        location[0] = (int) Math.floor(ratioY * getSeatArrayRow());
        Log.e(TAG, "offsetX , offsetY = " + mOffsetX + " , " + mOffsetY);
        Log.e(TAG, "mSeatWidth  = " + mSeatWidth);
    }

    /**
     * Get the key of click seat in seat map.
     * Though the key, can find the seatInfo bean object.
     * @param row the row number in seat data array
     * @param colu the colu number in seat data array
     * @param ratio the ratio relative of row and colu, it depend on the seatActivity.
     * @return the key of seatInfo in seatInfoMap.
     */
    public int getSeatKey(int row, int colu, int ratio) {
        int trueRow = row + mSpaceRow + 1;
        int trueColu = colu + mSpaceColumn + 1;
        return trueRow * ratio + trueColu;
    }

    public int getSelectedSeatNumber() {
        int count = 0;
        for (int row = 0; row < getSeatArrayRow(); row++) {
            for (int colu = 0; colu < getSeatArrayColumn(); colu++) {
                int status = mSeatDateArray[row][colu];
                if (status == TYPE_LOVER_SELECTED_LEFT ||
                        status == TYPE_LOVER_SELECTED_RIGHT ||
                        status == TYPE_SELECTED) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getMaxVisibleColumn() {
        return getSeatArrayColumn();
    }

    private int getSeatArrayRow() {
        return mSeatDateArray.length;
    }

    private int getSeatArrayColumn() {
        return mSeatDateArray[0].length;
    }

    /**
     * Find the Nearest lover seat.
     * Be Careful: space seat must between 2 Lover seats,
     * Lover seats DONT align one line without space separate.
     * @param loca the seat's location
     * @return the nearest lover seat's column
     */
    private int getNearestLoverColumn(int[] loca) {
        int row = loca[0];
        int colu = loca[1];
        int status = getSeatStatus(row, colu);
        int nearest = -1;
        if (status >= TYPE_LOVER_AVAIL_LEFT && status <= TYPE_LOVER_SELECTED_LEFT) {
            nearest = colu + 1;
        } else if (status >= TYPE_LOVER_AVAIL_RIGHT && status <= TYPE_LOVER_SELECTED_RIGHT) {
            nearest = colu - 1;
        }
        if (nearest >= 0 && nearest < getSeatArrayColumn()) {
            return nearest;
        }
        return -1;
    }

    private int getLeftSpaceColumn(int[][] array) {
        final int rowlength = array.length;
        final int coluLength = array[0].length;
        int count = 0;
        for (int colu = 0; colu < coluLength; colu++) {
            for (int row = 0; row < rowlength; row++) {
                if (array[row][colu] != TYPE_SPACE) {
                    return count;
                }
            }
            count++;
        }
        return count;
    }

    private int getTopSpaceRow(int[][] array) {
        final int rowlength = array.length;
        final int coluLength = array[0].length;
        int count = 0;
        for (int row = 0; row < rowlength; row++) {
            for (int colu = 0; colu < coluLength; colu++) {
                if (array[row][colu] != TYPE_SPACE) {
                    return count;
                }
            }
            count++;
        }
        return count;

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (getSeatArrayColumn() > getSeatArrayRow()) {
            mSeatWidth = (int) Math.floor(bounds.width() / getSeatArrayColumn());
            mSeatHeight = mSeatWidth;
        } else {
            mSeatHeight = (int) Math.floor(bounds.height() / (getSeatArrayRow()));
            mSeatWidth = mSeatHeight;

        }
        int width = (int) (mSeatWidth * SEAT_RATIO);
        int height = width;
        Rect rect = new Rect(0, 0, width, height);
        float loveRatio = (1 - SEAT_RATIO) * 0.5f;
        int LeftLoverBoundsRight = (int) (rect.right + mSeatWidth * loveRatio);
        mSeatAvail.setBounds(rect);
        mSeatUnavail.setBounds(rect);
        mSeatSelected.setBounds(rect);

        rect.right = LeftLoverBoundsRight;
        mSeatAvailLeft.setBounds(rect);
        mSeatUnavailLeft.setBounds(rect);
        mSeatSelectedLeft.setBounds(rect);

        mSeatAvailRight.setBounds(rect);
        mSeatUnavailRight.setBounds(rect);
        mSeatSelectedRight.setBounds(rect);
        mOffsetX = (int) ((bounds.width() - mSeatWidth * getSeatArrayColumn()) * 0.5);
        mOffsetY = (int) ((bounds.height() - mSeatHeight * getSeatArrayRow()) * 0.5);
        if (mOffsetX < 0) {
            mOffsetX = 0;
        }
        if (mOffsetY < 0) {
            mOffsetY = 0;
        }
        Log.e(TAG, "Bounds = " + bounds);
        super.onBoundsChange(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        drawCenterCross(canvas);
        drawCenterScreen(canvas);

        final float ratio = (1.0f - SEAT_RATIO) * 0.5f;
        int offsetX = (int) (mOffsetX + (mSeatWidth * ratio));
        int offsetY = (int) (mOffsetY + (mSeatHeight * ratio));
        final int rowSize = getSeatArrayRow();
        final int coluSize = getSeatArrayColumn();
        int rightTrans = (int) (mSeatWidth * (1 - SEAT_RATIO) * 0.5);
        int row, colu;
        canvas.save();
        canvas.translate(offsetX, offsetY);
        for (row = 0; row < rowSize; row++) {
            for (colu = 0; colu < coluSize; colu++) {
                switch (mSeatDateArray[row][colu]) {
                    case TYPE_AVAIL:
                        //mSeatAvail.getConstantState().newDrawable().draw(canvas);
                        mSeatAvail.draw(canvas);
                        break;
                    case TYPE_UNAVAIL:
                        mSeatUnavail.draw(canvas);
                        break;
                    case TYPE_SELECTED:
                        mSeatSelected.draw(canvas);
                        break;
                    case TYPE_LOVER_AVAIL_LEFT:
                        canvas.translate(rightTrans, 0);
                        mSeatAvailLeft.draw(canvas);
                        canvas.translate(-rightTrans, 0);
                        break;
                    case TYPE_LOVER_UNAVAIL_LEFT:
                        canvas.translate(rightTrans, 0);
                        mSeatUnavailLeft.draw(canvas);
                        canvas.translate(-rightTrans, 0);
                        break;
                    case TYPE_LOVER_SELECTED_LEFT:
                        canvas.translate(rightTrans, 0);
                        mSeatSelectedLeft.draw(canvas);
                        canvas.translate(-rightTrans, 0);
                        break;
                    case TYPE_LOVER_AVAIL_RIGHT:
                        canvas.translate(-rightTrans, 0);
                        mSeatAvailRight.draw(canvas);
                        canvas.translate(rightTrans, 0);
                        break;
                    case TYPE_LOVER_UNAVAIL_RIGHT:
                        canvas.translate(-rightTrans, 0);
                        mSeatUnavailRight.draw(canvas);
                        canvas.translate(rightTrans, 0);
                        break;
                    case TYPE_LOVER_SELECTED_RIGHT:
                        canvas.translate(-rightTrans, 0);
                        mSeatSelectedRight.draw(canvas);
                        canvas.translate(rightTrans, 0);
                        break;
                    case TYPE_INVISIBLE:
                    case TYPE_SPACE:
                        break;
                    default:
                        break;
                }

                canvas.translate(mSeatWidth, 0);

            }
            canvas.translate(-mSeatWidth * colu, mSeatHeight);
        }
        canvas.restore();
    }

    private void drawCenterCross(Canvas canvas) {
        int centerColu = (int) Math.floor(getSeatArrayColumn() * 0.5f);
        int centerX = mOffsetX + centerColu * mSeatWidth;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[] {
                2, 2, 2, 2
        }, 1);
        paint.setPathEffect(effects);
        canvas.drawLine(centerX, mOffsetY + mSeatHeight * SCREEN_HEIGHT_ROW, centerX, mOffsetY + getSeatArrayRow() * mSeatHeight, paint);
    }

    private void drawCenterScreen(Canvas canvas) {
        if (mScreen == null) {
            mScreen = mContext.getResources().getDrawable(R.drawable.round_corner_bkg);
        }
        int centerColu = (int) Math.floor(getSeatArrayColumn() * 0.5f);
        int centerX = mOffsetX + centerColu * mSeatWidth;
        int width = mSeatWidth * 4;

        Rect bounds = new Rect();
        bounds.left = centerX - width / 2;
        bounds.top = 0;
        bounds.right = centerX + width / 2;
        bounds.bottom = mSeatHeight * SCREEN_HEIGHT_ROW;
        mScreen.setBounds(bounds);
        mScreen.draw(canvas);

        drawTextInScreen(canvas, bounds);

    }

    private void drawTextInScreen(Canvas canvas, Rect bounds) {
        int textCenterX = (bounds.left + bounds.right) / 2;
        int textCenterY = (bounds.bottom - bounds.top) / 2;
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        paint.setTextSize(12f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("ÒøÄ»ÖÐÑë", textCenterX, textCenterY + (paint.descent() - paint.ascent()) / 2, paint);
    }

    @Override
    public int getIntrinsicWidth() {
        return mSeatAvail.getIntrinsicWidth() * getSeatArrayColumn();
    }

    @Override
    public int getIntrinsicHeight() {
        /*
         *  Keep the seatDrawable bounds is Square, NOT Rectangle
         *  mSeatAvail width = 33, height = 30
         */
        return mSeatAvail.getIntrinsicWidth() * getSeatArrayRow();
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
