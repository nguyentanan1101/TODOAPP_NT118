package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff; // Import này
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskModel;

import java.util.List;

public class CalendarTaskAdapter extends RecyclerView.Adapter<CalendarTaskAdapter.ViewHolder> {

    private Context context;
    private List<TaskModel> list;
    private String selectedDate;

    public CalendarTaskAdapter(Context context, List<TaskModel> list, String selectedDate) {
        this.context = context;
        this.list = list;
        this.selectedDate = selectedDate;
    }

    public void updateDate(String newDate) {
        this.selectedDate = newDate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = list.get(position);

        // --- KHAI BÁO MÀU SẮC ---
        int mainColor;      // Màu chấm tròn & chữ loại task
        int subTaskBgColor; // Màu nền khung subtask (nhạt hơn)

        if (task.getType() == TaskModel.TaskType.PERSONAL) {
            holder.tvTaskType.setText("Personal");
            mainColor = Color.parseColor("#4CAF50");      // Xanh lá đậm
            subTaskBgColor = Color.parseColor("#E8F5E9"); // Xanh lá cực nhạt (cho nền subtask)
        } else {
            holder.tvTaskType.setText("Work");
            mainColor = Color.parseColor("#FFA726");      // Cam đậm
            subTaskBgColor = Color.parseColor("#FFF3E0"); // Cam cực nhạt (cho nền subtask)
        }

        // 1. Set màu cho chấm tròn (Type)
        holder.viewTypeDot.getBackground().setTint(mainColor);

        // 2. Set màu cho nền Subtask Container (MỚI THÊM)
        // Dùng mutate() để đảm bảo không bị đổi màu nhầm sang các item khác khi cuộn
        holder.containerSubtasks.getBackground().mutate().setTint(subTaskBgColor);

        // 3. Hiển thị Tiêu đề
        holder.tvTaskTitle.setText(task.getTitle());

        // 4. Hiển thị Subtask trùng ngày
        holder.containerSubtasks.removeAllViews();

        if (task.getSubTasks() != null) {
            boolean hasSubtaskToday = false;
            for (SubTaskModel sub : task.getSubTasks()) {
                if (sub.getDueDate() != null && sub.getDueDate().equals(selectedDate)) {
                    hasSubtaskToday = true;
                    TextView tvSub = new TextView(context);

                    // Thêm icon gạch đầu dòng hoặc checkbox giả
                    tvSub.setText("• " + sub.getTitle());
                    tvSub.setTextSize(14);
                    tvSub.setTextColor(Color.parseColor("#424242")); // Màu chữ đậm hơn xíu cho dễ đọc
                    tvSub.setPadding(8, 8, 8, 8); // Tăng padding cho thoáng

                    holder.containerSubtasks.addView(tvSub);
                }
            }

            // Nếu không có subtask nào hôm nay thì ẩn khung này đi cho đẹp
            if (!hasSubtaskToday) {
                holder.containerSubtasks.setVisibility(View.GONE);
            } else {
                holder.containerSubtasks.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskType, tvTaskTitle;
        View viewTypeDot;
        LinearLayout containerSubtasks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskType = itemView.findViewById(R.id.tvTaskType);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            viewTypeDot = itemView.findViewById(R.id.viewTypeDot);
            containerSubtasks = itemView.findViewById(R.id.containerSubtasks);
        }
    }
}