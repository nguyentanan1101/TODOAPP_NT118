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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.SubTaskModel;
import com.example.todoapp.models.TaskGroup;
import com.example.todoapp.models.TaskItem;
import com.example.todoapp.models.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class TaskGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TASK = 1;

    private final Context context;
    private final List<TaskItem> items;
    private OnTaskClickListener listener;

    // Interface click
    public interface OnTaskClickListener {
        void onTaskClick(TaskModel task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    // Constructor nhận danh sách nhóm và phẳng hóa thành list items
    public TaskGroupAdapter(Context context, List<TaskGroup> groups) {
        this.context = context;
        this.items = new ArrayList<>();

        if (groups != null) {
            for (TaskGroup group : groups) {
                // Thêm Header
                items.add(new TaskItem(group.getDateTitle()));
                // Thêm các Task con
                for (TaskModel task : group.getTasks()) {
                    items.add(new TaskItem(task));
                }
            }
        }
    }

    private boolean isHeader(int position) {
        return items.get(position).isHeader();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER : TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.layout_task_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // Tái sử dụng layout của TaskAdapter
            View view = inflater.inflate(R.layout.layout_task_item, parent, false);
            return new TaskAdapter.TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(item.getHeaderTitle());
        }
        else if (holder instanceof TaskAdapter.TaskViewHolder) {
            TaskAdapter.TaskViewHolder h = (TaskAdapter.TaskViewHolder) holder;
            TaskModel task = item.getTask();

            // --- 1. MÀU NỀN & LOẠI TASK (Đồng bộ với TaskAdapter) ---
            String typeLabel = "Task";
            int backgroundColor;
            int titleColor;

            if (task.isDone()) {
                backgroundColor = Color.parseColor("#F5F5F5"); // Xám
                titleColor = Color.parseColor("#9E9E9E");
                if (task.getType() == TaskModel.TaskType.PERSONAL) typeLabel = "Personal";
                else typeLabel = "Work";
            } else {
                titleColor = Color.parseColor("#000000");
                if (task.getType() == TaskModel.TaskType.PERSONAL) {
                    typeLabel = "Personal";
                    backgroundColor = Color.parseColor("#E3F2FD"); // Xanh
                } else {
                    typeLabel = "Work";
                    backgroundColor = Color.parseColor("#FFFFFF"); // Trắng
                }
            }

            h.tvTaskType.setText(typeLabel);
            h.cardView.setCardBackgroundColor(backgroundColor);

            // --- 2. TÊN TASK ---
            h.tvTaskName.setText(task.getTitle() != null ? task.getTitle() : "");
            h.tvTaskName.setTextColor(titleColor);

            if (task.isDone()) {
                h.tvTaskName.setPaintFlags(h.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                h.tvTaskName.setPaintFlags(h.tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // --- 3. PRIORITY DOT ---
            String priority = task.getPriority();
            if (priority == null) priority = "Low";

            int priorityColor;
            switch (priority) {
                case "High":
                case "Critical": priorityColor = Color.parseColor("#D32F2F"); break;
                case "Medium": priorityColor = Color.parseColor("#FBC02D"); break;
                default: priorityColor = Color.parseColor("#388E3C"); break;
            }

            if (h.viewPriorityDot != null) {
                h.viewPriorityDot.getBackground().setTint(priorityColor);
            }

            // --- 4. SUBTASKS (Fix lỗi hiển thị nhỏ/mất) ---
            h.subtaskContainer.removeAllViews();

            if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
                // Có subtask -> Hiển thị container
                h.subtaskContainer.setVisibility(View.VISIBLE);

                int limit = Math.min(task.getSubTasks().size(), 4);
                for (int i = 0; i < limit; i++) {
                    SubTaskModel sub = task.getSubTasks().get(i);
                    TextView tv = new TextView(context);

                    String text = "— " + (sub.getTitle() != null ? sub.getTitle() : "");
                    tv.setText(text);
                    tv.setTextSize(13f); // Chỉnh lại size chữ hợp lý (13sp hoặc 14sp)
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setMaxLines(1);
                    tv.setEllipsize(android.text.TextUtils.TruncateAt.END);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 4, 0, 4);
                    tv.setLayoutParams(params);

                    if (sub.isDone()) {
                        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tv.setAlpha(0.6f);
                    }
                    h.subtaskContainer.addView(tv);
                }
            } else {
                // Không có subtask -> ẨN HOÀN TOÀN container
                h.subtaskContainer.setVisibility(View.GONE);
            }

            // --- 5. NGÀY HOÀN THÀNH ---
            if (task.isDone() && task.getCompletedDate() != null && !task.getCompletedDate().isEmpty()) {
                h.tvCompleted.setVisibility(View.VISIBLE);
                h.tvCompleted.setText("Done: " + task.getCompletedDate());
            } else {
                h.tvCompleted.setVisibility(View.GONE);
            }

            // --- 6. NÚT XÓA (Chỉ cho Personal) ---
            if (task.getType() == TaskModel.TaskType.PERSONAL) {
                h.btnDelete.setVisibility(View.VISIBLE);
                h.btnDelete.setOnClickListener(v -> {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        items.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, items.size());
                        // TODO: Gọi API xóa nếu cần thiết
                    }
                });
            } else {
                h.btnDelete.setVisibility(View.GONE);
            }

            // Click Listener
            h.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onTaskClick(task);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder cho Header ngày tháng
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo bạn có file layout_task_header.xml với TextView id là tvHeader
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    // Hàm hỗ trợ gắn SpanSize cho GridLayoutManager (để Header chiếm hết chiều ngang)
    public void attachToRecyclerView(RecyclerView recyclerView, GridLayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Nếu là Header -> Chiếm hết số cột (spanCount)
                // Nếu là Item -> Chiếm 1 cột
                return isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setAdapter(this);
    }
}