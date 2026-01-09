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

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private ProgressBar progress;

    // 历史登录用户名 Spinner
    private Spinner spHistoryUsers;
    private ArrayAdapter<String> historyAdapter;
    private final List<String> historyItems = new ArrayList<>();
    private LoginHistoryDao historyDao;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        progress = findViewById(R.id.progress);

        // 绑定 Spinner
        spHistoryUsers = findViewById(R.id.spHistoryUsers);

        presenter = new LoginPresenter(this, this);
        historyDao = new LoginHistoryDao(this);

        initHistoryUsersSpinner();

        btnLogin.setOnClickListener(v ->
                presenter.onLoginClicked(
                        safeText(etUsername),
                        safeText(etPassword)
                )
        );

        tvGoRegister.setOnClickListener(v -> presenter.onRegisterLinkClicked());
    }

    private String safeText(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void initHistoryUsersSpinner() {
        historyItems.clear();
        historyItems.add(getString(R.string.hint_select_history_user));

        historyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                historyItems
        );
        historyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHistoryUsers.setAdapter(historyAdapter);

        // 选中后填充用户名
        spHistoryUsers.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return; // 提示项
                String selected = (String) parent.getItemAtPosition(position);
                etUsername.setText(selected);
                etUsername.setSelection(selected.length());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // 异步加载历史用户名
        AppExecutors.runDb(() -> {
            List<String> usernames = historyDao.getDistinctUsernames();
            AppExecutors.runMain(() -> {
                historyItems.clear();
                historyItems.add(getString(R.string.hint_select_history_user));
                if (usernames != null) historyItems.addAll(usernames);
                historyAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        tvGoRegister.setEnabled(!show);
        etUsername.setEnabled(!show);
        etPassword.setEnabled(!show);
        spHistoryUsers.setEnabled(!show);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMain(String username) {
        Intent it = new Intent(this, MenuListActivity.class);
        it.putExtra("username", username);
        startActivity(it);
        finish();
    }

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
