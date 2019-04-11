package com.example.innfrared;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.BuildBean;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import okhttp3.Headers;

/**
 * Created by Lenovo on 2019/4/1.
 */

public class CheckDbDataActivity extends Activity {
    private Button call;
    private Button pass;
    private TextView title;
    private ImageView back;
    private ImageView review;
    private SharedPreferences sp;
    private String need_updata1;
    private String need_updata2;
    private TextView tv_db1;
    private TextView tv_db2;
    private TextView zxygdn1;
    private TextView zxygdn2;
    private TextView fxygdn1;
    private TextView fxygdn2;
    private LinearLayout ll1;
    private LinearLayout ll2;
    private String device_list="";

    private String deviceId1="";
    private String deviceId2="";
    private Boolean isyes1;
    private Boolean isyes2;
    private ImageView refresh;
    private TextView updateDate1;
    private TextView updateDate2;
    private String db_list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_data);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        call=findViewById(R.id.call);
        pass=findViewById(R.id.pass);
        title=findViewById(R.id.tv_title);
        back=findViewById(R.id.back);
        review=findViewById(R.id.review);
        tv_db1=findViewById(R.id.tv_db1);
        tv_db2=findViewById(R.id.tv_db2);
        zxygdn1=findViewById(R.id.zxygdn1);
        zxygdn2=findViewById(R.id.zxygdn2);;
        fxygdn1=findViewById(R.id.fxygdn1);;
        fxygdn2=findViewById(R.id.fxygdn2);;
        ll1=findViewById(R.id.ll1);
        ll2=findViewById(R.id.ll2);
        ll2.setVisibility(View.GONE);
        refresh=findViewById(R.id.question);
        refresh.setImageResource(R.mipmap.refresh);
        updateDate1=findViewById(R.id.updateDate1);
        updateDate2=findViewById(R.id.updateDate2);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        device_list=getIntent().getStringExtra("device_list");
        db_list=getIntent().getStringExtra("db_list");
        isyes1=getIntent().getBooleanExtra("isyes1",true);
        isyes2=getIntent().getBooleanExtra("isyes2",true);
    }

    private void initData() {
        device_list=getIntent().getStringExtra("device_list");
        title.setText("数据验证");
        need_updata1=device_list;
        tv_db1.setText("正在从电表"+need_updata1+"读取数据");

        //请求数据
        achieveData(db_list,"1");
    }
    private void refresh(){
        initData();
    }
    private long getTimeMillis(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
            Toast.makeText(CheckDbDataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }
    private  void achieveData(String deviceId, final String index){
        Log.i("onClick2: ", Api.doDetail +deviceId);
        final BuildBean[] dialog = new BuildBean[1];
        HttpRequest.get(Api.doDetail +deviceId, new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());


                Handler handler=new Handler();
                Runnable runnable=new Runnable(){
                    @Override
                    public void run() {
// TODO Auto-generated method stub
//要做的事情
                        dialog[0].dialog.dismiss();
                    }
                };
                handler.postDelayed(runnable, 500);//每1秒执行一次runnable.
            }
            @Override
            public void onStart () {
                super.onStart();
                dialog[0] = DialogUIUtils.showLoading(CheckDbDataActivity.this, "请求中...", true, true, false, true);
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
//                dialog.dialog.dismiss();
                JSONObject jsonArrayyy= (JSONObject) JSONObject.parse(response);
                if(jsonArrayyy.getString("result")=="-1"){
                    Toast.makeText(CheckDbDataActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                }else{
                    JSONArray realTimeDataTotal=jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
                    JSONObject dataJSON=jsonArrayyy.getJSONObject("DeviceWapper").getJSONObject("dataJSON");
                    String updateDate=jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
                    Log.i("onResponse: ", updateDate);
                    long mill=getTimeMillis(updateDate)+8*60*60*1000;
                    Date date = new Date(mill);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateee=sdf.format(date);
                    if(index.equals("1")){
                        updateDate1.setText(dateee);
                        fxygdn1.setText(dataJSON.getString("4sc")+"kWh");
                    }else{
                        updateDate2.setText(dateee);
                        fxygdn2.setText(dataJSON.getString("4sc")+"kWh");
                    }
                    Log.i("onClick2: ", realTimeDataTotal.toString());
                    for(int i=0;i<realTimeDataTotal.size();i++){
                        JSONObject json=realTimeDataTotal.getJSONObject(i);
                        String keyy=realTimeDataTotal.getJSONObject(i).getString("key");

                        if(keyy.equals("1bk")){
                            if(index.equals("1")){
                                zxygdn1.setText(realTimeDataTotal.getJSONObject(i).getString("value")+"kWh");
                            }else{
                                zxygdn2.setText(realTimeDataTotal.getJSONObject(i).getString("value")+"kWh");
                            }

                        }
//                        if(keyy.equals("1bp")){
//                            if(index.equals("1")){
//                                fxygdn1.setText(realTimeDataTotal.getJSONObject(i).getString("value")+"kWh");
//                            }else{
//                                fxygdn2.setText(realTimeDataTotal.getJSONObject(i).getString("value")+"kWh");
//                            }
//
//                        }
                    }
                }


            }
        });
    }

    private void initListener() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String mobile=(String) CheckDbDataActivity.this.getResources().getText(R.string.num_mobile);
                Uri data = Uri.parse("tel:" + mobile);
                intent.setData(data);
                startActivity(intent);
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按理退回到扫描界面
                SysApplication.getInstance().exit();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView1 = View.inflate(CheckDbDataActivity.this, R.layout.pop_review, null);
                ImageView iv_pop=rootView1.findViewById(R.id.iv_pop);
                iv_pop.setImageResource(R.mipmap.check_review);
                final Dialog dialog1 = DialogUIUtils.showCustomAlert(CheckDbDataActivity.this, rootView1, Gravity.CENTER, true, false).show();
                //设置弹出框透明
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                rootView1.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog1);
                    }
                });
            }
        });
    }
}
