package cn.edu.cqust.easymenu.presenter;

import android.content.Context;
import android.text.TextUtils;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.LoginContract;
import cn.edu.cqust.easymenu.model.LoginHistoryDao;
import cn.edu.cqust.easymenu.model.User;
import cn.edu.cqust.easymenu.model.UserDao;
import cn.edu.cqust.easymenu.utils.AppExecutors;
import cn.edu.cqust.easymenu.utils.DateTimeUtils;
import cn.edu.cqust.easymenu.utils.PasswordUtils;
import cn.edu.cqust.easymenu.utils.StringUtils;

/**
 * 【MVP设计模式-Presenter层】登录业务逻辑处理器
 *
 * 【功能说明】
 * 处理登录页面的业务逻辑，包括用户认证、登录状态管理、登录历史记录
 *
 * 【设计要点-MVP设计模式】
 * - 实现LoginContract.Presenter接口
 * - 持有View接口引用，通过接口调用View方法
 * - 负责调用Model层进行数据操作
 * - 使用异步操作避免阻塞UI线程
 *
 * 【设计要点-本地SQLite数据库】
 * - 通过UserDao进行用户查询和登录状态更新
 * - 通过LoginHistoryDao记录登录历史
 * - 登录成功后更新users表的login_status字段为1
 */
public class LoginPresenter implements LoginContract.Presenter {

    /** 上下文 */
    private final Context context;
    /** 【本地SQLite数据库】用户数据访问对象 */
    private final UserDao userDao;
    /** 【本地SQLite数据库】登录历史数据访问对象 */
    private final LoginHistoryDao historyDao;

    /** View接口引用 */
    private LoginContract.View view;
    /** 是否已解绑，防止内存泄漏 */
    private volatile boolean detached = false;

    /**
     * 构造函数
     * @param context 上下文
     * @param view View接口
     */
    public LoginPresenter(Context context, LoginContract.View view) {
        Context appContext = context.getApplicationContext();
        this.context = appContext;
        this.view = view;
        this.userDao = new UserDao(appContext);
        this.historyDao = new LoginHistoryDao(appContext);
    }

    /**
     * 【登录功能】处理登录按钮点击事件
     * @param username 用户名
     * @param password 密码
     */
    @Override
    public void onLoginClicked(String username, String password) {
        if (detached || view == null) return;

        String uName = StringUtils.safeTrim(username);
        String pwd = password == null ? "" : password.trim();

        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(pwd)) {
            view.showMessage(context.getString(R.string.error_login_empty));
            return;
        }

        view.showLoading(true);

        AppExecutors.runDb(() -> {
            // 【登录功能】查询用户信息
            User u = userDao.getUserByUsername(uName);
            // 验证密码
            boolean ok = (u != null) && PasswordUtils.verifyPassword(pwd, u.getPassword());

            if (ok) {
                // 【登录功能-登录成功】设置 login_status=1（确保后续 Splash/MenuList 能识别已登录）
                userDao.setLoggedInUser(uName);

                // 【登录功能-记录历史】记录登录历史到login_history表
                historyDao.insertHistory(uName, DateTimeUtils.nowDateTimeString());
            }

            AppExecutors.runMain(() -> {
                if (detached || view == null) return;

                view.showLoading(false);

                if (ok) {
                    // 这里传 username 只是为了界面立即显示；真正的登录态以 DB 为准
                    view.navigateToMain(uName);
                } else {
                    view.showMessage(context.getString(R.string.error_login_failed));
                }
            });
        });
    }

    /**
     * 【注册跳转】处理注册链接点击事件
     */
    @Override
    public void onRegisterLinkClicked() {
        if (detached || view == null) return;
        view.navigateToRegister();
    }

    /**
     * 【MVP设计模式】解绑View，防止内存泄漏
     */
    @Override
    public void detach() {
        detached = true;
        view = null;
    }
}
