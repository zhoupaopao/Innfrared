package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SettingLoadingActivity extends Activity {
    private ImageView back;
    private TextView title;
    private Button bt_back;
    private ProgressBar progressBar;
    private Handler handler;
//    private int nowdata=0;
    private ImageView iv_unfinish;
    private TextView tv_status;
    Runnable runnable;
    long longexpand=0;
    private String db_num1="";//电表1
    private String db_num2="";//电表2
    private SharedPreferences sp;
    private String SN="";
    private BuildBean dialog;
    private String device_list="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_data);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        back=findViewById(R.id.back);
        title=findViewById(R.id.tv_title);
        bt_back=findViewById(R.id.next);
        progressBar=findViewById(R.id.progressbar);
        iv_unfinish=findViewById(R.id.iv_unfinish);
        tv_status=findViewById(R.id.tv_status);
        longexpand=getIntent().getLongExtra("longexpand",0);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        SN=sp.getString("SN","");
    }

    private void initData() {
        title.setText(R.string.setting_db);
        tv_status.setText(R.string.setting_success);
        iv_unfinish.setImageResource(R.mipmap.finish);
        progressBar.setMax(300);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_back.setVisibility(View.GONE);
         handler=new Handler();
         runnable=new Runnable(){
            @Override
            public void run() {
// TODO Auto-generated method stub
//要做的事情
                Log.i("run: ", longexpand+"");

                progressBar.setProgress((int)longexpand);

                if(longexpand<300){
                    handler.postDelayed(this, 10);
                    longexpand++;
                }else{
                    handler.removeCallbacks(this);
//                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
                    //判断是否正确
                    String list_dbs=sp.getString("db_list","");
                    String[]dsa=list_dbs.split(",");
                    if(dsa.length==2){
                        db_num1=dsa[0];
                        db_num2=dsa[1];
                    }else{
                        db_num1=dsa[0];
                    }
                    checkdb(db_num1,db_num2);

                }
            }
        };
        handler.postDelayed(runnable, 1000);//每1秒执行一次runnable.

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
    private void checkdb(final String db1, final String db2){

        HttpRequest.get(Api.getAmmetersByDatalogerSn +SN, new JsonHttpRequestCallback() {
                        @Override
                        protected void onSuccess(Headers headers, JSONObject jsonObject) {
                            super.onSuccess(headers, jsonObject);
                            Log.i("onSuccess", jsonObject.toString());

                            dialog.dialog.dismiss();

                        }
                            @Override
                            public void onStart () {
                                super.onStart();
                                dialog = DialogUIUtils.showLoading(SettingLoadingActivity.this, "请求中...", true, true, false, true);
                                dialog.show();
                            }
                            @Override
                            public void onFailure ( int errorCode, String msg){
                                super.onFailure(errorCode, msg);
//                                Toast.makeText(AmmeterSettingActivity.this,"采集器下没有电表",Toast.LENGTH_SHORT).show();
                                dialog.dialog.dismiss();
                            }

                        @Override
                        public void onResponse(String response, Headers headers) {
                            super.onResponse(response, headers);
                            Log.i("onClick2: ", response);
                            JSONArray jsonArray=JSONArray.parseArray(response);
                            boolean isyes1=false;//电表1是否成功
                            boolean isyes2=true;//电表2是否成功

                            dialog.dialog.dismiss();
                            if(db2.equals("")){
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
                                        device_list=jsonArray.getJSONObject(i).getString("deviceId");
                                        //切换页面
                                        Log.i("isyes1: ", "312");
                                        break;
                                    }
                                }
                                if(isyes1){
                                    //成功
                                    Log.i("isyes1", "onResponse: ");
                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
                                    intent.putExtra("device_list",device_list);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    //失败
                                    Log.i("isyes1", "onResponse2: ");
                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
                                    intent.putExtra("device_list",device_list);
                                    startActivity(intent);
                                    finish();
                                }
                            }else{
                                //两个电表
                                isyes1=false;
                                isyes2=false;
                                String ddd1="";
                                String ddd2="";
                                Log.i("intent", achieve_format(db1)+"|"+achieve_format(db2));
                                for(int i=0;i<jsonArray.size();i++){
                                    if(jsonArray.getJSONObject(i).getString("sn").equals(achieve_format(db1))){
                                        isyes1=true;
                                        ddd1=jsonArray.getJSONObject(i).getString("deviceId");
//                                        Log.i("isyes1", "true");
//                                        if(device_list.equals("")){
//                                            device_list=jsonArray.getJSONObject(i).getString("deviceId");
//                                        }else{
//                                            device_list=device_list+","+jsonArray.getJSONObject(i).getString("deviceId");
//                                        }
//
//                                        continue;
                                    }
                                    if(jsonArray.getJSONObject(i).getString("sn").equals(achieve_format(db2))){
                                        isyes2=true;
                                        ddd2=jsonArray.getJSONObject(i).getString("deviceId");
//                                        Log.i("isyes2", "true");
//                                        if(device_list.equals("")){
//                                            device_list=jsonArray.getJSONObject(i).getString("deviceId");
//                                        }else{
//                                            device_list=device_list+","+jsonArray.getJSONObject(i).getString("deviceId");
//                                        }
//                                        continue;
                                    }
                                }
                                Log.i("intent", isyes1+"|"+isyes2);
                                if(isyes1&&isyes2){
                                    //配置成功界面
                                    device_list=ddd1+","+ddd2;
                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
                                    intent.putExtra("device_list",device_list);
                                    startActivity(intent);
                                    finish();
                                }else if((!isyes1)&&(!isyes2)){
                                    //配置失败
                                    device_list="";
                                    Log.i("intent", "fail: ");
                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
                                    intent.putExtra("device_list",device_list);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Log.i("intent", "failsuccess: ");
                                    //一个成功一个失败
                                    if(isyes1){
                                        device_list=ddd1;
                                    }else{
                                        device_list=ddd2;
                                    }
//                                    Toast.makeText(SettingLoadingActivity.this,"一个成功一个失败，页面开发中",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishFailSucActivity.class);
                                    intent.putExtra("isyes1",isyes1);//看1是正确还是2是正确
                                    intent.putExtra("device_list",device_list);//成功的deviceid
                                    startActivity(intent);
                                    finish();

                                }
                            }


                        }
                    });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy: ");
        handler.removeCallbacks(runnable);
    }
}
