package com.example.innfrared.SettingPage;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.innfrared.ImBarUtils;
import com.example.innfrared.R;
import com.example.innfrared.SysApplication;

/**
 * Created by Lenovo on 2019/4/12.
 */


public class CheckFinishActivity extends Activity {
    private TextView amsg;
    private TextView bmsg;
    private ImageView back;
    private Button exit;
    private Button next;
    private TextView tv_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_finish);
//        通信线缆严禁与高压（AC>1000V,DC>1500V）电缆捆绑走线，请保持最小间距450mm
//        通信线缆一般情况下避免与低压（AC<1000V,DC<1500V）电缆捆绑走线同时避免交叉走线。
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        amsg=findViewById(R.id.amsg);
        bmsg=findViewById(R.id.bmsg);
        back=findViewById(R.id.back);
        exit=findViewById(R.id.exit);
        next=findViewById(R.id.next);
        tv_title=findViewById(R.id.tv_title);
    }

    private void initData() {
        tv_title.setText("配置电表");
        amsg.setText("通信线缆严禁与高压（AC>1000V,DC>1500V）电缆捆绑走线，请保持最小间距450mm");
        bmsg.setText("通信线缆一般情况下避免与低压（AC<1000V,DC<1500V）电缆捆绑走线同时避免交叉走线。");
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysApplication.getInstance().exit();
            }
        });
    }
}
