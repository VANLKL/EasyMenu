package cn.edu.cqust.easymenu.contract;

public interface LoginContract {

    interface View {
        void showLoading(boolean show);
        void showMessage(String msg);
        void navigateToMain(String username);
        void navigateToRegister();
    }

    interface Presenter {
        void onLoginClicked(String username, String password);
        void onRegisterLinkClicked();
        void detach();
    }
}
