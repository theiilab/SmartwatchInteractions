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

import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityLoginBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Metrics;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.pairing.PairingManager;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.socket.RandomPositionSocketService;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.movies.MainActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.search.SearchActivity;

public class LoginActivity extends FragmentActivity {
    public static final String TAG = "LoginActivity";
    private int[] randoms;

    private FrameLayout container;
    private EditText editText;
    private TextView textView;
    private ActivityLoginBinding binding;

    private boolean isChannelSetUp = false;
    public PairingManager pairingManager;

    /** -------- log -------- */
    private Metrics metrics;
    /** -------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** -------- log -------- */
        metrics = (Metrics) getApplicationContext();
        /** --------------------- */

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        container = binding.container;
        textView = binding.text;
        editText = binding.verificationCode;

        pairingManager = new PairingManager(getApplicationContext()); // Android TV Remote Service

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
                        new AndroidTVRemotePairingAsyncTask().execute(editText.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter the pairing code from the TV", Toast.LENGTH_SHORT);
                    }
                    return true;
                }
                return false;
            }
        });
        editText.setVisibility(View.GONE);

        // get random positions via my own socket, and start android tv remote service after that
        new RandomPositionSocketAsyncTask().execute();
        // start the SSL Socket Connection for TV Remote service
        new AndroidTVRemotePairingAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pairingManager.stopSSLPairingConnection();
        RandomPositionSocketService.stopConnection();
    }
    private class RandomPositionSocketAsyncTask extends AsyncTask<String, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(String... strings) {
            // get random positions of movies from TV side
            RandomPositionSocketService.createConnection();
            String result = RandomPositionSocketService.receive();

            if (result.equals("")) { // session 3 is running
                MovieList.setUpMovies();
                return null;
            }

            // format result
            String[] tmp = result.split(";");
            String[] tmp1 = tmp[0].split(","); // pid, session id, method id
            String[] tmp2 = tmp[1].split(","); // random position indexes

            randoms = new int[tmp2.length];
            for (int i = 0; i < tmp2.length; ++i) {
                randoms[i] = Integer.parseInt(tmp2[i]);
            }
            MovieList.setUpMovies(randoms);

            /** -------- log -------- */
            metrics.init(Integer.parseInt(tmp1[0]), Integer.parseInt(tmp1[1]), tmp1[2], Integer.parseInt(tmp1[3]));
            /** --------------------- */
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            RandomPositionSocketService.stopConnection();
        }
    }

    private class AndroidTVRemotePairingAsyncTask extends AsyncTask<String, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(String... strings) {
            // Pairing with Android TV with Android TV Remote Service
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
                editText.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            } else {
                new AndroidTVRemoteConfigAsyncTask().execute();
            }
        }
    }

    private class AndroidTVRemoteConfigAsyncTask extends AsyncTask<Integer, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Integer... integers) {
            AndroidTVRemoteService.createSSLCommConnection(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            new AndroidTVRemoteInitAsyncTask().execute(KeyEvent.KEYCODE_S);
        }
    }

    private class AndroidTVRemoteInitAsyncTask extends AsyncTask<Integer, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Integer... integers) {
            AndroidTVRemoteService.sendCommand(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Intent intent;
            if (metrics.session == 3) {
                intent = new Intent(getApplicationContext(), SearchActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
            startActivity(intent);
        }
    }
}