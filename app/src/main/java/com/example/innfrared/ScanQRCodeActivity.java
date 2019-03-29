package com.example.innfrared;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
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

import com.google.zxing.ResultPoint;
import com.gyf.barlibrary.ImmersionBar;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result != null){
                bv_barcode.pause();
                Log.e(getClass().getName(), "获取到的扫描结果是：" + result.getText());
                Toast.makeText(ScanQRCodeActivity.this,"获取到的扫描结果是：" + result.getText(),Toast.LENGTH_SHORT).show();
//可以对result进行一些判断，比如说扫描结果不符合就再进行一次扫描
                if (result.getText().contains("符合我的结果")){
                    //符合的可以不在扫描了，当然你想继续扫描也是可以的
                    Toast.makeText(ScanQRCodeActivity.this,"获取到的扫描结果是：" + result.getText()+",正确跳转中……",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.setClass(ScanQRCodeActivity.this,StartSettingActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ScanQRCodeActivity.this,"不符合要求，获取到的扫描结果是：" + result.getText(),Toast.LENGTH_SHORT).show();
                    bv_barcode.resume();
                }
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
                    sdt.setImageResource(R.mipmap.sdt2);
                }else{
                    bv_barcode.setTorchOff(); // 关闭手电筒
                    isOpen=true;
                    sdt.setImageResource(R.mipmap.sdt1);
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
                    sdt.setImageResource(R.mipmap.sdt2);
                }else{
                    bv_barcode.setTorchOff(); // 关闭手电筒
                    isOpen=true;
                    sdt.setImageResource(R.mipmap.sdt1);
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
