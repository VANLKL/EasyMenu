package cn.edu.cqust.easymenu.presenter;

import android.content.Context;
import android.text.TextUtils;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.RegisterContract;
import cn.edu.cqust.easymenu.model.UserDao;
import cn.edu.cqust.easymenu.utils.AppExecutors;
import cn.edu.cqust.easymenu.utils.PasswordUtils;

public class RegisterPresenter implements RegisterContract.Presenter {

    private final Context context;
    private final UserDao userDao;

    private RegisterContract.View view;
    private volatile boolean detached = false;

    public RegisterPresenter(Context context, RegisterContract.View view) {
        Context appContext = context.getApplicationContext();
        this.context = appContext;
        this.view = view;
        this.userDao = new UserDao(appContext);
    }

    @Override
    public void onRegisterClicked(String username, String password, String confirmPassword) {
        if (detached || view == null) return;

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            view.showMessage(context.getString(R.string.error_fill_all_fields));
            return;
        }
        if (!password.equals(confirmPassword)) {
            view.showMessage(context.getString(R.string.error_password_mismatch));
            return;
        }
        if (password.length() < 6) {
            view.showMessage(context.getString(R.string.error_password_min_length));
            return;
        }

        view.showLoading(true);

        AppExecutors.runDb(() -> {
            boolean exists = userDao.isUsernameExists(username);
            long rowId = -1;

            if (!exists) {
                String stored = PasswordUtils.createStoredPassword(password);
                rowId = userDao.insertUser(username, stored);
            }

            long finalRowId = rowId;
            AppExecutors.runMain(() -> {
                if (detached || view == null) return;
                view.showLoading(false);

                if (exists) {
                    view.showMessage(context.getString(R.string.error_username_exists));
                } else if (finalRowId != -1) {
                    view.showMessage(context.getString(R.string.toast_register_success));
                    view.finishToLogin();
                } else {
                    view.showMessage(context.getString(R.string.error_register_failed));
                }
            });
        });
    }

    @Override
    public void detach() {
        detached = true;
        view = null;
    }
}
