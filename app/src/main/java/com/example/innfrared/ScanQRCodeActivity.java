package com.example.innfrared;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.BuildBean;
import com.google.zxing.ResultPoint;
import com.gyf.barlibrary.ImmersionBar;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;



import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.MediaType;

/**
 * Created by Lenovo on 2019/3/25.
 */

public class ScanQRCodeActivity extends Activity {
    private CaptureManager capture;
    private DecoratedBarcodeView bv_barcode;
    private TextView sgd;
    private static final int CAMERA_REQUEST_CODE = 2;//请求码
    private CameraManager cameraManager;
    private boolean isOpen = true;//默认关闭
    private Camera camera;
    protected String[] needPermissions = {
            Manifest.permission.CAMERA
    };
    private long mExitTime=0;
    private static final int PERMISSON_REQUESTCODE = 0;
    private ImageView question;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private ImageView sdt;
    private SharedPreferences sp;
    private BuildBean dialog;
    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result != null){
                bv_barcode.pause();
                Log.e(getClass().getName(), "获取到的扫描结果是：" + result.getText());
//                Toast.makeText(ScanQRCodeActivity.this,"获取到的扫描结果是：" + result.getText(),Toast.LENGTH_SHORT).show();
//可以对result进行一些判断，比如说扫描结果不符合就再进行一次扫描
//                if (result.getText().contains("符合我的结果")){
                doDataloggerCheck(result.getText());

//                    Intent intent=new Intent();
//                    intent.setClass(ScanQRCodeActivity.this,StartSettingActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(ScanQRCodeActivity.this,"不符合要求，获取到的扫描结果是：" + result.getText(),Toast.LENGTH_SHORT).show();
//                    bv_barcode.resume();
//                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        ImmersionBar.with(this)
                .init();
        closeAndroidPDialog();//关闭androidp提示框使用，要慎用
        checkPersion();
        initView();
        initListener();


    }
    public void doDataloggerCheck(final String re_SN){
        RequestParams params = new RequestParams();
//        params.addHeader("Content-Type", "application/json");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("sn", re_SN);
//        params.setRequestBody(MediaType.parse("application/json"), jsonObject.toString());
        Log.i("onSuccess", Api.doDataloggerCheck +"sn="+re_SN);
        HttpRequest.get(Api.doDataloggerCheck +"sn="+re_SN,params,5000, new JsonHttpRequestCallback() {
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
                    Log.i("barcodeResult: ", re_SN+"|"+lastSn);
                    if(re_SN.equals(lastSn)){
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
//                            Intent intent=new Intent(ScanQRCodeActivity.this,SettingFinishSuccessActivity.class);
//                            startActivity(intent);
//                        }else{
//                            //进入等待
//                            Intent intent1=new Intent(ScanQRCodeActivity.this,SettingLoadingActivity.class);
//                            intent1.putExtra("longexpand",longexpand);
//                            startActivity(intent1);
//                        }

                        //不比较时间，进入添加电表页面
                        Intent intent2=new Intent();
                        intent2.putExtra("SN",re_SN);
                        intent2.putExtra("rem",true);//是读取内存
                        intent2.setClass(ScanQRCodeActivity.this,AmmeterSettingActivity.class);
                        startActivity(intent2);
                    }else{
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("SN","");
                        editor.putString("db_list","");
                        editor.putString("lastesttime","");
                        editor.commit();
                        Intent intent2=new Intent();
                        intent2.putExtra("SN",re_SN);
                        intent2.putExtra("rem",false);//是读取内存
                        intent2.setClass(ScanQRCodeActivity.this,AmmeterSettingActivity.class);
                        startActivity(intent2);
                    }
                }else{
                    //没有这个，重新扫描
                    Toast.makeText(ScanQRCodeActivity.this,"该采集器不符合要求",Toast.LENGTH_SHORT).show();
                    bv_barcode.resume();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                dialog = DialogUIUtils.showLoading(ScanQRCodeActivity.this, "验证中...", true, true, false, true);
                dialog.show();
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Log.i("onFailure", msg);
                Toast.makeText(ScanQRCodeActivity.this,"请求超时",Toast.LENGTH_SHORT).show();
                bv_barcode.resume();
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
            Toast.makeText(ScanQRCodeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }

    private void initView() {
        bv_barcode = (DecoratedBarcodeView) findViewById(R.id.bv_barcode);
        bv_barcode.decodeContinuous(barcodeCallback);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
        sgd = (TextView) this.findViewById(R.id.sgd);
        if(!hasFlash()){
            sgd.setVisibility(View.GONE);
        }
        rl1=this.findViewById(R.id.rl1);
        rl2=this.findViewById(R.id.rl2);
        sdt=this.findViewById(R.id.pic2);
        question=this.findViewById(R.id.question);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("return","0");
        editor.commit();

    }

    private void initListener() {
        sgd.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if(isOpen){
                    bv_barcode.setTorchOn();
                    isOpen=false;
                    //不需要这个动态变化
//                    sdt.setImageResource(R.mipmap.sdt2);
                }else{
                    bv_barcode.setTorchOff(); // 关闭手电筒
                    isOpen=true;
                    //不需要这个动态变化
//                    sdt.setImageResource(R.mipmap.sdt1);
                }
            }
        });
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ScanQRCodeActivity.this,InputXlhActivity.class);
                startActivityForResult(intent,10);
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen){
                    bv_barcode.setTorchOn();
                    isOpen=false;
//                    sdt.setImageResource(R.mipmap.sdt2);
                }else{
                    bv_barcode.setTorchOff(); // 关闭手电筒
                    isOpen=true;
//                    sdt.setImageResource(R.mipmap.sdt1);
                }
            }
        });
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ScanQRCodeActivity.this,MainActivity.class);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("return","1");
                editor.commit();
                startActivity(intent);
            }
        });
    }

    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPersion() {
        try {

            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = new ArrayList<>();
                needRequestPermissonList.add(Manifest.permission.CAMERA);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                            int.class});

                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                }
            }
        } catch (Throwable e) {
        }
    }
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                finish();
            }
        }
    }
    /**
     * 检测是否所有的权限都已经授权
     * @param grantResults
     * @return
     * @since 2.5.0
     *
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
//        capture.onResume();
        bv_barcode.resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        capture.onPause();
        bv_barcode.pause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            String SN=data.getStringExtra("SN");
            Intent intent=new Intent();
            intent.putExtra("SN",SN);
            intent.setClass(ScanQRCodeActivity.this,AmmeterSettingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //看看能不能返回上一个界面

            finish();
//            if((System.currentTimeMillis()-mExitTime>2000)){ //如果两次按键时间间隔大于2000毫秒，则不退出
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                mExitTime = System.currentTimeMillis();// 更新mExitTime
//            }else{
//                System.exit(0);
//            }
        }
        return true;
    }


}
