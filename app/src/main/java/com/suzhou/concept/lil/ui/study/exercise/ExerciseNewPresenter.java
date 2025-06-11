package com.suzhou.concept.lil.ui.study.exercise;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.exercise.MultipleChoiceEntity;
import com.suzhou.concept.lil.data.newDB.exercise.VoaStructureExerciseEntity;
import com.suzhou.concept.lil.data.remote.RetrofitUtil;
import com.suzhou.concept.lil.data.remote.bean.Exercise_concept;
import com.suzhou.concept.lil.mvp.frame.BasePresenter;
import com.suzhou.concept.lil.util.LibRxUtil;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExerciseNewPresenter extends BasePresenter<ExerciseNewView> {

    //获取习题数据
    private Disposable getExerciseDis;

    @Override
    public void detachView() {
        super.detachView();

        LibRxUtil.unDisposable(getExerciseDis);
    }

    //获取习题数据
    public void getExerciseData(String voaId){
        String languageType = TypeLibrary.BookType.conceptFourUS;
        if (GlobalMemory.INSTANCE.getCurrentYoung()){
            languageType = TypeLibrary.BookType.conceptJunior;
        }else {
            String curLanguage = GlobalMemory.INSTANCE.getCurrentLanguage().getLanguage();
            if (curLanguage.equals("US")){
                languageType = TypeLibrary.BookType.conceptFourUS;
            }else if (curLanguage.equals("UK")){
                languageType = TypeLibrary.BookType.conceptFourUK;
            }
        }

        //从两个数据表中获取数据
        List<MultipleChoiceEntity> multiList = RoomDBManager.getInstance().getConceptMultiChoiceData(voaId,languageType);
        List<VoaStructureExerciseEntity> voaList = RoomDBManager.getInstance().getConceptVoaStructureData(voaId,languageType);
        if ((multiList!=null&&multiList.size()>0)||(voaList!=null&&voaList.size()>0)){
            getMvpView().showExercise(true);
        }else {
            getExerciseDataFromServer(voaId,languageType);
        }
    }

    //从服务器获取习题数据
    public void getExerciseDataFromServer(String voaId,String exerciseType){
        checkViewAttach();
        LibRxUtil.unDisposable(getExerciseDis);
        RetrofitUtil.getInstance().getConceptExerciseData(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Exercise_concept>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getExerciseDis = d;
                    }

                    @Override
                    public void onNext(Exercise_concept bean) {
                        if (bean!=null){
                            //保存到数据库
                            if (bean.getMultipleChoice()!=null&&bean.getMultipleChoice().size()>0){
                                RoomDBManager.getInstance().saveConceptMultiChoiceData(transMultiChoiceToDbData(bean.getMultipleChoice(),exerciseType));
                            }

                            if (bean.getVoaStructureExercise()!=null&&bean.getVoaStructureExercise().size()>0){
                                RoomDBManager.getInstance().saveConceptVoaStructureData(transVoaStructureToDbData(bean.getVoaStructureExercise(),exerciseType));
                            }

                            getMvpView().showExercise(true);
                        }else {
                            getMvpView().showExercise(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showExercise(false);
                    }

                    @Override
                    public void onComplete() {
                        LibRxUtil.unDisposable(getExerciseDis);
                    }
                });
    }

    //将选择题数据转换为数据库的类型
    private List<MultipleChoiceEntity> transMultiChoiceToDbData(List<Exercise_concept.MultipleChoiceDTO> list,String exerciseType){
        List<MultipleChoiceEntity> dbList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Exercise_concept.MultipleChoiceDTO temp = list.get(i);
            dbList.add(new MultipleChoiceEntity(
                    temp.getVoa_id(),
                    TextUtils.isEmpty(temp.getIndex_id())?i:Integer.parseInt(temp.getIndex_id()),
                    exerciseType,
                    formatSentenceShow(temp.getQuestion()),
                    formatSentenceShow(temp.getAnswer()),
                    temp.getChoice_B(),
                    temp.getChoice_C(),
                    temp.getChoice_D(),
                    temp.getChoice_A()
            ));
        }
        return dbList;
    }

    //将填空题数据转换为数据库的类型
    private List<VoaStructureExerciseEntity> transVoaStructureToDbData(List<Exercise_concept.VoaStructureExerciseDTO> list,String exerciseType){
        List<VoaStructureExerciseEntity> dbList = new ArrayList<>();
        //这里记录下相关的说明信息，下边没有信息的统一按照上面的说明信息来
        String descEn = "";
        String descCn = "";

        for (int i = 0; i < list.size(); i++) {
            Exercise_concept.VoaStructureExerciseDTO temp = list.get(i);
            if (!TextUtils.isEmpty(temp.getDesc_EN())||!TextUtils.isEmpty(temp.getDesc_CH())){
                if (!TextUtils.isEmpty(temp.getDesc_EN())){
                    descEn = temp.getDesc_EN();
                }else {
                    descEn = "";
                }

                if (!TextUtils.isEmpty(temp.getDesc_CH())){
                    descCn = temp.getDesc_CH();
                }else {
                    descCn = "";
                }
            }

            dbList.add(new VoaStructureExerciseEntity(
                    temp.getId(),
                    TextUtils.isEmpty(temp.getNumber())?i:Integer.parseInt(temp.getNumber()),
                    exerciseType,
                    formatSentenceShow(temp.getNote()),
                    temp.getQues_num(),
                    formatSentenceShow(temp.getAnswer()),
                    descCn,
                    temp.getColumn(),
                    descEn,
                    temp.getType()
            ));
        }

        return dbList;
    }


    //规范标点符号显示
    private String formatSentenceShow(String dataStr){
        if (TextUtils.isEmpty(dataStr)){
            return "";
        }

        dataStr = dataStr.replaceAll("’","'");
        dataStr = dataStr.replaceAll(" '","'");
        dataStr = dataStr.replaceAll("“","\"");
        return dataStr;
    }
}
