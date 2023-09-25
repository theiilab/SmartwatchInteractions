package yuanren.tvsamrtwatch.smartwatchinteractions.views.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivitySearchBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.OnGestureRegisterListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuActivity;
import yuanren.tvsamrtwatch.smartwatchinteractions.views.menu.MenuItemListAdapter;

public class SearchActivity extends Activity {
    private ActivitySearchBinding binding;
    private ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        container = binding.container;

        container.setOnTouchListener(new OnGestureRegisterListener(getApplicationContext()) {
            @Override
            public void onSwipeRight(View view) {

            }

            @Override
            public void onSwipeLeft(View view) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra(MenuActivity.MENU_ITEM_TYPE, MenuItemListAdapter.MENU_HOME);
                startActivity(intent);
            }

            @Override
            public void onSwipeBottom(View view) {

            }

            @Override
            public void onSwipeTop(View view) {

            }

            @Override
            public void onClick(View view) {

            }

            @Override
            public boolean onLongClick(View view) {
                return false;
            }

            @Override
            public boolean onTwoPointerTap(View view) {
                return false;
            }
        });
    }
}