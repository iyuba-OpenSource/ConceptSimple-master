package com.suzhou.concept.lil.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @title:
 * @date: 2023/4/26 18:26
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ToastUtil {

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context,int resId){
        Toast.makeText(context,resId,Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context,int resId){
        Toast.makeText(context,resId,Toast.LENGTH_LONG).show();
    }
}
