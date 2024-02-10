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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.ActionType;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Metrics;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.FileUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.detail.DetailActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuItemListAdapter;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_DETAILS = 101;


    private ActivityMainBinding binding;
    private ConstraintLayout container;
    private FrameLayout movieCard;
    private ImageView movieBg;
    private TextView movieName;
    private ImageButton indicatorLeft;
    private ImageButton indicatorRight;
    private ImageButton indicatorUp;
    private ImageButton indicatorDown;
    private Movie movie;

    private OnGestureRegisterListener gestureRegisterListener;
    public int currentSelectedMovieIndex = 0;

    /** ----- log ----- */
    private Metrics metrics;
    private boolean findStartFlag = false;
    /** --------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        container = binding.container;
        movieCard = binding.movieCard;
        movieName = binding.movieName;
        movieBg = binding.movieBg;
        indicatorLeft = binding.indicatorLeft;
        indicatorRight = binding.indicatorRight;
        indicatorUp = binding.indicatorUp;
        indicatorDown = binding.indicatorDown;

        // load movie
        movie = MovieList.getMovie(getApplicationContext(), currentSelectedMovieIndex);
        setMovieInfo();

        /** ----- log ----- */
        metrics = (Metrics) getApplicationContext();
        metrics.selectedMovie = movie.getTitle();
        /** --------------- */

        gestureRegisterListener = new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                Log.d(TAG, "Swipe right");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                // on the edge between first movie item
                if (MovieList.getIndex() % MovieList.NUM_COLS == 0) {  // show menu list
                    currentSelectedMovieIndex = MovieList.getIndex();
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.putExtra(MenuActivity.MENU_ITEM_TYPE, MenuItemListAdapter.MENU_HOME);
                    startActivity(intent);
                } else { //  slide left on movie list
                    changeMovie(view, KeyEvent.KEYCODE_DPAD_RIGHT);
                }

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_RIGHT);
                /** --------------- */
            }

            @Override
            public void onSwipeLeft(View view) {
                Log.d(TAG, "Swipe left");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_LEFT);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_LEFT);
                /** --------------- */
            }

            @Override
            public void onSwipeBottom(View view) {
                Log.d(TAG, "Swipe down");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_DOWN);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_DOWN);
                /** --------------- */
            }

            @Override
            public void onSwipeTop(View view) {
                Log.d(TAG, "Swipe up");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
                changeMovie(view, KeyEvent.KEYCODE_DPAD_UP);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_SWIPE_UP);
                /** --------------- */
            }

            @Override
            public void onClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

                /** ----- log ----- */
                updateLogData(ActionType.TYPE_ACTION_TAP);
                if (metrics.targetMovie.equals(movie.getTitle())) {
                    setLogData();
                }
                /** --------------- */

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.MOVIE_ID, movie.getId());
                startActivityForResult(intent, REQUEST_CODE_DETAILS);
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                // terminate the socket
                Log.d(TAG, "Socket manually terminated");
                AndroidTVRemoteService.stopSSLCommConnection();
                finish();
                return true;
            }
        };
        container.setOnTouchListener(gestureRegisterListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** ----- log ----- */
        if (requestCode == REQUEST_CODE_DETAILS) {
            if (metrics.targetMovie.equals(metrics.selectedMovie)) {
                clearLogData();
                metrics.nextBlock();
            } else {
                // for press back button from the wrong movie details
                metrics.actionsPerTask++;
                metrics.longPressesPerTasks++;
            }
        }
        /** --------------- */
    }

    private void setMovieInfo() {
        movieName.setText(movie.getTitle());
        Glide.with(getApplicationContext())
                .load(movie.getCardImageUrl())
                .centerCrop()
                .into(movieBg);
    }

    private void changeMovie(View view, int keyEvent) {
        if (MovieList.isToOutOfRow(keyEvent)) {
            return;
        }

        // set up animation for task update
        int out = 0;
        int in = 0;
        switch (keyEvent) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                out = R.anim.left_out;
                in = R.anim.right_in;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                out = R.anim.right_out;
                in = R.anim.left_in;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                out = R.anim.up_out;
                in = R.anim.down_in;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                out = R.anim.down_out;
                in = R.anim.up_in;
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
                movie = MovieList.getNextMovie(getApplicationContext(), keyEvent);
                movieCard.startAnimation(slideIn);
                setMovieInfo();
                changeIndicator(keyEvent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        movieCard.startAnimation(slideOut);
    }

    private void changeIndicator(int keyEvent) {
        boolean leftFlag = false;
        boolean rightFlag = false;
        boolean upFlag = false;
        boolean downFlag = false;

        int res = MovieList.isOnEdge();
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

    /** ----- log ----- */
    private void updateLogData(ActionType actionType) {
        if (!findStartFlag) {
            findStartFlag = true;
            metrics.startTime = System.currentTimeMillis();
        }

        Action action;
        switch (actionType) {
            case TYPE_ACTION_SWIPE_LEFT:
                metrics.swipesPerTasks++;

                // raw log
                action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_LEFT.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                break;
            case TYPE_ACTION_SWIPE_RIGHT:
                metrics.swipesPerTasks++;

                // raw log
                action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_RIGHT.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                break;
            case TYPE_ACTION_SWIPE_UP:
                metrics.swipesPerTasks++;

                // raw log
                action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_UP.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                break;
            case TYPE_ACTION_SWIPE_DOWN:
                metrics.swipesPerTasks++;

                // raw log
                action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_DOWN.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                break;
            case TYPE_ACTION_TAP:
                metrics.tapsPerTasks++;

                // raw log
                action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_TAP.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                break;
            default:
                action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_UNKNOWN.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
        }
        metrics.actionsPerTask++;
        FileUtils.writeRaw(getApplicationContext(), action);
    }

    /** ----- log ----- */
    private void clearLogData() {
        findStartFlag = false;
    }

    /** ----- log ----- */
    private void setLogData() {
        metrics.selectedMovie = movie.getTitle();
        metrics.endTime = System.currentTimeMillis();

        // only reflect navigation time in log for session 1
        if (metrics.session == 1) {
            FileUtils.write(getApplicationContext(), metrics);
            metrics.nextTask();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidTVRemoteService.stopSSLCommConnection();
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