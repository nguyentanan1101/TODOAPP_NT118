package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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

    public interface OnTaskDeleteListener {
        void onTaskDelete(int position);
    }

    private Context context;
    private List<TaskModel> taskList;
    private OnTaskDeleteListener deleteListener;

    public TaskAdapter(Context context, List<TaskModel> taskList, OnTaskDeleteListener deleteListener) {
        this.context = context;
        this.taskList = taskList;
        this.deleteListener = deleteListener;
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

        // Task lớn gạch chân
        holder.tvTaskName.setText(task.getTitle());
        holder.tvTaskName.setPaintFlags(holder.tvTaskName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Màu và nút delete
        switch (task.getType()) {
            case PERSONAL:
                holder.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                holder.btnDelete.setVisibility(View.VISIBLE);
                break;
            case WORK_PRIVATE:
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FF9800"));
                holder.btnDelete.setVisibility(View.GONE);
                break;
            case WORK_GROUP:
                holder.cardView.setCardBackgroundColor(Color.parseColor("#2196F3"));
                holder.btnDelete.setVisibility(View.GONE);
                break;
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (task.getType() == TaskModel.TaskType.PERSONAL && deleteListener != null) {
                deleteListener.onTaskDelete(position);
            }
        });

        // Hiển thị subtask (chỉ tên)
        holder.subtaskContainer.removeAllViews();
        for (SubTaskModel sub : task.getSubTasks()) {
            TextView subTv = new TextView(context);
            subTv.setText("• " + sub.getTitle());
            subTv.setTextSize(14f);
            subTv.setTextColor(Color.BLACK);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 4, 0, 4);
            subTv.setLayoutParams(params);

            holder.subtaskContainer.addView(subTv);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName;
        ImageView btnDelete;
        LinearLayout subtaskContainer;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            subtaskContainer = itemView.findViewById(R.id.subtaskContainer);
            cardView = itemView.findViewById(R.id.taskCard);
        }
    }
}
