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
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Block;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Session;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Task;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Point;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.QDollarRecognizer;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Result;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.socket.SearchSocketService;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.FileUtils;

public class SearchActivity extends Activity {
    private static final String TAG = "SearchActivity";
    private static final int REQUEST_CODE_SEARCH_RESULT = 101;
    private ActivitySearchBinding binding;
    private ConstraintLayout container;
    private EditText searchName;
    private DrawingView drawingView;
//    private ImageButton submitBtn;
    private QDollarRecognizer recognizer;
    private ArrayList<Point> strokePoints = new ArrayList<>();
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private int strokeNum = 1;
    private String text = "";
    private OnGestureRegisterListener gestureRegisterListener;
    private OnGestureRegisterListener tapRegisterListener;
    /** -------- log -------- */
    private Session session;
    private Block block;
    private Task task;
    private boolean startFlag = false;
    private Long actionStartTime = 0L;
    /** -------------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** -------- log -------- */
        session = (Session) getApplicationContext();
        block = session.getCurrentBlock();
        task = session.getCurrentBlock().getCurrentTask();
        /** --------------------- */

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        searchName = binding.searchName;
        drawingView = binding.drawingView;
//        submitBtn = binding.submit;

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

                /** -------- log -------- */
                task.totalCharacterEntered = text.length();
                /** --------------------- */
            }
        });

        gestureRegisterListener = new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                /** -------- log -------- */
                setTaskStartTime();
                task.actionsPerTask++;
                task.inputStream += " ";

                Action action = new Action(session, "", "SPACE", TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                FileUtils.writeRaw(getApplicationContext(), action);
                /** --------------------- */

                text += " ";
                searchName.setText(text);
            }

            @Override
            public void onSwipeLeft(View view) {
                /** -------- log -------- */
                setTaskStartTime();

                if (text.length() > 0) {
                    task.actionsPerTask++;
                    task.backspaceCount++;
                    task.inputStream += "<";

                    Action action = new Action(session, "", "DELETE", TAG, gestureRegisterListener.startTime, gestureRegisterListener.endTime);
                    FileUtils.writeRaw(getApplicationContext(), action);
                }
                /** --------------------- */

                text = text.length() == 0 ? "" : text.substring(0, text.length() - 1);
                searchName.setText(text);
            }

            @Override
            public boolean onLongClick(View view) {
//                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

//                onBackPressed();
                return false;
            }
        };
        searchName.setOnTouchListener(gestureRegisterListener);

        drawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    /** -------- log -------- */
                    setTaskStartTime();
                    actionStartTime = System.currentTimeMillis();
                    /** --------------------- */

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

                                /** -------- log -------- */
                                task.inputStream += result.name;
                                /** --------------------- */
                            } else {
                                Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT);
                            }

                            // provide haptic feedback
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

                            //clear everything
                            clearData();

                            /** -------- log -------- */
                            task.actionsPerTask++;
                            Action action = new Action(session, "", result.name, TAG, actionStartTime, System.currentTimeMillis());
                            FileUtils.writeRaw(getApplicationContext(), action);
                            /** --------------------- */
                        }
                    }, 0);
                }
                return false;
            }
        });

//        tapRegisterListener = new OnGestureRegisterListener(getApplicationContext()) {
//            @Override
//            public void onClick(View view) {
//                super.onClick(view);
//
//                /** -------- log -------- */
//                setTaskStartTime();
//                task.actionsPerTask++;
//                Action action = new Action(session, "", "SUBMIT", TAG, tapRegisterListener.startTime, tapRegisterListener.endTime);
//                FileUtils.writeRaw(getApplicationContext(), action);
//                /** --------------------- */
//
//                if (text.length() != 0) {
//                    Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
//                    intent.putExtra(SearchResultActivity.SEARCH_NAME, text);
//                    startActivityForResult(intent, REQUEST_CODE_SEARCH_RESULT);
//                }
//            }
//        };
//        submitBtn.setOnTouchListener(tapRegisterListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_STEM_1) {
            /** -------- log -------- */
            setTaskStartTime();
            task.actionsPerTask++;
            Action action = new Action(session, "", "SUBMIT", TAG, System.currentTimeMillis(), System.currentTimeMillis());
            FileUtils.writeRaw(getApplicationContext(), action);
            /** --------------------- */

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
            /** -------- log -------- */
            setLogData();
            /** --------------------- */

            text = "";
            searchName.setText(text);
        } else if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE_SEARCH_RESULT) {

        }
    }

    /** -------- log -------- */
    private void setTaskStartTime() {
        if (!startFlag) {
            startFlag = true;
            block.startTime = System.currentTimeMillis();
            task.startTime = block.startTime;
        }
    }

    private void setLogData() {
        block.actionsPerBlock += task.actionsPerTask;
        task.endTime = System.currentTimeMillis();
        task.textEntered = text;
        FileUtils.write(getApplicationContext(), task);

        if (block.id == session.SESSION_3_NUM_BLOCK && task.id == block.SESSION_3_NUM_TASK) {
            block.endTime = task.endTime;
            FileUtils.write(getApplicationContext(), block);
        } else if (block.id < session.SESSION_3_NUM_BLOCK && task.id == block.SESSION_3_NUM_TASK) {
            block.endTime = task.endTime;
            FileUtils.write(getApplicationContext(), block);

            block = session.nextBlock();
            task = block.getCurrentTask();
            block.startTime = System.currentTimeMillis();
            task.startTime = block.startTime;
        } else {
            task = block.nextTask();
            task.startTime = System.currentTimeMillis();
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