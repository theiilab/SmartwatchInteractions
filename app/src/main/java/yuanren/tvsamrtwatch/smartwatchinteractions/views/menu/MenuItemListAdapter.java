package yuanren.tvsamrtwatch.smartwatchinteractions.views.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;

public class MenuItemListAdapter extends WearableRecyclerView.Adapter {
    private int[] icons;

    private String[] names;

    public MenuItemListAdapter() {
        icons = new int[] {R.drawable.baseline_search_24, R.drawable.baseline_home_24, R.drawable.baseline_movie_24, R.drawable.baseline_tv_24, R.drawable.baseline_settings_24};

        names = new String[] {"Search", "Home", "Movies", "TV Channels", "Settings"};
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
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }
}
