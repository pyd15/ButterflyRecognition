package com.example.butterflyrecognition.recycleView.search;

import android.app.Application;

/**
 * Created by Dr.P on 2017/11/10.
 */
public class BaseApplication extends Application {


    private static BaseApplication mApplication;

    public static synchronized BaseApplication getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (null == mApplication) {
            mApplication = this;
        }
    }
}
