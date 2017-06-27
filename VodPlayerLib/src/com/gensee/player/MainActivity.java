package com.gensee.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gensee.R;
import com.gensee.player.activity.PlayerActivity;
import com.gensee.vod.activity.VodPlayerActivity;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());


        findViewById(R.id.live_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String domain = "xdfjt.gensee.com";
                String number = "28147243";
                String account = "";
                String accountPwd = "";
                String joinPwd = "155226";
                String nickName = "wuxu";
                String courseId = "4344";

                Intent intent = new Intent(new Intent(MainActivity.this, PlayerActivity.class));
                intent.putExtra("domain", domain);
                intent.putExtra("nickName", nickName);
                intent.putExtra("account", account);
                intent.putExtra("accPwd", accountPwd);
                intent.putExtra("number", number);
                intent.putExtra("joinPwd", joinPwd);
                intent.putExtra("courseId", courseId);

                startActivity(intent);
            }
        });

        findViewById(R.id.vod_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String domain = "xdfjt.gensee.com";
                String nickName = "android";
                String account = "";
                String accPwd = "";
                String number = "09914970";//09914970,82360445
                String vodPwd = "536962";//536962,242175
                String courseId = "4344";

                Intent intent = new Intent(new Intent(MainActivity.this, VodPlayerActivity.class));
                intent.putExtra("domain", domain);
                intent.putExtra("nickName", nickName);
                intent.putExtra("account", account);
                intent.putExtra("accPwd", accPwd);
                intent.putExtra("number", number);
                intent.putExtra("vodPwd", vodPwd);
                intent.putExtra("courseId", courseId);

                startActivity(intent);
            }
        });
    }

    String domain = "xdfjt.gensee.com";
    String nickName = "android";
    String account = "";
    String accPwd = "";
    String number = "82360445";
    String vodPwd = "242175";
}
