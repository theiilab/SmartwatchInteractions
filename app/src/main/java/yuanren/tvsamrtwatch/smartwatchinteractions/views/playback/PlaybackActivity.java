package yuanren.tvsamrtwatch.smartwatchinteractions.views.playback;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityPlaybackBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray.XRayListActivity;

public class PlaybackActivity extends Activity {
    public static final String MOVIE_ID = "selectedMovieId";
    private ActivityPlaybackBinding binding;
    private ConstraintLayout container;
    private ImageButton control;
    private TextView title;
    private Movie movie;
    private boolean isPlayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityPlaybackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        control = binding.control;
        title = binding.title;

        // get selected movie
        movie = MovieList.getMovie((int) getIntent().getLongExtra(MOVIE_ID, 0));
        title.setText(movie.getTitle());

        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);

                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_forward_24));

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

                control.setImageDrawable(getDrawable(R.drawable.baseline_fast_rewind_24));

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
            public void onClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

                if (isPlayed) {
                    isPlayed = false;
                    control.setImageDrawable(getDrawable(R.drawable.baseline_play_arrow_24));
                } else {
                    isPlayed = true;
                    control.setImageDrawable(getDrawable(R.drawable.baseline_pause_24));
                }
            }

            @Override
            public boolean onLongClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);

                PlaybackActivity.super.onBackPressed();
                return false;
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

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

                if (isPlayed) {
                    isPlayed = false;
                    control.setImageDrawable(getDrawable(R.drawable.baseline_play_arrow_24));
                } else {
                    isPlayed = true;
                    control.setImageDrawable(getDrawable(R.drawable.baseline_pause_24));
                }
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