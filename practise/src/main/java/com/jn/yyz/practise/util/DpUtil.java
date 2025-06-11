package com.jn.yyz.practise.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DpUtil {


    public static int dpToPx(Context context, float dp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
