package yuanren.tvsamrtwatch.smartwatchinteractions.models.listener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnGestureRegisterListener implements View.OnTouchListener {
    public static final String TAG = "OnGestureRegisterListener";

    private static final int SWIPE_HOLD_DURATION_THRESHOLD = 800;
    private final GestureDetector gestureDetector;
    private View view;

    private float gestureX;

    public OnGestureRegisterListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getPointerCount() > 1) {
            if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                onTwoPointerTap(view);
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gestureX = event.getX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                long duration = event.getEventTime() - event.getDownTime();
                float diffX = event.getX() - gestureX;

                if (duration > SWIPE_HOLD_DURATION_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRightHold(view);
                        Log.d(TAG, "swipe right + hold gesture");
                    } else {
                        onSwipeLeftHold(view);
                        Log.d(TAG, "swipe left + hold gesture");
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                long duration = event.getEventTime() - event.getDownTime();
                float diffX = event.getX() - gestureX;

                if (duration > SWIPE_HOLD_DURATION_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRightHold(view);
                        Log.d(TAG, "swipe right + hold gesture");
                    } else {
                        onSwipeLeftHold(view);
                        Log.d(TAG, "swipe left + hold gesture");
                    }
                }
            }
        }
        this.view = view;
        return gestureDetector.onTouchEvent(event);
    }

    public void onSwipeRight(View view){}
    public void onSwipeLeft(View view){}
    public void onSwipeBottom(View view){}
    public void onSwipeTop(View view){}
    public void onSwipeRightHold(View view){}
    public void onSwipeLeftHold(View view){}
    public void onClick(View view){}
    public boolean onLongClick(View view){return false;}

    public boolean onTwoPointerTap(View view){return false;}

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 20;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick(view);
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick(view);
            return super.onSingleTapUp(e);
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
                            onSwipeRight(view);
                        } else {
                            onSwipeLeft(view);
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom(view);
                    } else {
                        onSwipeTop(view);
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}