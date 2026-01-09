package cn.edu.cqust.easymenu.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class AppExecutors {
    private static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static volatile boolean isShutdown = false;

    private AppExecutors() {}

    public static void runDb(Runnable r) {
        if (isShutdown) return;
        DB_EXECUTOR.execute(r);
    }

    public static void runMain(Runnable r) {
        if (isShutdown) return;
        MAIN_HANDLER.post(r);
    }

    public static void shutdown() {
        isShutdown = true;
        if (DB_EXECUTOR != null && !DB_EXECUTOR.isShutdown()) {
            DB_EXECUTOR.shutdown();
            try {
                if (!DB_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    DB_EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException e) {
                DB_EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void runMainDelayed(Runnable r, long delayMillis) {
        if (isShutdown) return;
        MAIN_HANDLER.postDelayed(r, delayMillis);
    }
}
