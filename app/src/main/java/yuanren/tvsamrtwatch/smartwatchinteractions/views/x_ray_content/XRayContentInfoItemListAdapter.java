package yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray_content;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.ClickListener;

public class XRayContentInfoItemListAdapter extends RecyclerView.Adapter{

    private ClickListener clickListenerCallBack;
    private String[] descriptions;

    public XRayContentInfoItemListAdapter(ClickListener clickListenerCallBack, String[] descriptions) {
        this.clickListenerCallBack = clickListenerCallBack;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.x_ray_content_info_item, parent, false);
        return new XRayContentInfoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        XRayContentInfoItemViewHolder xRayContentInfoItemViewHolder = (XRayContentInfoItemViewHolder) holder;
        xRayContentInfoItemViewHolder.descriptionRow.setText(descriptions[position].substring(2));  // start from index 2 to skip "• " at the beginning

        xRayContentInfoItemViewHolder.descriptionRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListenerCallBack.onLongItemClick(v, xRayContentInfoItemViewHolder.getLayoutPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.length;
    }
}
