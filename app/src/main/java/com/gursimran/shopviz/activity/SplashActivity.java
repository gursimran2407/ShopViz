package com.gursimran.shopviz.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.gursimran.shopviz.R;

public class SplashActivity extends AppCompatActivity {

    TextView tvShopViz, tvBottom, tvTag;
    Typeface tf1, tfShopViz, tfTag;

    ShimmerFrameLayout shimmer;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101) {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // getSupportActionBar().hide();

        handler.sendEmptyMessageDelayed(101, 3000);

        shimmer = findViewById(R.id.shimmer_view_container);
        shimmer.setDropoff(0.5f);
        shimmer.setAutoStart(true);

        // tfShopViz =Typeface.createFromAsset(getAssets(),"LobsterTwo-BoldItalic.otf");
        //tfTag=Typeface.createFromAsset(getAssets(),"LobsterTwo-BoldItalic.otf");
        //tf1=Typeface.createFromAsset(getAssets(),"comic.ttf");

        tvTag = (TextView) findViewById(R.id.TextViewTag);
        tvTag.setTypeface(tfTag);

        tvShopViz = (TextView) findViewById(R.id.TextViewSplash);
        tvShopViz.setTypeface(tfShopViz);

//        tvBottom=(TextView) findViewById(R.id.textViewBottom);
//        tvBottom.setTypeface(tf1);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}