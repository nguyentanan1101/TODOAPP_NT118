package com.example.todoapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;

import com.example.todoapp.R;
import com.example.todoapp.main.MainActivity;
import com.example.todoapp.main.ProfileActivity;
import com.example.todoapp.main.CalendarActivity;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity) {
        ImageView iconHome = activity.findViewById(R.id.iconHome);
        ImageView iconCalendar = activity.findViewById(R.id.iconCalendar);
        ImageView iconUser = activity.findViewById(R.id.iconUser);

        // Áp dụng hiệu ứng click phóng to nhẹ
        View.OnClickListener withClickEffect = BottomNavHelper::animateClick;

        iconHome.setOnClickListener(v -> {
            animateClick(v);
            if (!(activity instanceof MainActivity)) {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });

        iconCalendar.setOnClickListener(v -> {
            animateClick(v);
            if (!(activity instanceof CalendarActivity)) {
                Intent intent = new Intent(activity, CalendarActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });

        iconUser.setOnClickListener(v -> {
            animateClick(v);
            if (!(activity instanceof ProfileActivity)) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });

        if (activity instanceof MainActivity) {
            iconHome.setColorFilter(activity.getColor(R.color.purple_bottomBar));
        } else if (activity instanceof CalendarActivity) {
            iconCalendar.setColorFilter(activity.getColor(R.color.purple_bottomBar));
        } else if (activity instanceof ProfileActivity) {
            iconUser.setColorFilter(activity.getColor(R.color.purple_bottomBar));
        }

    }

    // Hàm tạo hiệu ứng click (phóng to nhẹ rồi trở lại)
    private static void animateClick(View view) {
        ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.15f);
        ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.15f);
        ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(view, "scaleX", 1.15f, 1f);
        ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(view, "scaleY", 1.15f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleXUp).with(scaleYUp);
        set.play(scaleXDown).with(scaleYDown).after(scaleXUp);
        set.setDuration(100);
        set.start();
    }
}
