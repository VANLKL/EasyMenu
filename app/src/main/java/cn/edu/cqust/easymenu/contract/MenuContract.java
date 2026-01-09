package cn.edu.cqust.easymenu.contract;

import java.util.List;

import cn.edu.cqust.easymenu.model.Menu;

public interface MenuContract {

    interface ListView {
        void showLoading(boolean show);
        void showMessage(String msg);
        void showWelcome(String username);
        void renderMenus(List<Menu> menus);

        void navigateToAddMenu();
        void navigateToEditMenu(int menuId);
        void backToLogin();
    }

    interface EditView {
        void showLoading(boolean show);
        void showMessage(String msg);
        void fillForm(Menu menu);
        void finishWithSuccess();
    }

    interface Presenter {
        void loadMenus();
        void onSearchTextChanged(String query);
        void onAddClicked();
        void onMenuClicked(Menu menu);
        void onMenuLongPressed(Menu menu);
        void onDeleteConfirmed(Menu menu);
        void onBatchDeleteConfirmed(List<Integer> menuIds);
        void onLogoutClicked(String username);

        void loadMenuForEdit(int menuId);
        void saveMenu(Integer menuIdNullable, String name, String category, String priceText, String desc);

        void detach();
    }
}
