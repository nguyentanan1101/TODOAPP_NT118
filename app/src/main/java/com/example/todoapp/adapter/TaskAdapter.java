package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<TaskModel> taskList;

    public TaskAdapter(Context context, List<TaskModel> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        holder.tvTaskName.setText(task.getTitle());

        // màu card
        switch(task.getType()) {
            case PERSONAL: holder.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50")); break;
            case WORK_PRIVATE: holder.cardView.setCardBackgroundColor(Color.parseColor("#FF9800")); break;
            case WORK_GROUP: holder.cardView.setCardBackgroundColor(Color.parseColor("#2196F3")); break;
        }

        // Hiển thị nút xóa chỉ cho task cá nhân
        if(task.getType() == TaskModel.TaskType.PERSONAL) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                taskList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, taskList.size());
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // hiển thị subtask
        holder.subtaskContainer.removeAllViews();
        for(SubTaskModel sub : task.getSubTasks()) {
            TextView subTv = new TextView(context);
            subTv.setText("• " + sub.getTitle());
            subTv.setTextSize(14f);
            subTv.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,4,0,4);
            subTv.setLayoutParams(params);
            holder.subtaskContainer.addView(subTv);
        }
    }

    @Override
    public int getItemCount() { return taskList.size(); }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView btnDelete;
        TextView tvTaskName;
        LinearLayout subtaskContainer;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btnDelete); // ánh xạ nút xóa
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            subtaskContainer = itemView.findViewById(R.id.subtaskContainer);
            cardView = itemView.findViewById(R.id.taskCard);
        }
    }
}
