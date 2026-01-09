package cn.edu.cqust.easymenu.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.contract.MenuContract;
import cn.edu.cqust.easymenu.model.Menu;
import cn.edu.cqust.easymenu.presenter.MenuPresenter;

public class MenuEditActivity extends AppCompatActivity implements MenuContract.EditView {

    private EditText etName, etCategory, etPrice, etDesc;
    private Button btnSave;
    private ProgressBar progress;

    private MenuPresenter presenter;

    private String mode; // add/edit
    private Integer menuIdNullable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        btnSave = findViewById(R.id.btnSave);
        progress = findViewById(R.id.progress);

        presenter = new MenuPresenter(this, this);

        mode = getIntent().getStringExtra("mode");
        if ("edit".equals(mode)) {
            int id = getIntent().getIntExtra("menu_id", -1);
            if (id != -1) {
                menuIdNullable = id;
                presenter.loadMenuForEdit(id);
            } else {
                showMessage(getString(R.string.error_parameter_error));
                finish();
                return;
            }
        }

        btnSave.setOnClickListener(v -> presenter.saveMenu(
                menuIdNullable,
                etName.getText().toString(),
                etCategory.getText().toString(),
                etPrice.getText().toString(),
                etDesc.getText().toString()
        ));
    }

    @Override
    public void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }

    @Override
    public void showMessage(String msg) {
        if (msg == null || msg.isEmpty()) return;

        // 根据消息类型显示不同时长
        int duration = msg.contains(getString(R.string.toast_batch_delete_failed).replace("请重试", "")) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(this, msg, duration).show();
    }


    @Override
    public void fillForm(Menu menu) {
        etName.setText(menu.getName());
        etCategory.setText(menu.getCategory());
        etPrice.setText(String.valueOf(menu.getPrice()));
        etDesc.setText(menu.getDescription());
    }

    @Override
    public void finishWithSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.detach();
    }
}
