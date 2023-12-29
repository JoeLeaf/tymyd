package com.hygzs.tymyd;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
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
                        if (SPUtils.getInstance("config").getString("channel").equals("tf")) {
                            Data.INSTANCE.setPathName(Data.INSTANCE.getPathNametf());
                            Data.INSTANCE.setApp(Data.INSTANCE.getApptf());
                        }
                        Intent i = new Intent(this, ChatRecords.class);
                        startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                        finish();
                    } else {
                        if (SPUtils.getInstance("config").contains("channel")){
                            if (SPUtils.getInstance("config").getString("channel").equals("tf")) {
                                Data.INSTANCE.setPathName(Data.INSTANCE.getPathNametf());
                                Data.INSTANCE.setApp(Data.INSTANCE.getApptf());
                            }
                            Intent i = new Intent(this, Agreement.class);
                            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                            finish();
                        }else {
                            //这里做国服或者台服的选择弹窗
                            StyledDialog.buildIosAlert("小叶子提示", "请问你是国服还是台服版？",
                                    new MyDialogListener() {
                                        @Override
                                        public void onThird() {
                                            //台服
                                            SPUtils.getInstance("config").put("channel","tf");
                                            Data.INSTANCE.setPathName(Data.INSTANCE.getPathNametf());
                                            Data.INSTANCE.setApp(Data.INSTANCE.getApptf());
                                            runOnUiThread(() -> {
                                                Intent i = new Intent(MainActivity.this, Agreement.class);
                                                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                                                finish();
                                            });
                                        }

                                        @Override
                                        public void onFirst() {
                                            //国服
                                            SPUtils.getInstance("config").put("channel","gf");
                                            runOnUiThread(() -> {
                                                Intent i = new Intent(MainActivity.this, Agreement.class);
                                                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                                                finish();
                                            });
                                        }
                                        @Override
                                        public void onSecond() {
                                            ToastUtils.showLong("。。。这都不知道啊。。。那再见！");
                                            finish();
                                        }
                                    }
                            ).setBtnText("国服","不知道","台服").show();
                        }
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}