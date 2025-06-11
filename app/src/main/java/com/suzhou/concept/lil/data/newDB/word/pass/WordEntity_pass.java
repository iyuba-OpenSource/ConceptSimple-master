package com.suzhou.concept.lil.data.newDB.word.pass;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = {"type","bookId","id","userId"})
public class WordEntity_pass {

    @NonNull
    public String type;//类型
    @NonNull
    public String bookId;//书籍id
    @NonNull
    public String id;//全四册-voaId，青少版-unitId
    @NonNull
    public int userId;//用户id

    @NonNull
    public int rightCount;//正确数量
    @NonNull
    public int totalCount;//总数量
    @NonNull
    public int isPass;//是否通过[0-未通过，1-已通过](这里主要处理第一遍通过后，第二次故意不通过导致的显示错误)

    public WordEntity_pass() {
    }

    @Ignore

    public WordEntity_pass(@NonNull String type, @NonNull String bookId, @NonNull String id, int userId, int rightCount, int totalCount, int isPass) {
        this.type = type;
        this.bookId = bookId;
        this.id = id;
        this.userId = userId;
        this.rightCount = rightCount;
        this.totalCount = totalCount;
        this.isPass = isPass;
    }
}
