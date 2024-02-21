package yuanren.tvsamrtwatch.smartwatchinteractions.models.listener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public abstract class OnGestureRegisterListener implements View.OnTouchListener {
    private static final String TAG = "OnGestureRegisterListener";
    private static final int SWIPE_THRESHOLD = 50;
    private final GestureDetector gestureDetector;
    private View view;

    public Long startTime;
    public Long endTime;
    public Long duration;
    public float x0 = 0;
    public float y0 = 0;
    public boolean swipeGestureDetected = false;

    public OnGestureRegisterListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "OnGestureRegisterListener - onTouch - down");
            startTime = System.currentTimeMillis();
            x0 = event.getX();
            y0 = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "OnGestureRegisterListener - onTouch - move");
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            float diffX = event.getX() - x0;
            float diffY = event.getY() - y0;
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && !swipeGestureDetected) {
                    Log.d(TAG, "onScroll - horizontal");

                    // provide haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                    if (diffX > 0) {
                        onSwipeRight(view);
                    } else {
                        onSwipeLeft(view);
                    }
                    swipeGestureDetected = true;
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && !swipeGestureDetected) {
                    Log.d(TAG, "onScroll - vertical");

                    // provide haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                    if (diffY > 0) {
                        onSwipeBottom(view);
                    } else {
                        onSwipeTop(view);
                    }
                    swipeGestureDetected = true;
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "OnGestureRegisterListener - onTouch - up");
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            swipeGestureDetected = false;
        }

        if (event.getPointerCount() > 1) {
            if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;

                // provide haptic feedback
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                onTwoPointerTap(view);
            }
        }
        this.view = view;
        return gestureDetector.onTouchEvent(event);
    }

    public void onSwipeRight(View view){}
    public void onSwipeLeft(View view){}
    public void onSwipeBottom(View view){}
    public void onSwipeTop(View view){}
    public void onClick(View view){}
    public boolean onLongClick(View view){return false;}

    public boolean onTwoPointerTap(View view){return false;}

    private final class GestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {

        }

        @Override
        public void onLongPress(MotionEvent e) {
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            onLongClick(view);

            // provide haptic feedback
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            onClick(view);

            // provide haptic feedback
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            return true;
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}