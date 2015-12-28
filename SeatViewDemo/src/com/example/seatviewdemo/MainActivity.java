
package com.example.seatviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import widget.PhotoView;
import widget.PhotoViewAttacher.OnPhotoTapListener;

public class MainActivity extends Activity {

    public static final int ROW = 8;
    private static final int COLU = SeatDrawable.MAX_ROW_SEAT;
    private SeatDrawable mSeatDrawable;
    private PhotoView photoView;

    private int[][] array = new int[ROW][COLU];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        //initLoverData();

        String[] data = new String[MainActivity.ROW];
        for (int i = 0; i < data.length; i++) {
            data[i] = String.valueOf(i + 1);
        }
        mSeatDrawable = new SeatDrawable(this);
        photoView = (PhotoView) findViewById(R.id.photoView);

        mSeatDrawable.setSeatDate(array);

        photoView.setRowName(data);
        photoView.setImageDrawable(mSeatDrawable);

        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                Log.e("ymx", " ( " + x + " , " + y + ")");
                int[] loca = new int[2];
                mSeatDrawable.getSeatRowColumn(loca, x, y);
                Log.e("ymx", "Row, column = " + loca[0] + " , " + loca[1]);
                int status = mSeatDrawable.getSeatStatus(loca);
                switch (status) {
                    case SeatDrawable.TYPE_AVAIL:
                        mSeatDrawable.setSeatStatus(loca, SeatDrawable.TYPE_SELECTED, false);
                        break;
                    case SeatDrawable.TYPE_SELECTED:
                        mSeatDrawable.setSeatStatus(loca, SeatDrawable.TYPE_AVAIL, false);
                        break;
                    case SeatDrawable.TYPE_LOVER_AVAIL_LEFT:
                        mSeatDrawable.setSeatStatus(loca, SeatDrawable.TYPE_LOVER_SELECTED_LEFT, true);
                        break;
                    case SeatDrawable.TYPE_LOVER_SELECTED_LEFT:
                        mSeatDrawable.setSeatStatus(loca, SeatDrawable.TYPE_LOVER_AVAIL_LEFT, true);
                        break;
                    case SeatDrawable.TYPE_LOVER_AVAIL_RIGHT:
                        mSeatDrawable.setSeatStatus(loca, SeatDrawable.TYPE_LOVER_SELECTED_RIGHT, true);
                        break;
                    case SeatDrawable.TYPE_LOVER_SELECTED_RIGHT:
                        mSeatDrawable.setSeatStatus(loca, SeatDrawable.TYPE_LOVER_AVAIL_RIGHT, true);
                    default:
                        break;
                }
            }
        });

    }

    private void initData() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLU; j++) {
                if (j == 3) {
                    array[i][j] = SeatDrawable.TYPE_SPACE;
                } else if (j == 6) {
                    array[i][j] = SeatDrawable.TYPE_INVISIBLE;
                } else if (j == 8) {
                    array[i][j] = SeatDrawable.TYPE_UNAVAIL;
                } else {
                    array[i][j] = SeatDrawable.TYPE_AVAIL;
                }
            }
        }
    }

    private void initLoverData() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLU; j++) {
                if (j == 2 || j == 5 || j == 8) {
                    array[i][j] = SeatDrawable.TYPE_SPACE;
                } else if (j == 3 || j == 4) {
                    array[i][j] = SeatDrawable.TYPE_UNAVAIL;
                } else if (j == 6) {
                    array[i][j] = SeatDrawable.TYPE_LOVER_AVAIL_LEFT;
                } else if (j == 7) {
                    array[i][j] = SeatDrawable.TYPE_LOVER_AVAIL_RIGHT;
                } else {
                    array[i][j] = SeatDrawable.TYPE_AVAIL;
                }
            }
        }
    }
}
