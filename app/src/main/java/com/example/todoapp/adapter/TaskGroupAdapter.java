package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
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

    private Context context;
    private List<TaskItem> items;

    public TaskGroupAdapter(Context context, List<TaskGroup> groups) {
        this.context = context;
        this.items = new ArrayList<>();

        for(TaskGroup g : groups) {
            items.add(new TaskItem(g.getDateTitle())); // header
            for(TaskModel t : g.getTasks()) {
                items.add(new TaskItem(t)); // task
            }
        }
    }

    public boolean isHeader(int position) { return items.get(position).isHeader(); }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER : TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_task_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_task_item, parent, false);
            return new TaskAdapter.TaskViewHolder(view); // reuse TaskAdapter.ViewHolder
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskItem item = items.get(position);

        if(holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(item.getHeaderTitle());
        } else if(holder instanceof TaskAdapter.TaskViewHolder) {
            TaskAdapter.TaskViewHolder h = (TaskAdapter.TaskViewHolder) holder;
            h.tvTaskName.setText(item.getTask().getTitle());

            // màu card
            switch(item.getTask().getType()) {
                case PERSONAL: h.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50")); break;
                case WORK_PRIVATE: h.cardView.setCardBackgroundColor(Color.parseColor("#FF9800")); break;
                case WORK_GROUP: h.cardView.setCardBackgroundColor(Color.parseColor("#2196F3")); break;
            }

            h.subtaskContainer.removeAllViews();
            for(SubTaskModel sub : item.getTask().getSubTasks()) {
                TextView subTv = new TextView(context);
                subTv.setText("• " + sub.getTitle());
                subTv.setTextSize(14f);
                subTv.setTextColor(Color.BLACK);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,4,0,4);
                subTv.setLayoutParams(params);
                h.subtaskContainer.addView(subTv);
            }
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    // Attach RecyclerView với GridLayoutManager để header chiếm toàn bộ cột
    public void attachToRecyclerView(RecyclerView recyclerView, GridLayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setAdapter(this);
    }
}
