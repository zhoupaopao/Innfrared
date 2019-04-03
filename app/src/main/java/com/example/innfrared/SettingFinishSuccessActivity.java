package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by Lenovo on 2019/4/1.
 */

public class SettingFinishSuccessActivity extends Activity {
    private Button next;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_finish_success);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        next=findViewById(R.id.next);
    }

    private void initData() {

    }

    private void initListener() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingFinishSuccessActivity.this,CheckDataActivity.class);
                startActivity(intent);
            }
        });
    }
}
