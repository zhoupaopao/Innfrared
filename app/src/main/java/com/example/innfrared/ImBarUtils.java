package com.example.innfrared;

import android.app.Activity;

import com.gyf.barlibrary.ImmersionBar;

/**
 * Created by Lenovo on 2019/3/28.
 */

public class ImBarUtils {
    public static Boolean setBar(Activity activity){
        ImmersionBar.with(activity)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();
        return true;
    }
}
