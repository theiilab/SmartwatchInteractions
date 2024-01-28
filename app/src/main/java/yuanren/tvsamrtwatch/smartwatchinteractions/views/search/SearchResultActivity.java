package yuanren.tvsamrtwatch.smartwatchinteractions.views.search;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivitySearchResultBinding;

public class SearchResultActivity extends Activity {
    private static final String TAG = "SearchResultActivity";
    private ActivitySearchResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}