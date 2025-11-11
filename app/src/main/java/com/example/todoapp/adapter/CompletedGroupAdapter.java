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
import com.example.todoapp.models.CompletedGroupModel;
import com.example.todoapp.models.TaskModel;

import java.util.List;

public class CompletedGroupAdapter extends RecyclerView.Adapter<CompletedGroupAdapter.GroupViewHolder> {

    private final Context context;
    private final List<CompletedGroupModel> groups;

    public CompletedGroupAdapter(Context context, List<CompletedGroupModel> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_completed_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        CompletedGroupModel group = groups.get(position);
        holder.tvGroupTitle.setText(group.getDate());

        // Adapter con hiển thị các task trong ngày đó
        CompletedTaskAdapter adapter = new CompletedTaskAdapter(context, group.getTasks());
        holder.recyclerSubTasksInner.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerSubTasksInner.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;
        RecyclerView recyclerSubTasksInner;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tvGroupTitle);
            recyclerSubTasksInner = itemView.findViewById(R.id.recyclerSubTasksInner);
        }
    }
}
