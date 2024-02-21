package yuanren.tvsamrtwatch.smartwatchinteractions.views.search;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivitySearchBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Point;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.QDollarRecognizer;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Result;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.socket.SearchSocketService;

public class SearchActivity extends Activity {
    private static final String TAG = "SearchActivity";
    private static final int REQUEST_CODE_SEARCH_RESULT = 101;
    private ActivitySearchBinding binding;
    private ConstraintLayout container;
    private EditText searchName;
    private DrawingView drawingView;
    private ImageButton submitBtn;
    private QDollarRecognizer recognizer;
    private ArrayList<Point> strokePoints = new ArrayList<>();
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private int strokeNum = 1;
    private String text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        searchName = binding.searchName;
        drawingView = binding.drawingView;
        submitBtn = binding.submit;

        searchName.setShowSoftInputOnFocus(false);
        searchName.setPressed(true);
        searchName.setSelection(0); // set cursor visible at the beginning
        searchName.requestFocus();

        // set up letter recognizer
        recognizer = new QDollarRecognizer(getApplicationContext());

        searchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchName.requestFocus();
                searchName.setPressed(true);
                searchName.setSelection(text.length()); // Move the cursor to the end

                // send command to TV
                new SocketAsyncTask2().execute(text);
            }
        });

        searchName.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                text += " ";
                searchName.setText(text);
            }

            @Override
            public void onSwipeLeft(View view) {
                text = text.length() == 0 ? "" : text.substring(0, text.length() - 1);
                searchName.setText(text);
            }

            @Override
            public boolean onLongClick(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                onBackPressed();
                return false;
            }
        });

        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    timeHandler.removeCallbacksAndMessages(null);

                    strokePoints.add(new Point(motionEvent.getX(), motionEvent.getY(), strokeNum));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    strokePoints.add(new Point(motionEvent.getX(), motionEvent.getY(), strokeNum));
                    Log.d(TAG, "x, y = : " + String.valueOf(motionEvent.getX()) + ", " + String.valueOf(motionEvent.getY()));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    strokePoints.add(new Point(motionEvent.getX(), motionEvent.getY(), strokeNum));
                    if (strokePoints.size() < 3) {
                        strokePoints.clear();
                        return false;
                    }

                    // classify gestures
                    Result result = recognizer.classify(strokePoints);

                    Log.d(TAG, "num of points: " + String.valueOf(strokePoints.size()));
                    Log.d(TAG, result.name);
                    Log.d(TAG, String.valueOf(result.score));

                    // increase num of strokes
                    strokeNum += 1;

                    timeHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (result.score > 0) {
                                text += result.name;
                                searchName.setText(text);
                            } else {
                                Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT);
                            }

                            // provide haptic feedback
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                            //clear everything
                            clearData();
                        }
                    }, 1000);
                }
                return false;
            }
        });

//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (text.length() != 0) {
//                    Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
//                    intent.putExtra(SearchResultActivity.SEARCH_NAME, text);
//                    startActivityForResult(intent, REQUEST_CODE_SEARCH_RESULT);
//                }
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() ==KeyEvent.KEYCODE_STEM_1) {
            if (text.length() != 0) {
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra(SearchResultActivity.SEARCH_NAME, text);
                startActivityForResult(intent, REQUEST_CODE_SEARCH_RESULT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SEARCH_RESULT) {
            text = "";
            searchName.setText(text);
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_SEARCH_RESULT) {

        }
    }

    private void clearData(){
        strokePoints.clear();
        strokeNum = 1;
        drawingView.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SearchSocketService.stopConnection();
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

    private class SocketAsyncTask2 extends AsyncTask<String, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(String... strings) {
            SearchSocketService.createConnection();
            SearchSocketService.send(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}