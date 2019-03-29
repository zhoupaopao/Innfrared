package com.example.innfrared.SettingPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.innfrared.ImBarUtils;
import com.example.innfrared.R;
import com.example.innfrared.SysApplication;

/**
 * Created by Lenovo on 2019/3/28.
 */

public class FastSettingActivity5 extends Activity{
    private Button next;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting5);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initListener();
    }

    private void initView() {
        back=findViewById(R.id.back);
        next=findViewById(R.id.next);
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
                Intent intent=new Intent(FastSettingActivity5.this, FastSettingActivity6.class);
                startActivity(intent);
            }
        });
    }
}
