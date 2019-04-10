package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lenovo on 2019/4/4.
 */
//一个正确一个错

public class SettingFinishFailSucActivity extends Activity {
    private boolean isyes1;
    private boolean isyes2;
    private String device_list="";
    private SharedPreferences sp;
    private ImageView back;
    private TextView tv_db1;
    private TextView data_jy;//数据校验
    private TextView fail_db;//失败的电表
    private TextView re_scan;//重新扫描
    private TextView call;
    private String true_devid="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingfinishfailsuc);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        back=findViewById(R.id.back);
        tv_db1=findViewById(R.id.tv_db1);
        data_jy=findViewById(R.id.data_jy);//数据校验
        fail_db=findViewById(R.id.fail_db);//失败的电表
        re_scan=findViewById(R.id.re_scan);//重新扫描
        call=findViewById(R.id.call);
    }

    private void initData() {
        isyes1=getIntent().getBooleanExtra("isyes1",false);
        isyes2=!isyes1;
        //看看那个是正确的
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        device_list=getIntent().getStringExtra("device_list");
        String db_list=sp.getString("db_list","");
        Log.i("initData: ", db_list);
        String[]dsa=db_list.split(",");
//        String[]lists_dev=device_list.split(",");
        if(isyes1){
            //第一组数据正确
            tv_db1.setText("电表 "+dsa[0]+"数据已上传");
            fail_db.setText("电表 "+dsa[1]);

        }else{
            //第二组数据正确
            tv_db1.setText("电表 "+dsa[1]+"数据已上传");
            fail_db.setText("电表 "+dsa[0]);
        }
//        if(dsa.length==2){
//            tv_db2.setVisibility(View.VISIBLE);
//            tv_db1.setText("数据来源为电表： "+dsa[0]);
//            tv_db2.setText("数据来源为电表： "+dsa[1]);
//        }else{
//            tv_db1.setText("数据来源为电表： "+dsa[0]);
//        }

    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        re_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysApplication.getInstance().exit();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String mobile=(String) SettingFinishFailSucActivity.this.getResources().getText(R.string.num_mobile);
                Uri data = Uri.parse("tel:" + mobile);
                intent.setData(data);
                startActivity(intent);
            }
        });
        data_jy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据校验
                Intent intent=new Intent(SettingFinishFailSucActivity.this,CheckDataActivity.class);
                intent.putExtra("isyes1",isyes1);//假如1正确
                intent.putExtra("isyes2",isyes2);//假如2正确
                intent.putExtra("device_list",device_list);
                startActivity(intent);
            }
        });

    }
}
