package yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray_content;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;

public class XRayContentInfoItemViewHolder extends RecyclerView.ViewHolder  {
    TextView descriptionRow;

    public XRayContentInfoItemViewHolder(@NonNull View itemView) {
        super(itemView);

        descriptionRow = itemView.findViewById(R.id.row);
    }
}
