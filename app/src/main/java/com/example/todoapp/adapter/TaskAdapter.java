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

    // Interface xóa (nếu bạn dùng ở màn CompletedTasks)
    private OnDeleteClickListener deleteListener;

    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
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

        // --- 1. HIỂN THỊ LOẠI TASK & MÀU SẮC NỀN ---
        String typeLabel = "Task";
        String colorCode = "#FFFFFF";

        if (task.getType() != null) {
            switch (task.getType()) {
                case PERSONAL:
                    typeLabel = "Personal";
                    colorCode = "#67a4f5"; // Xanh dương
                    break;
                case WORK_PRIVATE:
                case WORK_GROUP:
                    typeLabel = "Work";
                    colorCode = "#d9a46f"; // Vàng
                    break;
                default:
                    typeLabel = "Other";
                    colorCode = "#EEEEEE";
                    break;
            }
        }

        holder.tvTaskType.setText(typeLabel);
        holder.cardView.setCardBackgroundColor(Color.parseColor(colorCode));

        // --- 2. HIỂN THỊ TÊN TASK ---
        holder.tvTaskName.setText(task.getTitle() != null ? task.getTitle() : "");

        // --- 3. MỚI THÊM: HIỂN THỊ PRIORITY ---
        // Giả sử TaskModel có getter getPriority(), nếu chưa có bạn nhớ thêm vào Model
        String priority = task.getPriority();
        if (priority == null) priority = "Low";

        int priorityColor;
        switch (priority) {
            case "High":
            case "Critical":
                priorityColor = Color.parseColor("#FF5252"); // Đỏ
                break;
            case "Medium":
                priorityColor = Color.parseColor("#AB47BC"); // Tím (cho khác biệt)
                break;
            default: // Low
                priorityColor = Color.parseColor("#757575"); // Xám đậm
                break;
        }

        // Tô màu chấm tròn
        if (holder.viewPriorityDot != null) {
            holder.viewPriorityDot.getBackground().setTint(priorityColor);
        }
        // Set chữ và màu chữ
        if (holder.tvPriority != null) {
            holder.tvPriority.setText(priority);
            holder.tvPriority.setTextColor(priorityColor);
        }

        // --- 4. NÚT DELETE ---
        if (task.getType() == TaskModel.TaskType.PERSONAL) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(task, holder.getAdapterPosition());
                } else {
                    // Fallback: Xóa local nếu không có listener (như code cũ)
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
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 4, 0, 4);
                tv.setLayoutParams(params);

                if (sub.isDone()) {
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tv.setAlpha(0.6f);
                }
                holder.subtaskContainer.addView(tv);
            }
        }

        // --- 6. TRẠNG THÁI HOÀN THÀNH ---
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

        // View mới cho Priority
        View viewPriorityDot;
        TextView tvPriority;

        LinearLayout subtaskContainer;
        CardView cardView;
        View btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskType = itemView.findViewById(R.id.tvTaskType);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvCompleted = itemView.findViewById(R.id.tvCompleted);

            // Ánh xạ ID mới
            viewPriorityDot = itemView.findViewById(R.id.viewPriorityDot);
            tvPriority = itemView.findViewById(R.id.tvPriority);

            subtaskContainer = itemView.findViewById(R.id.subtaskContainer);
            cardView = itemView.findViewById(R.id.taskCard);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}