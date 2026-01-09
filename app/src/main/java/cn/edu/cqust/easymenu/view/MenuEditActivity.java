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

/**
 * 菜单编辑页面（View层）- 用于添加和修改菜单
 *
 * 【功能说明】
 * 1. 【菜单添加】从MenuListActivity启动，添加新的菜单项
 * 2. 【菜单修改】从MenuListActivity启动，修改已有的菜单项
 * 3. 表单包含4个必填字段：菜名、分类、价格、描述
 * 4. 保存成功后返回MenuListActivity并刷新列表
 *
 * 【设计要点-启动另一个界面实现添加/修改】
 * - 这是一个独立的Activity，用于处理菜单的添加和修改操作
 * - 通过Intent传递mode参数区分是添加还是编辑模式
 * - 编辑模式下通过menu_id参数传递要编辑的菜单ID
 * - 使用ActivityResultLauncher处理编辑结果回调
 *
 * 【设计要点-MVP设计模式】
 * - View层：负责表单UI展示和用户输入
 * - Presenter层：处理表单验证和保存逻辑
 * - Model层：MenuDao负责数据持久化
 * - 通过MenuContract.EditView接口定义View契约
 *
 * 【设计要点-菜单字段】
 * 菜单信息字段（不少于4个）：
 * - name：菜名
 * - category：分类
 * - price：价格
 * - description：描述
 * - created_at：创建时间
 */
public class MenuEditActivity extends AppCompatActivity implements MenuContract.EditView {

    // 【菜单添加/修改】菜名输入框
    private EditText etName;
    // 【菜单添加/修改】分类输入框
    private EditText etCategory;
    // 【菜单添加/修改】价格输入框
    private EditText etPrice;
    // 【菜单添加/修改】描述输入框
    private EditText etDesc;
    // 【菜单添加/修改】保存按钮
    private Button btnSave;
    // 【菜单添加/修改】加载进度条
    private ProgressBar progress;

    // 【MVP设计模式】菜单业务逻辑处理器
    private MenuPresenter presenter;

    // 【菜单添加/修改】操作模式："add"添加或"edit"编辑
    private String mode;
    // 【菜单添加/修改】菜单ID（编辑模式下使用，添加模式下为null）
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
