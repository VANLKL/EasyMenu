package cn.edu.cqust.easymenu.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.RegisterContract;
import cn.edu.cqust.easymenu.presenter.RegisterPresenter;

/**
 * 用户注册页面（View层）
 *
 * 【功能说明】
 * 1. 【注册功能】新用户注册账号
 * 2. 输入用户名、密码和确认密码进行注册
 * 3. 注册成功后返回登录页
 *
 * 【设计要点-本地SQLite数据库】
 * - 使用SQLite数据库存储用户信息（users表）
 * - 用户名唯一性校验，不允许重复注册
 * - 密码经过加密后存储（使用PasswordUtils工具类）
 *
 * 【设计要点-MVP设计模式】
 * - View层：负责UI展示和用户交互
 * - Presenter层：处理注册业务逻辑和验证
 * - Model层：UserDao负责数据持久化
 * - 通过RegisterContract接口定义View和Presenter的契约
 */
public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {

    // 【注册功能-UI组件】用户名输入框
    private EditText etUsername;
    // 【注册功能-UI组件】密码输入框
    private EditText etPassword;
    // 【注册功能-UI组件】确认密码输入框
    private EditText etConfirmPassword;
    // 【注册功能-UI组件】注册按钮
    private Button btnRegister;
    // 【注册功能-UI组件】加载进度条
    private ProgressBar progress;

    // 【MVP设计模式】注册业务逻辑处理器
    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progress = findViewById(R.id.progress);

        presenter = new RegisterPresenter(this, this);

        btnRegister.setOnClickListener(v ->
                presenter.onRegisterClicked(
                        etUsername.getText().toString().trim(),
                        etPassword.getText().toString(),
                        etConfirmPassword.getText().toString()
                )
        );
    }

    /**
     * 【注册功能】显示/隐藏加载状态
     * @param show true显示加载进度条，false隐藏
     */
    @Override
    public void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }

    /**
     * 【注册功能】显示提示消息
     * @param msg 消息内容
     */
    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 【注册功能-注册成功】返回登录页面
     */
    @Override
    public void finishToLogin() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.detach();
    }
}
