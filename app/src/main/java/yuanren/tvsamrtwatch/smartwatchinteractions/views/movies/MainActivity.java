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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.movies.MenuItemListAdapter;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.movies.MoviesFragment;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private static final int MENU_SEARCH = 0;
    private static final int MENU_HOME = 1;
    private static final int MENU_MOVIES = 2;
    private static final int MENU_TV = 3;
    private static final int MENU_SETTINGS = 4;

    private ActivityMainBinding binding;
    private FrameLayout container;

    private MoviesFragment moviesFragment;
//    private ImageView movieBg;
//    private TextView movieName;
//    private WearableRecyclerView recyclerView;
//    private WearableRecyclerView.Adapter adapter;
//
//    private ScrollView menuContainer;
//    private boolean isChannelSetUp = false;
//    private int currentSelectedMenuItem = 1;
//    private Movie movie;
//    private boolean isMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        container = binding.container;

        moviesFragment = MoviesFragment.newInstance();
        
        // avoid duplicate fragment after screen rotation
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(container, moviesFragment)
                    .commit();
        }


//        container = binding.container;
//        movieName = binding.movieName;
//        movieBg = binding.movieBg;
////        menuContainer = binding.menuContainer;
//        recyclerView = binding.recyclerView;
//
//        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
//        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
//        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
////        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimens_10dp)));
//        adapter = new MenuItemListAdapter();
//        recyclerView.setAdapter(adapter);
//        recyclerView.setVisibility(View.GONE);
//        recyclerView.setEdgeItemsCenteringEnabled(true);
//
////        menuContainer.setVisibility(View.GONE);
//
//        // load movie
//        MovieList.setupMovies(MovieList.NUM_COLS);
//        movie = MovieList.getFirstMovie();
//
//        setMovieInfo();
//
//        // X-Ray sliding interactions
//        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
//            @Override
//            public void onSwipeRight(View view) {
//                if (isMenu) {
//                    isMenu = false;
////                    menuContainer.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.GONE);
//                    movieBg.setVisibility(View.VISIBLE);
//                    movieName.setVisibility(View.VISIBLE);
//
//                    movie = MovieList.getFirstMovie();
//                } else {
//                    movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_RIGHT);
//                }
//                setMovieInfo();
//
//                Log.d(TAG, "Swipe right");
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
//            }
//
//            @Override
//            public void onSwipeLeft(View view) {
//                // on the edge between first movie item
//                if (MovieList.getIndex() % MovieList.NUM_COLS == 0) {  // show menu list
//                    isMenu = true;
//                    movieBg.setVisibility(View.GONE);
//                    movieName.setVisibility(View.GONE);
////                    menuContainer.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                } else { //  slide left on movie list
//                    movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_LEFT);
//                    setMovieInfo();
//                }
//                Log.d(TAG, "Swipe left");
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
//            }
//
//            @Override
//            public void onSwipeBottom(View view) {
//                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_DOWN);
//                setMovieInfo();
//
//                Log.d(TAG, "Swipe down");
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
//            }
//
//            @Override
//            public void onSwipeTop(View view) {
//                movie = MovieList.getNextMovie(KeyEvent.KEYCODE_DPAD_UP);
//                setMovieInfo();
//
//                Log.d(TAG, "Swipe up");
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
//            }
//
//            @Override
//            public void onClick(View view) {
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);
//            }
//
//            @Override
//            public boolean onLongClick(View view) {
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_BACK);
//                return true;
//            }
//
//            @Override
//            public boolean onTwoPointerTap(View view) {
//                // terminate the socket
//                Log.d(TAG, "Socket manually terminated");
//                NetworkUtils.stopSSLPairingConnection();
//                finish();
//                return true;
//            }
//        });
//
////        menuContainer.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
////
////            @Override
////            public void onSwipeRight(View view) {
////
////            }
////
////            @Override
////            public void onSwipeLeft(View view) {
////                Log.d(TAG, "On menu swipe left");
////                menuContainer.setVisibility(View.GONE);
////                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
////            }
////
////            @Override
////            public void onSwipeBottom(View view) {
////
////            }
////
////            @Override
////            public void onSwipeTop(View view) {
////
////            }
////
////            @Override
////            public void onClick(View view) {
////
////            }
////
////            @Override
////            public boolean onLongClick(View view) {
////                return false;
////            }
////
////            @Override
////            public boolean onTwoPointerTap(View view) {
////                return false;
////            }
////        });
//
//        // start the SSL Socket Connection
        new SocketAsyncTask().execute();
    }

    private void setMovieInfo() {
        movieName.setText(movie.getTitle());
        Glide.with(getApplicationContext())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .into(movieBg);
    }

//    public void onCheckItemClicked(View view) {
//        Log.d(TAG, "Menu item selected");
//
//        Map<Integer, Integer> map = Stream.of(new Object[][] {
//                { R.id.search_icon, MENU_SEARCH},
//                { R.id.search_text, MENU_SEARCH},
//                { R.id.home_icon, MENU_HOME},
//                { R.id.home_text, MENU_HOME},
//                { R.id.movie_icon, MENU_MOVIES},
//                { R.id.movie_text, MENU_MOVIES},
//                { R.id.tv_icon, MENU_TV},
//                { R.id.tv_text, MENU_TV},
//                { R.id.setting_icon, MENU_SETTINGS},
//                { R.id.setting_text, MENU_SETTINGS},
//        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (Integer) data[1]));
//
//        int id = view.getId();
//        int diff = map.get(id) - currentSelectedMenuItem;
//        performActionBy(diff);
//        new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);
//    }

    private void performActionBy(int diff) {
        if (diff == 0) { return; }
        int count = diff < 0 ? diff * -1 : diff;

        if (diff < 0) {
            for (int i = 0; i < count; ++i) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
            }
        } else {
            for (int i = 0; i < count; ++i) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
            }
        }
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

    private class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        /** How much icons should scale, at most. */
        private static final float MAX_ICON_PROGRESS = 0.65f;

        private float progressToCenter;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {

            // Figure out % progress from top to bottom.
            float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            // Normalize for center.
            progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            // Adjust to the maximum scale.
            progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - progressToCenter);
            child.setScaleY(1 - progressToCenter);
        }
    }
}