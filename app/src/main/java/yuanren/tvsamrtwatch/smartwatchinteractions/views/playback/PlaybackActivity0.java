package yuanren.tvsamrtwatch.smartwatchinteractions.views.playback;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;

import com.bumptech.glide.Glide;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityPlaybackBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnSwipeHoldGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray.XRayListActivity;

public class PlaybackActivity0 extends Activity {
    public static final String TAG = "PlaybackActivity0";
    public static final String MOVIE_ID = "selectedMovieId";
    public static final int VALUE_VOLUME_UNIT = 2;
    private ActivityPlaybackBinding binding;
    private ConstraintLayout container;
    private ScrollView volumeCtrl;
    private ImageView movieBg;
    private ImageButton control;
    private TextView title;
    private View cover;
    private Movie movie;
    private boolean isPlayed = true;

    private float accumulatedVolume = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityPlaybackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        volumeCtrl = binding.volumeController;
        movieBg = binding.movieBg;
        control = binding.control;
        title = binding.title;
        cover = binding.cover;

        // get selected movie
        movie = MovieList.getMovie((int) getIntent().getLongExtra(MOVIE_ID, 0));
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

        cover.setOnTouchListener(new OnSwipeHoldGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);

                // provide haptic feedback
                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END);

                // change icon
                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_forward_24));

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

            @Override
            public void onSwipeLeft(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                // provide haptic feedback
                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END);

                // change icon
                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_rewind_24));

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

            @Override
            public void onSwipeRightHold(View view) {
                Log.d(TAG, "onSwipeRightHold");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);

                // change icon
                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_forward_24));

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

            @Override
            public void onSwipeLeftHold(View view) {
                Log.d(TAG, "onSwipeLeftHold");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                // change icon
                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_rewind_24));

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

            @Override
            public void onClick(View v) {
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
            }

            @Override
            public boolean onLongClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);

                PlaybackActivity0.super.onBackPressed();
                return true;
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);

                Intent intent = new Intent(getApplicationContext(), XRayListActivity.class);
                intent.putExtra(XRayListActivity.MOVIE_ID, movie.getId());
                startActivity(intent);
                return false;
            }
        });

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
                    }

                    Log.d(TAG, "Scrolling crown: " + String.valueOf(accumulatedVolume));

                    return true;
                }
                return false;
            }
        });
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
