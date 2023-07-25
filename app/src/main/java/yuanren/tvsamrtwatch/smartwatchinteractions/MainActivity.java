package yuanren.tvsamrtwatch.smartwatchinteractions;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.Random;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private TextView textView;
    private ActivityMainBinding binding;

    private String[] dummy_x_ray_items= {"item1", "item2", "item3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textView = binding.text;

        // X-Ray sliding interactions
        textView.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                Log.d(TAG, "Swipe right");
            }

            @Override
            public void onSwipeLeft(View view) {
                Random rand = new Random();
                int id = rand.nextInt(2);
                textView.setText(dummy_x_ray_items[id]);
                Log.d(TAG, "Swipe left");
            }

            @Override
            public void onSwipeBottom(View view) {
                Log.d(TAG, "Swipe down");
            }

            @Override
            public void onSwipeTop(View view) {
                Log.d(TAG, "Swipe up");
            }

            @Override
            public void onClick(View view) {

            }

            @Override
            public boolean onLongClick(View view) {
                return false;
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                // terminate the socket
                Log.d(TAG, "Socket manually terminated");
                NetworkUtils.stopConnection();
                finish();
                return true;
            }
        });

        // start the SSL Socket Connection
        new SocketAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkUtils.stopConnection();
    }

    private class SocketAsyncTask extends AsyncTask<Void, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Start Async Tasks.");
            NetworkUtils.createSSLConnection(getApplicationContext());
            return null;
        }
    }
}