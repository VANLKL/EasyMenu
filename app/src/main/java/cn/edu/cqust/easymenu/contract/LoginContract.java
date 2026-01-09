package cn.edu.cqust.easymenu.contract;

/**
 * 【MVP设计模式】登录模块的契约接口
 *
 * 定义View层和Presenter层之间的交互规范，实现视图与业务逻辑的解耦
 */
public interface LoginContract {

    /**
     * 【MVP设计模式-View层】登录视图接口
     * 由LoginActivity实现，定义登录页面的UI操作
     */
    interface View {
        /** 显示/隐藏加载进度条 */
        void showLoading(boolean show);
        /** 显示提示消息 */
        void showMessage(String msg);
        /** 【登录功能-登录成功】跳转到主页面 */
        void navigateToMain(String username);
        /** 【注册跳转】跳转到注册页面 */
        void navigateToRegister();
    }

    /**
     * 【MVP设计模式-Presenter层】登录业务逻辑接口
     * 由LoginPresenter实现，处理登录相关的业务逻辑
     */
    interface Presenter {
        /** 【登录功能】处理登录按钮点击事件 */
        void onLoginClicked(String username, String password);
        /** 【注册跳转】处理注册链接点击事件 */
        void onRegisterLinkClicked();
        /** 解绑View，防止内存泄漏 */
        void detach();
    }
}
