package yuanren.tvsamrtwatch.smartwatchinteractions.views.detail;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityDetailBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.ActionType;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Metrics;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.FileUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.playback.PlaybackActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.playback.PlaybackActivity0;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.playback.PlaybackActivity2;

public class DetailActivity extends Activity {
    private static final String TAG = "DetailActivity";
    public static final String MOVIE_ID = "selectedMovieId";

    private ActivityDetailBinding binding;
    private ConstraintLayout container;
    private ImageView movieBg;
    private TextView title;
    private TextView studio;
    private TextView category;
    private ImageButton playIB;

    private Movie movie;

    private OnGestureRegisterListener clickListener;
    private OnGestureRegisterListener longPressListener;

    /** ----- log ----- */
    private Metrics metrics;
    /** --------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** ----- log ----- */
        metrics = (Metrics) getApplicationContext();
        /** --------------- */

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        container = binding.container;
        movieBg = binding.movieBg;
        title = binding.title;
        studio = binding.studio;
        category = binding.category;
        playIB = binding.play;

        // get selected movie
        movie = MovieList.getMovie((int) getIntent().getLongExtra(MOVIE_ID, 0));
        title.setText(movie.getTitle());
        studio.setText(movie.getStudio());
        category.setText(movie.getCategory());
        Glide.with(getApplicationContext())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .into(movieBg);

        clickListener = new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

                /** ----- log ----- */
                if (!metrics.targetMovie.equals(movie.getTitle()) && (metrics.session == 1 || metrics.session == 2)) {
                    return;
                }
                // raw log
                Action action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_TAP.name, TAG, clickListener.startTime, clickListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */

                Intent intent;
                if (metrics.session == 1){
                    intent = new Intent(getApplicationContext(), PlaybackActivity.class);
                } else if (metrics.session == 2){ // session 2
                    intent = new Intent(getApplicationContext(), PlaybackActivity2.class);
                } else {
                    intent = new Intent(getApplicationContext(), PlaybackActivity0.class);
                }
                intent.putExtra(PlaybackActivity.MOVIE_ID, movie.getId());
                startActivity(intent);
            }
        };
        playIB.setOnTouchListener(clickListener);

        longPressListener = new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public boolean onLongClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);

                /** ----- log ----- */
                Action action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_LONG_PRESS.name, TAG, longPressListener.startTime, longPressListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */

                DetailActivity.super.onBackPressed();
                return true;
            }
        };
        container.setOnTouchListener(longPressListener);
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