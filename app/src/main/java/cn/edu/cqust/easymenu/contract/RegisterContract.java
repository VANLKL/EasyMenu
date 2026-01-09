package cn.edu.cqust.easymenu.contract;

public interface RegisterContract {

    interface View {
        void showLoading(boolean show);
        void showMessage(String msg);
        void finishToLogin();
    }

    interface Presenter {
        void onRegisterClicked(String username, String password, String confirmPassword);
        void detach();
    }
}
