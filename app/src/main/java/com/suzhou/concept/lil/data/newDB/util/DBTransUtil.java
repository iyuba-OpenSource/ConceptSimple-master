package com.suzhou.concept.lil.data.newDB.util;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.bean.WordShowBean;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;

import java.util.ArrayList;
import java.util.List;

/**
 * @desction: 数据库数据转换
 * @date: 2023/4/20 13:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class DBTransUtil {

    /*******************转为通用类*******************/
    //将用户信息数据转为通用类
    /*public static UserInfo toUserInfo(UserInfoEntity entity){
        if (entity==null){
            return null;
        }

        return new UserInfo(
                entity.uId,
                entity.userName,
                entity.nickName,
                entity.userPic,
                entity.email,
                entity.mobile,
                entity.vipStatus,
                entity.vipTime,
                entity.iyuIcon,
                entity.integral,
                entity.money);
    }*/

    /****单词评测****/
    //将单词评测数据转换为通用类
    /*public static EvalShowBean wordEvalToShowData(EvalEntity_word word){
        if (word==null){
            return null;
        }

        //根据类型转换单词数据
        List<EvalShowBean.WordResultBean> wordList = new ArrayList<>();
        switch (word.types){
            case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
            case TypeLibrary.BookType.conceptJunior:
                //新概念
                wordList = toConceptWord(word.words);
                break;
            case TypeLibrary.BookType.juniorPrimary:
            case TypeLibrary.BookType.juniorMiddle:
                //中小学
                wordList = toJuniorWord(word.words);
                break;
        }

        return new EvalShowBean(
                transWordToShowData(word.sentence),
                TextUtils.isEmpty(word.url)?"":FixUtil.fixEvalAudioUrl(word.url),
                word.localPath,
                word.totalScore,
                wordList
        );
    }*/
    //将评测结果中的单词转换成新概念类型数据
    /*private static List<EvalShowBean.WordResultBean> toConceptWord(String words){
        List<EvalShowBean.WordResultBean> wordList = new ArrayList<>();
        if (!TextUtils.isEmpty(words)){
            List<Concept_eval.WordsBean> tempList = GsonUtil.toList(words,Concept_eval.WordsBean.class);
            if (tempList!=null&&tempList.size()>0){
                for (int i = 0; i < tempList.size(); i++) {
                    Concept_eval.WordsBean temp = tempList.get(i);
                    wordList.add(new EvalShowBean.WordResultBean(
                            transWordToShowData(temp.getContent()),
                            temp.getScore(),
                            temp.getIndex(),
                            "",
                            ""
                    ));
                }
            }
        }
        return wordList;
    }*/

    //将评测结果中的单词转换成中小学类型数据
    /*private static List<EvalShowBean.WordResultBean> toJuniorWord(String words){
        List<EvalShowBean.WordResultBean> wordList = new ArrayList<>();
        if (!TextUtils.isEmpty(words)){
            List<Junior_eval.WordsBean> tempList = GsonUtil.toList(words,Junior_eval.WordsBean.class);
            if (tempList!=null&&tempList.size()>0){
                for (int i = 0; i < tempList.size(); i++) {
                    Junior_eval.WordsBean temp = tempList.get(i);
                    wordList.add(new EvalShowBean.WordResultBean(
                            transWordToShowData(temp.getContent()),
                            temp.getScore(),
                            TextUtils.isEmpty(temp.getIndex())?0:Integer.parseInt(temp.getIndex()),
                            temp.getPron2(),
                            temp.getUser_pron2()
                    ));
                }
            }
        }
        return wordList;
    }*/

    /**小说**/
    //将章节数据-小说转换为通用类
    /*public static List<BookChapterBean> novelToChapterData(List<ChapterEntity_novel> list){
        List<BookChapterBean> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterEntity_novel novel = list.get(i);

                temp.add(novelToSingleChapterData(novel));
            }
        }
        return temp;
    }*/

    /*public static BookChapterBean novelToSingleChapterData(ChapterEntity_novel novel){
        if (novel==null){
            return null;
        }

        return new BookChapterBean(
                novel.types,
                novel.voaid,
                novel.orderNumber,
                "",
                "",
                FixUtil.fixNovelAudioUrl(novel.sound),
                "",
                novel.cname_en,
                novel.cname_cn
        );
    }*/

    //将章节详情数据-小说转换为通用类
    /*public static List<ChapterDetailBean> novelToChapterDetailData(List<ChapterDetailEntity_novel> list){
        List<ChapterDetailBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterDetailEntity_novel novel = list.get(i);

                temp.add(new ChapterDetailBean(
                        novel.types,
                        novel.voaid,
                        String.valueOf(novel.paraid),
                        String.valueOf(novel.indexId),
                        novel.textEN,
                        novel.textCH,
                        Double.parseDouble(novel.BeginTiming),
                        Double.parseDouble(novel.EndTiming),
                        novel.image,
                        "",
                        "",
                        "",
                        ""
                ));
            }
        }

        return temp;
    }*/

    /**新概念**/
    //将章节数据-新概念-全四册转换为通用类
    /*public static List<BookChapterBean> conceptFourToChapterData(List<ChapterEntity_conceptFour> list){
        List<BookChapterBean> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterEntity_conceptFour novel = list.get(i);

                temp.add(conceptFourToSingleChapterData(novel));
            }
        }
        return temp;
    }*/

    /*public static BookChapterBean conceptFourToSingleChapterData(ChapterEntity_conceptFour concept){
        if (concept==null){
            return null;
        }

        String audioUrl = null;
        if (concept.types.equals(TypeLibrary.BookType.conceptFourUS)){
            audioUrl = FixUtil.fixConceptUSPlayUrl(concept.bookId,concept.voa_id);
        }else if (concept.types.equals(TypeLibrary.BookType.conceptFourUK)){
            audioUrl = FixUtil.fixConceptUKPlayUrl(concept.bookId,concept.voa_id);
        }

        return new BookChapterBean(
                concept.types,
                concept.voa_id,
                concept.bookId,
                concept.pic,
                "",
                audioUrl,
                "",
                concept.title,
                concept.title_cn
        );
    }*/

    //将章节详情数据-新概念全四册转换为通用类
    /*public static List<ChapterDetailBean> conceptFourToChapterDetailData(List<ChapterDetailEntity_conceptFour> list){
        List<ChapterDetailBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterDetailEntity_conceptFour conceptFour = list.get(i);

                temp.add(new ChapterDetailBean(
                        conceptFour.types,
                        conceptFour.voaid,
                        String.valueOf(conceptFour.Paraid),
                        String.valueOf(conceptFour.IdIndex),
                        conceptFour.Sentence,
                        conceptFour.Sentence_cn,
                        BigDecimalUtil.trans2Double(conceptFour.Timing),
                        BigDecimalUtil.trans2Double(conceptFour.EndTiming),
                        "",
                        "",
                        "",
                        "",
                        ""
                ));
            }
        }

        return temp;
    }*/

    //将章节数据-新概念-青少版转换为通用类
    /*public static List<BookChapterBean> conceptJuniorToChapterData(List<ChapterEntity_conceptJunior> list){
        List<BookChapterBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterEntity_conceptJunior novel = list.get(i);

                temp.add(conceptJuniorToSingleChapterData(novel));
            }
        }
        return temp;
    }*/

    /*public static BookChapterBean conceptJuniorToSingleChapterData(ChapterEntity_conceptJunior concept){
        if (concept==null){
            return null;
        }

        return new BookChapterBean(
                TypeLibrary.BookType.conceptJunior,
                concept.voaId,
                concept.bookId,
                concept.pic,
                FixUtil.fixTalkVideoUrl(concept.video),
                FixUtil.fixConceptJuniorPlayUrl(concept.voaId),
                concept.sound,
                concept.title,
                concept.title_cn
        );
    }*/

    //将章节详情数据-新概念全四册转换为通用类
    /*public static List<ChapterDetailBean> conceptJuniorToChapterDetailData(List<ChapterDetailEntity_conceptJunior> list){
        List<ChapterDetailBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            ChapterDetailEntity_conceptJunior junior = list.get(i);

            temp.add(new ChapterDetailBean(
                    TypeLibrary.BookType.conceptJunior,
                    junior.voaId,
                    String.valueOf(junior.paraId),
                    String.valueOf(junior.idIndex),
                    junior.sentence,
                    junior.sentence_cn,
                    junior.timing,
                    junior.endTiming,
                    "",//从这里将图片链接合并完成，暂时不设置，后续设置
                    junior.start_x,
                    junior.start_y,
                    junior.end_x,
                    junior.end_y
            ));
        }
        return temp;
    }*/

    //将单词数据-新概念全四册转换为通用类
    public static List<WordShowBean> conceptFourWordToWordData(String types, List<WordEntity_Four> list){
        List<WordShowBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordEntity_Four concept = list.get(i);

            temp.add(new WordShowBean(
                    types,
                    concept.bookId,
                    String.valueOf(concept.voaId),
                    String.valueOf(concept.voaId),
                    transWordToShowData(concept.word),
                    concept.pron,
                    concept.def,
                    concept.position,
                    transWordToShowData(concept.sentence),
                    concept.sentence_cn,
                    "",
                    "",
                    concept.audio,
                    concept.sentence_single_audio
            ));
        }

        return temp;
    }

    //将单词数据-新概念青少版转换为通用类
    public static List<WordShowBean> conceptJuniorWordToWordData(String types, List<WordEntity_Junior> list){
        List<WordShowBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordEntity_Junior concept = list.get(i);

            temp.add(new WordShowBean(
                    types,
                    concept.book_id,
                    String.valueOf(concept.voaId),
                    String.valueOf(concept.unit_id),
                    transWordToShowData(concept.word),
                    concept.pron,
                    concept.def,
                    String.valueOf(concept.position),
                    transWordToShowData(concept.Sentence),
                    concept.Sentence_cn,
                    TextUtils.isEmpty(concept.pic_url)?"":concept.pic_url,
                    concept.videoUrl,
                    concept.audio,
                    concept.Sentence_audio
            ));
        }

        return temp;
    }

    /**中小学**/
    //将章节数据-中小学转为通用类
    /*public static List<BookChapterBean> juniorChapterToChapterData(List<ChapterEntity_junior> list){
        List<BookChapterBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                temp.add(juniorChapterToSingleChapterData(list.get(i)));
            }
        }
        return temp;
    }*/

    /*public static BookChapterBean juniorChapterToSingleChapterData(ChapterEntity_junior junior){
        if (junior==null){
            return null;
        }

        return new BookChapterBean(
                junior.types,
                String.valueOf(junior.voaId),
                String.valueOf(junior.series),
                junior.pic,
                FixUtil.fixTalkVideoUrl(junior.video),
                FixUtil.fixJuniorAudioUrl(String.valueOf(junior.voaId),junior.sound),
                junior.sound,
                junior.title,
                junior.title_cn
        );
    }*/

    //中小学-转换章节详情数据
    /*public static List<ChapterDetailBean> juniorChapterDetailData(List<ChapterDetailEntity_junior> list){
        List<ChapterDetailBean> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                ChapterDetailEntity_junior junior = list.get(i);

                temp.add(new ChapterDetailBean(
                        junior.types,
                        junior.voaId,
                        String.valueOf(junior.paraId),
                        String.valueOf(junior.idIndex),
                        junior.sentence,
                        junior.sentence_cn,
                        junior.timing,
                        junior.endTiming,
                        TextUtils.isDigitsOnly(junior.imgPath)?"": FixUtil.fixJuniorImagePicUrl(junior.imgPath),
                        junior.start_x,
                        junior.start_y,
                        junior.end_x,
                        junior.end_y
                ));
            }
        }
        return temp;
    }*/

    //将单词数据-中小学转换为通用类
    /*public static List<WordBean> juniorWordToWordData(String types, List<WordEntity_junior> list){
        List<WordBean> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordEntity_junior concept = list.get(i);

            temp.add(new WordBean(
                    types,
                    concept.book_id,
                    String.valueOf(concept.voaId),
                    String.valueOf(concept.unit_id),
                    transWordToShowData(concept.word),
                    concept.pron,
                    concept.def,
                    String.valueOf(concept.position),
                    transWordToShowData(concept.Sentence),
                    concept.Sentence_cn,
                    TextUtils.isEmpty(concept.pic_url)?"":FixUtil.fixWordPicUrl(concept.pic_url),
                    concept.videoUrl,
                    concept.audio,
                    concept.Sentence_audio
            ));
        }

        return temp;
    }*/

    /**************************辅助方法******************/
    //将单词转为通用类型的数据
    private static String transWordToShowData(String word){
        if (TextUtils.isEmpty(word)){
            return word;
        }

        //因为数据库存储的特殊字符存在问题，因此需要转换
        if (word.contains("‘")){
            word = word.replace("‘","'");
        }

        return word;
    }
}
