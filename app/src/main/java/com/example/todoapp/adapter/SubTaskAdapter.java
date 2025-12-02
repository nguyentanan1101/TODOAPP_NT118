package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Paint;
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

    private final Context context;
    private final List<SubTaskModel> subTasks;

    public interface OnSubTaskCheckedChangeListener {
        void onCheckedChanged(SubTaskModel subTask, boolean isChecked);
    }

    private OnSubTaskCheckedChangeListener listener;

    public void setOnSubTaskCheckedChangeListener(OnSubTaskCheckedChangeListener listener) {
        this.listener = listener;
    }

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

        // 1. Hiển thị tên
        holder.tvTitle.setText(subTask.getTitle() != null ? subTask.getTitle() : "");

        // 2. QUAN TRỌNG: Gỡ bỏ listener cũ trước khi setChecked để tránh xung đột
        holder.checkbox.setOnCheckedChangeListener(null);

        // 3. Đặt trạng thái checkbox từ Model
        holder.checkbox.setChecked(subTask.isDone());

        // 4. Xử lý hiệu ứng gạch ngang chữ nếu đã hoàn thành
        updateStrikeThrough(holder.tvTitle, subTask.isDone());

        // 5. Gán listener mới
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Cập nhật ngay vào Model
            subTask.setDone(isChecked);

            // Cập nhật hiệu ứng gạch ngang chữ ngay lập tức
            updateStrikeThrough(holder.tvTitle, isChecked);

            // Gửi callback (nếu có)
            if (listener != null) {
                listener.onCheckedChanged(subTask, isChecked);
            }
        });
    }

    // Hàm phụ trợ để gạch ngang chữ
    private void updateStrikeThrough(TextView tv, boolean isDone) {
        if (isDone) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setAlpha(0.5f); // Làm mờ đi một chút
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tv.setAlpha(1.0f); // Hiện rõ lại
        }
    }

    @Override
    public int getItemCount() {
        return subTasks != null ? subTasks.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo ID này khớp với file xml item_subtask.xml
            tvTitle = itemView.findViewById(R.id.tvSubTaskTitle);
            checkbox = itemView.findViewById(R.id.chkSubtask);
        }
    }
}