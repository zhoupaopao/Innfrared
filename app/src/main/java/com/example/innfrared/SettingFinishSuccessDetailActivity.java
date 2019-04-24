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
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private String db_num = "";
    private String sn = "";
    private SharedPreferences sp;
    private TextView tv_status;
    private TextView tv_status2;
    private ImageView img_status;
    boolean isyes1 = false;//电表1是否成功
    private String db_devid = "";
    private Boolean qjbl = false;
    private String device_list = "";
    //    final BuildBean[] dialog = new BuildBean[1];
    private BuildBean dialog;
    private TextView pz_status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_finish_success);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        tv_db1 = findViewById(R.id.tv_db1);
        tv_db2 = findViewById(R.id.tv_db2);
        tv_status = findViewById(R.id.tv_status);
        tv_status2 = findViewById(R.id.tv_status2);
        img_status = findViewById(R.id.img_status);
        sp = getSharedPreferences("Infrared", MODE_PRIVATE);
        pz_status=findViewById(R.id.pz_status);
    }

    private void initData() {
        next.setText("我知道了");
        db_num = getIntent().getStringExtra("db_num");
        tv_db1.setText("数据来源为电表： " + db_num);
        sn = sp.getString("SN", "");
        checkdb(db_num);
    }

    private void initListener() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isyes1) {
                    Intent intent = new Intent(SettingFinishSuccessDetailActivity.this, CheckDbDataActivity.class);
                    intent.putExtra("device_list", db_num);
                    intent.putExtra("db_list", db_devid);
                    startActivity(intent);
                } else {
                    finish();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkdb(final String db1) {

        Log.i("onResponse", Api.getAmmetersByDatalogerSn + sn);
        HttpRequest.get(Api.getAmmetersByDatalogerSn + sn, new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());

//                dialog[0].dialog.dismiss();

            }

            @Override
            public void onStart() {
                super.onStart();
                dialog = DialogUIUtils.showLoading(SettingFinishSuccessDetailActivity.this, "请求中...", true, true, false, true);
                dialog.show();
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
//                                Toast.makeText(AmmeterSettingActivity.this,"采集器下没有电表",Toast.LENGTH_SHORT).show();
//                dialog.dialog.dismiss();
            }

            @Override
            public void onResponse(String response, Headers headers) {
                super.onResponse(response, headers);
                Log.i("onClick2: ", response);
                JSONArray jsonArray = JSONArray.parseArray(response);
//失败
                //判断当前时间和记录时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new java.util.Date());
                String lasttime = sp.getString("lastesttime", "");
                //时间差（秒）
                long longexpand = getTimeExpend(lasttime, date);
                tv_status2.setVisibility(View.VISIBLE);
                //这里现在肯定有返回
                String ddd1 = "";
                //只有一个电表
                for (int i = 0; i < jsonArray.size(); i++) {
                    String ssn = jsonArray.getJSONObject(i).getString("sn");
                    String ddb1 = achieve_format(db1).toString();


                    if (ssn.contains(ddb1)) {
                        ddd1 = jsonArray.getJSONObject(i).getString("deviceId");
                        isyes1 = true;
                        db_devid = ddd1;
                        Log.i("onResponse: db_devid", ssn);
                        Log.i("onResponse: db_devid", ddb1);
//                                isyes1=achieveData(ddd1);
                        device_list = ddd1;
                        new Thread(networkTask).start();
                        break;
                    }
                    Log.i("onResponse: jsonArray", i + "|" + jsonArray.size());
                    if (i == jsonArray.size() - 1) {
                        if (longexpand > 900) {
                            //超过15分钟了还没数据
                            pz_status.setText("配置异常");
                            img_status.setImageResource(R.mipmap.none_data);
                            tv_status.setText("无数据");
                            tv_status2.setVisibility(View.GONE);
                        } else {
                            //没有超过时间
                            img_status.setImageResource(R.mipmap.wait);
                            tv_status.setText("无数据");
                            tv_status2.setVisibility(View.VISIBLE);
                            tv_status2.setText("请耐心等待...");
                        }
                        dialog.dialog.dismiss();
                    }
                }


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
        });
    }

    //自动补全12位，同时按照要求从后到前每隔2位重组
    private String achieve_format(String updata1) {
        String las_sub = "";
        DecimalFormat df = new DecimalFormat("00000000000000");
        String str2 = df.format(Long.parseLong(updata1));
        str2 = str2.substring(2, 14);
        for (int i = 0; i < 6; i++) {
            String sub_str = str2.substring(12 - i * 2 - 2, 12 - i * 2);
            las_sub = las_sub + sub_str;
        }
//        Log.i("AmmeterSettingActivity", "onClick: "+las_sub);
//        Log.i("AmmeterSettingActivity", "onClick: "+str2);
        return las_sub;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            Boolean val = data.getBoolean("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            if (val) {
                //显示数据校验
                next.setText("数据校验");
                img_status.setImageResource(R.mipmap.updata);
                tv_status.setText("数据上传成功");
                tv_status2.setVisibility(View.GONE);
            } else {
                next.setText("我知道了");
                //失败
                //判断当前时间和记录时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new java.util.Date());
                String lasttime = sp.getString("lastesttime", "");
                //时间差（秒）
                long longexpand = getTimeExpend(lasttime, date);
                tv_status2.setVisibility(View.VISIBLE);
                if (longexpand > 900) {
                    //超过15分钟了还没数据
                    pz_status.setText("配置异常");
                    img_status.setImageResource(R.mipmap.none_data);
                    tv_status.setText("无数据");
                    tv_status2.setVisibility(View.GONE);
                } else {
                    //没有超过时间
                    img_status.setImageResource(R.mipmap.wait);
                    tv_status.setText("无数据");
                    tv_status2.setText("请耐心等待...");
                }
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            dialog.dialog.dismiss();
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
            OkHttpClient okHttpClient = new OkHttpClient();
            Log.i("onResponse2", Api.doDetail + device_list);
            Request request = new Request.Builder()
                    .url(Api.doDetail + device_list)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
//                Log.i("onResponse22", response.body().string());
//                Log.i("onResponse2", response.body().string());
                JSONObject jsonArrayyy = (JSONObject) JSONObject.parse(response.body().string());
                Log.i("onResponse2", jsonArrayyy.toString());
                if (jsonArrayyy.getString("result") == "-1") {
//                    Toast.makeText(SettingFinishSuccessDetailActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                } else {
                    JSONArray realTimeDataTotal = jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
                    String updateDate = jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
                    Log.i("onResponse: ", updateDate);
                    mill[0] = getTimeMillis(updateDate) + 8 * 60 * 60 * 1000;
                    Log.i("onResponse: ", mill[0] + "");
                    Date date = new Date(mill[0]);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateee = sdf.format(date);
                    String datee = sp.getString("lastesttime", "");
                    Log.i("onResponse: ", datee);
                    Log.i("onResponse: 返回的时间", dateee);
                    //判断这个时间是否大于点击时间（数据的时间，点击时间）
                    long longexpand = getTimeExpend(dateee, datee);
                    Log.i("onResponse: 返回的时间1", longexpand + "");
                    if (longexpand > 0) {
                        //无数据
                        //老数据
                        isyes1 = false;
                        //失败的话判断这个时间是否大于15分钟
                        qjbl = false;
                    } else {
                        //有最新数据
                        qjbl = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putBoolean("value", qjbl);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    private boolean achieveData(final String deviceId) throws IOException {
        Log.i("onClick2: ", Api.doDetail + deviceId);
//        final long[] mill = {0};
//        final BuildBean[] dialog = new BuildBean[1];
//        final boolean[] istr = {false};
        new Thread(networkTask).start();
//        HttpRequest.get(Api.doDetail +deviceId, new JsonHttpRequestCallback() {
//            @Override
//            protected void onSuccess(Headers headers, JSONObject jsonObject) {
//                super.onSuccess(headers, jsonObject);
//                Log.i("onSuccess", jsonObject.toString());
//                dialog[0].dialog.dismiss();
//            }
//            @Override
//            public void onStart () {
//                super.onStart();
//                dialog[0] = DialogUIUtils.showLoading(SettingFinishSuccessDetailActivity.this, "请求中...", true, true, false, true);
//                dialog[0].show();
//            }
//            @Override
//            public void onFailure ( int errorCode, String msg){
//                super.onFailure(errorCode, msg);
//                dialog[0].dialog.dismiss();
//            }
//
//            @Override
//            public void onResponse(String response, Headers headers) {
//                super.onResponse(response, headers);
//                Log.i("onClick2: ", response);
//                dialog.dialog.dismiss();
//                JSONObject jsonArrayyy= (JSONObject) JSONObject.parse(response);
//                if(jsonArrayyy.getString("result")=="-1"){
//                    Toast.makeText(SettingFinishSuccessDetailActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
//                }else{
//                    JSONArray realTimeDataTotal=jsonArrayyy.getJSONObject("DeviceWapper").getJSONArray("realTimeDataTotal");
//                    String updateDate=jsonArrayyy.getJSONObject("DeviceWapper").getString("updateDate");
//                    Log.i("onResponse: ", updateDate);
//                     mill[0] =getTimeMillis(updateDate)+8*60*60*1000;
//                    Log.i("onResponse: ", mill[0]+"");
//                    Date date = new Date(mill[0]);
//                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String dateee=sdf.format(date);
//                    String datee=sp.getString("lastesttime","");
//                    Log.i("onResponse: ", datee);
//                    Log.i("onResponse: 返回的时间", dateee);
//                    //判断这个时间是否大于点击时间（数据的时间，点击时间）
//                    long longexpand=getTimeExpend(dateee,datee);
//                    Log.i("onResponse: 返回的时间1", longexpand+"");
//                    if(longexpand>0){
//                        //无数据
//                        //老数据
//                        isyes1=false;
//                        //失败的话判断这个时间是否大于15分钟
//
//
//                         istr[0] =false;
//                    }else{
//                        //有最新数据
//                        istr[0] =true;
//                    }
//                }
//            }
//        });
        //数据获取不到
        Log.i("onResponse: 返回的时间", qjbl + "");
        return qjbl;
    }

    private long getTimeExpend(String startTime, String endTime) {
        //传入字串类型 2016/06/28 08:30
        long longStart = getTimeMillis(startTime); //获取开始时间毫秒数
        long longEnd = getTimeMillis(endTime);  //获取结束时间毫秒数
        long longExpend = longEnd - longStart;  //获取时间差
        long longsecond = longExpend / 1000;


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
            Toast.makeText(SettingFinishSuccessDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }
}
