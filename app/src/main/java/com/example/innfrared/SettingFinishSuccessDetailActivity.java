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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.BuildBean;

import java.text.DecimalFormat;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import okhttp3.Headers;

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
    private String sn="";
    private SharedPreferences sp;
    private TextView tv_status;
    private TextView tv_status2;
    private ImageView img_status;
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
        tv_status=findViewById(R.id.tv_status);
        tv_status2=findViewById(R.id.tv_status2);
        img_status=findViewById(R.id.img_status);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
    }

    private void initData() {
        next.setText("我知道了");
        db_num=getIntent().getStringExtra("db_num");
        tv_db1.setText("数据来源为电表： "+db_num);
        sn=sp.getString("SN","");
        checkdb(db_num);
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
    private void checkdb(final String db1){
        final BuildBean[] dialog = new BuildBean[1];
        HttpRequest.get(Api.getAmmetersByDatalogerSn +sn, new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());

                dialog[0].dialog.dismiss();

            }
            @Override
            public void onStart () {
                super.onStart();
                dialog[0] = DialogUIUtils.showLoading(SettingFinishSuccessDetailActivity.this, "请求中...", true, true, false, true);
                dialog[0].show();
            }
            @Override
            public void onFailure ( int errorCode, String msg){
                super.onFailure(errorCode, msg);
//                                Toast.makeText(AmmeterSettingActivity.this,"采集器下没有电表",Toast.LENGTH_SHORT).show();
                dialog[0].dialog.dismiss();
            }

            @Override
            public void onResponse(String response, Headers headers) {
                super.onResponse(response, headers);
                Log.i("onClick2: ", response);
                JSONArray jsonArray=JSONArray.parseArray(response);
                boolean isyes1=false;//电表1是否成功
                dialog[0].dialog.dismiss();
                    //只有一个电表
                    for(int i=0;i<jsonArray.size();i++){
                        String ssn=jsonArray.getJSONObject(i).getString("sn");
                        String ddb1=achieve_format(db1).toString();
                        Log.i("onResponse: ", ssn+"|"+ddb1);
//                                    String ddbb=ddb1.substring(0,12);
                        Log.i("onResponse: ", ddb1);
                        Log.i("onResponse: ", ssn);

                        if(ssn.contains(ddb1)){
                            isyes1=true;
                            break;
                        }
                    }
                    if(isyes1){

                    }else{
                        //失败
                        img_status.setImageResource(R.mipmap.none_data);
                        tv_status.setText("无数据");
                        tv_status2.setVisibility(View.VISIBLE);
                    }


            }
        });
    }
    //自动补全12位，同时按照要求从后到前每隔2位重组
    private String achieve_format(String updata1){
        String las_sub="";
        DecimalFormat df=new DecimalFormat("000000000000");
        String str2=df.format(Long.parseLong(updata1));

        for(int i=0;i<6;i++){
            String sub_str=str2.substring(12-i*2-2, 12-i*2);
            las_sub=las_sub+sub_str;
        }
//        Log.i("AmmeterSettingActivity", "onClick: "+las_sub);
//        Log.i("AmmeterSettingActivity", "onClick: "+str2);
        return las_sub;
    }
}
