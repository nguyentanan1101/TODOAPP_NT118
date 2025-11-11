package com.example.todoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public interface OnSubTaskCheckedChangeListener {
        void onCheckedChanged(SubTaskModel subTask, boolean isChecked);
    }

    private OnSubTaskCheckedChangeListener listener;

    public void setOnSubTaskCheckedChangeListener(OnSubTaskCheckedChangeListener listener) {
        this.listener = listener;
    }

    public SubTaskGroupAdapter(Context context, List<SubTaskGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    // Lấy tất cả subtask để gửi lên server
    public List<SubTaskModel> getAllSubTasks() {
        List<SubTaskModel> all = new ArrayList<>();
        for (SubTaskGroup group : groups) {
            all.addAll(group.getSubTasks());
        }
        return all;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subtask_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        SubTaskGroup group = groups.get(position);
        holder.tvGroupTitle.setText(group.getDueLabel());

        // Adapter con cho các subtask trong nhóm
        SubTaskAdapter adapter = new SubTaskAdapter(context, group.getSubTasks());
        holder.recyclerSubTasks.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerSubTasks.setAdapter(adapter);

        // Gắn listener để thay đổi trạng thái local khi tick checkbox
        adapter.setOnSubTaskCheckedChangeListener((subTask, isChecked) -> {
            subTask.setDone(isChecked); // cập nhật trạng thái local
            if (listener != null) listener.onCheckedChanged(subTask, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;
        RecyclerView recyclerSubTasks;

        GroupViewHolder(View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tvGroupTitle);
            recyclerSubTasks = itemView.findViewById(R.id.recyclerSubTasksInner);
        }
    }
}
