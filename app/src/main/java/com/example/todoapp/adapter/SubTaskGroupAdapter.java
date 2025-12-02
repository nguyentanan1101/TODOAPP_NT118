package com.example.todoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.SubTaskGroup;
import com.example.todoapp.models.SubTaskModel;

import java.util.ArrayList;
import java.util.List;

public class SubTaskGroupAdapter extends RecyclerView.Adapter<SubTaskGroupAdapter.GroupViewHolder> {

    private Context context;
    private List<SubTaskGroup> groups;

    // Pool dùng chung để tái sử dụng view giữa các recyclerview con -> Tăng hiệu năng cuộn
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public SubTaskGroupAdapter(Context context, List<SubTaskGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    /**
     * Hàm quan trọng: Gom tất cả subtask từ các nhóm lại thành 1 list phẳng.
     * Vì các adapter con đã cập nhật trực tiếp vào model object,
     * nên list này sẽ chứa trạng thái isDone mới nhất.
     */
    public List<SubTaskModel> getAllSubTasks() {
        List<SubTaskModel> all = new ArrayList<>();
        if (groups != null) {
            for (SubTaskGroup group : groups) {
                if (group.getSubTasks() != null) {
                    all.addAll(group.getSubTasks());
                }
            }
        }
        return all;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subtask_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        SubTaskGroup group = groups.get(position);

        // 1. Hiển thị tiêu đề nhóm (Today, Tomorrow...)
        holder.tvGroupTitle.setText(group.getDueLabel());

        // 2. Cấu hình LayoutManager cho RecyclerView con
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerSubTasks.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        // Tối ưu: Cho biết trước số lượng item để Android vẽ nhanh hơn
        layoutManager.setInitialPrefetchItemCount(group.getSubTasks().size());

        // 3. Tạo Adapter cho danh sách con
        SubTaskAdapter subTaskAdapter = new SubTaskAdapter(context, group.getSubTasks());

        holder.recyclerSubTasks.setLayoutManager(layoutManager);
        holder.recyclerSubTasks.setAdapter(subTaskAdapter);
        holder.recyclerSubTasks.setRecycledViewPool(viewPool); // Chia sẻ pool

        // 4. Lắng nghe sự kiện tick từ Adapter con
        // Khi user tick, ta cập nhật ngay vào Model
        subTaskAdapter.setOnSubTaskCheckedChangeListener((subTask, isChecked) -> {
            subTask.setDone(isChecked);
            // Không cần làm gì thêm, vì khi bấm nút TICK ở Activity,
            // hàm getAllSubTasks() sẽ lấy được dữ liệu đã update này.
        });
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;
        RecyclerView recyclerSubTasks;

        GroupViewHolder(View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tvGroupTitle);
            // Đảm bảo ID này khớp với file item_subtask_group.xml
            recyclerSubTasks = itemView.findViewById(R.id.recyclerSubTasksInner);
        }
    }
}