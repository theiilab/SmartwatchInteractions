package yuanren.tvsamrtwatch.smartwatchinteractions.views.detail;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityDetailBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.playback.PlaybackActivity;

public class DetailActivity extends Activity {
    public static final String TAG = "DetailActivity";
    public static final String MOVIE_ID = "selectedMovieId";

    private ActivityDetailBinding binding;
    private ConstraintLayout container;
    private ImageView movieBg;
    private TextView title;
    private TextView studio;
    private TextView category;
    private ImageButton playIB;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        playIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

                Intent intent = new Intent(getApplicationContext(), PlaybackActivity.class);
                intent.putExtra(PlaybackActivity.MOVIE_ID, movie.getId());
                startActivity(intent);
            }
        });

        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);

                DetailActivity.super.onBackPressed();
                return true;
            }
        });
    }

    private class SocketAsyncTask extends AsyncTask<Integer, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Integer... integers) {
            NetworkUtils.sendCommand(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}