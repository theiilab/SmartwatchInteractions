package yuanren.tvsamrtwatch.smartwatchinteractions;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityLoginBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.MainActivity;

public class LoginActivity extends FragmentActivity {
    public static final String TAG = "LoginActivity";

    private FrameLayout container;
    private EditText editText;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        container = binding.container;
        editText = binding.verificationCode;

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() > 1) {
                    // terminate the socket
                    Log.d(TAG, "Socket manually terminated");
                    NetworkUtils.stopConnection();
                    finish();
                }
                return true;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    new PairingAsyncTask().execute(s.toString());
                }
            }
        });
        editText.setVisibility(View.GONE);

        // start the SSL Socket Connection
        new ChannelSetUpAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkUtils.stopConnection();
    }

    private class ChannelSetUpAsyncTask extends AsyncTask<Void, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Start Async Tasks.");
            NetworkUtils.createSSLConnection(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            editText.setVisibility(View.VISIBLE);
        }
    }

    private class PairingAsyncTask  extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            byte[] payload = NetworkUtils.encodingSecret(strings[0]);
            NetworkUtils.send(payload);
            NetworkUtils.receive();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}