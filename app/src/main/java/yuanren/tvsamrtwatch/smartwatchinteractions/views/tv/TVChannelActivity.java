package yuanren.tvsamrtwatch.smartwatchinteractions.views.tv;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivitySearchBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityTvchannelBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuItemListAdapter;

public class TVChannelActivity extends Activity {
    private static int channel = 1;
    private ActivityTvchannelBinding binding;
    private ConstraintLayout container;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityTvchannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;
        textView = binding.textView;

        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);

                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra(MenuActivity.MENU_ITEM_TYPE, MenuItemListAdapter.MENU_TV);
                startActivity(intent);
            }

            @Override
            public void onSwipeLeft(View view) {

            }

            @Override
            public void onSwipeBottom(View view) {
                channel = Math.max(1, channel + 1);
                textView.setText(String.valueOf(channel));
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
            }

            @Override
            public void onSwipeTop(View view) {
                channel = Math.min(100, channel + 1);
                textView.setText(String.valueOf(channel));
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
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
                return false;
            }
        });

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