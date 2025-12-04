package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
    private OnDeleteClickListener deleteListener;
    private boolean isCompletedScreen = false;

    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
    }
    public void setCompletedScreen(boolean isCompletedScreen) {
        this.isCompletedScreen = isCompletedScreen;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(TaskModel task, int position);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public TaskAdapter(Context context, List<TaskModel> taskList) {
        this.context = context;
        this.taskList = taskList != null ? taskList : new ArrayList<>();
    }

    public void setTasks(List<TaskModel> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        }
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

        // --- 1. MÀU NỀN & GẠCH NGANG CHỮ ---
        String typeLabel = "Task";
        int backgroundColor;
        int titleColor;
        boolean shouldStrikeThrough = false; // Biến cờ để kiểm soát gạch ngang

        if (isCompletedScreen) {
            // TRƯỜNG HỢP: MÀN HÌNH COMPLETED TASKS
            backgroundColor = Color.parseColor("#F5F5F5");
            titleColor = Color.parseColor("#000000"); // Chữ đen rõ ràng

            if (task.getType() == TaskModel.TaskType.PERSONAL) typeLabel = "Personal";
            else typeLabel = "Work";

            // KHÔNG GẠCH NGANG CHỮ (QUAN TRỌNG)
            shouldStrikeThrough = false;

        } else {
            // TRƯỜNG HỢP: MÀN HÌNH CHÍNH (MAIN)
            if (task.isDone()) {
                backgroundColor = Color.parseColor("#F5F5F5");
                titleColor = Color.parseColor("#9E9E9E");
                if (task.getType() == TaskModel.TaskType.PERSONAL) typeLabel = "Personal";
                else typeLabel = "Work";

                // Gạch ngang
                shouldStrikeThrough = true;
            } else {
                titleColor = Color.parseColor("#000000");
                if (task.getType() == TaskModel.TaskType.PERSONAL) {
                    typeLabel = "Personal";
                    backgroundColor = Color.parseColor("#E3F2FD");
                } else {
                    typeLabel = "Work";
                    backgroundColor = Color.parseColor("#FFFFFF");
                }
                // Không gạch ngang
                shouldStrikeThrough = false;
            }
        }

        holder.tvTaskType.setText(typeLabel);
        holder.cardView.setCardBackgroundColor(backgroundColor);
        holder.tvTaskName.setText(task.getTitle() != null ? task.getTitle() : "");
        holder.tvTaskName.setTextColor(titleColor);

        // Áp dụng gạch ngang dựa trên biến cờ đã tính toán ở trên
        if (shouldStrikeThrough) {
            holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // (Đã xóa đoạn code setPaintFlags bị lặp thừa ở đây)

        // --- 3. MÀU PRIORITY (CHỈ HIỆN DOT) ---
        String priority = task.getPriority();
        if (priority == null) priority = "Low";

        int priorityColor;
        switch (priority) {
            case "High":
            case "Critical":
                priorityColor = Color.parseColor("#D32F2F"); // Đỏ
                break;
            case "Medium":
                priorityColor = Color.parseColor("#FBC02D"); // Vàng
                break;
            default: // Low
                priorityColor = Color.parseColor("#388E3C"); // Xanh
                break;
        }

        if (holder.viewPriorityDot != null) {
            holder.viewPriorityDot.getBackground().setTint(priorityColor);
        }

        // --- 4. DELETE ---
        if (task.getType() == TaskModel.TaskType.PERSONAL) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(task, holder.getAdapterPosition());
                } else {
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        taskList.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, taskList.size());
                    }
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // --- 5. SUBTASKS ---
        holder.subtaskContainer.removeAllViews();

        if(task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            holder.subtaskContainer.setVisibility(View.VISIBLE);

            int limit = Math.min(task.getSubTasks().size(), 4);
            for(int i = 0; i < limit; i++) {
                SubTaskModel sub = task.getSubTasks().get(i);
                TextView tv = new TextView(context);
                String text = "— " + (sub.getTitle() != null ? sub.getTitle() : "");
                tv.setText(text);
                tv.setTextSize(13f);
                tv.setTextColor(Color.parseColor("#333333"));
                tv.setMaxLines(1);
                tv.setEllipsize(android.text.TextUtils.TruncateAt.END);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 4, 0, 4);
                tv.setLayoutParams(params);

                if (sub.isDone()) {
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tv.setAlpha(0.6f);
                } else {
                    tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    tv.setAlpha(1.0f);
                }
                holder.subtaskContainer.addView(tv);
            }
        } else {
            holder.subtaskContainer.setVisibility(View.GONE);
        }

        // --- 6. DONE DATE ---
        if(task.isDone()) {
            holder.tvCompleted.setVisibility(View.VISIBLE);
            String date = task.getCompletedDate() != null ? task.getCompletedDate() : "Just now";
            holder.tvCompleted.setText("Done: " + date);
        } else {
            holder.tvCompleted.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if(listener != null) listener.onTaskClick(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskType, tvTaskName, tvCompleted;
        View viewPriorityDot;
        LinearLayout subtaskContainer;
        CardView cardView;
        View btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskType = itemView.findViewById(R.id.tvTaskType);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvCompleted = itemView.findViewById(R.id.tvCompleted);
            viewPriorityDot = itemView.findViewById(R.id.viewPriorityDot);
            subtaskContainer = itemView.findViewById(R.id.subtaskContainer);
            cardView = itemView.findViewById(R.id.taskCard);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}