package com.example.innfrared;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Lenovo on 2019/4/1.
 */

public class LookDataActivity extends Activity {
    private ImageView back;
    private TextView title;
    private Button bt_back;
    private ProgressBar progressBar;
    private TextView wait;
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
        wait=findViewById(R.id.wait);
        progressBar=findViewById(R.id.progressbar);
    }

    private void initData() {
        title.setText(R.string.setting_db);
    }

    private void initListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressBar.setVisibility(View.GONE);
        wait.setVisibility(View.GONE);
    }
}
