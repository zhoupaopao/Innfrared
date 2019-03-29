package com.example.innfrared;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lenovo on 2019/3/28.
 */

public class SysApplication extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static SysApplication instance;

    private SysApplication() {
    }
    public synchronized static SysApplication getInstance() {
        if (null == instance) {
            instance = new SysApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //注意：次方法用于垃圾回收，如果手机内存小，或使用虚拟机测试，一定要注释掉这段代码
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
