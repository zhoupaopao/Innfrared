package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lenovo on 2019/4/1.
 */

public class SettingFinishSuccessDetailActivity extends Activity {
    private Button next;
    private ImageView back;
    private String need_updata1;
    private String need_updata2;
    private TextView tv_db1;
    private TextView tv_db2;
    private String db_num="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_finish_success);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        next=findViewById(R.id.next);
        back=findViewById(R.id.back);
        tv_db1=findViewById(R.id.tv_db1);
        tv_db2=findViewById(R.id.tv_db2);
    }

    private void initData() {
        next.setText("我知道了");
        db_num=getIntent().getStringExtra("db_num");
        tv_db1.setText("数据来源为电表： "+db_num);
    }

    private void initListener() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
