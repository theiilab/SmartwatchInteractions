package yuanren.tvsamrtwatch.smartwatchinteractions.views.search;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivitySearchResultBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;

public class SearchResultActivity extends Activity {
    private static final String TAG = "SearchResultActivity";
    public static final String SEARCH_NAME = "searchName";
    private static final int NUM_COLS = 5;
    private ActivitySearchResultBinding binding;
    private ConstraintLayout container;
    private FrameLayout movieCard;
    private ImageView movieBg;
    private TextView movieName;
    private ImageButton indicatorLeft;
    private ImageButton indicatorRight;
    private ImageButton indicatorUp;
    private ImageButton indicatorDown;
    private List<Movie> pool;
    private List<Movie> results;
    private Movie movie;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        movieCard = binding.movieCard;
        movieName = binding.movieName;
        movieBg = binding.movieBg;
        indicatorLeft = binding.indicatorLeft;
        indicatorRight = binding.indicatorRight;
        indicatorUp = binding.indicatorUp;
        indicatorDown = binding.indicatorDown;

        pool = MovieList.setUpSearchDummyMovies();
        pool.addAll(MovieList.getRealList());
        results = getSearchResult(getIntent().getStringExtra(SEARCH_NAME));
        movie = results.get(0);
        setMovieInfo();
        setIndicator();
        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                super.onSwipeRight(view);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_RIGHT);
            }

            @Override
            public void onSwipeLeft(View view) {
                super.onSwipeLeft(view);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_LEFT);
            }

            @Override
            public void onSwipeBottom(View view) {
                super.onSwipeBottom(view);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_DOWN);
            }

            @Override
            public void onSwipeTop(View view) {
                super.onSwipeTop(view);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_UP);
            }

            @Override
            public void onClick(View view) {
                super.onClick(view);
                setResult(RESULT_OK);
                finish();
            }

//            @Override
//            public boolean onLongClick(View view) {
//                setResult(RESULT_CANCELED);
//                finish();
//                return super.onLongClick(view);
//            }
        });

        // move focus to the movie grid on TV
        new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
        new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_STEM_2) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setMovieInfo() {
        movieName.setText(movie.getTitle());
        Glide.with(getApplicationContext())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .into(movieBg);
    }

    private void changeMovie(View view, int keyEvent) {
        if (isToOutOfRow(keyEvent)) {
            return;
        }

        // set up animation for task update
        int out = 0;
        int in = 0;
        switch (keyEvent) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                out = R.anim.left_out;
                in = R.anim.right_in;
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                out = R.anim.right_out;
                in = R.anim.left_in;
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                out = R.anim.up_out;
                in = R.anim.down_in;
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                out = R.anim.down_out;
                in = R.anim.up_in;
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
                break;
        }

        Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(), out);
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), in);
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                movie = getNextMovie(keyEvent);
                movieCard.startAnimation(slideIn);
                setMovieInfo();
                setIndicator();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        movieCard.startAnimation(slideOut);
    }

    private void setIndicator() {
        boolean leftFlag = false;
        boolean rightFlag = false;
        boolean upFlag = false;
        boolean downFlag = false;

        int res = isOnEdge();
        if ((res & 1) == 1) {
            upFlag = true;
        }

        if ((res & 2) == 2) {
            downFlag = true;
        }

        if ((res & 4) == 4) {
            leftFlag = true;
        }

        if ((res & 8) == 8) {
            rightFlag = true;
        }
        indicatorLeft.setAlpha(!leftFlag ? 1 : 0.3f);
        indicatorRight.setAlpha(!rightFlag ? 1 : 0.3f);
        indicatorUp.setAlpha(!upFlag ? 1 : 0.3f);
        indicatorDown.setAlpha(!downFlag ? 1 : 0.3f);
    }

    private Movie getNextMovie(int keyEvent) {
        switch (keyEvent) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                index = Math.min(results.size() - 1, index + 1);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                index = Math.max(0, index - 1);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                index = index + NUM_COLS >= results.size() ? results.size() - 1 : index + NUM_COLS;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                index = index - NUM_COLS < 0 ? index : index - NUM_COLS;
                break;
            default:
                return null;
        }
        return results.get(index);
    }

    private boolean isToOutOfRow(int keyEvent) {
        if (keyEvent == KeyEvent.KEYCODE_DPAD_DOWN) {         // on the first row
            return index - NUM_COLS < 0;
        } else if (keyEvent == KeyEvent.KEYCODE_DPAD_UP) {    // on the last row
            return index >= NUM_COLS * (results.size() / NUM_COLS - 1);
        } else if (keyEvent == KeyEvent.KEYCODE_DPAD_RIGHT) { // on the most left
            return index % NUM_COLS == 0;
        } else if (keyEvent == KeyEvent.KEYCODE_DPAD_LEFT) {  // on the most right
            return index % NUM_COLS == NUM_COLS - 1 || index == results.size() - 1;
        }
        return false;
    }

    private int isOnEdge() {
        int res = 0;

        if (index < NUM_COLS) { // on the upper bound
            res |= 1;
        }

        if (index >= NUM_COLS * (results.size() / NUM_COLS - 1)) { // on the lower bound
            res |= 2;
        }

        if (index % NUM_COLS == 0) {  // on the left bound
            res |= 4;
        }

        if (index % NUM_COLS == NUM_COLS - 1 || index == results.size() - 1) { // ont the right bound
            res |= 8;
        }
        return res;
    }

    private List<Movie> getSearchResult(String searchName) {
        List<Movie> result = new ArrayList<>();
        Map<Movie, Integer> map = new HashMap<>();

        for (Movie movie: pool) {
//            int score = minDistance(movie.getTitle().toLowerCase(), searchName);
            int score = prefixMatch(movie.getTitle().toLowerCase(), searchName);

            if (score != 0) {
                map.put(movie, score);
            }
        }

        // Create a list from elements of HashMap
        List<Map.Entry<Movie, Integer> > list =
                new LinkedList<Map.Entry<Movie, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Movie, Integer> >() {
            @Override
            public int compare(Map.Entry<Movie, Integer> o1, Map.Entry<Movie, Integer> o2) {
                if (o1.getValue() < o2.getValue()) {
                    return -1;
                } else if (Objects.equals(o1.getValue(), o2.getValue())) {
                    return o1.getKey().getTitle().length() - o2.getKey().getTitle().length();
                } else {
                    return 1;
                }
            }
        });

        for (int i = 0; i < list.size(); ++i) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        index = 0;
    }

    private int prefixMatch(String movieName, String searchName) {
        if (searchName.length() > movieName.length()) {
            return 0;
        }

        if (searchName.equals(movieName.substring(0, searchName.length()))) {
            return searchName.length();
        }
        return 0;
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