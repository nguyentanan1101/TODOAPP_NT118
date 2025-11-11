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

    private final Context context;
    private final List<SubTaskModel> subTasks;

    // Listener để callback khi checkbox thay đổi
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

        // Hiển thị tên subtask
        holder.tvTitle.setText(subTask.getTitle() != null ? subTask.getTitle() : "");

        // Đặt trạng thái checkbox theo subTask.isDone()
        holder.checkbox.setChecked(subTask.isDone());

        // Listener khi checkbox thay đổi
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subTask.setDone(isChecked); // cập nhật trạng thái local
            if (listener != null) {
                listener.onCheckedChanged(subTask, isChecked); // callback lên GroupAdapter hoặc Activity
            }
        });
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
            tvTitle = itemView.findViewById(R.id.tvSubTaskTitle);
            checkbox = itemView.findViewById(R.id.chkSubtask); // trong layout item_subtask.xml
        }
    }
}
