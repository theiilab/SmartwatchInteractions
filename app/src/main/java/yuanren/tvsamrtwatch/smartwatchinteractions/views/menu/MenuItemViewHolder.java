package yuanren.tvsamrtwatch.smartwatchinteractions.views.nav_menu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;

public class MenuItemViewHolder extends WearableRecyclerView.ViewHolder {
    ImageView icon;
    TextView name;

    public MenuItemViewHolder(@NonNull View itemView) {
        super(itemView);

        icon = itemView.findViewById(R.id.icon);
        name = itemView.findViewById(R.id.name);
    }
}
