package yuanren.tvsamrtwatch.smartwatchinteractions.models.listener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.ActionType;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Metrics;

public abstract class OnSwipeHoldGestureRegisterListener implements View.OnTouchListener {
    private static final String TAG = "OnSwipeHoldGestureRegisterListener";
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_HOLD_DURATION_THRESHOLD = 800;
    public final GestureDetector gestureDetector;
    private View view;

    private float gestureX;
    private boolean gestureHapticLock = false;
    public Long startTime;
    public Long endTime;
    public Long duration = 0L;
    public int swipeHoldLeftCount = 0;
    public int swipeHoldRightCount = 0;

    /** ----- log ----- */
    private Metrics metrics;
    public List<Action> swipeHolds = new ArrayList<>();
    /** --------------- */

    public OnSwipeHoldGestureRegisterListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        metrics = (Metrics) context;
    }

    public void clearSwipeHoldCounts() {
        swipeHoldLeftCount = 0;
        swipeHoldRightCount = 0;
        swipeHolds.removeAll(swipeHolds);
    }

    public void clearSwipeHoldActions() {
        swipeHolds.removeAll(swipeHolds);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTime = System.currentTimeMillis();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
        }

        if (event.getPointerCount() > 1) {
            if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                // provide haptic feedback
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                onTwoPointerTap(view);
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gestureX = event.getX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                long duration = event.getEventTime() - event.getDownTime();
                float diffX = event.getX() - gestureX;

                if (Math.abs(diffX) > SWIPE_THRESHOLD && duration > SWIPE_HOLD_DURATION_THRESHOLD) {
                    // provide haptic feedback
                    if (!gestureHapticLock) {
                        gestureHapticLock = true;
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                    }

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

                if (Math.abs(diffX) > SWIPE_THRESHOLD && duration > SWIPE_HOLD_DURATION_THRESHOLD) {
                    // provide haptic feedback
//                    if (!gestureHapticLock) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
//                    } else {
                        gestureHapticLock = false;
//                    }

                    if (diffX > 0) {
                        /** ----- log ----- */
                        swipeHoldRightCount++;  // must call before onSwipeRightHold(view), otherwise the data will be 1 less behind

                        // raw
                        Action action = new Action(metrics, metrics.selectedMovie,
                                ActionType.TYPE_ACTION_SWIPE_RIGHT_HOLD.name, TAG, startTime, endTime);
                        swipeHolds.add(action);
                        /** --------------- */

                        onSwipeRightHold(view);
                        Log.d(TAG, "swipe right + hold gesture: " + swipeHoldRightCount);
                    } else {
                        /** ----- log ----- */
                        swipeHoldLeftCount++; // must call before onSwipeRightHold(view), otherwise the data will be 1 less behind

                        // raw
                        Action action = new Action(metrics, metrics.selectedMovie,
                                ActionType.TYPE_ACTION_SWIPE_LEFT_HOLD.name, TAG, startTime, endTime);
                        swipeHolds.add(action);
                        /** --------------- */

                        onSwipeLeftHold(view);
                        Log.d(TAG, "swipe left + hold gesture: " + swipeHoldLeftCount);
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
        private static final int SWIPE_VELOCITY_THRESHOLD = 20;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            onLongClick(view);

            // provide haptic feedback
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick(view);

            // provide haptic feedback
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

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
                        // provide haptic feedback
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                        if (diffX > 0) {
                            onSwipeRight(view);
                        } else {
                            onSwipeLeft(view);
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    // provide haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

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