package yuanren.tvsamrtwatch.smartwatchinteractions.models;

import android.view.View;

public interface ClickListener {
    void onItemClick(View v, int position);
    void onLongItemClick(View v, int position);
}
