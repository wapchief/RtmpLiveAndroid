package com.wapchief.livertmpandroid;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getApplication() {
        return mApplication;
    }
}
