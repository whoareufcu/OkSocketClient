package com.example.oksocketclient;

import android.app.Application;

import com.example.oksocketclient.utils.CrashHandler;
import com.example.oksocketclient.utils.OkSocketUtils;

public class MyApplication extends Application {
    public static MyApplication instances;
    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        CrashHandler.getInstance().init(this);
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());

        //Socket服务端注册端口号
        OkSocketUtils.getInstace().registerServerPort();
    }

    /**
     * 单例模式 * *
     */
    public static MyApplication getInstances() {
        return instances;
    }
}
