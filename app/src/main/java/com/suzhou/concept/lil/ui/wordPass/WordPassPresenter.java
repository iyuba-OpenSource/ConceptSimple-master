package com.suzhou.concept.lil.ui.wordPass;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_pass;
import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordClassifyBean;
import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordPassBean;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.Concept_four_word;
import com.suzhou.concept.lil.data.remote.bean.Concept_junior_word;
import com.suzhou.concept.lil.data.remote.bean.Word_pass;
import com.suzhou.concept.lil.data.remote.bean.base.BaseBean;
import com.suzhou.concept.lil.data.remote.util.RemoteTransUtil;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.util.BigDecimalUtil;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WordPassPresenter extends BasePresenter<WordPassView> {

    //获取单词数据
    private Disposable getWordDataDis;
    //获取单词的进度数据
    private Disposable getWordPassDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(getWordDataDis);
        LibRxUtil.unDisposable(getWordPassDis);
    }

    //获取单词的数据
    public void getWordData(String type,int bookId){
        if (TextUtils.isEmpty(type)){
            getMvpView().showWordData(null,"数据错误，请重试~");
            return;
        }

        switch (type){
            case TypeLibrary.BookType.conceptFour:
                //全四册
                List<WordEntity_Four> fourList = RoomDBManager.getInstance().getFourWordDataFromBook(bookId);
                if (fourList!=null && fourList.size()>0){
                    //合并数据
                    List<WordPassBean> passList = transLocalDataToShow(type, bookId);
                    //显示数据
                    getMvpView().showWordData(passList,null);
                }else {
                    getMvpView().loadRemoteData();
                }
                break;
            case TypeLibrary.BookType.conceptJunior:
                //青少版
                List<WordEntity_Junior> juniorList = RoomDBManager.getInstance().getJuniorWordDataFromBook(bookId);
                if (juniorList!=null&&juniorList.size()>0){
                    //合并数据
                    List<WordPassBean> passList = transLocalDataToShow(type, bookId);
                    //显示数据
                    getMvpView().showWordData(passList,null);
                }else {
                    getMvpView().loadRemoteData();
                }
                break;
        }
    }

    //获取远端的单词数据
    public void getRemoteWordData(String type,int bookId){
        if (TextUtils.isEmpty(type)){
            getMvpView().showWordData(null,"数据错误，请重试～");
            return;
        }

        switch (type){
            case TypeLibrary.BookType.conceptFour:
                //全四册
                getFourWordData(type,bookId);
                break;
            case TypeLibrary.BookType.conceptJunior:
                //青少版
                getJuniorWordData(type,bookId);
                break;
        }
    }

    //停止获取远程的单词数据
    public void cancelRemoteWordData(){
        LibRxUtil.unDisposable(getWordDataDis);
    }

    //获取全四册的远端数据
    private void getFourWordData(String type,int bookId){
        checkViewAttach();
        LibRxUtil.unDisposable(getWordDataDis);
        RetrofitUtil.getInstance().getConceptFourWordData(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean<List<Concept_four_word>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getWordDataDis = d;
                    }

                    @Override
                    public void onNext(BaseBean<List<Concept_four_word>> bean) {
                        if (bean!=null&&bean.getData().size()>0){
                            //将数据保存在数据库中
                            RoomDBManager.getInstance().saveFourWordData(RemoteTransUtil.transConceptFourWordToDB(bookId,bean.getData()));
                            //将数据库中的数据转换
                            List<WordPassBean> passList = transLocalDataToShow(type, bookId);
                            //发送到前台界面中
                            getMvpView().showWordData(passList,null);
                        }else {
                            getMvpView().showWordData(null,"获取全四册单词失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showWordData(null,"获取全四册单词异常");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取青少版的远端数据
    private void getJuniorWordData(String type,int bookId){
        checkViewAttach();
        LibRxUtil.unDisposable(getWordDataDis);
        RetrofitUtil.getInstance().getConceptJuniorWordData(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean<List<Concept_junior_word>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getWordDataDis = d;
                    }

                    @Override
                    public void onNext(BaseBean<List<Concept_junior_word>> bean) {
                        if (bean!=null&&bean.getResult().equals("200")){
                            //将数据存在数据库中
                            RoomDBManager.getInstance().saveJuniorWordData(RemoteTransUtil.transConceptJuniorWordToDB(bean.getData()));
                            //从数据库中获取数据
                            List<WordPassBean> passList = transLocalDataToShow(type, bookId);
                            //展示数据
                            getMvpView().showWordData(passList,null);
                        }else {
                            getMvpView().showWordData(null,"获取青少版单词失败，请重试～");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showWordData(null,"获取青少版单词异常");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //合并远程数据和进度数据
    private List<WordPassBean> margePassData(List<WordClassifyBean> list,String type){
        List<WordPassBean> showlist = new ArrayList<>();

        if (list==null||list.size()==0){
            return showlist;
        }

        for (int i = 0; i < list.size(); i++) {
            WordClassifyBean bean = list.get(i);
            //获取进度数据
            WordEntity_pass passData = RoomDBManager.getInstance().getWordPassData(type,bean.getBookId(),bean.getId(), GlobalMemory.INSTANCE.getUserInfo().getUid());

            int rightCount = 0;
            int passState = 0;
            if (passData!=null){
                rightCount = passData.rightCount;
                passState = passData.isPass;
            }

            //合并数据并显示
            showlist.add(new WordPassBean(
                    type,
                    bean.getBookId(),
                    bean.getId(),
                    rightCount,
                    bean.getWordCount(),
                    passState
            ));
        }

        //循环判断通过数量，然后对最后一个进行处理
        /*int checkIndex = 0;
        if (showlist.get(showlist.size()-1).getPassState()!=1){
            for (int i = 0; i < showlist.size(); i++) {
                //获取下一个数据
                if (i == showlist.size()-1){
                    continue;
                }

                //当前数据
                WordPassBean curWordData = showlist.get(i);
                //下一个数据
                WordPassBean nextWordData = showlist.get(i+1);
                //判断是否需要处理
                if (curWordData.getPassState()==1 && nextWordData.getPassState()==0){
                    checkIndex = i+1;
                    break;
                }
            }
        }

        //当前数据
        WordPassBean passBean = showlist.get(checkIndex);
        //处理数据
        showlist.set(checkIndex,new WordPassBean(
                type,
                passBean.getBookId(),
                passBean.getId(),
                passBean.getRightCount(),
                passBean.getTotalCount(),
                1
        ));*/
        return showlist;
    }

    //合并本地数据和进度数据
    private List<WordPassBean> margePassData(List<WordPassBean> list){
        List<WordPassBean> showList = new ArrayList<>();
        //合并新的进度数据
        for (int i = 0; i < list.size(); i++) {
            WordPassBean oldData = list.get(i);
            //获取进度数据
            WordEntity_pass passData = RoomDBManager.getInstance().getWordPassData(oldData.getType(), oldData.getBookId(), oldData.getId(),GlobalMemory.INSTANCE.getUserInfo().getUid());

            //合并新的数据
            WordPassBean newData = new WordPassBean(
                    oldData.getType(),
                    oldData.getBookId(),
                    oldData.getId(),
                    passData.rightCount,
                    oldData.getTotalCount(),
                    passData.isPass
            );
            showList.add(newData);
        }
        return showList;
    }

    //转换当前的单词和进度数据为显示数据
    private List<WordPassBean> transLocalDataToShow(String bookType,int bookId){
        List<WordPassBean> passList = new ArrayList<>();
        //当前的单词数据
        List<WordClassifyBean> wordList = new ArrayList<>();
        switch (bookType){
            case TypeLibrary.BookType.conceptFour:
                //全四册数据
                wordList = RoomDBManager.getInstance().getFourWordGroupData(bookId);
                break;
            case TypeLibrary.BookType.conceptJunior:
                //青少版数据
                wordList = RoomDBManager.getInstance().getJuniorWordGroupData(bookId);
                break;
        }

        //合并上进度数据
        if (wordList==null || wordList.size()==0){
            return passList;
        }

        for (int i = 0; i < wordList.size(); i++) {
            WordClassifyBean classifyBean = wordList.get(i);
            //当前的进度数据
            WordEntity_pass passData = RoomDBManager.getInstance().getWordPassData(bookType,bookId, classifyBean.getId(), GlobalMemory.INSTANCE.getUserInfo().getUid());
            //合并数据
            if (passData==null){
                passList.add(new WordPassBean(
                        bookType,
                        bookId,
                        classifyBean.getId(),
                        0,
                        classifyBean.getWordCount(),
                        0
                ));
            }else {
                passList.add(new WordPassBean(
                        bookType,
                        bookId,
                        classifyBean.getId(),
                        passData.rightCount,
                        classifyBean.getWordCount(),
                        passData.isPass
                ));
            }
        }

        return passList;
    }

    //获取单词闯关进度数据
    public void getWordPassData(String bookType,int bookId){
        checkViewAttach();
        LibRxUtil.unDisposable(getWordPassDis);
        RetrofitUtil.getInstance().getConceptWordPassData(bookId, GlobalMemory.INSTANCE.getUserInfo().getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Word_pass>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getWordPassDis = d;
                    }

                    @Override
                    public void onNext(Word_pass wordPass) {
                        if (wordPass!=null && wordPass.getResult()==1){
                            //将数据保存在数据库中
                            List<WordEntity_pass> passList = transRemotePassDataToLocal(bookType,bookId,wordPass.getDataRight());
                            if (passList!=null&&passList.size()>0){
                                //插入数据库
                                RoomDBManager.getInstance().saveMultiWordPassData(passList);
                            }

                            //获取新的进度数据
                            List<WordPassBean> newPassList = transLocalDataToShow(bookType, bookId);
                            //显示数据
                            getMvpView().showWordPassResult(true,"同步单词进度数据完成",newPassList);
                        }else {
                            getMvpView().showWordPassResult(false,"同步单词进度数据失败～",new ArrayList<>());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showWordPassResult(false,"同步单词进度数据异常~",new ArrayList<>());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //将数据转换成需要保存的进度数据
    private List<WordEntity_pass> transRemotePassDataToLocal(String bookType,int bookId,List<Word_pass.DataRightDTO> list){
        List<WordEntity_pass> passList = new ArrayList<>();
        if (list==null || list.size()==0){
            return passList;
        }

        //先将正确数据合并出来，然后根据id获取总数据，然后将数据逐步保存在本地
        Map<String,Integer> remoteGroupMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Word_pass.DataRightDTO rightData = list.get(i);
            if (rightData.getLessonId().equals("0")){
                continue;
            }

            if (remoteGroupMap.get(rightData.getLessonId())==null){
                remoteGroupMap.put(rightData.getLessonId(),1);
            }else {
                remoteGroupMap.put(rightData.getLessonId(),remoteGroupMap.get(rightData.getLessonId())+1);
            }
        }

        //转换成需要上传的数据
        for (String id:remoteGroupMap.keySet()){
            //查询单词的总数据
            int voaWordCount = 0;
            if (bookType.equals(TypeLibrary.BookType.conceptFour)){
                List<WordEntity_Four> fourWordList = RoomDBManager.getInstance().getFourWordDataFromVoaId(Integer.parseInt(id));
                if (fourWordList!=null&&fourWordList.size()>0){
                    voaWordCount = fourWordList.size();
                }else {
                    voaWordCount = 0;
                }
            }else if (bookType.equals(TypeLibrary.BookType.conceptJunior)){
                List<WordEntity_Junior> juniorWordList = RoomDBManager.getInstance().getJuniorWordDataFromUnitId(bookId,Integer.parseInt(id));
                if (juniorWordList!=null&&juniorWordList.size()>0){
                    voaWordCount = juniorWordList.size();
                }else {
                    voaWordCount = 0;
                }
            }

            if (voaWordCount==0){
                return passList;
            }

            //计算是否通过
            boolean isPass = BigDecimalUtil.trans2Double(remoteGroupMap.get(id)*100.0f/voaWordCount) >=80;

            //保存数据
            passList.add(new WordEntity_pass(
                    bookType,
                    String.valueOf(bookId),
                    id,
                    GlobalMemory.INSTANCE.getUserInfo().getUid(),
                    remoteGroupMap.get(id),
                    voaWordCount,
                    isPass?1:0
            ));
        }

        return  passList;
    }
}
