package yuanren.tvsamrtwatch.smartwatchinteractions.views;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import java.util.Random;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private FrameLayout container;
    private ImageView movieBg;
    private TextView movieName;
    private boolean isChannelSetUp = false;

    private Movie movie;

    private boolean isMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        container = binding.container;
        movieName = binding.movieName;
        movieBg = binding.movieBg;

        // load movie
        MovieList.setupMovies(MovieList.NUM_COLS);
        movie = MovieList.getFirstMovie();

        setMovieInfo();

        // X-Ray sliding interactions
        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                if (isMenu) {
                    isMenu = false;
                    movieBg.setVisibility(View.VISIBLE);
                    movieName.setVisibility(View.VISIBLE);

                    movie = MovieList.getFirstMovie();
                } else {
                    movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_RIGHT);
                }
                setMovieInfo();

                Log.d(TAG, "Swipe right");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
            }

            @Override
            public void onSwipeLeft(View view) {
                // on the edge between first movie item
                if (MovieList.getIndex() % MovieList.NUM_COLS == 0) {  // show menu list
                    isMenu = true;
                    movieBg.setVisibility(View.GONE);
                    movieName.setVisibility(View.GONE);

                    Log.d(TAG, "menu");
                } else { //  slide left on movie list
                    movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_LEFT);
                    setMovieInfo();
                }
                Log.d(TAG, "Swipe left");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
            }

            @Override
            public void onSwipeBottom(View view) {
                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_DOWN);
                setMovieInfo();

                Log.d(TAG, "Swipe down");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
            }

            @Override
            public void onSwipeTop(View view) {
                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_UP);
                setMovieInfo();

                Log.d(TAG, "Swipe up");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
            }

            @Override
            public void onClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);
            }

            @Override
            public boolean onLongClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);
                return true;
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                // terminate the socket
                Log.d(TAG, "Socket manually terminated");
                NetworkUtils.stopSSLPairingConnection();
                finish();
                return true;
            }
        });

        // start the SSL Socket Connection
        new SocketAsyncTask().execute();
    }

    private void setMovieInfo() {
        movieName.setText(movie.getTitle());
        Glide.with(getApplicationContext())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .into(movieBg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkUtils.stopSSLPairingConnection();
    }

    private class SocketAsyncTask extends AsyncTask<Integer, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Integer... integers) {
            if (!isChannelSetUp) {
                isChannelSetUp = true;
                NetworkUtils.createSSLCommConnection();
            } else {
                NetworkUtils.sendCommand(integers[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}