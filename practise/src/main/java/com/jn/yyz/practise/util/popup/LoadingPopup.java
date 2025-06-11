package com.jn.yyz.practise.util.popup;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jn.yyz.practise.R;

public class LoadingPopup {

    private PopupWindow popupWindow;

    public LoadingPopup getPopup(Activity activity) {


        View contentView = LayoutInflater.from(activity).inflate(R.layout.popup_loading, null);
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(contentView);
        popupWindow.setOutsideTouchable(false);

        View maskView = new View(activity);
        maskView.setBackgroundColor(Color.BLACK); // 设置遮罩的背景颜色
        maskView.setAlpha(0.7f);

        ViewGroup parent = activity.findViewById(android.R.id.content);
        parent.addView(maskView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                parent.removeView(maskView);
            }
        });
        return this;
    }


    public void dismiss() {

        if (popupWindow != null) {

            popupWindow.dismiss();
        }
    }
}
