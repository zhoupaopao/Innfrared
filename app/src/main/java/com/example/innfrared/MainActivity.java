package com.example.innfrared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.innfrared.SettingPage.FastSettingActivity2;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gyf.barlibrary.ImmersionBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends CheckPermissionsActivity {
//    private Button scan;
    private ImageView back;
    private TextView title;
    private Button next;
    private SharedPreferences sp;
    private long mExitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeAndroidPDialog();//关闭androidp提示框使用，要慎用
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();
        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(this);
        initView();
        initData();
        initListener();
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
    private void initView() {
//        scan=findViewById(R.id.scan);
        back=findViewById(R.id.back);
        title=findViewById(R.id.tv_title);
        next=findViewById(R.id.next);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
    }

    private void initData() {
        String isfirst=sp.getString("isfirst","");
        if(isfirst==""){
            back.setVisibility(View.GONE);
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("isfirst","213");
        editor.commit();

    }

    private void initListener() {
//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new IntentIntegrator(MainActivity.this).setCaptureActivity(ScanQRCodeActivity.class).initiateScan();
//            }
//        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, FastSettingActivity2.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d(getClass().getName(), "Cancelled");
                Toast.makeText(this, "扫描结果为空", Toast.LENGTH_LONG).show();
            } else {
                Log.d(getClass().getName(), "Scanned: " + result.getContents());
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        }


    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode==KeyEvent.KEYCODE_BACK){
//            if((System.currentTimeMillis()-mExitTime>2000)){ //如果两次按键时间间隔大于2000毫秒，则不退出
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                mExitTime = System.currentTimeMillis();// 更新mExitTime
//            }else{
//                System.exit(0);
//            }
//        }
//        return true;
//    }
}
