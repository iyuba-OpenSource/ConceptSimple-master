package com.suzhou.concept.lil.ui.study.eval.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.iyuba.module.toolbox.GsonUtils;
import com.suzhou.concept.AppClient;
import com.suzhou.concept.R;
import com.suzhou.concept.bean.EvaluationSentenceItem;
import com.suzhou.concept.bean.YoungSentenceItem;
import com.suzhou.concept.lil.data.newDB.eval.EvalResultBean;
import com.suzhou.concept.lil.ui.study.eval.bean.EvalShowBean;
import com.suzhou.concept.lil.ui.study.eval.bean.SentenceTransBean;
import com.suzhou.concept.utils.GlobalMemory;
import com.suzhou.concept.utils.OtherUtils;

import java.util.ArrayList;
import java.util.List;

public class HelpUtil {

    //将英音美音数据转换成句子数据
    public static List<SentenceTransBean> transUSUKToSentence(List<EvaluationSentenceItem> list){
        List<SentenceTransBean> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                EvaluationSentenceItem item = list.get(i);
                double startTime = item.getTiming();
                double endTime = item.getEndTiming();

                temp.add(new SentenceTransBean(
                        item.getVoaid(),
                        String.valueOf(item.getIdIndex()),
                        item.getParaid(),
                        item.getSentence(),
                        item.getSentence_cn(),
                        Math.round(startTime*100)/100.0,
                        Math.round(endTime*100)/100.0
                ));
            }
        }

        return temp;
    }

    //将青少版数据转换成句子数据
    public static List<SentenceTransBean> transYoungToSentence(List<YoungSentenceItem> list){
        List<SentenceTransBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                YoungSentenceItem item = list.get(i);
                double startTime = Double.parseDouble(item.getTiming());
                double endTime = Double.parseDouble(item.getEndTiming());

                temp.add(new SentenceTransBean(
                        item.getVoaId(),
                        item.getIdIndex(),
                        item.getParaId(),
                        item.getSentence(),
                        item.getSentence_cn(),
                        Math.round(startTime*100)/100.0,
                        Math.round(endTime*100)/100.0)
                );
            }
        }

        return temp;
    }

    //获取voaId
    public static int getVoaId(){
        if (AppClient.Companion.getConceptItem().getBookId()<0){
            if (GlobalMemory.INSTANCE.getCurrentLanguage().isUK()){
                return 1001;
            }

            if (GlobalMemory.INSTANCE.getCurrentLanguage().isUS()){
                return 10010;
            }

            if (GlobalMemory.INSTANCE.getCurrentYoung()){
                return 321001;
            }

            return 1001;
        }else {
            return Integer.parseInt(AppClient.Companion.getConceptItem().getVoa_id());
        }
    }

    private static final double SCORE_HIGH = 4D;
    private static final double SCORE_LOW = 2.5D;
    //根据句子单词数据返回句子样式
    public static SpannableString getSentenceSpan(EvalShowBean bean){
        List<EvalShowBean.WordsBean> list = bean.getWords();
        String sentence = bean.getSentence();

        Log.d("显示评测的数据", bean.toString());

        SpannableString sentenceSpan = new SpannableString(sentence);
        for (int i = 0; i < list.size(); i++) {
            EvalShowBean.WordsBean temp = list.get(i);
            String word = temp.getContent().trim();
            double score = Double.parseDouble(temp.getScore());

            //剔除相关的错误内容
            if (word.equals("---")){
                continue;
            }

            //筛选数据，清除非必要的标点符号
            word = word.replace(",","");
            word = word.replace("!","");
            word = word.replace(".","");
            word = word.replace("?","");
            word = word.replace("'","");

            if (sentence.contains(word)){
                int index = sentence.indexOf(word);
                if (score>=SCORE_HIGH){
                    sentenceSpan.setSpan(new ForegroundColorSpan(AppClient.Companion.getContext().getResources().getColor(R.color.answer_right)),index,index+word.length(),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                }else if (score<=SCORE_LOW){
                    sentenceSpan.setSpan(new ForegroundColorSpan(AppClient.Companion.getContext().getResources().getColor(R.color.answer_wrong)),index,index+word.length(),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }

        return sentenceSpan;
    }

    //将数据库的评测数据转换成展示数据
    public static EvalShowBean transEvalShowData(EvalResultBean bean){
        EvalShowBean showBean = null;

        if (bean!=null){
            //转换数据
            List<EvalShowBean.WordsBean> wordList = GsonUtils.toObjectList(bean.words,EvalShowBean.WordsBean.class);

            showBean = new EvalShowBean(
                    bean.sentence,
                    bean.scores,
                    bean.total_score,
                    bean.filepath,
                    bean.url,
                    wordList
            );
        }

        return showBean;
    }

    //音频播放地址--评测
    //规则：http://userspeech.iyuba.cn/voa/+数据中的url
    public static String getEvalPlayUrl(String url){
        String playPrefix = "http://userspeech."+ OtherUtils.INSTANCE.getIyuba_cn()+"/voa/";
        return playPrefix+url;
    }

    //评测单词后的url
    //private static String baseHeader = "http://iuserspeech.iyuba.cn:9001/voa/";
    public static String getEvalPlayWordUrl(String suffix){
        String top = "http://iuserspeech."+OtherUtils.INSTANCE.getIyuba_cn()+":9001/voa/";
        return top+suffix;
    }

    //将数据转换成字符答案
    public static String transCharAnswer(String numAnswer){
        switch (numAnswer){
            case "1":
                return "A";
            case "2":
                return "B";
            case "3":
                return "C";
            case "4":
                return "D";
            case "5":
                return "E";
        }
        return "";
    }

    /***********************************系统相关功能*******************************/
    /**
     * 获取程序的版本号
     *
     * @param context
     * @param packname
     * @return
     */
    public static String getAppVersion(Context context, String packname) {
        //包管理操作管理类
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packname;
    }

    /*******************手机品牌判断*****************/
    //是否是oppo旗下的手机
    public static boolean isBelongToOppoPhone(){
        String brand = Build.BRAND.toLowerCase();
        switch (brand){
            case "oppo"://oppo
            case "oneplus"://一加
                return true;
        }
        return false;
    }
}
