package hcmus.studentscalendar;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

/**
 * Created by Blue on 1/1/2016.
 */
public class MonthSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public MonthSwipeTouchListener (Context ctx, GridView calendarView,
                                    CalendarAdapter calendarAdapter){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.calendarAdapter = calendarAdapter;
        this.calendarView = calendarView;
    }

    private float firstX = 0;
    private float firstY = 0;

    private GridView calendarView;
    private CalendarAdapter calendarAdapter;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isClick = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = event.getX();
                firstY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(firstX == event.getX() && firstY == event.getY()) {
                    isClick = true;
                }
                break;
        }

        if(isClick) {
            onClick(v, event);
            onClick();
            return true;
        }

        return gestureDetector.onTouchEvent(event);
    }

    private void onClick(View v, MotionEvent event) {
        int position = calendarView.pointToPosition((int)event.getX(), (int)event.getY());
        View day = calendarView.getChildAt(position);

        calendarAdapter.select(day, position);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onClick() {

    }
}
