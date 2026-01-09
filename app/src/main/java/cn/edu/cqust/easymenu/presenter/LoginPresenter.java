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

public class LoginPresenter implements LoginContract.Presenter {

    private final Context context;
    private final UserDao userDao;
    private final LoginHistoryDao historyDao;

    private LoginContract.View view;
    private volatile boolean detached = false;

    public LoginPresenter(Context context, LoginContract.View view) {
        Context appContext = context.getApplicationContext();
        this.context = appContext;
        this.view = view;
        this.userDao = new UserDao(appContext);
        this.historyDao = new LoginHistoryDao(appContext);
    }

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
            User u = userDao.getUserByUsername(uName);
            boolean ok = (u != null) && PasswordUtils.verifyPassword(pwd, u.getPassword());

            if (ok) {
                // 设置 login_status=1（确保后续 Splash/MenuList 能识别已登录）
                userDao.setLoggedInUser(uName);

                // 记录登录历史
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

    @Override
    public void onRegisterLinkClicked() {
        if (detached || view == null) return;
        view.navigateToRegister();
    }

    @Override
    public void detach() {
        detached = true;
        view = null;
    }
}
