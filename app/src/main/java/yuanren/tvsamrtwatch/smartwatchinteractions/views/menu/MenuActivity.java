package yuanren.tvsamrtwatch.smartwatchinteractions.views.menu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMainBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityMenuBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.MenuItemBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.movies.MainActivity;

public class MenuActivity extends Activity {
    public static final String TAG = "MenuActivity";

    private ActivityMenuBinding binding;
    private WearableRecyclerView recyclerView;
    private WearableRecyclerView.Adapter adapter;
    public int currentSelectedMenuItem = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.recyclerView;

        Log.d(TAG, "MenuActivity");

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        adapter = new MenuItemListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setEdgeItemsCenteringEnabled(true);
    }

    //    public void onCheckItemClicked(View view) {
//        Log.d(TAG, "Menu item selected");
//
//        Map<Integer, Integer> map = Stream.of(new Object[][] {
//                { R.id.search_icon, MENU_SEARCH},
//                { R.id.search_text, MENU_SEARCH},
//                { R.id.home_icon, MENU_HOME},
//                { R.id.home_text, MENU_HOME},
//                { R.id.movie_icon, MENU_MOVIES},
//                { R.id.movie_text, MENU_MOVIES},
//                { R.id.tv_icon, MENU_TV},
//                { R.id.tv_text, MENU_TV},
//                { R.id.setting_icon, MENU_SETTINGS},
//                { R.id.setting_text, MENU_SETTINGS},
//        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (Integer) data[1]));
//
//        int id = view.getId();
//        int diff = map.get(id) - currentSelectedMenuItem;
//        performActionBy(diff);
//        new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);
//    }

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