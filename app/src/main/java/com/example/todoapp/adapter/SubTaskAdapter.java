package com.example.todoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.SubTaskModel;

import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {

    private Context context;
    private List<SubTaskModel> subTasks;

    public SubTaskAdapter(Context context, List<SubTaskModel> subTasks) {
        this.context = context;
        this.subTasks = subTasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subtask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubTaskModel subTask = subTasks.get(position);

        // Set tiêu đề
        holder.tvTitle.setText(subTask.getTitle());

        // Set checkbox
        holder.chkSubtask.setChecked(subTask.isDone());
        holder.chkSubtask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subTask.setDone(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkSubtask;
        TextView tvTitle, tvDueDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chkSubtask = itemView.findViewById(R.id.chkSubtask);
            tvTitle = itemView.findViewById(R.id.tvSubTaskTitle);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
        }
    }
}
