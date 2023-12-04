package yuanren.tvsamrtwatch.smartwatchinteractions.views.menu;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMenuBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.ClickListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.movies.MainActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.search.SearchActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.tv.TVChannelActivity;

public class MenuActivity extends Activity implements ClickListener {
    public static final String TAG = "MenuActivity";
    public static final String MENU_ITEM_TYPE = "TYPE";
    private ActivityMenuBinding binding;
    private WearableRecyclerView recyclerView;
    private WearableRecyclerView.Adapter adapter;
    private boolean gestureLock = false;
    public int currentSelectedMenuItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.recyclerView;
        currentSelectedMenuItem = getIntent().getIntExtra(MENU_ITEM_TYPE, 1);
        gestureLock = false;  // must reset here

        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        adapter = new MenuItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.smoothScrollToPosition(currentSelectedMenuItem);  //scroll to HOME item to the center of the screen (not effective)
    }

    @Override
    protected void onResume() {
        super.onResume();
        gestureLock = false;  // must reset here
    }

    private void performActionBy(int diff) {
        if (diff == 0) { return; }
        int count = diff < 0 ? diff * -1 : diff;

        if (diff < 0) {
            for (int i = 0; i < count; ++i) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_UP);
            }
        } else {
            for (int i = 0; i < count; ++i) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_DOWN);
            }
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        int diff = position - currentSelectedMenuItem;
        Log.d(TAG, "diff: " + String.valueOf(diff));
        performActionBy(diff);
        new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);

        currentSelectedMenuItem = position;

        Intent intent;
        switch (position) {
            case MenuItemListAdapter.MENU_SEARCH:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case MenuItemListAdapter.MENU_HOME:
            case MenuItemListAdapter.MENU_MOVIES:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case MenuItemListAdapter.MENU_TV:
                intent = new Intent(getApplicationContext(), TVChannelActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLongItemClick(View v, int position) {
        if (!gestureLock) {
            gestureLock = true;
            new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
        }
        super.onBackPressed();
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

    private class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
        /** How much icons should scale, at most. */
        private static final float MAX_ICON_PROGRESS = 0.65f;

        private float progressToCenter;

        @Override
        public void onLayoutFinished(View child, RecyclerView parent) {

            // Figure out % progress from top to bottom.
            float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
            float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

            // Normalize for center.
            progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
            // Adjust to the maximum scale.
            progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);

            child.setScaleX(1 - progressToCenter);
            child.setScaleY(1 - progressToCenter);
        }
    }
}