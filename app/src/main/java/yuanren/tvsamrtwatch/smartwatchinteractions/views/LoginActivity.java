package yuanren.tvsamrtwatch.smartwatchinteractions.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityLoginBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.pairing.PairingManager;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.movies.MainActivity;

public class LoginActivity extends FragmentActivity {
    public static final String TAG = "LoginActivity";

    private FrameLayout container;
    private EditText editText;
    private TextView textView;
    private ActivityLoginBinding binding;

    private boolean isChannelSetUp = false;
    public PairingManager pairingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        container = binding.container;
        textView = binding.text;
        editText = binding.verificationCode;

        pairingManager = new PairingManager(getApplicationContext());

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() > 1) {
                    // terminate the socket
                    Log.d(TAG, "Socket manually terminated");
                    pairingManager.stopSSLPairingConnection();
                    finish();
                }
                return true;
            }
        });

        // set for always Cap letter (upper case)
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform action on key press
                    if (editText.getText().length() >= 6) {
                        new SocketAsyncTask().execute(editText.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter the pairing code from the TV", Toast.LENGTH_SHORT);
                    }
                    return true;
                }
                return false;
            }
        });
        editText.setVisibility(View.GONE);

        // start the SSL Socket Connection
        new SocketAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pairingManager.stopSSLPairingConnection();
    }

    private class SocketAsyncTask extends AsyncTask<String, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(String... strings) {
            if (!isChannelSetUp) {
                pairingManager.createSSLPairingConnection(getApplicationContext());
            } else {
                pairingManager.startPairing(strings[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (!isChannelSetUp) {
                isChannelSetUp = true;
//                editText.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    }
}