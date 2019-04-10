package com.example.innfrared;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.BuildBean;
import com.dou361.dialogui.listener.DialogUIListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import okhttp3.Headers;

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
    private SharedPreferences sp;
    private String SN;
    //给三个按钮设置属性
    private String str_review1="1";//1代表添加的时候2代表已匹配
    private String str_review2="1";//1代表添加的时候2代表已匹配
    private String str_more1="1";
    private String str_add1="1";
    private TextView tv_add_db2;
    private String need_updata1="";//最终上传的数据1
    private String need_updata2="";//最终上传的数据2
    private Boolean cannext=false;//能否下一步
    private BuildBean dialog;
    private Boolean rem;
    private Boolean nochange=false;//查看数据是否有变化
    private String db_num1="";//存放的是内存中的原始数据，用来和最终查看的数据进行比对
    private String db_num2="";
    private String device_list="";
    private double pencent=0.0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ammeter_setting);
        SysApplication.getInstance().addActivity(this);
        ImBarUtils.setBar(this);
        getAndroiodScreenProperty();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("TAG", "onRestart: ");
//        nochange=true;
        String db_list=sp.getString("db_list","");
        Log.i("initData: ", db_list);
        if(db_list.equals("")){
            //是空
        }else{
            String[]dsa=db_list.split(",");
            if(dsa.length==2){
                db_num1=dsa[0];
                db_num2=dsa[1];
                need_updata1=dsa[0];
                need_updata2=dsa[1];
            }else{
                db_num1=dsa[0];
                need_updata1=dsa[0];
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: ");
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        mActivity=this;
        SN=getIntent().getStringExtra("SN");
        rem=getIntent().getBooleanExtra("rem",false);
        sp=getSharedPreferences("Infrared",MODE_PRIVATE);
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
          add1.setVisibility(View.GONE);
          more2=findViewById(R.id.more2);
          delete2=findViewById(R.id.delete2);
          ll2=findViewById(R.id.ll2);
          rl2=findViewById(R.id.rl2);
          rl3=findViewById(R.id.rl3);
        next=findViewById(R.id.next);
        tv_add_db2=findViewById(R.id.tv_add_db2);
    }

    private void initData() {
        if(rem){
            nochange=true;
            cannext=true;
            next.setBackgroundResource(R.drawable.btn_blue);
            //读取数据
            String db_list=sp.getString("db_list","");
            Log.i("initData: ", db_list);
            String[]dsa=db_list.split(",");
            if(dsa.length==2){
                db_num1=dsa[0];
                db_num2=dsa[1];
                need_updata1=dsa[0];
                need_updata2=dsa[1];
            }else{
                db_num1=dsa[0];
                need_updata1=dsa[0];
            }
            //更改布局
            tv_add_db.setText("已匹配电表");
            et_db1.setVisibility(View.GONE);
            tv_db_num1.setText(need_updata1);
            tv_db_num1.setVisibility(View.VISIBLE);
            review1.setImageResource(R.mipmap.more);
            more1.setVisibility(View.GONE);
            str_review1="2";
            if(need_updata2.equals("")){
                //隐藏布局2，显示添加按钮
                more1.setVisibility(View.VISIBLE);
                more1.setImageResource(R.mipmap.add);
                ll2.setVisibility(View.GONE);
            }else{
                //显示布局2，
                str_review2="2";
                et_db2.setVisibility(View.GONE);
                tv_db_num2.setText(need_updata2);
                tv_db_num2.setVisibility(View.VISIBLE);
                review2.setImageResource(R.mipmap.more);
                more2.setVisibility(View.GONE);
                delete2.setVisibility(View.GONE);
                tv_add_db2.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initListener() {
        tv_title.setText("配置电表");
        now_sn.setText("当前采集器SN   "+SN);
        sys.setOnClickListener(this);
        back.setOnClickListener(this);
        review1.setOnClickListener(this);
        review2.setOnClickListener(this);
        more1.setOnClickListener(this);
        next.setOnClickListener(this);
        add1.setOnClickListener(this);
        delete2.setOnClickListener(this);
        more2.setOnClickListener(this);
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
                        final View edit_rootView = View.inflate(AmmeterSettingActivity.this, R.layout.dialog_db, null);
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
                        @SuppressLint("WrongViewCast") final EditText ed_text1=edit_rootView.findViewById(R.id.ed_text);
                        edit_rootView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(ed_text1.getText().toString().trim().equals("")){
                                    //是空
                                    Toast.makeText(AmmeterSettingActivity.this,"请输入电表号",Toast.LENGTH_SHORT).show();
                                }else{
                                    DialogUIUtils.dismiss(edit_dialog);
                                    nochange=false;
                                    Log.i("onClick: ", "onClick:nochange ");
                                    //执行编辑操作
                                    if(nowmoreid=="1"){
                                        //是第一组数据
                                        tv_db_num1.setText(ed_text1.getText().toString());
                                        need_updata1=ed_text1.getText().toString();
                                    }else{
                                        tv_db_num2.setText(ed_text1.getText().toString());
                                        need_updata2=ed_text1.getText().toString();
                                    }
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
                                nochange=false;
                                //执行删除操作
                                if(nowmoreid=="1"){
                                    //是第一组数据
                                    //判断是否有两组数据
                                    if(str_review2.equals("2")){
                                        //有两组数据
                                        //将第二组的数据赋值给1
                                        //2的数据删除
                                        need_updata1=need_updata2;
                                        need_updata2="";
                                        ll2.setVisibility(View.GONE);
                                        //给1赋值2的数据
                                        tv_db_num1.setText(tv_db_num2.getText().toString());
                                        //先恢复2的布局
                                        more2.setVisibility(View.VISIBLE);
                                        more2.setImageResource(R.mipmap.icon_sure);
                                        review2.setVisibility(View.VISIBLE);
                                        review2.setImageResource(R.mipmap.review);
                                        delete2.setVisibility(View.VISIBLE);
                                        delete2.setImageResource(R.mipmap.delete);
                                        tv_db_num2.setText("");
                                        tv_db_num2.setVisibility(View.GONE);

                                        et_db2.setText("");
                                        et_db2.setVisibility(View.VISIBLE);
                                        tv_add_db2.setVisibility(View.VISIBLE);
                                        more1.setVisibility(View.VISIBLE);
                                        more1.setImageResource(R.mipmap.add);
                                        str_review2="1";
                                        SharedPreferences.Editor editor=sp.edit();
                                        editor.putString("db_list",need_updata1);
                                        editor.putString("lastesttime","");
                                        editor.commit();
                                    }else{
                                        //一组
                                        //如果ll2是显示的就隐藏，同时还原一组的布局
                                        //删除数据1
                                        need_updata1="";
                                        tv_add_db.setText("添加电表");
                                        tv_db_num1.setText("");
                                        tv_db_num1.setVisibility(View.GONE);
                                        et_db1.setText("");
                                        et_db1.setVisibility(View.VISIBLE);
                                        more1.setImageResource(R.mipmap.icon_sure);
                                        more1.setVisibility(View.VISIBLE);
                                        review1.setImageResource(R.mipmap.review);
                                        review1.setVisibility(View.VISIBLE);
                                        ll2.setVisibility(View.GONE);
                                        next.setBackgroundResource(R.drawable.btn_blue_dark);
                                        cannext=false;
                                        str_review1="1";
                                        SharedPreferences.Editor editor=sp.edit();
                                        editor.putString("db_list","");
                                        editor.putString("lastesttime","");
                                        editor.commit();
                                    }
                                }else{
                                    //删除第二组数据
                                    //有两组数据
                                    ll2.setVisibility(View.GONE);
                                    need_updata2="";
                                    //给1赋值2的数据
                                    //先恢复2的布局
                                    more2.setVisibility(View.VISIBLE);
                                    more2.setImageResource(R.mipmap.icon_sure);
                                    review2.setVisibility(View.VISIBLE);
                                    review2.setImageResource(R.mipmap.review);
                                    delete2.setVisibility(View.VISIBLE);
                                    delete2.setImageResource(R.mipmap.delete);
                                    tv_db_num2.setText("");
                                    tv_db_num2.setVisibility(View.GONE);
                                    et_db2.setText("");
                                    et_db2.setVisibility(View.VISIBLE);
                                    tv_add_db2.setVisibility(View.VISIBLE);
                                    more1.setVisibility(View.VISIBLE);
                                    more1.setImageResource(R.mipmap.add);
                                    str_review2="1";
                                    SharedPreferences.Editor editor=sp.edit();
                                    editor.putString("db_list",need_updata1);
                                    editor.putString("lastesttime","");
                                    editor.commit();
                                }
                            }
                        });
                        break;
                    case R.id.rl3:
                        //查看数据

                        if(nowmoreid=="1"){
                            //是第一组数据
                            //判断是否是内存中记录的数据
                            Log.i("onClick:1 ", need_updata1+"|"+db_num1+"|"+db_num2);
                            if(need_updata1.equals(db_num1)||need_updata1.equals(db_num2)){
                                //等于1或者2
                                //进入成功页面
                                Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishSuccessDetailActivity.class);
                                intent.putExtra("db_num",need_updata1);
                                startActivity(intent);
                            }else{
                                Intent intent=new Intent(AmmeterSettingActivity.this,LookDataActivity.class);
                                startActivity(intent);
                            }
                            //
                        }else{
                            //第二组数据
                            Log.i("onClick:2 ", need_updata2+"|"+db_num1+"|"+db_num2);
                            if(need_updata2.equals(db_num1)||need_updata2.equals(db_num2)){
                                //等于1或者2
                                //进入成功页面
                                Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishSuccessDetailActivity.class);
                                intent.putExtra("db_num",need_updata2);
                                startActivity(intent);
                            }else{
                                Intent intent=new Intent(AmmeterSettingActivity.this,LookDataActivity.class);
                                startActivity(intent);
                            }
                        }
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
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
        pencent=(float)width/(float) screenWidth;
        Log.d("h_bl", "比例：" + pencent);
    }
    private void bgAlpha(float alpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = alpha;// 0.0-1.0
        this.getWindow().setAttributes(lp);
    }
    private void showPopupWindow(View anchorView) {
        View contentView = getPopupWindowContentView();
        Log.i("showPopupWindow: ", 120*pencent+"");
        mPopupWindow = new PopupWindow(contentView,
                width-60, (int) (120*pencent), true);
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
                if(str_review2.equals("1")){
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
                }else{
                    //已匹配
                    nowmoreid="2";
                    showPopupWindow(review2);
                }

                break;
            case R.id.more1:
                if(str_review1.equals("2")){
                    //已匹配
                    ll2.setVisibility(View.VISIBLE);
                    more1.setVisibility(View.GONE);
                }else{
                    //确定按钮
                    if(et_db1.getText().toString().trim().equals("")){
                        //没有输入
                        DialogUIUtils.showAlert(mActivity, "提示", "请输入电表编号后点击确定", "", "", "确定", "", true, true, true, new DialogUIListener() {
                            @Override
                            public void onPositive() {
                            }
                            @Override
                            public void onNegative() {
                            }
                        }).show();
                        break;
                    }else{
                        //界面发生变化
                        //头变成已匹配电表，edittext隐藏，textview显示，最后一个图标变成...，设置图标属性
                        tv_add_db.setText("已匹配电表");
                        need_updata1=et_db1.getText().toString();
                        tv_db_num1.setText(et_db1.getText().toString());
                        et_db1.setVisibility(View.GONE);
                        et_db1.setText("");
                        tv_db_num1.setVisibility(View.VISIBLE);
                        more1.setImageResource(R.mipmap.add);
                        review1.setImageResource(R.mipmap.more);
                        str_review1="2";
                        //将底部button变色
                        next.setBackgroundResource(R.drawable.btn_blue);
                        cannext=true;
                        hidekey();
                    }
                }

                break;
            case R.id.add1:
                //验证一下是否超过12位
                //模拟数据添加（自动补全12位）
                String format_db1=achieve_format(et_db1.getText().toString());
                Log.i("AmmeterSettingActivity", format_db1);
                break;
            case R.id.more2:
                //点击确认，界面变化

                    //nowmoreid="1";
                    //showPopupWindow(more1);
                    //确定按钮
                    if(et_db2.getText().toString().trim().equals("")){
                        //没有输入
                        DialogUIUtils.showAlert(mActivity, "提示", "请输入电表编号后点击确定", "", "", "确定", "", true, true, true, new DialogUIListener() {
                            @Override
                            public void onPositive() {
                            }
                            @Override
                            public void onNegative() {
                            }
                        }).show();
                        break;
                    }else{
                        //界面发生变化
                        //添加电表隐藏，edittext隐藏，textview显示，最后一个图标变成...，设置图标属性
                        nochange=false;
                        tv_add_db2.setVisibility(View.GONE);
                        need_updata2=et_db2.getText().toString();
                        tv_db_num2.setText(et_db2.getText().toString());
                        et_db2.setVisibility(View.GONE);
                        et_db2.setText("");
                        tv_db_num2.setVisibility(View.VISIBLE);
                        delete2.setVisibility(View.GONE);
                        more2.setVisibility(View.GONE);
                        review2.setImageResource(R.mipmap.more);
                        str_review2="2";
                        hidekey();
                    }
                break;
            case R.id.delete2:
                ll2.setVisibility(View.GONE);
                more1.setVisibility(View.VISIBLE);
                break;
            case R.id.review1:
                Log.i("review1: ", "onClick: ");
                if(str_review1.equals("2")){
                    nowmoreid="1";
                    showPopupWindow(review1);
                }else{
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
                }

                break;
            case R.id.next:
                //之后这个界面就不需要来了
                //判断是否有数据
                if(cannext){
                    //请求数据，将SN号上传，获取电表deviceid

                    //不需要请求这个接口
                    //最后才获取
//                    SN="1415094552";
//                    Log.i("onSuccess", Api.getAmmetersByDatalogerSn +SN);
//                    HttpRequest.get(Api.getAmmetersByDatalogerSn +SN, new JsonHttpRequestCallback() {
//                        @Override
//                        protected void onSuccess(Headers headers, JSONObject jsonObject) {
//                            super.onSuccess(headers, jsonObject);
//                            Log.i("onSuccess", jsonObject.toString());
//
//                            dialog.dialog.dismiss();
//                            if (jsonObject.getString("result").equals("1")) {
//                                //符合结果
//                                //符合的可以不在扫描了，当然你想继续扫描也是可以的
//                                //判断是不是本地记录的那个sn
//
//                            }
//                        }
//                            @Override
//                            public void onStart () {
//                                super.onStart();
//                                dialog = DialogUIUtils.showLoading(AmmeterSettingActivity.this, "请求中...", true, true, false, true);
//                                dialog.show();
//                            }
//                            @Override
//                            public void onFailure ( int errorCode, String msg){
//                                super.onFailure(errorCode, msg);
////                                Toast.makeText(AmmeterSettingActivity.this,"采集器下没有电表",Toast.LENGTH_SHORT).show();
//                                dialog.dialog.dismiss();
//                            }
//
//                        @Override
//                        public void onResponse(String response, Headers headers) {
//                            super.onResponse(response, headers);
//                            Log.i("onClick2: ", response);
//                            JSONArray jsonArray=JSONArray.parseArray(response);
//                            String sadas=jsonArray.getJSONObject(1).getString("sn");
//                            Log.i("onClick2: ", sadas);
//                            dialog.dialog.dismiss();
//                        }
//                    });
                    //先看是不是之前记录的电表
                    //不是就loading
                    //是的话查看时间
                    if(nochange){
                        //没有改变，通过时间进入loading和success
                        Log.e(getClass().getName(), "213");
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String date=sdf.format(new java.util.Date());
                        String lasttime=sp.getString("lastesttime","");
                        //时间差（秒）
                        long longexpand=getTimeExpend(lasttime,date);
                        if(longexpand>300){
                            //要么进入配置错误，要么进入配置成功
                            //请求接口查看进入对和错界面或者一对易错
                            String list_dbs=sp.getString("db_list","");
                            String[]dsa=list_dbs.split(",");
                            if(dsa.length==2){
                                db_num1=dsa[0];
                                db_num2=dsa[1];
                            }else{
                                db_num1=dsa[0];
                            }
                            checkdb(db_num1,db_num2);
//                            Intent intentt=new Intent(AmmeterSettingActivity.this,SettingFinishSuccessActivity.class);
//                            startActivity(intentt);
                        }else{
                            //进入等待
                            Intent intent1=new Intent(AmmeterSettingActivity.this,SettingLoadingActivity.class);
                            intent1.putExtra("longexpand",longexpand);
                            startActivity(intent1);
                        }
                    }else{
                        //先将sn和电表传到后台成功的话就继续
//                        sn=采集器sn&devuceSn=电表的sn号
                        String db1=need_updata1;
                        String db2=need_updata2;
                        saveCommandAmmeter(SN,db1,db2);

                    }

                }else{

                }

                break;
        }
    }
    private  void saveCommandAmmeter(String sn, final String db1, final String db2){
        String up_db1="";
        String up_db2="";
        if(db2.equals("")){
             up_db1=achieve_format(db1);
        }else{
            //两个电表
             up_db1=achieve_format(db1);
             up_db2=achieve_format(db2);
        }
        String up_dbs=up_db1+","+up_db2;
        Log.i("saveCommandAmmeter: ", Api.saveCommandAmmeter+"sn="+sn+"&deviceSn="+up_dbs);
        HttpRequest.get(Api.saveCommandAmmeter+"sn="+sn+"&deviceSn="+up_dbs,new JsonHttpRequestCallback(){
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());

                dialog.dialog.dismiss();
                if(jsonObject.getString("result").equals("1")){
                    //成功
                    Intent intent1=new Intent(AmmeterSettingActivity.this,SettingLoadingActivity.class);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String date=sdf.format(new java.util.Date());
                    //先进行匹配
                    //匹配通过后，本地记录时间
                    Log.i("onClick: ", sp.getString("db_list",""));
                    SharedPreferences.Editor editor=sp.edit();
//                        String db1=need_updata1;
//                        String db2=need_updata2;
                    //这个地方记录SN号
                    Log.i("onClick: ", db1+","+db2);
                    editor.putString("SN",SN);
                    editor.putString("db_list",db1+","+db2);
                    editor.putString("lastesttime",date);
                    editor.commit();
                    startActivity(intent1);
//                    finish();
                }else{
                    Toast.makeText(AmmeterSettingActivity.this,"保存电表异常",Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onStart () {
                super.onStart();
                dialog = DialogUIUtils.showLoading(AmmeterSettingActivity.this, "保存中...", true, true, false, true);
                dialog.show();
            }
            @Override
            public void onFailure ( int errorCode, String msg){
                super.onFailure(errorCode, msg);
                Toast.makeText(AmmeterSettingActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                dialog.dialog.dismiss();

            }
        });
    }
    private void checkdb(final String db1, final String db2){

        HttpRequest.get(Api.getAmmetersByDatalogerSn +SN, new JsonHttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                Log.i("onSuccess", jsonObject.toString());

                dialog.dialog.dismiss();

            }
            @Override
            public void onStart () {
                super.onStart();
                dialog = DialogUIUtils.showLoading(AmmeterSettingActivity.this, "请求中...", true, true, false, true);
                dialog.show();
            }
            @Override
            public void onFailure ( int errorCode, String msg){
                super.onFailure(errorCode, msg);
//                                Toast.makeText(AmmeterSettingActivity.this,"采集器下没有电表",Toast.LENGTH_SHORT).show();
                dialog.dialog.dismiss();
            }

            @Override
            public void onResponse(String response, Headers headers) {
                super.onResponse(response, headers);
                Log.i("onClick2: ", response);
                JSONArray jsonArray=JSONArray.parseArray(response);
                boolean isyes1=false;//电表1是否成功
                boolean isyes2=true;//电表2是否成功

                dialog.dialog.dismiss();
                if(db2.equals("")){
                    //只有一个电表
                    for(int i=0;i<jsonArray.size();i++){
                        String ssn=jsonArray.getJSONObject(i).getString("sn");
                        String ddb1=achieve_format(db1).toString();
                        Log.i("onResponse: ", ssn+"|"+ddb1);
//                                    String ddbb=ddb1.substring(0,12);
                        Log.i("onResponse: ", ddb1);
                        Log.i("onResponse: ", ssn);

                        if(ssn.contains(ddb1)){
                            isyes1=true;
                            device_list=jsonArray.getJSONObject(i).getString("deviceId");
                            //切换页面
                            Log.i("isyes1: ", "312");
                            break;
                        }
                    }
                    if(isyes1){
                        //成功
                        Log.i("isyes1", "onResponse: ");
                        Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishSuccessActivity.class);
                        intent.putExtra("device_list",device_list);
                        startActivity(intent);
//                        finish();
                    }else{
                        //失败
                        Log.i("isyes1", "onResponse2: ");
                        Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishActivity.class);
                        startActivity(intent);
//                        finish();
                    }
                }else{
                    //两个电表
                    isyes1=false;
                    isyes2=false;
                    String ddd1="";
                    String ddd2="";
                    for(int i=0;i<jsonArray.size();i++){
                        if(jsonArray.getJSONObject(i).getString("sn").equals(achieve_format(db1))){
                            isyes1=true;
                            ddd1=jsonArray.getJSONObject(i).getString("deviceId");
//                            Log.i("onResponse: ", "1");
//                            if(device_list.equals("")){
//                                device_list=jsonArray.getJSONObject(i).getString("deviceId");
//                            }else{
//                                device_list=device_list+","+jsonArray.getJSONObject(i).getString("deviceId");
//                            }
//                            continue;
                        }

                    }
                    for(int i=0;i<jsonArray.size();i++){
                        if(jsonArray.getJSONObject(i).getString("sn").equals(achieve_format(db2))){
                            isyes2=true;
                            ddd2=jsonArray.getJSONObject(i).getString("deviceId");
//                            Log.i("onResponse: ", "2");
//                            if(device_list.equals("")){
//                                device_list=jsonArray.getJSONObject(i).getString("deviceId");
//                            }else{
//                                device_list=device_list+","+jsonArray.getJSONObject(i).getString("deviceId");
//                            }
//                            continue;
                        }
                    }


                    Log.i("onResponse: ", device_list);
                    if(isyes1&&isyes2){
                        //配置成功界面
                        device_list=ddd1+","+ddd2;
                        Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishSuccessActivity.class);
                        intent.putExtra("device_list",device_list);
                        startActivity(intent);
//                        finish();
                    }else if((!isyes1)&&(!isyes2)){
                        //配置失败
                        device_list="";
                        Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishActivity.class);
                        intent.putExtra("device_list",device_list);
                        startActivity(intent);
//                        finish();
                    }else{
                        //一个成功一个失败
//                        Toast.makeText(AmmeterSettingActivity.this,"一个成功一个失败，页面开发中",Toast.LENGTH_SHORT).show();
                        if(isyes1){
                            device_list=ddd1;
                        }else{
                            device_list=ddd2;
                        }
                        Intent intent=new Intent(AmmeterSettingActivity.this,SettingFinishFailSucActivity.class);
                        intent.putExtra("isyes1",isyes1);//看1是正确还是2是正确
                        intent.putExtra("device_list",device_list);//成功的deviceid
                        startActivity(intent);
//                        finish();
                    }
                }


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
            Toast.makeText(AmmeterSettingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return returnMillis;
    }
    //自动补全12位，同时按照要求从后到前每隔2位重组
    private String achieve_format(String updata1){
        String las_sub="";
        DecimalFormat df=new DecimalFormat("000000000000");
        String str2=df.format(Long.parseLong(updata1));

        for(int i=0;i<6;i++){
            String sub_str=str2.substring(12-i*2-2, 12-i*2);
            las_sub=las_sub+sub_str;
        }
//        Log.i("AmmeterSettingActivity", "onClick: "+las_sub);
//        Log.i("AmmeterSettingActivity", "onClick: "+str2);
        return las_sub;
    }
    private void hidekey(){
        /**隐藏软键盘**/
        View view1 = getWindow().peekDecorView();
        if (view1 != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

}
