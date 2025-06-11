package com.suzhou.concept.lil.ui.study.word;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.util.DBTransUtil;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;

import java.util.List;

public class KnowledgeNewPresenter extends BasePresenter<KnowledgeNewView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取单词数据
    public void getWordData(String showType,int id){
        checkViewAttach();

        if (TextUtils.isDigitsOnly(showType)){
            getMvpView().showWordData(null);
            return;
        }

        switch (showType) {
            case TypeLibrary.BookType.conceptFour:
                //全四册
                List<WordEntity_Four> fourList = RoomDBManager.getInstance().getFourWordDataFromVoaId(id);
                if (fourList != null && fourList.size() > 0) {
                    getMvpView().showWordData(DBTransUtil.conceptFourWordToWordData(showType, fourList));
                } else {
                    getMvpView().showWordData(null);
                }
                break;
            case TypeLibrary.BookType.conceptJunior:
                //青少版
                List<WordEntity_Junior> juniorList = RoomDBManager.getInstance().getJuniorWordDataFromVoaId(id);
                if (juniorList != null && juniorList.size() > 0) {
                    getMvpView().showWordData(DBTransUtil.conceptJuniorWordToWordData(showType, juniorList));
                } else {
                    getMvpView().showWordData(null);
                }
                break;
            default:
                getMvpView().showWordData(null);
                break;
        }
    }
}
