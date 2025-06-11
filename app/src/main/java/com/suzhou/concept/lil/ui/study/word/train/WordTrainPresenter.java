package com.suzhou.concept.lil.ui.study.word.train;

import android.util.Pair;

import com.suzhou.concept.lil.data.library.TypeLibrary;
import com.suzhou.concept.lil.data.newDB.RoomDBManager;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/10/7 15:44
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrainPresenter {

    private static WordTrainPresenter instance;

    public static WordTrainPresenter getInstance(){
        if (instance==null){
            synchronized (WordTrainPresenter.class){
                if (instance==null){
                    instance = new WordTrainPresenter();
                }
            }
        }
        return instance;
    }

    /************************************************新的操作**************************************/
    //获取单词数据进行处理
    public List<Pair<WordBean,List<WordBean>>> getRandomWordShowDataNew(String wordType, int bookId, int voaId){
        //先根据类型获取相应的随机数据和课程单词数据
        List<WordBean> wordRandomList = new ArrayList<>();
        List<WordBean> wordList = new ArrayList<>();

        switch (wordType){
            case TypeLibrary.BookType.conceptFour:
                wordRandomList = getFourAnswerRandomData(bookId);
                wordList = getFourWordData(voaId);
                break;
            case TypeLibrary.BookType.conceptJunior:
                wordRandomList = getJuniorAnswerRandomData(bookId);
                wordList = getJuniorWordData(voaId);
                break;
        }

        //本课程的单词内容
        List<Pair<Integer,WordBean>> oldPairList = new ArrayList<>();

        //新的随机数据
        List<Pair<WordBean,List<WordBean>>> randomList = new ArrayList<>();

        if (wordList == null||wordList.size() == 0){
            return randomList;
        }

        //1.将单词数据转为map数据
        for (int i = 0; i < wordList.size(); i++) {
            oldPairList.add(new Pair<>(i,wordList.get(i)));
        }

        //2.将数据转换为随机数据
        while (oldPairList.size()>0){
            //获取随机数据
            int randomInt = (int) (Math.random()*oldPairList.size());
            Pair<Integer,WordBean> randomPair = oldPairList.get(randomInt);

            //将原来的数据中删除选中的数据
            oldPairList.remove(randomPair);

            //获取答案数据(获取不重复的3个数据，然后将标准答案放在随机的位置)
            List<WordBean> answerList = new ArrayList<>();
            Map<String,WordBean> answerMap = new HashMap<>();
            int answerCount = Math.min(wordRandomList.size(), 3);
            while (answerMap.keySet().size()<answerCount){
                //获取随机数据内容
                int answerInt = (int) (Math.random()*wordRandomList.size());
                WordBean answerPair = wordRandomList.get(answerInt);
                //当前选中的单词相关数据
                WordBean selectBean = randomPair.second;

                //去掉同一个数据、已经存在的数据和相同释义的数据
                if (!answerPair.getWord().equals(selectBean.getWord())
                        && !answerPair.getDef().trim().equals(selectBean.getDef().trim())){
                    answerMap.put(answerPair.getWord(), answerPair);
                }
            }
            for (String key:answerMap.keySet()){
                answerList.add(answerMap.get(key));
            }
            //将数据随机处理
            answerList.add(randomPair.second);
            Collections.shuffle(answerList);

            //组合数据显示
            randomList.add(new Pair<>(randomPair.second,answerList));
        }

        return randomList;
    }


    /**************************************转换数据*******************************/
    //将全四册数据转换为标准数据
    private List<WordBean> transFourWordData(List<WordEntity_Four> list){
        //组合数据显示
        List<WordBean> showList = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                WordEntity_Four wordData = list.get(i);
                showList.add(new WordBean(
                        TypeLibrary.BookType.conceptFour,
                        String.valueOf(wordData.bookId),
                        String.valueOf(wordData.voaId),
                        String.valueOf(wordData.voaId),
                        wordData.word,
                        wordData.pron,
                        wordData.def,
                        String.valueOf(i),
                        wordData.sentence,
                        wordData.sentence_cn,
                        "",
                        "",
                        wordData.audio,
                        wordData.sentence_audio
                ));
            }
        }
        return showList;
    }

    //将青少版数据转换为标准数据
    private List<WordBean> transJuniorWordData(List<WordEntity_Junior> list){
        //组合数据显示
        List<WordBean> showList = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                WordEntity_Junior wordData = list.get(i);
                showList.add(new WordBean(
                        TypeLibrary.BookType.conceptFour,
                        wordData.book_id,
                        String.valueOf(wordData.voaId),
                        String.valueOf(wordData.unit_id),
                        wordData.word,
                        wordData.pron,
                        wordData.def,
                        String.valueOf(i),
                        wordData.Sentence,
                        wordData.Sentence_cn,
                        "",
                        "",
                        wordData.audio,
                        wordData.Sentence_audio
                ));
            }
        }
        return showList;
    }

    /****************************************随机数据*******************************/
    //获取全四册的答案随机数据
    private List<WordBean> getFourAnswerRandomData(int bookId){
        List<WordEntity_Four> oldList = RoomDBManager.getInstance().getFourWordDataFromBookLimit100(bookId);

        //这里处理下，从100个数据中挑选出不同的10个处理
        //这里需要10个数据都不能相同
        Map<Integer,WordEntity_Four> limitMap = new HashMap<>();
        List<WordEntity_Four> list = new ArrayList<>();
        while (limitMap.keySet().size()<10){

            int randomInt = (int) (Math.random()*100);
            if (limitMap.get(randomInt)==null){
                limitMap.put(randomInt,oldList.get(randomInt));
            }
        }

        //放到list中
        for (int keyIndex:limitMap.keySet()){
            list.add(limitMap.get(keyIndex));
        }

        return transFourWordData(list);
    }

    //获取青少版的答案随机数据
    private List<WordBean> getJuniorAnswerRandomData(int bookId){
        List<WordEntity_Junior> oldList = RoomDBManager.getInstance().getJuniorWordDataFromBookLimit100(bookId);
        return transJuniorWordData(oldList);
    }

    /****************************************单词数据*******************************/
    //获取全四册的课程单词数据
    private List<WordBean> getFourWordData(int voaId){
        List<WordEntity_Four> list = RoomDBManager.getInstance().getFourWordDataFromVoaId(voaId);
        return transFourWordData(list);
    }

    //获取青少版的课程单词数据
    private List<WordBean> getJuniorWordData(int voaId){
        List<WordEntity_Junior> list = RoomDBManager.getInstance().getJuniorWordDataFromVoaId(voaId);
        return transJuniorWordData(list);
    }
}
