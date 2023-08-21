package yuanren.tvsamrtwatch.smartwatchinteractions.views.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuActivity;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private static final int MENU_SEARCH = 0;
    private static final int MENU_HOME = 1;
    private static final int MENU_MOVIES = 2;
    private static final int MENU_TV = 3;
    private static final int MENU_SETTINGS = 4;

    private ActivityMainBinding binding;
    private FrameLayout container;
    private ImageView movieBg;
    private TextView movieName;
    private boolean isChannelSetUp = false;
    private Movie movie;

    public int currentSelectedMovieIndex = 0;

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
        movie = MovieList.getMovie(currentSelectedMovieIndex);
        setMovieInfo();

        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                Log.d(TAG, "Swipe right");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);

                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_RIGHT);
                setMovieInfo();
            }

            @Override
            public void onSwipeLeft(View view) {
                Log.d(TAG, "Swipe left");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                // on the edge between first movie item
                if (MovieList.getIndex() % MovieList.NUM_COLS == 0) {  // show menu list
                    currentSelectedMovieIndex = MovieList.getIndex();
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(intent);
                } else { //  slide left on movie list
                    movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_LEFT);
                    setMovieInfo();
                }
            }

            @Override
            public void onSwipeBottom(View view) {
                Log.d(TAG, "Swipe down");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);

                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_DOWN);
                setMovieInfo();
            }

            @Override
            public void onSwipeTop(View view) {
                Log.d(TAG, "Swipe up");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);

                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_UP);
                setMovieInfo();
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