package yuanren.tvsamrtwatch.smartwatchinteractions.models.listener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.ActionType;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Session;

public abstract class OnSwipeHoldGestureRegisterListener implements View.OnTouchListener {
    private static final String TAG = "OnSwipeHoldGestureRegisterListener";
    private static final int SWIPE_THRESHOLD = 50;
    private static final int SWIPE_HOLD_DURATION_THRESHOLD = 700;
    public final GestureDetector gestureDetector;
    private View view;
    private boolean gestureHapticLock = false;
    public Long startTime;
    public Long endTime;
    public Long duration = 0L;
    public float x0 = 0;
    public float y0 = 0;
    public int swipeHoldLeftCount = 0;
    public int swipeHoldRightCount = 0;
    public boolean swipeGestureDetected = false;

    /** ----- log ----- */
    private Session session;
    public List<Action> swipeHolds = new ArrayList<>();
    /** --------------- */

    public OnSwipeHoldGestureRegisterListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        session = (Session) context;
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
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;

                // provide haptic feedback
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                onTwoPointerTap(view);
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x0 = event.getX();
                y0 = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;
                float diffX = event.getX() - x0;
                float diffY = event.getY() - y0;

                /** swipe + hold gesture */
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
                    swipeGestureDetected = true;
                } /** swipe gesture */
                else if ((Math.abs(diffX) > SWIPE_THRESHOLD || Math.abs(diffY) > SWIPE_THRESHOLD) && !swipeGestureDetected) {
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        Log.d(TAG, "onScroll - horizontal");

                        // provide haptic feedback
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                        if (diffX > 0) {
                            onSwipeRight(view);
                        } else {
                            onSwipeLeft(view);
                        }
                    } else {
                        Log.d(TAG, "onScroll - vertical");

                        // provide haptic feedback
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                        if (diffY > 0) {
                            onSwipeBottom(view);
                        } else {
                            onSwipeTop(view);
                        }
                    }
                    swipeGestureDetected = true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                long duration = event.getEventTime() - event.getDownTime();
                float diffX = event.getX() - x0;
                swipeGestureDetected = false;

                if (Math.abs(diffX) > SWIPE_THRESHOLD && duration > SWIPE_HOLD_DURATION_THRESHOLD) {
                    // provide haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                    gestureHapticLock = false;

                    if (diffX > 0) {
                        /** ----- log ----- */
                        swipeHoldRightCount++;  // must call before onSwipeRightHold(view), otherwise the data will be 1 less behind

                        // raw
                        Action action = new Action(session, session.getCurrentBlock().selectedMovie,
                                ActionType.TYPE_ACTION_SWIPE_RIGHT_HOLD.name, TAG, startTime, endTime);
                        swipeHolds.add(action);
                        /** --------------- */

                        onSwipeRightHold(view);
                        Log.d(TAG, "swipe right + hold gesture: " + swipeHoldRightCount);
                    } else {
                        /** ----- log ----- */
                        swipeHoldLeftCount++; // must call before onSwipeRightHold(view), otherwise the data will be 1 less behind

                        // raw
                        Action action = new Action(session, session.getCurrentBlock().selectedMovie,
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