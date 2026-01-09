package cn.edu.cqust.easymenu.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.MenuContract;
import cn.edu.cqust.easymenu.model.Menu;
import cn.edu.cqust.easymenu.presenter.MenuPresenter;
import cn.edu.cqust.easymenu.utils.AppExecutors;
import cn.edu.cqust.easymenu.utils.InsetUtils;
import cn.edu.cqust.easymenu.view.adapter.MenuListAdapter;

/**
 * 菜单列表页面（View层）
 *
 * 【功能说明】
 * 1. 【菜单展示】使用ListView展示菜单信息列表
 * 2. 【菜单添加】点击添加按钮启动MenuEditActivity进行菜单添加
 * 3. 【菜单修改】点击菜单项启动MenuEditActivity进行菜单编辑
 * 4. 【菜单删除】长按菜单项显示操作菜单，支持单个删除和批量删除
 * 5. 【退出登录】点击退出按钮，清除登录状态并返回登录页
 * 6. 【搜索功能】支持按菜名和分类搜索菜单
 *
 * 【设计要点-本地SQLite数据库】
 * - 使用SQLite数据库存储菜单信息（menus表）
 * - 菜单字段包含：menu_id, name, category, price, description（不少于4个字段）
 * - 初始数据包含10条以上的菜单记录
 *
 * 【设计要点-MVP设计模式】
 * - View层：负责UI展示和用户交互
 * - Presenter层：处理菜单业务逻辑
 * - Model层：MenuDao负责数据持久化
 * - 通过MenuContract接口定义View和Presenter的契约
 *
 * 【设计要点-适配器+ListView】
 * - 使用MenuListAdapter适配器绑定数据到ListView
 * - 支持普通模式和多选模式
 * - 多选模式下显示复选框，支持批量操作
 */
public class MenuListActivity extends AppCompatActivity implements MenuContract.ListView {

    private TextView tvWelcome, tvEmpty, tvSelectedCount;
    private Button btnAdd, btnLogout, btnBatchDelete, btnCancelSelection;
    private EditText etSearch;
    private ProgressBar progress;
    private ListView lvMenus;
    private View normalOperationBar;
    private View selectionOperationBar;

    private MenuListAdapter adapter;
    private MenuPresenter presenter;
    private String username = "";

    // 多选模式相关
    private Set<Integer> selectedMenuIds = new HashSet<>();
    private boolean isSelectionMode = false;

    private ActivityResultLauncher<Intent> editLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        InsetUtils.applySystemBarInsets(findViewById(android.R.id.content));

        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        normalOperationBar = findViewById(R.id.normalOperationBar);
        selectionOperationBar = findViewById(R.id.selectionOperationBar);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);
        btnBatchDelete = findViewById(R.id.btnBatchDelete);
        btnCancelSelection = findViewById(R.id.btnCancelSelection);
        etSearch = findViewById(R.id.etSearch);
        progress = findViewById(R.id.progress);
        lvMenus = findViewById(R.id.lvMenus);

        presenter = new MenuPresenter(this, this);

        adapter = new MenuListAdapter(this);
        adapter.setOnMenuActionListener(new MenuListAdapter.OnMenuActionListener() {
            @Override
            public void onItemClick(Menu menu) {
                // 多选模式下点击用于选择/取消选择
                if (isSelectionMode) {
                    toggleSelection(menu);
                } else {
                    // 正常模式下点击进入编辑模式
                    if (menu != null) {
                        presenter.onMenuClicked(menu);
                    }
                }
            }

            @Override
            public void onItemLongClick(Menu menu) {
                // 长按弹出操作菜单
                showActionMenu(menu);
            }
        });
        lvMenus.setAdapter(adapter);

        editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        presenter.loadMenus();
                    }
                }
        );

        btnAdd.setOnClickListener(v -> presenter.onAddClicked());
        btnLogout.setOnClickListener(v -> presenter.onLogoutClicked(username));

        // 批量删除按钮
        btnBatchDelete.setOnClickListener(v -> showBatchDeleteConfirm());

        // 取消多选按钮
        btnCancelSelection.setOnClickListener(v -> exitSelectionMode());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                presenter.onSearchTextChanged(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        presenter.loadMenus();
    }

    //显示长按操作菜单

    private void showActionMenu(Menu menu) {
        if (menu == null) return;

        new AlertDialog.Builder(this)
                .setTitle(menu.getName())
                .setItems(new CharSequence[]{
                        getString(R.string.action_edit),
                        getString(R.string.action_delete),
                        getString(R.string.action_multi_select)
                }, (dialog, which) -> {
                    switch (which) {
                        case 0: // 编辑
                            presenter.onMenuClicked(menu);
                            break;
                        case 1: // 删除
                            showDeleteConfirm(menu);
                            break;
                        case 2: // 多选
                            enterSelectionMode(menu);
                            break;
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    //进入多选模式

    private void enterSelectionMode(Menu firstSelectedMenu) {
        isSelectionMode = true;
        selectedMenuIds.clear();
        if (firstSelectedMenu != null) {
            selectedMenuIds.add(firstSelectedMenu.getMenuId());
        }
        updateSelectionModeUI();
        adapter.setSelectionMode(true, selectedMenuIds);
        Toast.makeText(this, getString(R.string.toast_enter_selection_mode), Toast.LENGTH_SHORT).show();
    }

    //退出多选模式

    private void exitSelectionMode() {
        isSelectionMode = false;
        selectedMenuIds.clear();
        updateSelectionModeUI();
        adapter.setSelectionMode(false, null);
    }

    //更新多选模式UI

    private void updateSelectionModeUI() {
        if (isSelectionMode) {
            // 显示多选操作栏，隐藏正常操作栏
            normalOperationBar.setVisibility(View.GONE);
            selectionOperationBar.setVisibility(View.VISIBLE);
            tvSelectedCount.setText(getString(R.string.selected_count_format, selectedMenuIds.size()));
        } else {
            // 显示正常操作栏，隐藏多选操作栏
            normalOperationBar.setVisibility(View.VISIBLE);
            selectionOperationBar.setVisibility(View.GONE);
        }
    }

    //切换选中状态（多选模式下）

    private void toggleSelection(Menu menu) {
        if (!isSelectionMode) return;

        if (menu != null) {
            if (selectedMenuIds.contains(menu.getMenuId())) {
                selectedMenuIds.remove(menu.getMenuId());
            } else {
                selectedMenuIds.add(menu.getMenuId());
            }
            tvSelectedCount.setText(getString(R.string.selected_count_format, selectedMenuIds.size()));
            adapter.setSelectionMode(true, selectedMenuIds);
        }
    }

    //显示批量删除确认对话框

    private void showBatchDeleteConfirm() {
        if (selectedMenuIds.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_no_selection), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_batch_delete_title)
                .setMessage(getString(R.string.dialog_batch_delete_msg_format, selectedMenuIds.size()))
                .setPositiveButton(R.string.btn_confirm_delete, (dialog, which) -> performBatchDelete())
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    //执行批量删除

    private void performBatchDelete() {
        if (selectedMenuIds.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_no_selection), Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示进度提示
        Toast.makeText(this,
                String.format(getString(R.string.toast_deleting_count), selectedMenuIds.size()),
                Toast.LENGTH_SHORT).show();

        List<Integer> menuIdsToDelete = new ArrayList<>(selectedMenuIds);

        // 删除完成后退出多选模式
        AppExecutors.runMainDelayed(() -> {
            exitSelectionMode();
        }, 100);

        presenter.onBatchDeleteConfirmed(menuIdsToDelete);
    }


    /**
     * 显示单个删除确认对话框
     */
    private void showDeleteConfirm(Menu menu) {
        if (menu == null) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_msg_format, menu.getName()))
                .setPositiveButton(R.string.btn_confirm_delete, (d, w) -> presenter.onDeleteConfirmed(menu))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    @Override
    public void renderMenus(List<Menu> menus) {
        adapter.setData(menus);
        boolean empty = (menus == null || menus.isEmpty());
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvMenus.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showWelcome(String username) {
        this.username = username;
        tvWelcome.setText(getString(R.string.welcome_user_format, username));
    }

    @Override
    public void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnAdd.setEnabled(!show);
        btnLogout.setEnabled(!show);
        etSearch.setEnabled(!show);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToAddMenu() {
        Intent it = new Intent(this, MenuEditActivity.class);
        it.putExtra("mode", "add");
        editLauncher.launch(it);
    }

    @Override
    public void navigateToEditMenu(int menuId) {
        Intent it = new Intent(this, MenuEditActivity.class);
        it.putExtra("mode", "edit");
        it.putExtra("menu_id", menuId);
        editLauncher.launch(it);
    }

    @Override
    public void backToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }
}
