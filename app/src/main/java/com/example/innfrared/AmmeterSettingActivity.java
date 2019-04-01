package com.example.innfrared;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;

/**
 * Created by Lenovo on 2019/3/29.
 */

public class AmmeterSettingActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private TextView tv_title;
    private TextView now_sn;
    private ImageView sys;
    private ImageView review2;
    private ImageView more1;
    private PopupWindow mPopupWindow;
    private int screenWidth;
    private int width;
    private TextView tv_pp_db;
    private TextView tv_add_db;
    private TextView tv_db_num1;
    private TextView tv_db_num2;
    private EditText et_db1;
    private EditText et_db2;
    private ImageView review1;
    private ImageView add1;
    private ImageView more2;
    private ImageView delete2;
    private LinearLayout ll2;
    Activity mActivity;
    private String nowmoreid="";//当前所选是匹配电表是那个
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private Button next;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ammeter_setting);
        ImBarUtils.setBar(this);
        getAndroiodScreenProperty();
        initView();
        initData();
        initListener();
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        mActivity=this;
        DialogUIUtils.init(getApplicationContext());
        back=findViewById(R.id.back);
        tv_title=findViewById(R.id.tv_title);
        now_sn=findViewById(R.id.now_sn);
        sys=findViewById(R.id.sys);
        review2=findViewById(R.id.review2);
        more1=findViewById(R.id.more1);
        tv_pp_db=findViewById(R.id.tv_pp_db);
        tv_add_db=findViewById(R.id.tv_add_db);
        tv_db_num1=findViewById(R.id.tv_db_num1);
        tv_db_num2=findViewById(R.id.tv_db_num2);
        et_db1=findViewById(R.id.et_db1);
        et_db2=findViewById(R.id.et_db2);
          review1=findViewById(R.id.review1);
          add1=findViewById(R.id.add1);
          more2=findViewById(R.id.more2);
          delete2=findViewById(R.id.delete2);
          ll2=findViewById(R.id.ll2);
          rl2=findViewById(R.id.rl2);
          rl3=findViewById(R.id.rl3);
        next=findViewById(R.id.next);
    }

    private void initData() {

    }

    private void initListener() {
        tv_title.setText("配置电表");
        sys.setOnClickListener(this);
        back.setOnClickListener(this);
        review2.setOnClickListener(this);
        more1.setOnClickListener(this);
        next.setOnClickListener(this);
    }
    private View getPopupWindowContentView() {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.popup_content_layout;   // 布局ID
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                    bgAlpha(1.0f);
                }
                switch (((LinearLayout) v).getId()){
                    case R.id.rl1:
                        //编辑电表
                        View edit_rootView = View.inflate(AmmeterSettingActivity.this, R.layout.dialog_db, null);
                        final Dialog edit_dialog = DialogUIUtils.showCustomAlert(AmmeterSettingActivity.this, edit_rootView, Gravity.CENTER, true, false).show();
                        //设置弹出框透明
                        edit_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        WindowManager m1 = getWindowManager();
                        Display d1 = m1.getDefaultDisplay(); // 获取屏幕宽、高
                        WindowManager.LayoutParams params1 = edit_dialog.getWindow().getAttributes();
                        params1.width = (int) (d1.getWidth() * 0.8); // 宽度设置为屏幕的0.6
                        edit_dialog.getWindow().setAttributes(params1);

                        edit_rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUIUtils.dismiss(edit_dialog);
                            }
                        });

                        edit_rootView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUIUtils.dismiss(edit_dialog);
                                //执行编辑操作
                                if(nowmoreid=="1"){
                                    //是第一组数据
//                                    rl2.setVisibility(View.GONE);
                                }else{
//                                    rl3.setVisibility(View.GONE);
                                }
                            }
                        });
                        break;
                    case R.id.rl2:
                        //删除电表

                        View delete_rootView = View.inflate(AmmeterSettingActivity.this, R.layout.dialog_db, null);
                        final Dialog delete_dialog = DialogUIUtils.showCustomAlert(AmmeterSettingActivity.this, delete_rootView, Gravity.CENTER, true, false).show();
                        //设置弹出框透明
                        delete_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        WindowManager m = getWindowManager();
                        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
                        WindowManager.LayoutParams params = delete_dialog.getWindow().getAttributes();
                        params.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.6
                        delete_dialog.getWindow().setAttributes(params);
                        delete_rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUIUtils.dismiss(delete_dialog);
                            }
                        });
                        EditText ed_text=delete_rootView.findViewById(R.id.ed_text);
                        ed_text.setVisibility(View.GONE);
                        TextView tv_text=delete_rootView.findViewById(R.id.tv_text);
                        tv_text.setVisibility(View.VISIBLE);
                        TextView tv_db_title=delete_rootView.findViewById(R.id.tv_db_title);
                        tv_db_title.setText(R.string.delete_db);
                        TextView sss=(TextView)delete_rootView.findViewById(R.id.sure);
                        sss.setText("确定删除");
                        sss.setTextColor(getResources().getColor(R.color.red));
                        delete_rootView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUIUtils.dismiss(delete_dialog);
                                //执行删除操作
                                if(nowmoreid=="1"){
                                    //是第一组数据
                                    rl2.setVisibility(View.GONE);
                                }else{
                                    rl3.setVisibility(View.GONE);
                                }
                            }
                        });
                        break;
                    case R.id.rl3:
                        //查看数据
                        Intent intent=new Intent(AmmeterSettingActivity.this,LookDataActivity.class);
                        startActivity(intent);
                        if(nowmoreid=="1"){
                            //是第一组数据

                        }else{

                        }
                        break;
                    case R.id.next:

                        break;
                }
            }
        };
        contentView.findViewById(R.id.rl1).setOnClickListener(menuItemOnClickListener);
        contentView.findViewById(R.id.rl2).setOnClickListener(menuItemOnClickListener);
        contentView.findViewById(R.id.rl3).setOnClickListener(menuItemOnClickListener);
        return contentView;
    }
    //获取屏幕宽度
    public void getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
         width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
         screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
//        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
//        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
//        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }
    private void bgAlpha(float alpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = alpha;// 0.0-1.0
        this.getWindow().setAttributes(lp);
    }
    private void showPopupWindow(View anchorView) {
        View contentView = getPopupWindowContentView();
        mPopupWindow = new PopupWindow(contentView,
                width-60, 350, true);
//        mPopupWindow.setWidth((int)3500);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(anchorView, contentView);
        int xOff = 20; // 可以自己调整偏移
        windowPos[0] -= xOff;
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupWindow.dismiss();
                bgAlpha(1.0f);
            }
        });
        mPopupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, 30, windowPos[1]);
        bgAlpha(0.5f);
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
            case R.id.more1:
                nowmoreid="1";
                showPopupWindow(more1);
                break;
            case R.id.add1:
                //验证一下是否超过12位

                break;
            case R.id.more2:
                nowmoreid="2";
                showPopupWindow(more2);
                break;
            case R.id.delete2:
                ll2.setVisibility(View.GONE);
                break;
            case R.id.review1:
                Log.i("review2: ", "onClick: ");
                View rootView1 = View.inflate(AmmeterSettingActivity.this, R.layout.pop_review, null);
                final Dialog dialog1 = DialogUIUtils.showCustomAlert(this, rootView1, Gravity.CENTER, true, false).show();
                //设置弹出框透明
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                rootView1.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUIUtils.dismiss(dialog1);
                    }
                });
                break;
        }
    }
}
