package yuanren.tvsamrtwatch.smartwatchinteractions.views.menu;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.wear.widget.WearableRecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.NetworkUtils;

public class MenuItemListAdapter extends WearableRecyclerView.Adapter {
    public static final String TAG = "MenuItemListAdapter";
    public static int currentSelectedMenuItem = 1;
    private int[] icons;

    private String[] names;

    public MenuItemListAdapter() {
        icons = new int[] {R.drawable.baseline_search_24, R.drawable.baseline_home_24, R.drawable.baseline_movie_24, R.drawable.baseline_tv_24, R.drawable.baseline_settings_24};

        names = new String[] {"SEARCH", "HOME", "MOVIES", "TV CHANNELS", "SETTINGS"};
    }

    @NonNull
    @Override
    public WearableRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WearableRecyclerView.ViewHolder holder, int position) {
        MenuItemViewHolder menuItemViewHolder = (MenuItemViewHolder) holder;
        menuItemViewHolder.icon.setImageDrawable(holder.itemView.getContext().getDrawable(icons[position]));
        menuItemViewHolder.name.setText(names[position]);
        menuItemViewHolder.menuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int diff = menuItemViewHolder.getLayoutPosition() - currentSelectedMenuItem;
                performActionBy(diff);
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);
                currentSelectedMenuItem = menuItemViewHolder.getLayoutPosition();
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.length;
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
