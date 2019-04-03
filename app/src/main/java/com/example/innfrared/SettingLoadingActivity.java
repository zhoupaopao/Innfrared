package com.example.innfrared;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    }

    private void initData() {
        title.setText(R.string.setting_db);
        tv_status.setText(R.string.setting_success);
        iv_unfinish.setImageResource(R.mipmap.finish);
        progressBar.setMax(300);
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

                if(longexpand<300){
                    handler.postDelayed(this, 1000);
                    longexpand++;
                }else{
                    handler.removeCallbacks(this);
//                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishActivity.class);
                    Intent intent=new Intent(SettingLoadingActivity.this,SettingFinishSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 1000);//每1秒执行一次runnable.

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
