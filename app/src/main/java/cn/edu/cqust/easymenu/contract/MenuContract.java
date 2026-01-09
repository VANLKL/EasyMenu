package cn.edu.cqust.easymenu.contract;

import java.util.List;

import cn.edu.cqust.easymenu.model.Menu;

/**
 * 【MVP设计模式】菜单模块的契约接口
 *
 * 定义View层和Presenter层之间的交互规范，实现视图与业务逻辑的解耦
 */
public interface MenuContract {

    /**
     * 【MVP设计模式-View层】菜单列表视图接口
     * 由MenuListActivity实现，定义列表页面的UI操作
     */
    interface ListView {
        /** 显示/隐藏加载进度条 */
        void showLoading(boolean show);
        /** 显示提示消息 */
        void showMessage(String msg);
        /** 显示欢迎用户信息 */
        void showWelcome(String username);
        /** 渲染菜单列表数据 */
        void renderMenus(List<Menu> menus);

        /** 【菜单添加】跳转到菜单添加页面 */
        void navigateToAddMenu();
        /** 【菜单修改】跳转到菜单编辑页面 */
        void navigateToEditMenu(int menuId);
        /** 【退出登录】返回登录页面 */
        void backToLogin();
    }

    /**
     * 【MVP设计模式-View层】菜单编辑视图接口
     * 由MenuEditActivity实现，定义编辑页面的UI操作
     */
    interface EditView {
        /** 显示/隐藏加载进度条 */
        void showLoading(boolean show);
        /** 显示提示消息 */
        void showMessage(String msg);
        /** 填充表单数据（编辑模式下） */
        void fillForm(Menu menu);
        /** 保存成功后关闭页面 */
        void finishWithSuccess();
    }

    /**
     * 【MVP设计模式-Presenter层】菜单业务逻辑接口
     * 由MenuPresenter实现，处理所有菜单相关的业务逻辑
     */
    interface Presenter {
        /** 加载菜单列表 */
        void loadMenus();
        /** 处理搜索文本变化 */
        void onSearchTextChanged(String query);
        /** 【菜单添加】添加按钮点击事件 */
        void onAddClicked();
        /** 【菜单修改】菜单项点击事件 */
        void onMenuClicked(Menu menu);
        /** 菜单项长按事件 */
        void onMenuLongPressed(Menu menu);
        /** 【菜单删除】确认删除菜单 */
        void onDeleteConfirmed(Menu menu);
        /** 【菜单删除-批量删除】确认批量删除 */
        void onBatchDeleteConfirmed(List<Integer> menuIds);
        /** 【退出登录】退出登录按钮点击事件 */
        void onLogoutClicked(String username);

        /** 加载菜单数据用于编辑 */
        void loadMenuForEdit(int menuId);
        /** 【菜单添加/修改】保存菜单 */
        void saveMenu(Integer menuIdNullable, String name, String category, String priceText, String desc);

        /** 解绑View，防止内存泄漏 */
        void detach();
    }
}
