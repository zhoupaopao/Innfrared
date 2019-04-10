package com.example.innfrared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.BuildBean;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.MediaType;

/**
 * Created by Lenovo on 2019/3/28.
 */

public class InputXlhActivity extends Activity {

    private ImageView sdt;
    private CameraManager manager;// 声明CameraManager对象
    private Camera m_Camera = null;// 声明Camera对象
    private boolean isOpen = true;//默认关闭
    private ImageView back;
    private Button sure;
    private EditText et_xlh;
    private SharedPreferences sp;
    private BuildBean dialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_xlh);
        ImBarUtils.setBar(this);
        sdt=findViewById(R.id.question);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] camerList = manager.getCameraIdList();
            for (String str : camerList) {
            }
        } catch (CameraAccessException e) {
            Log.e("error", e.getMessage());
        }
        sdt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if(isOpen){
                    lightSwitch(false);
                    isOpen=false;
                }else{
                    lightSwitch(true);
                    isOpen=true;
                }
            }
        });
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_xlh=findViewById(R.id.et_xlh);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        sure=findViewById(R.id.next);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.putExtra("SN",et_xlh.getText().toString().trim());
//                setResult(1,intent);
//                finish();
                doDataloggerCheck(et_xlh.getText().toString());

            }
        });
    }
    public void doDataloggerCheck(final String re_SN){
        RequestParams params = new RequestParams();
//        params.addHeader("Content-Type", "application/json");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("sn", re_SN);
//        params.setRequestBody(MediaType.parse("application/json"), jsonObject.toString());
        Log.i("onSuccess", Api.doDataloggerCheck +"sn="+re_SN);
        HttpRequest.get(Api.doDataloggerCheck +"sn="+re_SN,params,5000 ,new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());

                dialog.dialog.dismiss();
                if(jsonObject.getString("result").equals("1")){
                    //符合结果
                    //符合的可以不在扫描了，当然你想继续扫描也是可以的
                    //判断是不是本地记录的那个sn
                    String lastSn=sp.getString("SN","");
                    Log.i("barcodeResult: ", et_xlh.getText()+"|"+lastSn);
                    if(et_xlh.getText().toString().equals(lastSn)){
                        //和本地最后记录的相同
                        //查看时间差
//                        Log.e(getClass().getName(), "213");
//                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                        String date=sdf.format(new java.util.Date());
//                        String lasttime=sp.getString("lastesttime","");
//                        //时间差（秒）
//                        long longexpand=getTimeExpend(lasttime,date);
//                        if(longexpand>300){
//                            //要么进入配置错误，要么进入配置成功
//                            Intent intent=new Intent(InputXlhActivity.this,SettingFinishSuccessActivity.class);
//                            startActivity(intent);
//                        }else{
//                            //进入等待
//                            Intent intent1=new Intent(InputXlhActivity.this,SettingLoadingActivity.class);
//                            intent1.putExtra("longexpand",longexpand);
//                            startActivity(intent1);
//                        }
                        //不比较时间，进入添加电表页面
                        Intent intent2=new Intent();
                        intent2.putExtra("SN",re_SN);
                        intent2.putExtra("rem",true);//是读取内存
                        intent2.setClass(InputXlhActivity.this,AmmeterSettingActivity.class);
                        startActivity(intent2);
                    }else{
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("SN",et_xlh.getText().toString());
                        editor.putString("db_list","");
                        editor.putString("lastesttime","");
                        editor.commit();
                        Log.i("onClick: ", et_xlh.getText().toString());
                        Intent intent2=new Intent();
                        intent2.putExtra("SN",et_xlh.getText().toString());
                        intent2.setClass(InputXlhActivity.this,AmmeterSettingActivity.class);
                        startActivity(intent2);

                    }
                    finish();
                }else{
                    //没有这个，重新扫描
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("SN","");
                    editor.putString("db_list","");
                    editor.putString("lastesttime","");
                    editor.commit();
                    Toast.makeText(InputXlhActivity.this,"该采集器不符合要求",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                dialog = DialogUIUtils.showLoading(InputXlhActivity.this, "验证中...", true, true, false, true);
                dialog.show();
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Log.i("onFailure", msg);
                Toast.makeText(InputXlhActivity.this,"请求超时",Toast.LENGTH_SHORT).show();
                dialog.dialog.dismiss();
            }
        });
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
            Toast.makeText(InputXlhActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }
    public void lightSwitch(final boolean lightStatus) {
        if (lightStatus) { // 关闭手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (m_Camera != null) {
                    m_Camera.stopPreview();
                    m_Camera.release();
                    m_Camera = null;
                }
            }
        } else { // 打开手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final PackageManager pm = getPackageManager();
                final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                for (final FeatureInfo f : features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                        if (null == m_Camera) {
                            m_Camera = Camera.open();
                        }
                        final Camera.Parameters parameters = m_Camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        m_Camera.setParameters(parameters);
                        m_Camera.startPreview();
                    }
                }
            }
        }
    }
    /**
     * 判断Android系统版本是否 >= M(API23)
     */
    private boolean isM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        } else {
            return false;
        }
    }
}
