package cn.edu.cqust.easymenu;

import android.app.Application;

import cn.edu.cqust.easymenu.utils.AppExecutors;

public class EasyMenuApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 清理资源
        AppExecutors.shutdown();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 内存不足时清理缓存
        AppExecutors.shutdown();
    }
}
