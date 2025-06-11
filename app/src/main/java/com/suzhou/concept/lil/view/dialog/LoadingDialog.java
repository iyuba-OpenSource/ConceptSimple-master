package com.suzhou.concept.lil.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.suzhou.concept.R;

/**
 * @desction:
 * @date: 2023/3/1 17:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.DialogTheme);
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);

        TextView textView = findViewById(R.id.loading_tv);
        textView.setText("正在操作中～");
    }
}
