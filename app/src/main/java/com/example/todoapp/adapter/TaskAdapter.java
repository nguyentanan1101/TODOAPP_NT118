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

    public void setTasks(List<TaskModel> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
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

        // --- HIỂN THỊ LOẠI TASK (Tiêu đề lớn) ---
        String typeLabel = "";
        String colorCode = "#FFFFFF";

        switch(task.getType()) {
            case PERSONAL:
                typeLabel = "Personal";
                colorCode = "#A5D6A7";  // Xanh lá Pastel
                break;
            case WORK_PRIVATE:
                typeLabel = "Work";
                colorCode = "#FFE082";  // Vàng Pastel
                break;
            case WORK_GROUP:
                typeLabel = "Team";
                colorCode = "#90CAF9";  // Xanh dương Pastel
                break;
        }

        holder.tvTaskType.setText(typeLabel);
        holder.cardView.setCardBackgroundColor(Color.parseColor(colorCode));

        // --- HIỂN THỊ TÊN TASK (Dòng nhỏ bên dưới) ---
        holder.tvTaskName.setText(task.getTitle() != null ? task.getTitle() : "");


        // --- NÚT DELETE ---
        holder.btnDelete.setVisibility(task.getType() == TaskModel.TaskType.PERSONAL ? View.VISIBLE : View.GONE);
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION) {
                taskList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, taskList.size());
            }
        });

        // --- 4. HIỂN THỊ DANH SÁCH SUBTASK ---
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
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 4, 0, 4);
                tv.setLayoutParams(params);

                if (sub.isDone()) {
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tv.setAlpha(0.6f);
                }

                holder.subtaskContainer.addView(tv);
            }
        }

        // --- 5. TRẠNG THÁI HOÀN THÀNH ---
        if(task.isDone() && task.getCompletedDate() != null && !task.getCompletedDate().isEmpty()) {
            holder.tvCompleted.setVisibility(View.VISIBLE);
            holder.tvCompleted.setText("Done: " + task.getCompletedDate());
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
        TextView tvTaskType;
        TextView tvTaskName;
        TextView tvCompleted;
        LinearLayout subtaskContainer;
        CardView cardView;
        View btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskType = itemView.findViewById(R.id.tvTaskType);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            // Đã xóa ánh xạ tvProgress

            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvCompleted = itemView.findViewById(R.id.tvCompleted);
            subtaskContainer = itemView.findViewById(R.id.subtaskContainer);
            cardView = itemView.findViewById(R.id.taskCard);
        }
    }
}