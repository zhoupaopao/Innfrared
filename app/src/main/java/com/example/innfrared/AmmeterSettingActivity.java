package com.example.innfrared;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;

/**
 * Created by Lenovo on 2019/3/29.
 */

public class AmmeterSettingActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private TextView tv_title;
    private TextView now_sn;
    private ImageView sys;
    private ImageView review2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ammeter_setting);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        DialogUIUtils.init(getApplicationContext());
        back=findViewById(R.id.back);
        tv_title=findViewById(R.id.tv_title);
        now_sn=findViewById(R.id.now_sn);
        sys=findViewById(R.id.sys);
        review2=findViewById(R.id.review2);
    }

    private void initData() {

    }

    private void initListener() {
        tv_title.setText("配置电表");
        sys.setOnClickListener(this);
        back.setOnClickListener(this);
        review2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sys:
                Intent intent=new Intent(AmmeterSettingActivity.this,ScanQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.review2:
                Log.i("review2: ", "onClick: ");
                View rootView = View.inflate(AmmeterSettingActivity.this, R.layout.pop_review, null);
                final Dialog dialog = DialogUIUtils.showCustomAlert(this, rootView, Gravity.CENTER, true, false).show();
                //设置弹出框透明
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                rootView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog);
                    }
                });
                break;
        }
    }
}
