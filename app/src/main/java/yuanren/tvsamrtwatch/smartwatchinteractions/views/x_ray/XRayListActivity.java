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
import yuanren.tvsamrtwatch.smartwatchinteractions.models.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.XRayItem;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray_content.XRayContentActivity;

public class XRayListActivity extends Activity {
    public static final String TAG = "XRayListActivity";
    public static final String MOVIE_ID = "selectedMovieId";
    private ActivityXrayListBinding binding;
    private ConstraintLayout container;
    private CardView infoContainer;
    private ImageView infoImage;
    private TextView infoDetails;

    private Movie movie;
    private List<XRayItem> data;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                changeXRayCard(KeyEvent.KEYCODE_DPAD_RIGHT);
            }

            @Override
            public void onSwipeLeft(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);

                changeXRayCard(KeyEvent.KEYCODE_DPAD_LEFT);
            }

            @Override
            public void onSwipeBottom(View view) {

            }

            @Override
            public void onSwipeTop(View view) {

            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), XRayContentActivity.class);
                intent.putExtra(XRayContentActivity.MOVIE_ID, movie.getId());
                intent.putExtra(XRayContentActivity.XRAY_ID, data.get(index).getItemId());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view) {
                return false;
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                Log.d(TAG, "onTwoPointerTap");
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);

                finish();
                return false;
            }
        });

    }

    private void changeXRayCard(int keyEvent) {
        int out = 0;
        int in = 0;
        if (keyEvent == KeyEvent.KEYCODE_DPAD_LEFT) {
            out = R.anim.left_out;
            in = R.anim.right_in;

            index = Math.min(data.size(), index + 1);
        } else {  // right
            out = R.anim.right_out;
            in = R.anim.left_in;

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
            NetworkUtils.sendCommand(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}