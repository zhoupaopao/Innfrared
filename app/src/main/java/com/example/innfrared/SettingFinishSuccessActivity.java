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

public class SettingFinishSuccessActivity extends Activity {
    private Button next;
    private SharedPreferences sp;
    private ImageView back;
    private String need_updata1;
    private String need_updata2;
    private TextView tv_db1;
    private TextView tv_db2;
    private String device_list="";
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
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        back=findViewById(R.id.back);
        tv_db1=findViewById(R.id.tv_db1);
        tv_db2=findViewById(R.id.tv_db2);
        device_list=getIntent().getStringExtra("device_list");
    }

    private void initData() {
        String db_list=sp.getString("db_list","");
        Log.i("initData: ", db_list);
        String[]dsa=db_list.split(",");
        if(dsa.length==2){
            need_updata1=dsa[0];
            need_updata2=dsa[1];
            tv_db2.setVisibility(View.VISIBLE);
            tv_db1.setText(dsa[0]);
            tv_db2.setText(dsa[1]);
        }else{
            need_updata1=dsa[0];
            tv_db1.setText(dsa[0]);
        }

    }

    private void initListener() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingFinishSuccessActivity.this,CheckDataActivity.class);
                intent.putExtra("device_list",device_list);
                startActivity(intent);
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
