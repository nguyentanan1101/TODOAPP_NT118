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

import java.util.List;

public class SubTaskGroupAdapter extends RecyclerView.Adapter<SubTaskGroupAdapter.GroupViewHolder> {

    private Context context;
    private List<SubTaskGroup> groups;

    public SubTaskGroupAdapter(Context context, List<SubTaskGroup> groups) {
        this.context = context;
        this.groups = groups;
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

        holder.recyclerSubTasks.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerSubTasks.setAdapter(new SubTaskAdapter(context, group.getSubTasks()));
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
