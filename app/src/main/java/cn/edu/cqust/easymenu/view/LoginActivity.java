package cn.edu.cqust.easymenu.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.LoginContract;
import cn.edu.cqust.easymenu.model.LoginHistoryDao;
import cn.edu.cqust.easymenu.presenter.LoginPresenter;
import cn.edu.cqust.easymenu.utils.AppExecutors;

/**
 * 用户登录页面（View层）
 * 
 * 【功能说明】
 * 1. 【登录功能】用户输入用户名和密码进行登录验证
 * 2. 【历史登录】显示历史登录过的用户名，支持快速选择
 * 3. 【注册跳转】提供跳转到注册页面的入口
 * 
 * 【设计要点-本地SQLite数据库】
 * - 使用SQLite数据库存储用户信息（users表）和登录历史（login_history表）
 * - 用户名和密码经过加密后存储
 * - 登录成功后更新users表的login_status字段为1
 * - 每次登录成功都会在login_history表中记录一条登录历史
 * 
 * 【设计要点-MVP设计模式】
 * - View层：负责UI展示和用户交互
 * - Presenter层：处理登录业务逻辑
 * - Model层：UserDao和LoginHistoryDao负责数据持久化
 * - 通过LoginContract接口定义View和Presenter的契约
 */
public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    // 【登录功能-UI组件】用户名输入框
    private EditText etUsername;
    // 【登录功能-UI组件】密码输入框
    private EditText etPassword;
    // 【登录功能-UI组件】登录按钮
    private Button btnLogin;
    // 【登录功能-UI组件】跳转到注册页的文本链接
    private TextView tvGoRegister;
    // 【登录功能-UI组件】加载进度条
    private ProgressBar progress;

    // 【历史登录功能】历史登录用户名下拉选择框
    private Spinner spHistoryUsers;
    // 【历史登录功能】下拉框适配器
    private ArrayAdapter<String> historyAdapter;
    // 【历史登录功能】历史用户名列表数据
    private final List<String> historyItems = new ArrayList<>();
    // 【历史登录功能-本地SQLite数据库】登录历史数据访问对象
    private LoginHistoryDao historyDao;

    // 【MVP设计模式】登录业务逻辑处理器
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化UI组件
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        progress = findViewById(R.id.progress);

        // 【历史登录功能】绑定历史用户名Spinner
        spHistoryUsers = findViewById(R.id.spHistoryUsers);

        // 【MVP设计模式】创建Presenter实例，绑定View
        presenter = new LoginPresenter(this, this);
        // 【本地SQLite数据库】创建登录历史数据访问对象
        historyDao = new LoginHistoryDao(this);

        // 【历史登录功能】初始化历史用户名下拉框
        initHistoryUsersSpinner();

        // 【登录功能】设置登录按钮点击事件
        btnLogin.setOnClickListener(v ->
                presenter.onLoginClicked(
                        safeText(etUsername),
                        safeText(etPassword)
                )
        );

        // 【注册跳转】设置注册链接点击事件
        tvGoRegister.setOnClickListener(v -> presenter.onRegisterLinkClicked());
    }

    private String safeText(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    /**
     * 【历史登录功能】初始化历史登录用户名下拉框
     *
     * 功能特点：
     * 1. 从本地SQLite数据库的login_history表查询所有曾登录过的用户名
     * 2. 在Spinner中展示历史用户名列表
     * 3. 选择历史用户名后自动填充到用户名输入框
     * 4. 异步加载数据，避免阻塞UI线程
     */
    private void initHistoryUsersSpinner() {
        // 清空列表，添加提示项
        historyItems.clear();
        historyItems.add(getString(R.string.hint_select_history_user));

        // 创建Spinner适配器
        historyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                historyItems
        );
        historyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHistoryUsers.setAdapter(historyAdapter);

        // 【历史登录功能】设置选中事件：选择历史用户名后自动填充
        spHistoryUsers.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return; // 跳过提示项
                // 获取选中的用户名并填充到输入框
                String selected = (String) parent.getItemAtPosition(position);
                etUsername.setText(selected);
                etUsername.setSelection(selected.length()); // 光标移到末尾
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // 【本地SQLite数据库】异步加载历史用户名
        AppExecutors.runDb(() -> {
            // 从login_history表查询所有不同的用户名
            List<String> usernames = historyDao.getDistinctUsernames();
            // 切换回主线程更新UI
            AppExecutors.runMain(() -> {
                historyItems.clear();
                historyItems.add(getString(R.string.hint_select_history_user));
                if (usernames != null) historyItems.addAll(usernames);
                historyAdapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * 【登录功能】显示/隐藏加载状态
     * @param show true显示加载进度条，false隐藏
     */
    @Override
    public void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        tvGoRegister.setEnabled(!show);
        etUsername.setEnabled(!show);
        etPassword.setEnabled(!show);
        spHistoryUsers.setEnabled(!show);
    }

    /**
     * 【登录功能】显示提示消息
     * @param msg 消息内容
     */
    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 【登录功能-登录成功】跳转到菜单列表页
     * @param username 登录的用户名
     */
    @Override
    public void navigateToMain(String username) {
        Intent it = new Intent(this, MenuListActivity.class);
        it.putExtra("username", username);
        startActivity(it);
        finish();
    }

    /**
     * 【注册跳转】跳转到注册页面
     */
    @Override
    public void navigateToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.detach();
    }
}
