package com.example.innfrared.SettingPage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.innfrared.ImBarUtils;
import com.example.innfrared.R;
import com.example.innfrared.ScanQRCodeActivity;
import com.example.innfrared.SysApplication;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by Lenovo on 2019/3/28.
 */

public class FastSettingActivity8 extends Activity{
    private Button next;
    private ImageView back;
    private SharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting8);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initListener();
    }

    private void initView() {
        back=findViewById(R.id.back);
        next=findViewById(R.id.next);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(FastSettingActivity8.this, ScanQRCodeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                if (sp.getString("return","")=="1"){
                    SysApplication.getInstance().exit();
                }else{
                    new IntentIntegrator(FastSettingActivity8.this).setCaptureActivity(ScanQRCodeActivity.class).initiateScan();
                    SysApplication.getInstance().exit();
                }

            }
        });
    }
}
