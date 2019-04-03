package com.example.innfrared;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dou361.dialogui.DialogUIUtils;

/**
 * Created by Lenovo on 2019/4/1.
 */

public class CheckDataActivity extends Activity {
    private Button call;
    private Button pass;
    private TextView title;
    private ImageView back;
    private ImageView review;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_data);
        ImBarUtils.setBar(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        call=findViewById(R.id.call);
        pass=findViewById(R.id.pass);
        title=findViewById(R.id.tv_title);
        back=findViewById(R.id.back);
        review=findViewById(R.id.review);
    }

    private void initData() {
        title.setText("数据验证");

    }

    private void initListener() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String mobile=(String) CheckDataActivity.this.getResources().getText(R.string.num_mobile);
                Uri data = Uri.parse("tel:" + mobile);
                intent.setData(data);
                startActivity(intent);
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView1 = View.inflate(CheckDataActivity.this, R.layout.pop_review, null);
                ImageView iv_pop=rootView1.findViewById(R.id.iv_pop);
                iv_pop.setImageResource(R.mipmap.check_review);
                final Dialog dialog1 = DialogUIUtils.showCustomAlert(CheckDataActivity.this, rootView1, Gravity.CENTER, true, false).show();
                //设置弹出框透明
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                rootView1.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog1);
                    }
                });
            }
        });
    }
}
