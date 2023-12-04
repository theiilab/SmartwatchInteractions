package yuanren.tvsamrtwatch.smartwatchinteractions.views.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.ClickListener;

public class MenuItemListAdapter extends WearableRecyclerView.Adapter {
    public static final String TAG = "MenuItemListAdapter";
    public static final int MENU_SEARCH = 0;
    public static final int MENU_HOME = 1;
    public static final int MENU_MOVIES = 2;
    public static final int MENU_TV = 3;
    public static final int MENU_SETTINGS = 4;
    private ClickListener clickListenerCallBack;
    private int[] icons;

    private String[] names;

    public MenuItemListAdapter() {

    }

    public MenuItemListAdapter(ClickListener clickListener) {
        this.clickListenerCallBack = clickListener;

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
                clickListenerCallBack.onItemClick(v, menuItemViewHolder.getLayoutPosition());
            }
        });

        menuItemViewHolder.menuItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListenerCallBack.onLongItemClick(v, menuItemViewHolder.getLayoutPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }

}
