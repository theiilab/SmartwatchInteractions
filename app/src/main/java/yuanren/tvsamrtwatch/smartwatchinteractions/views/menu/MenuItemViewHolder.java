package yuanren.tvsamrtwatch.smartwatchinteractions.views.menu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.wear.widget.WearableRecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;

public class MenuItemViewHolder extends WearableRecyclerView.ViewHolder {
    ConstraintLayout menuItem;
    ImageView icon;
    TextView name;

    public MenuItemViewHolder(@NonNull View itemView) {
        super(itemView);
        menuItem = itemView.findViewById(R.id.menu_item);
        icon = itemView.findViewById(R.id.icon);
        name = itemView.findViewById(R.id.name);
    }


}
