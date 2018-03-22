package com.wapchief.livertmpandroid;

import android.app.Application;
import android.content.Context;

/**
 * @author wapchief
 * @date 2018/3/21
 */

public class BaseApplication extends Application{

    public static Application mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

    }

    public static Context getApplication() {
        return mApplication;
    }
}
