package cn.edu.cqust.easymenu.presenter;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.MenuContract;
import cn.edu.cqust.easymenu.model.Menu;
import cn.edu.cqust.easymenu.model.MenuDao;
import cn.edu.cqust.easymenu.model.User;
import cn.edu.cqust.easymenu.model.UserDao;
import cn.edu.cqust.easymenu.utils.AppExecutors;
import cn.edu.cqust.easymenu.utils.StringUtils;

public class MenuPresenter implements MenuContract.Presenter {

    private final Context context;
    private final MenuDao menuDao;
    private final UserDao userDao;

    private MenuContract.ListView listView;
    private MenuContract.EditView editView;

    private volatile boolean detached = false;

    private List<Menu> allMenus = new ArrayList<>();

    public MenuPresenter(Context context, MenuContract.ListView listView) {
        this.context = context.getApplicationContext();
        this.listView = listView;
        this.editView = null;
        this.menuDao = new MenuDao(context);
        this.userDao = new UserDao(context);

        // 进入菜单列表页时，读取当前登录用户，显示欢迎语
        loadLoggedInUserAndShowWelcome();
    }

    public MenuPresenter(Context context, MenuContract.EditView editView) {
        this.context = context.getApplicationContext();
        this.listView = null;
        this.editView = editView;
        this.menuDao = new MenuDao(context);
        this.userDao = new UserDao(context);
    }

    /**
     * 查询 users 表中 login_status=1 的用户，并显示 “欢迎，用户名”
     */
    private void loadLoggedInUserAndShowWelcome() {
        if (detached || listView == null) return;

        AppExecutors.runDb(() -> {
            User u = userDao.getLoggedInUser();

            AppExecutors.runMain(() -> {
                if (detached || listView == null) return;

                if (u != null && !TextUtils.isEmpty(u.getUsername())) {
                    listView.showWelcome(u.getUsername());
                } else {
                    //因为未登录应被拦截回登录页
                    listView.showWelcome(context.getString(R.string.default_username));
                }
            });
        });
    }

    @Override
    public void loadMenus() {
        if (detached || listView == null) return;

        listView.showLoading(true);
        AppExecutors.runDb(() -> {
            List<Menu> list = menuDao.getAllMenus();
            allMenus = (list == null) ? new ArrayList<>() : list;

            AppExecutors.runMain(() -> {
                if (detached || listView == null) return;
                listView.showLoading(false);
                listView.renderMenus(new ArrayList<>(allMenus));
            });
        });
    }

    @Override
    public void onSearchTextChanged(String query) {
        if (detached || listView == null) return;

        String q = (query == null) ? "" : query.trim();
        if (q.isEmpty()) {
            listView.renderMenus(new ArrayList<>(allMenus));
            return;
        }

        String qLower = q.toLowerCase(Locale.getDefault());
        List<Menu> filtered = new ArrayList<>();
        for (Menu m : allMenus) {
            String name = m.getName() == null ? "" : m.getName();
            String cat = m.getCategory() == null ? "" : m.getCategory();
            if (name.toLowerCase(Locale.getDefault()).contains(qLower)
                    || cat.toLowerCase(Locale.getDefault()).contains(qLower)) {
                filtered.add(m);
            }
        }
        listView.renderMenus(filtered);
    }

    @Override
    public void onAddClicked() {
        if (detached || listView == null) return;
        listView.navigateToAddMenu();
    }

    @Override
    public void onMenuClicked(Menu menu) {
        if (detached || listView == null || menu == null) return;
        listView.navigateToEditMenu(menu.getMenuId());
    }

    @Override
    public void onMenuLongPressed(Menu menu) {
        // 删除确认框由 Activity 弹出
    }

    @Override
    public void onDeleteConfirmed(Menu menu) {
        if (detached || listView == null || menu == null) return;

        listView.showLoading(true);
        AppExecutors.runDb(() -> {
            int rows = menuDao.deleteMenuById(menu.getMenuId());
            List<Menu> list = menuDao.getAllMenus();
            allMenus = (list == null) ? new ArrayList<>() : list;

            AppExecutors.runMain(() -> {
                if (detached || listView == null) return;
                listView.showLoading(false);
                if (rows > 0) {
                    listView.showMessage(context.getString(R.string.toast_delete_success));
                } else {
                    listView.showMessage(context.getString(R.string.toast_delete_failed));
                }
                listView.renderMenus(new ArrayList<>(allMenus));
            });
        });
    }

    @Override
    public void onBatchDeleteConfirmed(List<Integer> menuIds) {
        if (detached || listView == null || menuIds == null || menuIds.isEmpty()) return;

        listView.showLoading(true);
        AppExecutors.runDb(() -> {
            int rows = menuDao.batchDeleteMenus(menuIds);
            List<Menu> list = menuDao.getAllMenus();
            allMenus = (list == null) ? new ArrayList<>() : list;

            AppExecutors.runMain(() -> {
                if (detached || listView == null) return;
                listView.showLoading(false);
                if (rows > 0) {
                    listView.showMessage(String.format(context.getString(R.string.toast_batch_delete_count), rows));
                } else {
                    listView.showMessage(context.getString(R.string.toast_delete_failed));
                }
                listView.renderMenus(new ArrayList<>(allMenus));
            });
        });
    }

    @Override
    public void onLogoutClicked(String username) {
        if (detached || listView == null) return;

        listView.showLoading(true);
        AppExecutors.runDb(() -> {
            if (!TextUtils.isEmpty(username)) {
                userDao.setLogout(username);
            }
            AppExecutors.runMain(() -> {
                if (detached || listView == null) return;
                listView.showLoading(false);
                listView.backToLogin();
            });
        });
    }

    @Override
    public void loadMenuForEdit(int menuId) {
        if (detached || editView == null) return;

        editView.showLoading(true);
        AppExecutors.runDb(() -> {
            Menu m = menuDao.getMenuById(menuId);
            AppExecutors.runMain(() -> {
                if (detached || editView == null) return;
                editView.showLoading(false);
                if (m != null) {
                    editView.fillForm(m);
                } else {
                    editView.showMessage(context.getString(R.string.error_menu_not_found));
                }
            });
        });
    }

    @Override
    public void saveMenu(Integer menuIdNullable, String name, String category, String priceText, String desc) {
        if (detached || editView == null) return;

        String n = StringUtils.safeTrim(name);
        String c = StringUtils.safeTrim(category);
        String d = StringUtils.safeTrim(desc);
        String pTxt = StringUtils.safeTrim(priceText);

        // 改进的输入验证
        if (StringUtils.isEmpty(n)) {
            editView.showMessage(context.getString(R.string.error_menu_name_empty));
            return;
        }
        if (StringUtils.isEmpty(c)) {
            editView.showMessage(context.getString(R.string.error_category_empty));
            return;
        }
        if (StringUtils.isEmpty(pTxt)) {
            editView.showMessage(context.getString(R.string.error_price_empty));
            return;
        }

        double price;
        try {
            price = Double.parseDouble(pTxt);
            // 添加范围验证
            if (price < 0 || price > 99999.99) {
                editView.showMessage(context.getString(R.string.error_price_range));
                return;
            }
        } catch (Exception e) {
            editView.showMessage(context.getString(R.string.error_price_format));
            return;
        }

        Menu m = new Menu();
        if (menuIdNullable != null) m.setMenuId(menuIdNullable);
        m.setName(n);
        m.setCategory(c);
        m.setPrice(price);
        m.setDescription(d);

        editView.showLoading(true);

        AppExecutors.runDb(() -> {
            boolean ok;
            if (menuIdNullable == null) {
                long id = menuDao.insertMenu(m);
                ok = id != -1;
            } else {
                ok = menuDao.updateMenu(m) > 0;
            }

            AppExecutors.runMain(() -> {
                if (detached || editView == null) return;
                editView.showLoading(false);
                if (ok) {
                    editView.showMessage(context.getString(R.string.toast_save_success));
                    editView.finishWithSuccess();
                } else {
                    editView.showMessage(context.getString(R.string.toast_save_failed));
                }
            });
        });
    }


    @Override
    public void detach() {
        detached = true;
        listView = null;
        editView = null;
    }
}
