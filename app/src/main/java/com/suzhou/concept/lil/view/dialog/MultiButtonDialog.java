package com.suzhou.concept.lil.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.suzhou.concept.R;
import com.suzhou.concept.lil.util.ScreenUtils;

/**
 * @title: 多按钮弹窗
 * @date: 2023/5/14 19:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class MultiButtonDialog extends AlertDialog {

    private Context context;
    private TextView titleView;
    private TextView agreeView;
    private TextView disagreeView;
    private TextView msgView;

    public MultiButtonDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_multi_button);
        setCancelable(false);

        titleView = findViewById(R.id.title);
        agreeView = findViewById(R.id.agree);
        disagreeView = findViewById(R.id.disagree);
        msgView = findViewById(R.id.content);
    }

    @Override
    public void show() {
        super.show();

        int width = ScreenUtils.getScreenW(context);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (width*0.8);
        getWindow().setAttributes(lp);
    }

    //设置标题
    public void setTitle(String title){
        titleView.setText(title);
    }

    //设置按钮
    public void setButton(String left,String right,OnMultiClickListener listener){
        disagreeView.setText(left);
        agreeView.setText(right);
        agreeView.setOnClickListener(v->{
            dismiss();

            if (listener!=null){
                listener.onAgree();
            }
        });
        disagreeView.setOnClickListener(v->{
            dismiss();

            if (listener!=null){
                listener.onDisagree();
            }
        });
    }

    //设置信息
    public void setMsg(String content){
        msgView.setText(content);
    }

    //设置回调
    public interface OnMultiClickListener{
        //同意
        void onAgree();
        //不同意
        void onDisagree();
    }
}
