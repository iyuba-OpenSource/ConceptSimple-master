package com.jn.yyz.practise.util;

public class OptionUtil {


    /**
     * 根据位置获取选项
     * @param position
     * @return
     */
    public static String getOption(int position) {

        if (position == 0) {

            return "A";
        } else if (position == 1) {

            return "B";
        } else if (position == 2) {

            return "C";
        } else if (position == 3) {

            return "D";
        } else if (position == 4) {

            return "E";
        } else {

            return null;
        }
    }

}
