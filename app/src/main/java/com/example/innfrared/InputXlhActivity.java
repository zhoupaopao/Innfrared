package com.example.innfrared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
        sure=findViewById(R.id.next);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("SN",et_xlh.getText().toString().trim());
                setResult(1,intent);
                finish();
            }
        });
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
