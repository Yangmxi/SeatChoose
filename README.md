# SeatChoose
>- [Sample](#sample)
>- [Usage](#usage)
>- [Reference](#reference)

This is the demo about choose the seat of cinema. The SeatView could zoom and translate.

##Sample
You can double click the view change the size among of three size (MIN / MIDDLE / MAX).
Also, moving the view is support. Next is the screenshot of different size :

>The seat view size is MIN, it is the origin screenshot when enter the application:

![](https://github.com/Yangmxi/SeatChoose/raw/master/ImageCache/seatMin.png) 

>The seat view size is MIDDLE and TRANSLATE :

![](https://github.com/Yangmxi/SeatChoose/raw/master/ImageCache/seatMidTrans.png) 

>The seat view size is MAX and TRANSLATE :

![](https://github.com/Yangmxi/SeatChoose/raw/master/ImageCache/seatMaxTrans.png) 

##Usage
That's easy to use. Just follow next steps:

>#### Step 1: There is View in `Layout` 
>> For example: `main_layout.xml`
```javascript
    <widget.PhotoView
        android:id="@+id/photoView"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:paddingLeft="30dp"
        android:paddingTop="30dp"
        android:visibility="visible" />

>#### Step 2: Organizing the data of seat map. 
>> `Note`:   Data is `two-dimensional array`:
`int[][] array = new int[ROW][COLU];`
```java
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

>#### Step 3: Get the Row name array. 
>>Sometimes, the row name DONT like (1,2,3...), but using (A,B,C...) 
```java
     String[] data = new String[ROW_SIZE];
        for (int i = 0; i < data.length; i++) {
            data[i] = String.valueOf(i + 1);
        }

>#### Step 4: Setting data to view.
>>`Notice` New the view before setData to view.
```java
        mSeatDrawable = new SeatDrawable(this);
        photoView = (PhotoView) findViewById(R.id.photoView);
        mSeatDrawable.setSeatDate(array);
        photoView.setRowName(data);
        photoView.setImageDrawable(mSeatDrawable);

>#### Step 5: Setting the onClick event listener.
>> photoView using `setOnPhotoTapListener()`
```java
photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                int[] loca = new int[2];
                mSeatDrawable.getSeatRowColumn(loca, x, y);
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

## Reference
Thanks For Chrisbanes Helping.
> [Chrisbanes Github] (https://github.com/chrisbanes/PhotoView)
