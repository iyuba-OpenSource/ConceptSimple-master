package com.suzhou.concept.lil.ui.wordPass.list;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.util.DBTransUtil;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.ui.my.wordNote.WordDeleteBean;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WordListPresenter extends BasePresenter<WordListView> {

    //收藏/取消收藏单词
    private Disposable collectWordDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(collectWordDis);
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

    //收藏/取消收藏单词
    public void collectWord(WordShowBean showBean,boolean isDelete) {
        checkViewAttach();
        LibRxUtil.unDisposable(collectWordDis);

        //收藏数据
        WordCollectBean collectBean = new WordCollectBean();
        collectBean.word = showBean.getWord();
        collectBean.pron = showBean.getPron();
        collectBean.def = showBean.getDef();
        collectBean.audio = showBean.getWordAudioUrl();
        collectBean.userId = GlobalMemory.INSTANCE.getUserInfo().getUid();

        List<WordCollectBean> collectList = new ArrayList<>();
        collectList.add(collectBean);
        //操作类型
        String mode = "insert";
        if (isDelete){
            mode = "delete";
        }

        RetrofitUtil.getInstance().collectWord(collectList,mode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WordDeleteBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectWordDis = d;
                    }

                    @Override
                    public void onNext(WordDeleteBean bean) {
                        String showMsg = "收藏单词";
                        if (isDelete){
                            showMsg = "取消收藏单词";
                        }

                        if (bean!=null&&bean.result==1){
                            //根据操作处理数据库
                            if (isDelete){
                                RoomDBManager.getInstance().deleteMultiWordCollectData(collectList);
                            }else {
                                RoomDBManager.getInstance().saveMultiWordCollectData(collectList);
                            }

                            getMvpView().showCollectWordData(true,showMsg+"成功");
                        }else {
                            getMvpView().showCollectWordData(false,showMsg+"失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String showMsg = "收藏单词";
                        if (isDelete){
                            showMsg = "取消收藏单词";
                        }
                        getMvpView().showCollectWordData(false,showMsg+"异常，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
