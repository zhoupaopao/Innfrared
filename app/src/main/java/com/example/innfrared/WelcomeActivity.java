package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Lenovo on 2019/3/27.
 */

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
//                startActivity(intent);
//                WelcomeActivity.this.finish();
//            }
//        },2000);

        SharedPreferences sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        String isfirst=sp.getString("isfirst","");
        if(isfirst==""){
            Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }else{
            Intent intent=new Intent(WelcomeActivity.this,ScanQRCodeActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }

    }
}
