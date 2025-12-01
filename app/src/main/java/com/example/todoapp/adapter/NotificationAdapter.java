package com.example.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {

    private Context context;
    private List<NotificationModel> list;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    // Hàm để cập nhật list khi filter
    public void updateList(List<NotificationModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        NotificationModel item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
        holder.tvTime.setText(item.getTime() + " | " + item.getDate());
        holder.tvTagName.setText(item.getType());

        // Xử lý giao diện theo Type
        if (item.getType().equals("App")) {
            holder.imgTagIcon.setImageResource(R.drawable.ic_android); // Icon điện thoại
            holder.tvTagName.setTextColor(Color.parseColor("#1976D2")); // Xanh đậm
            // Bạn có thể setBackgroundTint nếu muốn màu nền khác nhau
        } else {
            holder.imgTagIcon.setImageResource(R.drawable.ic_task); // Icon cờ/công việc
            holder.tvTagName.setTextColor(Color.parseColor("#1976D2")); // Xanh nhạt hơn
        }

        // Ẩn hiện chấm đỏ
        holder.viewUnread.setVisibility(item.isUnread() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagName, tvTime, tvTitle, tvContent;
        ImageView imgTagIcon;
        View viewUnread;
        LinearLayout layoutTag;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tvTagName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            imgTagIcon = itemView.findViewById(R.id.imgTagIcon);
            viewUnread = itemView.findViewById(R.id.viewUnread);
            layoutTag = itemView.findViewById(R.id.layoutTag);
        }
    }
}