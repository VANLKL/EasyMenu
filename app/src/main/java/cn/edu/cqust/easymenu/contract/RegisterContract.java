package cn.edu.cqust.easymenu.contract;

/**
 * 【MVP设计模式】注册模块的契约接口
 *
 * 定义View层和Presenter层之间的交互规范，实现视图与业务逻辑的解耦
 */
public interface RegisterContract {

    /**
     * 【MVP设计模式-View层】注册视图接口
     * 由RegisterActivity实现，定义注册页面的UI操作
     */
    interface View {
        /** 显示/隐藏加载进度条 */
        void showLoading(boolean show);
        /** 显示提示消息 */
        void showMessage(String msg);
        /** 【注册功能-注册成功】返回登录页面 */
        void finishToLogin();
    }

    /**
     * 【MVP设计模式-Presenter层】注册业务逻辑接口
     * 由RegisterPresenter实现，处理注册相关的业务逻辑
     */
    interface Presenter {
        /** 【注册功能】处理注册按钮点击事件 */
        void onRegisterClicked(String username, String password, String confirmPassword);
        /** 解绑View，防止内存泄漏 */
        void detach();
    }
}
