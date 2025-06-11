package com.suzhou.concept.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"type","bookId","unitId","userId"})
public class WordBreakBean {
    @Ignore
    public static final String type_four = "four";//全四册
    @Ignore
    public static final String type_junior = "junior";//青少版


    @NonNull
    public String type;//全四册和青少版
    @NonNull
    public int bookId;//书籍id
    @NonNull
    public int unitId;//单元id
    @NonNull
    public int userId;//用户id

    @NonNull
    public int rightCount;//正确数量
    @NonNull
    public int totalCount;//总数量

    public WordBreakBean() {
    }

    @Ignore
    public WordBreakBean(String type, int bookId, int unitId, int userId, int rightCount, int totalCount) {
        this.type = type;
        this.bookId = bookId;
        this.unitId = unitId;
        this.userId = userId;
        this.rightCount = rightCount;
        this.totalCount = totalCount;
    }
}
