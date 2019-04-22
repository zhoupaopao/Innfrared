package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cz.msebera.android.httpclient.Header;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    boolean isyes1=false;//电表1是否成功
    boolean isyes2=true;//电表2是否成功
    private Boolean qjbl=false;
    private String nowdevice_id="";
    private String nowdevice_id1="";
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
        progressBar.setMax(900);
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

                if(longexpand<900){

                    //判断是否有完成了，每1分钟执行一次
                    String list_dbs=sp.getString("db_list","");
                    String[]dsa=list_dbs.split(",");
                    if(dsa.length==2){
                        db_num1=dsa[0];
                        db_num2=dsa[1];
                    }else{
                        db_num1=dsa[0];
                    }
                    if(longexpand%30==0){
                        //能整除
                        jyload(db_num1,db_num2);
                    }
                    handler.postDelayed(this, 1000);
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
    private void jyload(final String db1, final String db2){

        HttpRequest.get(Api.getAmmetersByDatalogerSn +SN, new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());

            }
            @Override
            public void onStart () {
                super.onStart();
            }
            @Override
            public void onFailure ( int errorCode, String msg){
                super.onFailure(errorCode, msg);
            }

            @Override
            public void onResponse(String response, Headers headers) {
                super.onResponse(response, headers);
                Log.i("onClick2: ", response);
                JSONArray jsonArray=JSONArray.parseArray(response);
                if(db2.equals("")){
                    //只有一个电表
                    for(int i=0;i<jsonArray.size();i++){
                        String ssn=jsonArray.getJSONObject(i).getString("sn");
                        String ddb1=achieve_format(db1).toString();
                        Log.i("onResponse: ", ssn+"|"+ddb1);
                        if(ssn.contains(ddb1)){
                            isyes1=true;
                            device_list=jsonArray.getJSONObject(i).getString("deviceId");
                            //切换页面
                            //查看改数据是否是最新的，比对时间
                            nowdevice_id=device_list;
                            Thread td1=new Thread(networkTask2);
                            td1.start();
                            try {
                                td1.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.i("isyes1: ", "312");
                            break;
                        }
                    }
                    Log.i("onResponse: ", isyes1+"");
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
                            Log.i("isyes1: ", "312");
                            ddd1=jsonArray.getJSONObject(i).getString("deviceId");
//                                        isyes1=achieveData(ddd1,"1");
                            nowdevice_id1=ddd1;
                            Thread tt1=new Thread(networkTask1);
                            tt1.start();
                            try {
                                tt1.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.i("onResponse: isyes1", qjbl+"");
                            isyes1=qjbl;
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
                            Log.i("isyes2: ", "312");
                            ddd2=jsonArray.getJSONObject(i).getString("deviceId");
//                                        isyes2=achieveData(ddd2,"2");
                            nowdevice_id1=ddd2;
                            Thread tt1=new Thread(networkTask1);
                            tt1.start();
                            try {
                                tt1.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.i("onResponse: isyes2", qjbl+"");
                            isyes2=qjbl;
//                                        Log.i("isyes2", "true");
//                                        if(device_list.equals("")){
//                                            device_list=jsonArray.getJSONObject(i).getString("deviceId");
//                                        }else{
//                                            device_list=device_list+","+jsonArray.getJSONObject(i).getString("deviceId");
//                                        }
//                                        continue;
                        }
                    }
                    Log.i("onResponse: ", ddd1+"|"+ddd2);
                    Log.i("intent", isyes1+"|"+isyes2);
                    if(isyes1&&isyes2){
                        //配置成功界面
                        device_list=ddd1+","+ddd2;
                        Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
                        intent.putExtra("device_list",device_list);
                        startActivity(intent);
                        handler.removeCallbacks(runnable);
                        finish();
                    }else if((!isyes1)&&(!isyes2)){
                    }else{
                    }
                }


            }
        });
    }
    //自动补全12位，同时按照要求从后到前每隔2位重组
    private String achieve_format(String updata1){
        String las_sub="";
        DecimalFormat df=new DecimalFormat("00000000000000");
        String str2=df.format(Long.parseLong(updata1));
        str2=str2.substring(2,14);
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



                            if(db2.equals("")){
                                //只有一个电表
                                for(int i=0;i<jsonArray.size();i++){
                                    String ssn=jsonArray.getJSONObject(i).getString("sn");
                                    String ddb1=achieve_format(db1).toString();
                                    Log.i("onResponse: ", ssn+"|"+ddb1);
//                                    String ddbb=ddb1.substring(0,12);

                                    if(ssn.contains(ddb1)){
                                        isyes1=true;
                                        device_list=jsonArray.getJSONObject(i).getString("deviceId");
                                        //切换页面
                                        //查看改数据是否是最新的，比对时间
//                                        isyes1=achieveData(device_list,"1");
                                        nowdevice_id=device_list;
                                        Thread td1=new Thread(networkTask);
                                        td1.start();
                                        try {
                                            td1.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("isyes1: ", "312");
                                        break;
                                    }
                                    if (i==jsonArray.size()-1){
                                        //没有匹配到
                                        //失败
//                                    Log.i("isyes1", "onResponse2: ");
                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
                                    intent.putExtra("device_list",device_list);
                                    startActivity(intent);
                                    finish();
                                    }
                                }
                                Log.i("onResponse: ", isyes1+"");
                                dialog.dialog.dismiss();
//                                if(isyes1){
//                                    //成功
//                                    Log.i("isyes1", "onResponse: ");
//                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
//                                    intent.putExtra("device_list",device_list);
//                                    startActivity(intent);
//                                    finish();
//                                }else{
//                                    //失败
//                                    Log.i("isyes1", "onResponse2: ");
//                                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
//                                    intent.putExtra("device_list",device_list);
//                                    startActivity(intent);
//                                    finish();
//                                }
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
                                        Log.i("isyes1: ", "312");
                                        ddd1=jsonArray.getJSONObject(i).getString("deviceId");
//                                        isyes1=achieveData(ddd1,"1");
                                        nowdevice_id1=ddd1;
                                        Thread tt1=new Thread(networkTask1);
                                        tt1.start();
                                        try {
                                            tt1.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("onResponse: isyes1", qjbl+"");
                                        isyes1=qjbl;
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
                                        Log.i("isyes2: ", "312");
                                        ddd2=jsonArray.getJSONObject(i).getString("deviceId");
//                                        isyes2=achieveData(ddd2,"2");
                                        nowdevice_id1=ddd2;
                                        Thread tt1=new Thread(networkTask1);
                                        tt1.start();
                                        try {
                                            tt1.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("onResponse: isyes2", qjbl+"");
                                        isyes2=qjbl;
//                                        Log.i("isyes2", "true");
//                                        if(device_list.equals("")){
//                                            device_list=jsonArray.getJSONObject(i).getString("deviceId");
//                                        }else{
//                                            device_list=device_list+","+jsonArray.getJSONObject(i).getString("deviceId");
//                                        }
//                                        continue;
                                    }
                                }
                                dialog.dialog.dismiss();
                                Log.i("onResponse: ", ddd1+"|"+ddd2);
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
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            Boolean val = data.getBoolean("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            if(val){
                //显示数据校验
                isyes1=val;
                Log.i("isyes1", "onResponse: ");
                Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
                intent.putExtra("device_list",device_list);
                startActivity(intent);
                finish();
            }else{
                isyes1=val;
                Log.i("isyes1", "onResponse2: ");
                Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
                intent.putExtra("device_list",device_list);
                startActivity(intent);
                finish();

//                        if(longexpand>900){
//                            Log.i("onResponse: ", "无数据");
//                            img_status.setImageResource(R.mipmap.none_data);
//                            tv_status.setText("无数据");
//                        }else{
//                            //换图标
//                            Log.i("onResponse: ", "请耐心等待");
//                            img_status.setImageResource(R.mipmap.wait);
//                            tv_status.setText("无数据");
//                            tv_status2.setText("请耐心等待...");
//                        }

            }
        }
    };
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            Boolean val = data.getBoolean("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            if(val){
                //显示数据校验
                isyes1=val;
                Log.i("isyes1", "onResponse: ");
                Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
                intent.putExtra("device_list",device_list);
                startActivity(intent);
                finish();
                handler.removeCallbacks(runnable);
            }else{
            }
        }
    };

    Runnable networkTask2 = new Runnable() {

        @Override
        public void run() {
            // TODO
            final long[] mill = {0};
            // 在这里进行 http request.网络请求相关操作
            OkHttpClient okHttpClient=new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Api.doDetail +nowdevice_id)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
//                Log.i("onResponse22", response.body().string());
//                Log.i("onResponse2", response.body().string());
                JSONObject jsonArrayyy= (JSONObject) JSONObject.parse(response.body().string());
                if(jsonArrayyy.getString("result")=="-1"){
                    Toast.makeText(SettingLoadingActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                }else{
                    JSONArray realTimeDataTotal=jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
                    String updateDate=jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
                    Log.i("onResponse: ", updateDate);
                    mill[0] =getTimeMillis(updateDate)+8*60*60*1000;
                    Log.i("onResponse: ", mill[0]+"");
                    Date date = new Date(mill[0]);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateee=sdf.format(date);
                    String datee=sp.getString("lastesttime","");
                    Log.i("onResponse: ", datee);
                    Log.i("onResponse: 返回的时间", dateee);
                    //判断这个时间是否大于点击时间（数据的时间，点击时间）
                    long longexpand=getTimeExpend(dateee,datee);
                    Log.i("onResponse: 返回的时间1", longexpand+"");
                    if(longexpand>0){
                        //无数据
                        //老数据
                        isyes1=false;
                        //失败的话判断这个时间是否大于15分钟
                        qjbl =false;
                    }else{
                        //有最新数据
                        qjbl =true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putBoolean("value",qjbl);
            msg.setData(data);
            handler2.sendMessage(msg);
        }
    };
    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            final long[] mill = {0};
            // 在这里进行 http request.网络请求相关操作
            OkHttpClient okHttpClient=new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Api.doDetail +nowdevice_id)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
//                Log.i("onResponse22", response.body().string());
//                Log.i("onResponse2", response.body().string());
                JSONObject jsonArrayyy= (JSONObject) JSONObject.parse(response.body().string());
                if(jsonArrayyy.getString("result")=="-1"){
                    Toast.makeText(SettingLoadingActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                }else{
                    JSONArray realTimeDataTotal=jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
                    String updateDate=jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
                    Log.i("onResponse: ", updateDate);
                    mill[0] =getTimeMillis(updateDate)+8*60*60*1000;
                    Log.i("onResponse: ", mill[0]+"");
                    Date date = new Date(mill[0]);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateee=sdf.format(date);
                    String datee=sp.getString("lastesttime","");
                    Log.i("onResponse: ", datee);
                    Log.i("onResponse: 返回的时间", dateee);
                    //判断这个时间是否大于点击时间（数据的时间，点击时间）
                    long longexpand=getTimeExpend(dateee,datee);
                    Log.i("onResponse: 返回的时间1", longexpand+"");
                    if(longexpand>0){
                        //无数据
                        //老数据
                        isyes1=false;
                        //失败的话判断这个时间是否大于15分钟
                        qjbl =false;
                    }else{
                        //有最新数据
                        qjbl =true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putBoolean("value",qjbl);
            msg.setData(data);
            handler1.sendMessage(msg);
        }
    };
    Runnable networkTask1 = new Runnable() {

        @Override
        public void run() {
            // TODO
            final long[] mill = {0};
            Log.i("onResponse2", Api.doDetail +nowdevice_id1);
//            SyncHttpClient client=new SyncHttpClient();
////                AsyncHttpClient client=new AsyncHttpClient();
//            com.loopj.android.http.RequestParams params=new com.loopj.android.http.RequestParams();
//            client.post(Api.doDetail +nowdevice_id1, params, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                    try {
//                        String json=new String(bytes,"UTF-8").toString().trim();
//                        Log.i("onResponse2", json.toString());
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//                @Override
//                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//
//                }
//            });
            // 在这里进行 http request.网络请求相关操作
            OkHttpClient okHttpClient=new OkHttpClient();
            Log.i("onResponse2", Api.doDetail +nowdevice_id1);
            Request request = new Request.Builder()
                    .url(Api.doDetail +nowdevice_id1)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                JSONObject jsonArrayyy= (JSONObject) JSONObject.parse(response.body().string());
                Log.i("onResponse22", jsonArrayyy.toString());
                if(jsonArrayyy.getInteger("result")==-1){
                    Toast.makeText(SettingLoadingActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                }else{
                    JSONArray realTimeDataTotal=jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
                    String updateDate=jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
                    Log.i("onResponse: ", updateDate);
                    mill[0] =getTimeMillis(updateDate)+8*60*60*1000;
                    Log.i("onResponse: ", mill[0]+"");
                    Date date = new Date(mill[0]);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateee=sdf.format(date);
                    String datee=sp.getString("lastesttime","");
                    Log.i("onResponse: ", datee);
                    Log.i("onResponse: 返回的时间", dateee);
                    //判断这个时间是否大于点击时间（数据的时间，点击时间）
                    long longexpand=getTimeExpend(dateee,datee);
                    Log.i("onResponse: 返回的时间1", longexpand+"");
                    if(longexpand>0){
                        //无数据
                        //老数据
                        isyes1=false;
                        //失败的话判断这个时间是否大于15分钟
                        qjbl =false;
                    }else{
                        //有最新数据
                        qjbl =true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Message msg = new Message();
//            Bundle data = new Bundle();
//            data.putBoolean("value",qjbl);
//            msg.setData(data);
//            handler2.sendMessage(msg);
        }
    };
    private  boolean achieveData(String deviceId, final String index){
        Log.i("onClick2: ", Api.doDetail +deviceId);
        final long[] longexpand = new long[1];
        final long[] mill = {0};
        final boolean[] istr = {false};
        final BuildBean[] dialog = new BuildBean[1];
        HttpRequest.get(Api.doDetail +deviceId, new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());
//                dialog[0].dialog.dismiss();
            }
            @Override
            public void onStart () {
                super.onStart();
//                dialog[0] = DialogUIUtils.showLoading(SettingLoadingActivity.this, "请求中...", true, true, false, true);
//                dialog[0].show();
            }
            @Override
            public void onFailure ( int errorCode, String msg){
                super.onFailure(errorCode, msg);
//                dialog[0].dialog.dismiss();
            }

            @Override
            public void onResponse(String response, Headers headers) {
                super.onResponse(response, headers);
                Log.i("onClick2: ", response);
//                dialog.dialog.dismiss();
                JSONObject jsonArrayyy= (JSONObject) JSONObject.parse(response);
                if(jsonArrayyy.getString("result")=="-1"){
                    Toast.makeText(SettingLoadingActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                }else{
                    JSONArray realTimeDataTotal=jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
                    String updateDate=jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
                    Log.i("onResponse: ", updateDate);
                     mill[0] =getTimeMillis(updateDate)+8*60*60*1000;
                    Date date = new Date(mill[0]);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateee=sdf.format(date);
                    String datee=sp.getString("lastesttime","");
                    //判断这个时间是否大于点击时间（数据的时间，点击时间）
                    longexpand[0] =getTimeExpend(dateee,datee);
                    Log.i("onResponse: ", longexpand[0]+"");
                    Log.i("onResponse: ", isyes1+"");
                    if(longexpand[0] >0){
                        //无数据
                        if(index.equals("1")){
                            isyes1=false;
                            Log.i("onResponse: ", isyes1+"");
                            istr[0] =false;
                        }else{
                            Log.i("onResponse:2 ", isyes2+"");
                            isyes2=false;
                            istr[0] =false;
                        }
                    }else{
                        //有最新数据
                        Log.i("onResponse:3 ", isyes1+"");
                        istr[0] =true;
                    }


                }


            }
        });
        return istr[0];
    }
    private long getTimeExpend(String startTime, String endTime){
        //传入字串类型 2016/06/28 08:30
        long longStart = getTimeMillis(startTime); //获取开始时间毫秒数
        long longEnd = getTimeMillis(endTime);  //获取结束时间毫秒数
        long longExpend = longEnd - longStart;  //获取时间差
        long longsecond=longExpend/1000;


        return longsecond;
    }
    private long getTimeMillis(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
            Toast.makeText(SettingLoadingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy: ");
        handler.removeCallbacks(runnable);
    }
//    public class MyThread2 implements Runnable
//    {
//        private String name;
//        public void setName(String name)
//        {
//            this.name = name;
//        }
//        public void run()
//        {
//            System.out.println("hello " + name);
//        }
//        public  void main(String[] args)
//        {
//            MyThread2 myThread = new MyThread2();
//            myThread.setName("world");
//            Thread thread = new Thread(myThread);
//            thread.start();
//        }
//    }
}
