package com.suzhou.concept.lil.data.newDB;

import com.iyuba.module.toolbox.GsonUtils;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.dao.AppDatabase;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
import com.suzhou.concept.lil.data.newDB.exercise.ExerciseResultEntity;
import com.suzhou.concept.lil.data.newDB.exercise.MultipleChoiceEntity;
import com.suzhou.concept.lil.data.newDB.exercise.VoaStructureExerciseEntity;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_pass;
import com.suzhou.concept.lil.data.newDB.word.pass.bean.WordClassifyBean;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.util.HelpUtil;
import com.suzhou.concept.utils.GlobalMemory;

import java.util.ArrayList;
import java.util.List;

public class RoomDBManager {

    private static RoomDBManager instance;

    public static RoomDBManager getInstance(){
        if (instance==null){
            synchronized (RoomDBManager.class){
                if (instance==null){
                    instance = new RoomDBManager();
                }
            }
        }
        return instance;
    }

    //保存英音美音的句子数据

    //保存青少版的句子数据

    //查询英音美音的句子数据
    public List<EvaluationSentenceItem> searchUSUKSentenceList(){
        return AppDatabase.Companion.getDatabase(AppClient.Companion.getContext()).localSentenceDao().selectSentenceList(10101, HelpUtil.getVoaId());
    }

    //查询青少版的句子数据
    public List<YoungSentenceItem> searchYoungSentenceList(){
        return AppDatabase.Companion.getDatabase(AppClient.Companion.getContext()).youngSentenceDao().selectClassSentence(10101,HelpUtil.getVoaId());
    }

    /***********************************评测***************************/
    //获取单个评测数据
    public EvalResultBean getSingleEval(int uId, String voaId, String idIndex, String paraId){
        return RoomDB.getInstance().getEvalResultDao().getSingleEval(uId, voaId, idIndex, paraId);
    }

    //保存单个评测数据
    public void saveSingleEval(EvalResultBean bean){
        RoomDB.getInstance().getEvalResultDao().saveEval(bean);
    }

    //保存单个评测数据
    public void saveSingleEval(String voaId,String paraId,String idIndex,EvalShowBean bean, String filePath){
        EvalResultBean resultBean = new EvalResultBean();
        resultBean.uid = String.valueOf(GlobalMemory.INSTANCE.getUserInfo().getUid());
        resultBean.voaId = voaId;
        resultBean.paraId = paraId;
        resultBean.idIndex = idIndex;

        resultBean.filepath = filePath;
        resultBean.sentence = bean.getSentence();
        resultBean.scores = bean.getScores();
        resultBean.total_score = bean.getTotal_score();
        resultBean.url = bean.getUrl();

        String wordList = GsonUtils.toJson(bean.getWords());
        resultBean.words = wordList;

        saveSingleEval(resultBean);
    }

    //获取本课程的评测数据
    public List<EvalResultBean> getEvalResultData(int uid,String voaId){
        List<EvalResultBean> temp = RoomDB.getInstance().getEvalResultDao().getEvalByVoaId(uid,voaId);
        if (temp==null){
            return new ArrayList<>();
        }
        return temp;
    }

    /*****************************************单词收藏**************************/
    //查询所有的单词收藏
    public List<WordCollectBean> getWordCollectAllData(int userId){
        return RoomDB.getInstance().getWordCollectDao().getAllData(userId);
    }

    //查询单个单词的收藏
    public WordCollectBean getSingleWordCollectData(int userId,String word){
        return RoomDB.getInstance().getWordCollectDao().getSingleData(userId, word);
    }

    //插入多个单词收藏
    public void saveMultiWordCollectData(List<WordCollectBean> list){
        RoomDB.getInstance().getWordCollectDao().saveMultiData(list);
    }

    //删除多个单词收藏
    public void deleteMultiWordCollectData(List<WordCollectBean> list){
        RoomDB.getInstance().getWordCollectDao().deleteMultiData(list);
    }

    /***************************************单词数据**********************************/
    /*********全四册*********/
    //保存全四册单词数据
    public void saveFourWordData(List<WordEntity_Four> list){
        RoomDB.getInstance().getWordFourDao().saveData(list);
    }

    //获取全四册的当前书籍的单词数据
    public List<WordEntity_Four> getFourWordDataFromBook(int bookId){
        return RoomDB.getInstance().getWordFourDao().searchWordByBookId(bookId);
    }

    //获取全四册的当前书籍的随机100个单词数据
    public List<WordEntity_Four> getFourWordDataFromBookLimit100(int bookId){
        return RoomDB.getInstance().getWordFourDao().searchRandomDataByBookId(bookId);
    }

    //获取全四册的当前单元的单词数据
    public List<WordEntity_Four> getFourWordDataFromVoaId(int voaId){
        return RoomDB.getInstance().getWordFourDao().searchWordByVoaId(voaId);
    }

    //获取全四册的每个课程的单词数据
    public List<WordClassifyBean> getFourWordGroupData(int bookId){
        return RoomDB.getInstance().getWordFourDao().searchUnitCountData(bookId);
    }

    /**********青少版***********/
    //保存青少版的单词数据
    public void saveJuniorWordData(List<WordEntity_Junior> list){
        RoomDB.getInstance().getWordJuniorDao().saveData(list);
    }

    //获取青少版的当前书籍的单词数据
    public List<WordEntity_Junior> getJuniorWordDataFromBook(int bookId){
        return RoomDB.getInstance().getWordJuniorDao().searchWordByBookId(bookId);
    }

    //获取全四册的当前书籍的随机100个单词数据
    public List<WordEntity_Junior> getJuniorWordDataFromBookLimit100(int bookId){
        return RoomDB.getInstance().getWordJuniorDao().searchRandomWordByBookId(bookId);
    }

    //获取青少版的当前单元的单词数据
    public List<WordEntity_Junior> getJuniorWordDataFromUnitId(int bookId,int unitId){
        return RoomDB.getInstance().getWordJuniorDao().searchWordByBookIdAndUnitId(bookId,unitId);
    }

    //获取青少版的当前课程的单词数据
    public List<WordEntity_Junior> getJuniorWordDataFromVoaId(int voaId){
        return RoomDB.getInstance().getWordJuniorDao().searchWordByVoaId(voaId);
    }

    //获取青少版的单词分组数据
    public List<WordClassifyBean> getJuniorWordGroupData(int bookId){
        return RoomDB.getInstance().getWordJuniorDao().searchUnitCountData(bookId);
    }

    /***********单词闯关***********/
    //保存单词闯关数据
    public void saveWordPassData(WordEntity_pass data){
        RoomDB.getInstance().getWordPassDao().saveSingleData(data);
    }

    //保存多个单词闯关数据
    public void saveMultiWordPassData(List<WordEntity_pass> list){
        RoomDB.getInstance().getWordPassDao().saveMultiData(list);
    }

    //获取单词闯关数据
    public WordEntity_pass getWordPassData(String type,int bookId,int id,int userId){
        return RoomDB.getInstance().getWordPassDao().searchDataById(type,bookId,id,userId);
    }

    /**************************************习题数据*********************************/
    //保存当前课程的选择题数据
    public void saveConceptMultiChoiceData(List<MultipleChoiceEntity> list){
        RoomDB.getInstance().getMultiChoiceDao().saveMultiData(list);
    }

    //查询当前课程的选择题数据
    public List<MultipleChoiceEntity> getConceptMultiChoiceData(String voaId,String exerciseType){
        return RoomDB.getInstance().getMultiChoiceDao().getMultiData(voaId, exerciseType);
    }

    //保存当前课程的填空题数据
    public void saveConceptVoaStructureData(List<VoaStructureExerciseEntity> list){
        RoomDB.getInstance().getVoaStructureDao().saveMultiData(list);
    }

    //查询当前课程的填空题数据
    public List<VoaStructureExerciseEntity> getConceptVoaStructureData(String voaId,String exerciseType){
        return RoomDB.getInstance().getVoaStructureDao().getMultiData(voaId, exerciseType);
    }

    //保存习题结果数据
    public void saveConceptExerciseResultData(ExerciseResultEntity entity){
        RoomDB.getInstance().getExerciseResultDao().saveSingleData(entity);
    }

    //查询习题结果数据
    public ExerciseResultEntity getConceptExerciseResultData(String voaId,int userId,String lessonType,String exerciseType){
        return RoomDB.getInstance().getExerciseResultDao().getSingleData(voaId, userId, lessonType, exerciseType);
    }
}
