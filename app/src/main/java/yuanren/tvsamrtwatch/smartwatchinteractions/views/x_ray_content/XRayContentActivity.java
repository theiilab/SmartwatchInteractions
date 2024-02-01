package yuanren.tvsamrtwatch.smartwatchinteractions.views.x_ray_content;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import yuanren.tvsamrtwatch.smartwatchinteractions.R;
import yuanren.tvsamrtwatch.smartwatchinteractions.databinding.ActivityXrayContentBinding;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.listener.ClickListener;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.XRayItem;
import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;

public class XRayContentActivity extends Activity implements ClickListener {
    public static final String MOVIE_ID = "selectedMovieId";
    public static final String XRAY_ID = "selectedXRayItemId";
    private static final String TYPE_ITEM_ACTOR = "0";
    private static final String TYPE_ITEM_PRODUCT = "1";
    private static final String TYPE_MERCHANDISE_AMAZON = "amazon";
    private static final String TYPE_MERCHANDISE_APPLE= "apple";
    private static final String TYPE_MERCHANDISE_BESTBUY = "bestbuy";
    private static final String TYPE_MERCHANDISE_COSTCO = "costco";
    private static final String TYPE_MERCHANDISE_TARGET = "target";
    private static final String TYPE_MERCHANDISE_WALMART = "walmart";
    private ActivityXrayContentBinding binding;
    private LinearLayout container;
    private TextView title;
    private TextView price;
    private ImageButton btn1;
    private ImageButton btn2;
    private ImageButton btn3;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Movie movie;
    private XRayItem xRayItem;
    private int currentClickedButtonIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = ActivityXrayContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get selected movie
        movie = MovieList.getMovie((int) getIntent().getLongExtra(MOVIE_ID, 0));
        xRayItem = movie.getXRayItems().get((int) getIntent().getLongExtra(XRAY_ID, 0));
        String[] text = xRayItem.getDescription().split("\\;"); //System.lineSeparator())
        String name = text[0]; // common
        String actorDetail = text[1];  // for actor
        String[] actorRows = actorDetail.split("\\n");
        String prices = text[1];  // for product
        String description = text[2];
        String[] keyNotes = description.split("\\n\\n");
        String[] productRows = keyNotes[0].split("\\n");

        title = binding.title;
        price = binding.price;
        container = binding.container;
        recyclerView = binding.recyclerView;
        btn1 = binding.xRayBtn1;
        btn2 = binding.xRayBtn2;
        btn3 = binding.xRayBtn3;
        currentClickedButtonIndex = 0;

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayout.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayout);

        title.setText(name);
        if (xRayItem.getType() == TYPE_ITEM_PRODUCT) {  // product
            // set up price text view
            price.setVisibility(View.VISIBLE);
//            price2.setVisibility(View.VISIBLE);
//            price3.setVisibility(View.VISIBLE);
            int min = 20;
            int max = 40;
            double basePrice = Float.parseFloat(prices.substring(1));
            double randomDelta1 = Math.random()*(max-min+1)+min;
            double randomDelta2 = Math.random()*(max-min+1)+min;
            price.setText(prices);
//            price2.setText("$" + String.format("%.2f", basePrice + randomDelta1));
//            price3.setText("$" + String.format("%.2f", basePrice + randomDelta2));

            // set up buttons for purchase link
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn3.setVisibility(View.VISIBLE);
            String merchandises[] = xRayItem.getMerchandise().split(" ");
            btn1.setImageDrawable(getDrawable(getMerchandiseLogo(merchandises[0])));
            btn2.setImageDrawable(getDrawable(getMerchandiseLogo(merchandises[1])));
            btn3.setImageDrawable(getDrawable(getMerchandiseLogo(merchandises[2])));

            // set up details bullets
            adapter = new XRayContentInfoItemListAdapter(this, productRows);
        } else {  // actor
            price.setVisibility(View.GONE);
//            price2.setVisibility(View.GONE);
//            price3.setVisibility(View.GONE);

            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
            btn3.setVisibility(View.GONE);

            // set up details bullets
            adapter = new XRayContentInfoItemListAdapter(this, actorRows);
        }
        recyclerView.setAdapter(adapter);

        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                XRayContentActivity.super.onBackPressed();

                // provide haptic feedback
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                return false;
            }
        });

//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int diff = currentClickedButtonIndex - 0;
//                performActionBy(diff);
//                currentClickedButtonIndex = 0;
//            }
//        });
//
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int diff = currentClickedButtonIndex - 1;
//                performActionBy(diff);
//                currentClickedButtonIndex = 1;
//            }
//        });
//
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int diff = currentClickedButtonIndex - 2;
//                performActionBy(diff);
//                currentClickedButtonIndex = 2;
//            }
//        });
    }

    private int getMerchandiseLogo(String name) {
        switch (name) {
            case TYPE_MERCHANDISE_AMAZON:
                return R.drawable.logo_amazon;
            case TYPE_MERCHANDISE_APPLE:
                return R.drawable.logo_apple;
            case TYPE_MERCHANDISE_BESTBUY:
                return R.drawable.logo_bestbuy;
            case TYPE_MERCHANDISE_COSTCO:
                return R.drawable.logo_costco;
            case TYPE_MERCHANDISE_TARGET:
                return R.drawable.logo_target;
            case TYPE_MERCHANDISE_WALMART:
                return R.drawable.logo_walmart;
            default:
                return R.drawable.logo_amazon;
        }
    }

    private String getLink(String name) {
        switch (name) {
            case TYPE_MERCHANDISE_AMAZON:
                return "Amazon";
            case TYPE_MERCHANDISE_APPLE:
                return "Apple";
            case TYPE_MERCHANDISE_BESTBUY:
                return "Best Buy";
            case TYPE_MERCHANDISE_COSTCO:
                return "Costco";
            case TYPE_MERCHANDISE_TARGET:
                return "Target";
            case TYPE_MERCHANDISE_WALMART:
                return "Walmart";
            default:
                return "default";
        }
    }

    private void performActionBy(int diff) {
        if (diff == 0) { return; }
        int count = diff < 0 ? diff * -1 : diff;

        if (diff < 0) {
            for (int i = 0; i < count; ++i) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_LEFT);
            }
        } else {
            for (int i = 0; i < count; ++i) {
                new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_RIGHT);
            }
        }
        new SocketAsyncTask().execute(KeyEvent.KEYCODE_DPAD_CENTER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentClickedButtonIndex = 0;
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onLongItemClick(View v, int position) {
        XRayContentActivity.super.onBackPressed();

        // provide haptic feedback
//        v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
    }

    private class SocketAsyncTask extends AsyncTask<Integer, String, Void> {
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        protected Void doInBackground(Integer... integers) {
            AndroidTVRemoteService.sendCommand(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}