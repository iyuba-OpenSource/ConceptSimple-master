package com.suzhou.concept.lil.ui.wordPass.list.passShow;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.util.DBTransUtil;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;

import java.util.List;

public class WordPassShowPresenter extends BasePresenter<WordPassShowView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取当前单元/课程的单词数据
    public void getWordDataByUnit(String type, int bookId,int id) {
        checkViewAttach();

        if (TextUtils.isEmpty(type)) {
            getMvpView().showUnitWordData(null);
            return;
        }

        switch (type) {
            case TypeLibrary.BookType.conceptFour:
                //全四册
                List<WordEntity_Four> fourList = RoomDBManager.getInstance().getFourWordDataFromVoaId(id);
                if (fourList != null && fourList.size() > 0) {
                    getMvpView().showUnitWordData(DBTransUtil.conceptFourWordToWordData(type, fourList));
                } else {
                    getMvpView().showUnitWordData(null);
                }
                break;
            case TypeLibrary.BookType.conceptJunior:
                //青少版
                List<WordEntity_Junior> juniorList = RoomDBManager.getInstance().getJuniorWordDataFromUnitId(bookId,id);
                if (juniorList != null && juniorList.size() > 0) {
                    getMvpView().showUnitWordData(DBTransUtil.conceptJuniorWordToWordData(type, juniorList));
                } else {
                    getMvpView().showUnitWordData(null);
                }
                break;
            default:
                getMvpView().showUnitWordData(null);
                break;
        }
    }
}
