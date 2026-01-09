package cn.edu.cqust.easymenu.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.model.User;
import cn.edu.cqust.easymenu.model.UserDao;
import cn.edu.cqust.easymenu.utils.AppExecutors;
import cn.edu.cqust.easymenu.utils.InsetUtils;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long MIN_SPLASH_TIME = 2000L;
    private static final long MAX_SPLASH_TIME = 5000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean hasNavigated = false;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        InsetUtils.applySystemBarInsets(findViewById(android.R.id.content));

        startTime = System.currentTimeMillis();

        // 设置超时保护
        handler.postDelayed(this::navigateToLogin, MAX_SPLASH_TIME);

        checkLoginStatusAndNavigate();
    }

    private void checkLoginStatusAndNavigate() {
        AppExecutors.runDb(() -> {
            UserDao userDao = new UserDao(getApplicationContext());
            User loggedUser = userDao.getLoggedInUser();

            AppExecutors.runMain(() -> {
                if (hasNavigated) return;

                long costTime = System.currentTimeMillis() - startTime;
                long waitTime = MIN_SPLASH_TIME - costTime;

                // 确保至少展示2秒，但不超过5秒
                long finalDelay = Math.max(0, Math.min(waitTime, MAX_SPLASH_TIME - costTime));

                handler.postDelayed(() -> navigateToNext(loggedUser), finalDelay);
            });
        });
    }

    private void navigateToNext(User loggedUser) {
        if (hasNavigated || isFinishing() || isDestroyed()) {
            return;
        }

        hasNavigated = true;

        Intent intent;
        if (loggedUser != null) {
            intent = new Intent(SplashActivity.this, MenuListActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        if (!hasNavigated) {
            navigateToNext(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
