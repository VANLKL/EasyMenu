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

/**
 * 闪屏启动页面
 * 
 * 【功能说明】
 * 1. 应用启动时的闪屏页面，展示应用Logo和欢迎界面
 * 2. 检查用户登录状态，决定跳转到登录页还是菜单列表页
 * 3. 实现了闪屏时间控制：最少显示2秒，最多5秒
 * 
 * 【设计要点】
 * - 使用Handler实现延迟跳转
 * - 异步查询数据库避免阻塞主线程
 * - 超时保护机制，确保不会因数据库操作卡死
 * - 使用volatile和标志位防止多次跳转
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    // 闪屏最小展示时间（毫秒）：保证用户能看清Logo
    private static final long MIN_SPLASH_TIME = 2000L;
    // 闪屏最大展示时间（毫秒）：防止因异常长时间卡住
    private static final long MAX_SPLASH_TIME = 5000L;

    // 主线程Handler，用于处理延迟跳转
    private final Handler handler = new Handler(Looper.getMainLooper());
    // 标记是否已经执行跳转，防止多次跳转
    private volatile boolean hasNavigated = false;
    // 记录Activity创建时间，用于计算闪屏展示时长
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 应用系统栏内边距，适配全面屏
        InsetUtils.applySystemBarInsets(findViewById(android.R.id.content));

        // 记录开始时间
        startTime = System.currentTimeMillis();

        // 【闪屏功能-超时保护】设置最大超时保护，最多5秒后强制跳转
        handler.postDelayed(this::navigateToLogin, MAX_SPLASH_TIME);

        // 【闪屏功能-登录状态检查】检查登录状态并决定跳转目标
        checkLoginStatusAndNavigate();
    }

    /**
     * 【闪屏功能-核心逻辑】检查登录状态并计算延迟跳转
     * 
     * 执行流程：
     * 1. 在后台线程查询数据库，获取当前登录用户（login_status=1的用户）
     * 2. 回到主线程，计算实际需要的等待时间
     * 3. 确保闪屏至少显示2秒，但不超时
     * 4. 根据登录状态跳转到对应页面（已登录→菜单列表，未登录→登录页）
     */
    private void checkLoginStatusAndNavigate() {
        // 在数据库线程执行查询，避免阻塞UI
        AppExecutors.runDb(() -> {
            UserDao userDao = new UserDao(getApplicationContext());
            // 查询当前登录状态的用户
            User loggedUser = userDao.getLoggedInUser();

            // 切换回主线程处理UI跳转
            AppExecutors.runMain(() -> {
                if (hasNavigated) return;

                // 计算已耗时
                long costTime = System.currentTimeMillis() - startTime;
                // 计算还需要等待的时间（至少展示2秒）
                long waitTime = MIN_SPLASH_TIME - costTime;

                // 确保至少展示2秒，但不超过5秒
                long finalDelay = Math.max(0, Math.min(waitTime, MAX_SPLASH_TIME - costTime));

                // 延迟后执行跳转
                handler.postDelayed(() -> navigateToNext(loggedUser), finalDelay);
            });
        });
    }

    /**
     * 【闪屏功能-页面跳转】根据登录状态跳转到对应页面
     * 
     * @param loggedUser 当前登录的用户对象，如果为null表示未登录
     */
    private void navigateToNext(User loggedUser) {
        // 防止重复跳转和Activity销毁后的跳转
        if (hasNavigated || isFinishing() || isDestroyed()) {
            return;
        }

        // 标记已跳转
        hasNavigated = true;

        // 根据登录状态决定跳转目标
        Intent intent;
        if (loggedUser != null) {
            // 【闪屏功能-已登录】跳转到菜单列表页
            intent = new Intent(SplashActivity.this, MenuListActivity.class);
        } else {
            // 【闪屏功能-未登录】跳转到登录页
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    /**
     * 【闪屏功能-超时处理】超时回调，强制跳转到登录页
     * 当数据库查询耗时超过最大限制时触发
     */
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
