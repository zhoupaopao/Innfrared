package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class SettingFinishActivity extends Activity {
    private ImageView back;
    private Button rescan;
    private Button call;
    private TextView tv_db1;
    private TextView tv_db2;
    private String device_list="";
    private SharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinng_finish);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        back=findViewById(R.id.back);
        rescan=findViewById(R.id.rescan);
        call=findViewById(R.id.call);
        tv_db1=findViewById(R.id.tv_db1);
        tv_db2=findViewById(R.id.tv_db2);
    }

    private void initData() {
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        device_list=getIntent().getStringExtra("device_list");
        String db_list=sp.getString("db_list","");
        Log.i("initData: ", db_list);
        String[]dsa=db_list.split(",");
        if(dsa.length==2){
            tv_db2.setVisibility(View.VISIBLE);
            tv_db1.setText("数据来源为电表： "+dsa[0]);
            tv_db2.setText("数据来源为电表： "+dsa[1]);
        }else{
            tv_db1.setText("数据来源为电表： "+dsa[0]);
        }
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysApplication.getInstance().exit();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String mobile=(String) SettingFinishActivity.this.getResources().getText(R.string.num_mobile);
                Uri data = Uri.parse("tel:" + mobile);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }
}
