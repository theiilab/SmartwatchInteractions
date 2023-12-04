package yuanren.tvsamrtwatch.smartwatchinteractions.models.listener;

import android.view.View;

public interface ClickListener {
    void onItemClick(View v, int position);
    void onLongItemClick(View v, int position);
}
