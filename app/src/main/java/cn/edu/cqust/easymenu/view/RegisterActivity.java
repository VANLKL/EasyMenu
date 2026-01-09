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

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progress;

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

    @Override
    public void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

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
