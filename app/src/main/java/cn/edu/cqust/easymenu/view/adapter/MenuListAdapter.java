package cn.edu.cqust.easymenu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.edu.cqust.easymenu.R;
import cn.edu.cqust.easymenu.model.Menu;

/**
 * 【适配器+ListView】菜单列表适配器
 *
 * 【功能说明】
 * 将菜单数据绑定到ListView进行展示
 *
 * 【设计要点-适配器+ListView】
 * - 继承BaseAdapter实现自定义适配器
 * - 支持普通展示模式和批量选择模式
 * - 使用ViewHolder优化ListView性能
 * - 多选模式下显示复选框
 */
public class MenuListAdapter extends BaseAdapter {

    /**
     * 菜单项操作监听接口
     * 定义点击和长按事件回调
     */
    public interface OnMenuActionListener {
        /** 菜单项点击事件 */
        void onItemClick(Menu menu);
        /** 菜单项长按事件 */
        void onItemLongClick(Menu menu);
    }

    /** 上下文 */
    private final Context context;
    /** 菜单数据列表 */
    private final List<Menu> data = new ArrayList<>();
    /** 操作监听器 */
    private OnMenuActionListener listener;
    /** 【菜单删除-批量删除】选中的菜单ID集合 */
    private Set<Integer> selectedMenuIds;
    /** 【菜单删除-批量删除】是否处于选择模式 */
    private boolean isSelectionMode = false;

    public MenuListAdapter(Context context) {
        this.context = context;
    }

    public void setOnMenuActionListener(OnMenuActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<Menu> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    //设置多选模式和选中项集合

    public void setSelectionMode(boolean isSelectionMode, Set<Integer> selectedMenuIds) {
        this.isSelectionMode = isSelectionMode;
        this.selectedMenuIds = selectedMenuIds;
        notifyDataSetChanged();
    }

    public Menu getItemData(int position) {
        if (position < 0 || position >= data.size()) return null;
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tvName, tvCategory, tvPrice, tvDesc;
        CheckBox checkboxSelection;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        Menu menu = data.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
            vh = new ViewHolder();
            vh.tvName = convertView.findViewById(R.id.tvName);
            vh.tvCategory = convertView.findViewById(R.id.tvCategory);
            vh.tvPrice = convertView.findViewById(R.id.tvPrice);
            vh.tvDesc = convertView.findViewById(R.id.tvDesc);
            vh.checkboxSelection = convertView.findViewById(R.id.checkboxSelection);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // 设置菜单内容
        vh.tvName.setText(menu.getName());
        vh.tvCategory.setText(context.getString(R.string.menu_item_category) + menu.getCategory());
        vh.tvPrice.setText(String.format(context.getString(R.string.price_format), menu.getPrice()));

        String desc = menu.getDescription();
        if (desc == null || desc.isEmpty()) {
            vh.tvDesc.setVisibility(View.GONE);
        } else {
            vh.tvDesc.setVisibility(View.VISIBLE);
            vh.tvDesc.setText(desc);
        }

        // 多选模式下的勾选标记
        if (isSelectionMode) {
            vh.checkboxSelection.setVisibility(View.VISIBLE);
            boolean isSelected = selectedMenuIds != null && selectedMenuIds.contains(menu.getMenuId());
            vh.checkboxSelection.setChecked(isSelected);
        } else {
            vh.checkboxSelection.setVisibility(View.GONE);
        }

        // 点击事件
        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(menu);
            }
        });

        convertView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(menu);
            }
            return true;
        });

        return convertView;
    }
}
