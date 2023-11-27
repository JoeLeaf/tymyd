package com.hygzs.tymyd;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.hygzs.tymyd.customView.PathAnimTextView;
import com.hygzs.tymyd.ui.Agreement;
import com.hygzs.tymyd.ui.ChatRecords;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setExitTransition(new Explode());
//        getWindow().setEnterTransition(new Explode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PathAnimTextView textView = findViewById(R.id.patv2);
        textView.startTextAnim();
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                runOnUiThread(() -> {
                    if (SPUtils.getInstance("config").contains("isAgree")) {
                        Intent i = new Intent(this, ChatRecords.class);
                        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                        finish();
                    } else {
                        Intent i = new Intent(this, Agreement.class);
                        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                        finish();
                    }

                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}