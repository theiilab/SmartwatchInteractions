package yuanren.tvsamrtwatch.smartwatchinteractions;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private TextView mTextView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;

        NetworkUtils.generateCertificate(getApplicationContext());

        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int count = event.getPointerCount();
                if (count > 1) {
                    NetworkUtils.stopConnection();
                }
                finish();
                return true;
            }
        });

//        new SocketAsyncTask().execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkUtils.stopConnection();
    }

    private class SocketAsyncTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Start Async Tasks.");
//            NetworkUtils.createSSLConnection(getApplicationContext());
            NetworkUtils.createConnection();
            NetworkUtils.send(new byte[]{45, 8, 2, 16, (byte) 200, 1, 82, 43, 10, 21, 105, 110, 102, 111, 46, 107, 111, 100, 111, 110, 111, 46, 97, 115, 115, 105, 115, 116, 97, 110, 116, 18, 13, 105, 110, 116, 101, 114, 102, 97, 99, 101, 32, 119, 101, 98});
            NetworkUtils.receive();
            return null;
        }
    }
}