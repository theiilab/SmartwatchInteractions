package yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityXrayListBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.ActionType;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Metrics;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.XRayItem;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.FileUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray_content.XRayContentActivity;

public class XRayListActivity extends Activity {
    private static final String TAG = "XRayListActivity";
    private static final int REQUEST_CODE_X_RAY_LIST = 101;

    public static final String MOVIE_ID = "selectedMovieId";
    private ActivityXrayListBinding binding;
    private ConstraintLayout container;
    private CardView infoContainer;
    private ImageView infoImage;
    private TextView infoDetails;

    private Movie movie;
    private List<XRayItem> data;
    private int index = 0;
    private OnGestureRegisterListener gestureRegisterListener;
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

        binding = ActivityXrayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        infoContainer = binding.infoContainer;
        infoImage = binding.infoImage;
        infoDetails = binding.infoDetails;

        // get selected movie
        movie = MovieList.getMovie((int) getIntent().getLongExtra(MOVIE_ID, 0));
        data = movie.getXRayItems();
        index = 0;
        setXRayCardInfo();

        gestureRegisterListener = new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
                changeXRayCard(KeyEvent.KEYCODE_DPAD_RIGHT);

                /** ----- log ----- */
                metrics.actionsPerTask++;
                metrics.swipesPerTasks++;

                Action action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_RIGHT.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */
            }

            @Override
            public void onSwipeLeft(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
                changeXRayCard(KeyEvent.KEYCODE_DPAD_LEFT);

                /** ----- log ----- */
                metrics.actionsPerTask++;
                metrics.swipesPerTasks++;

                Action action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_SWIPE_LEFT.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */
            }

            @Override
            public void onClick(View view) {
                /** ----- log ----- */
                metrics.actionsPerTask++;
                metrics.tapsPerTasks++;

                Action action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_TAP.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */

                Intent intent = new Intent(getApplicationContext(), XRayContentActivity.class);
                intent.putExtra(XRayContentActivity.MOVIE_ID, movie.getId());
                intent.putExtra(XRayContentActivity.XRAY_ID, data.get(index).getItemId());
                startActivityForResult(intent, REQUEST_CODE_X_RAY_LIST);
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                /** ----- log ----- */
                Action action = new Action(metrics, movie.getTitle(),
                        ActionType.TYPE_ACTION_TWO_FINGER_TAP.name, TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------- */

                Log.d(TAG, "onTwoPointerTap");

                // make sure every x-ray card is visited before exit this page
                if (metrics.taskNum < movie.getXRayItems().size()){
                    return true;
                }
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
                finish();
                return false;
            }
        };
        container.setOnTouchListener(gestureRegisterListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_X_RAY_LIST) {
            metrics.actionsPerTask++; // back from x-ray content page by long press
            metrics.longPressesPerTasks++;

            if (metrics.taskNum == index + 1) {
                metrics.endTime = System.currentTimeMillis();
                FileUtils.write(getApplicationContext(), metrics);
                metrics.nextTask();
                metrics.startTime = System.currentTimeMillis();
            }
        }
    }

    private void changeXRayCard(int keyEvent) {
        int out = 0;
        int in = 0;
        if (keyEvent == KeyEvent.KEYCODE_DPAD_LEFT) {
            out = R.anim.left_out;
            in = R.anim.right_in;

            // if the current is on the last one and SWIPE LEFT, don't do animation
            if (index == data.size() - 1) {
                return;
            }

            index = Math.min(data.size() - 1, index + 1);
        } else {  // right
            out = R.anim.right_out;
            in = R.anim.left_in;

            // if the current is on the first one and SWIPE RIGHT, don't do animation
            if (index == 0) {
                return;
            }

            index = Math.max(0, index - 1);
        }

        Animation slideOut = AnimationUtils.loadAnimation(getApplicationContext(), out);
        Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), in);
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                infoContainer.startAnimation(slideIn);
                setXRayCardInfo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        infoContainer.startAnimation(slideOut);
    }

    private void setXRayCardInfo() {
        infoDetails.setText(data.get(index).getName());
        Glide.with(getApplicationContext())
                .load(data.get(index).getImageUrl())
                .centerCrop()
                .into(infoImage);
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