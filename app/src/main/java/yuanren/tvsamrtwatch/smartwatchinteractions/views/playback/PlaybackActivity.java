package yuanren.tvsamrtwatch.smartwatchinteractions.views.playback;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;

import com.bumptech.glide.Glide;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityPlaybackBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.ActionType;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Block;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Session;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Task;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnSwipeHoldGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.FileUtils;

public class PlaybackActivity extends Activity {
    private static final String TAG = "PlaybackActivity";
    public static final String MOVIE_ID = "selectedMovieId";
    public static final int VALUE_VOLUME_UNIT = 2;
    private ActivityPlaybackBinding binding;
    private ScrollView volumeCtrl;
    private ImageView movieBg;
    private ImageButton control;
    private TextView title;
    private View cover;
    private Movie movie;
    private boolean isPlayed = true;
    private float accumulatedVolume = 0;
    private OnSwipeHoldGestureRegisterListener swipeHoldGestureListener;

    /** ----- log ----- */
    private Session session;
    private Block block;
    private Task task;
    private int actionCount = 0;
    private int swipeCount = 0;
    private int swipeLeftHoldCount = 0;
    private int swipeRightHoldCount = 0;
    private int crownRotateCount = 0;
    private int tapCount = 0;

    private Handler handler = new Handler();
    private Long playStartTime = 0L;
    private Long playEndTime = 0L;
    private boolean playFlag = false;

    private Long changeVolumeStartTime = 0L;
    private Long changeVolumeEndTime = 0L;
    private int changeVolumeSemaphore = 0;
    private boolean changeVolumeFlag = false;

    private Long forwardStartTime = 0L;
    private Long forwardEndTime = 0L;
    private int forwardSemaphore = 0;
    private boolean forwardFlag = false;

    private Long pauseStartTime = 0L;
    private Long pauseEndTime = 0L;
    private int pauseSemaphore = 0;
    private boolean pauseFlag = false;

    private Long backwardStartTime = 0L;
    private Long backwardEndTime = 0L;
    private int backwardSemaphore = 0;
    private boolean backwardFlag = false;

    private Long goToEndStartTime = 0L;
    private Long goToEndEndTime = 0L;
    private Long goToEndCurTimeIndex = 0L;
    private int goToEndSemaphore = 0;
    private boolean goToEndFlag = false;


    private Long goToStartStartTime = 0L;
    private Long goToStartEndTime = 0L;
    private int goToStartSemaphore = 0;
    private boolean goToStartFlag = false;
    private boolean blockDoneFlag = false;

    private Long crownRotatesTime = 0L;
    /** --------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** ----- log ----- */
        session = (Session) getApplicationContext();
        block = session.getCurrentBlock();
        task = session.getCurrentBlock().getCurrentTask();
        playStartTime = System.currentTimeMillis();
        /** --------------- */

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityPlaybackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        volumeCtrl = binding.volumeController;
        movieBg = binding.movieBg;
        control = binding.control;
        title = binding.title;
        cover = binding.cover;

        // get selected movie
        movie = MovieList.getMovie(getApplicationContext(), (int) getIntent().getLongExtra(MOVIE_ID, 0));
        title.setText(movie.getTitle());
        Glide.with(getApplicationContext())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .into(movieBg);

        // start rotation anim for movie disk
        AnimatorSet rotate = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.rotate);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setTarget(movieBg);
        rotate.start();

        swipeHoldGestureListener = new OnSwipeHoldGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);

                animateControl(KeyEvent.KEYCODE_DPAD_RIGHT);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_RIGHT);

                Action action = new Action(session, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_RIGHT.name, TAG, swipeHoldGestureListener.startTime, swipeHoldGestureListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */
            }

            @Override
            public void onSwipeLeft(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                animateControl(KeyEvent.KEYCODE_DPAD_LEFT);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_LEFT);

                Action action = new Action(session, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_LEFT.name, TAG, swipeHoldGestureListener.startTime, swipeHoldGestureListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */
            }

            @Override
            public void onSwipeRightHold(View view) {
                Log.d(TAG, "onSwipeRightHold");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
                animateControl(KeyEvent.KEYCODE_DPAD_RIGHT);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_RIGHT_HOLD);
                /** --------------- */
            }

            @Override
            public void onSwipeLeftHold(View view) {
                Log.d(TAG, "onSwipeLeftHold");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
                animateControl(KeyEvent.KEYCODE_DPAD_LEFT);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_LEFT_HOLD);
                /** --------------- */
            }

            @Override
            public void onClick(View view) {
                super.onClick(view);
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

                if (isPlayed) {
                    isPlayed = false;
                    control.setImageDrawable(getDrawable(R.drawable.baseline_play_arrow_24));
                    rotate.pause();
                } else {
                    isPlayed = true;
                    control.setImageDrawable(getDrawable(R.drawable.baseline_pause_24));
                    rotate.resume();
                }

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_TAP);
                Action action = new Action(session, movie.getTitle(),
                        ActionType.TYPE_ACTION_TAP.name, TAG, swipeHoldGestureListener.startTime, swipeHoldGestureListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */
            }

            @Override
            public boolean onLongClick(View view) {
                /** ----- log ----- */
                // raw
                Action action = new Action(session, movie.getTitle(),
                        ActionType.TYPE_ACTION_LONG_PRESS.name, TAG, swipeHoldGestureListener.startTime, swipeHoldGestureListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);

                if (goToStartFlag && task.id == 8) {
                    goToStartEndTime = System.currentTimeMillis();
                    setLogData(goToStartStartTime, goToStartEndTime);
                    clearCounts();

                    blockDoneFlag = true;
                }
                if (!blockDoneFlag) {
                    return true;
                }
                clearLogData();
                /** --------------- */

                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);
                PlaybackActivity.super.onBackPressed();
                return true;
            }

            @Override
            public boolean onTwoPointerTap(View view) {
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);

//                Intent intent = new Intent(getApplicationContext(), XRayListActivity.class);
//                intent.putExtra(XRayListActivity.MOVIE_ID, movie.getId());
//                startActivity(intent);
                return false;
            }
        };
        cover.setOnTouchListener(swipeHoldGestureListener);

        volumeCtrl.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)) {
                    float delta = -event.getAxisValue(MotionEventCompat.AXIS_SCROLL);

                    accumulatedVolume += delta;

                    if (Math.abs(accumulatedVolume) >= VALUE_VOLUME_UNIT) {
                        if (accumulatedVolume > 0) {
                            new SocketAsyncTask().execute(KeyEvent.KEYCODE_VOLUME_UP);
                        } else {
                            new SocketAsyncTask().execute(KeyEvent.KEYCODE_VOLUME_DOWN);
                        }
                        accumulatedVolume = 0;

                        // provide haptic feedback
                        v.performHapticFeedback(HapticFeedbackConstants.GESTURE_END);

                        /** ----- log ----- */
                        updateLogData(ActionType.TYPE_ACTION_CROWN_ROTATE);
                        Action action = new Action(session, movie.getTitle(),
                                ActionType.TYPE_ACTION_CROWN_ROTATE.name, TAG, crownRotatesTime, System.currentTimeMillis());
                        FileUtils.writeRaw(getApplicationContext(), action);
                        /** --------------- */
                    }
                    /** ----- raw log ----- */
                    crownRotatesTime = System.currentTimeMillis();
                    /** ------------------- */
                    Log.d(TAG, "Scrolling crown: " + String.valueOf(accumulatedVolume));
                    return true;
                }
                return false;
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playFlag = true; // task 2 done
            }
        }, 5000);
    }

    private void animateControl(int keyEvent) {
        switch (keyEvent) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_rewind_24));
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_forward_24));
                break;
        }
        // start animation
        Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isPlayed) {
                    control.setImageDrawable(getDrawable(R.drawable.baseline_pause_24));
                } else {
                    control.setImageDrawable(getDrawable(R.drawable.baseline_play_arrow_24));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        control.startAnimation(fadeOut);
    }

    /** ----- log ----- */
    private void updateLogData(ActionType actionType) {
        switch (actionType) {
            case TYPE_ACTION_CROWN_ROTATE:
                if (playFlag && task.id == 2) {
                    // completion of task 2
                    playEndTime = System.currentTimeMillis();
                    setLogData(playStartTime, playEndTime);
                    clearCounts();

                    // on task 3
                    changeVolumeStartTime = System.currentTimeMillis();
                }
                actionCount++;
                crownRotateCount++;
                changeVolumeSemaphore++;
                if (changeVolumeSemaphore == task.crownRotatesNeeded && task.id == 3) {
                    changeVolumeFlag = true; // task 3 done
                }
                break;
            case TYPE_ACTION_SWIPE_RIGHT:
                if (changeVolumeFlag && task.id == 3) {
                    // completion of task 3
                    changeVolumeEndTime = System.currentTimeMillis();
                    setLogData(changeVolumeStartTime, changeVolumeEndTime);
                    clearCounts();

                    // on task 4
                    forwardStartTime = System.currentTimeMillis();
                }
                actionCount++;
                swipeCount++;
                forwardSemaphore++;
                if (forwardSemaphore == task.swipesNeeded && task.id == 4) {
                    forwardFlag = true; // task 4 done
                }
                break;
            case TYPE_ACTION_TAP:
                if (forwardFlag && task.id == 4) {
                    // completion of task 4
                    forwardEndTime = System.currentTimeMillis();
                    setLogData(forwardStartTime, forwardEndTime);
                    clearCounts();

                    // on task 5
                    pauseStartTime = System.currentTimeMillis();
                }
                actionCount++;
                tapCount++;
                pauseSemaphore++;
                if (pauseSemaphore == task.tapsNeeded && task.id == 5) {
                    pauseFlag = true; // task 5 done
                }
                break;
            case TYPE_ACTION_SWIPE_LEFT:
                if (pauseFlag && task.id == 5) {
                    // completion of task 5
                    pauseEndTime = System.currentTimeMillis();
                    setLogData(pauseStartTime, pauseEndTime);
                    clearCounts();

                    // on task 6
                    backwardStartTime = System.currentTimeMillis();
                }
                actionCount++;
                swipeCount++;
                backwardSemaphore++;

                if (backwardSemaphore == task.swipesNeeded && task.id == 6) {
                    backwardFlag = true; // task 6 done
                }
                break;
            case TYPE_ACTION_SWIPE_RIGHT_HOLD:
                if (backwardFlag && task.id == 6) {
                    // completion of task 6
                    backwardEndTime = System.currentTimeMillis();
                    setLogData(backwardStartTime, backwardEndTime);
                    clearCounts();

                    // on task 7
                    goToEndStartTime = System.currentTimeMillis();
                }
                goToEndSemaphore++;
                swipeRightHoldCount = swipeHoldGestureListener.swipeHoldRightCount;
                Log.d(TAG, "TYPE_ACTION_SWIPE_RIGHT_HOLD: " + swipeRightHoldCount);

                if (goToEndSemaphore == task.swipeHoldNeeded && task.id == 7) {
                    goToEndFlag = true; // task 7 done
                }

                /** ---raw for swipe + hold--- */
                for (Action action: swipeHoldGestureListener.swipeHolds) {
                    FileUtils.writeRaw(getApplicationContext(), action);
                }
                swipeHoldGestureListener.clearSwipeHoldActions();
                /** -------------------------- */
                break;
            case TYPE_ACTION_SWIPE_LEFT_HOLD:
                if (goToEndFlag && task.id == 7) {
                    // completion of task 7
                    goToEndEndTime = System.currentTimeMillis();
                    setLogData(goToEndStartTime, goToEndEndTime);
                    clearCounts();

                    // on task 8
                    goToStartStartTime = System.currentTimeMillis();
                }
                goToStartSemaphore++;
                swipeLeftHoldCount = swipeHoldGestureListener.swipeHoldLeftCount;
                Log.d(TAG, "TYPE_ACTION_SWIPE_LEFT_HOLD: " + swipeLeftHoldCount);

                if (goToStartSemaphore == task.swipeHoldNeeded && task.id == 8) {
                    goToStartFlag = true; // task 8 done
                }

                /** ---raw for swipe + hold--- */
                for (Action action: swipeHoldGestureListener.swipeHolds) {
                    FileUtils.writeRaw(getApplicationContext(), action);
                }
                swipeHoldGestureListener.clearSwipeHoldActions();
                /** -------------------------- */
                break;
        }
    }

    /** ----- log ----- */
    private void clearCounts() {
        actionCount = 0;
        swipeCount = 0;
        swipeLeftHoldCount = 0;
        swipeRightHoldCount = 0;
        crownRotateCount = 0;
        tapCount = 0;

        swipeHoldGestureListener.clearSwipeHoldCounts();

        changeVolumeSemaphore = 0;
        forwardSemaphore = 0;
        pauseSemaphore = 0;
        backwardSemaphore = 0;
        goToEndSemaphore = 0;
        goToStartSemaphore = 0;
    }

    /** ----- log ----- */
    private void clearLogData() {
        playFlag = false;
        changeVolumeFlag = false;
        forwardFlag = false;
        pauseFlag = false;
        backwardFlag = false;
        goToEndFlag = false;
        goToStartFlag = false;
        blockDoneFlag = false;

        actionCount = 0;
        swipeCount = 0;
        swipeLeftHoldCount = 0;
        swipeRightHoldCount = 0;
        crownRotateCount = 0;
        tapCount = 0;

        playStartTime = 0L;
        playEndTime = 0L;

        changeVolumeStartTime = 0L;
        changeVolumeEndTime = 0L;
        changeVolumeSemaphore = 0;

        forwardStartTime = 0L;
        forwardEndTime = 0L;
        forwardSemaphore = 0;

        pauseStartTime = 0L;
        pauseEndTime = 0L;
        pauseSemaphore = 0;

        backwardStartTime = 0L;
        backwardEndTime = 0L;
        backwardSemaphore = 0;

        goToEndStartTime = 0L;
        goToEndEndTime = 0L;
        goToEndSemaphore = 0;

        goToStartStartTime = 0L;
        goToStartEndTime = 0L;
        goToStartSemaphore = 0;

        crownRotatesTime = 0L;

        swipeHoldGestureListener.clearSwipeHoldCounts();
    }

    /** ----- log ----- */
    private void setLogData(Long startTime, Long endTime) {
        task.selectedMovie = movie.getTitle();
        task.startTime = startTime;
        task.endTime = endTime;
        task.actionsPerTask = actionCount + swipeLeftHoldCount + swipeRightHoldCount;
        task.crownRotatesPerTasks = crownRotateCount;
        task.swipesPerTasks = swipeCount;
        task.tapsPerTasks = tapCount;
        task.swipeHoldsPerTasks = swipeLeftHoldCount + swipeRightHoldCount;
        block.actionsPerBlock += task.actionsPerTask;
        FileUtils.write(getApplicationContext(), task);
        task = block.nextTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearLogData();
    }

    private class SocketAsyncTask extends AsyncTask<Integer, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Integer... integers) {
            AndroidTVRemoteService.sendCommand(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}