package data;

import com.suzhou.concept.lil.data.library.TypeLibrary;

public interface App {

    //是否开启分享
    public static final boolean isOpenShare = false;

    //是否显示微课
    public static final boolean isShowMoc = true;

    interface WordConfig{
        //显示名称
        public static final String showName = "第一册";
        //显示类型
        public static final String showType = TypeLibrary.BookType.conceptFour;
        //显示的书籍id
        public static final int showBookId = 1;
    }
}
