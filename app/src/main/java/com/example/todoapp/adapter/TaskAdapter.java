package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private List<TaskModel> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter(Context context, List<TaskModel> taskList) {
        this.context = context;
        this.taskList = taskList != null ? taskList : new ArrayList<>();
    }

    // Cập nhật data mà không tạo adapter mới
    public void setTasks(List<TaskModel> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
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

        // Hiển thị tên task
        holder.tvTaskName.setText(task.getTitle() != null ? task.getTitle() : "");

        // Màu card theo loại task
        switch(task.getType()) {
            case PERSONAL: holder.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50")); break;
            case WORK_PRIVATE: holder.cardView.setCardBackgroundColor(Color.parseColor("#FF9800")); break;
            case WORK_GROUP: holder.cardView.setCardBackgroundColor(Color.parseColor("#2196F3")); break;
        }

        // Hiển thị nút xóa chỉ cho task cá nhân
        holder.btnDelete.setVisibility(task.getType() == TaskModel.TaskType.PERSONAL ? View.VISIBLE : View.GONE);
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION) {
                taskList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, taskList.size());
            }
        });

        // Hiển thị subtask
        holder.subtaskContainer.removeAllViews();
        if(task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            for(SubTaskModel sub : task.getSubTasks()) {
                TextView tv = new TextView(context);
                String text = "• " + (sub.getTitle() != null ? sub.getTitle() : "");

                tv.setText(text);
                tv.setTextSize(14f);
                tv.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 4, 0, 4);
                tv.setLayoutParams(params);

                holder.subtaskContainer.addView(tv);
            }
        }

        // Nếu task đã hoàn thành, hiển thị ngày hoàn thành
        if(task.isDone() && task.getCompletedDate() != null && !task.getCompletedDate().isEmpty()) {
            holder.tvCompleted.setVisibility(View.VISIBLE);
            holder.tvCompleted.setText("Completed: " + task.getCompletedDate());
        } else {
            holder.tvCompleted.setVisibility(View.GONE);
        }

        // Click vào task
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onTaskClick(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName, tvCompleted;
        LinearLayout subtaskContainer;
        CardView cardView;
        View btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvCompleted = itemView.findViewById(R.id.tvCompleted);
            subtaskContainer = itemView.findViewById(R.id.subtaskContainer);
            cardView = itemView.findViewById(R.id.taskCard);
        }
    }
}
