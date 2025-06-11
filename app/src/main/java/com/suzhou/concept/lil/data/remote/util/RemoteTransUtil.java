package com.suzhou.concept.lil.data.remote.util;

import android.text.TextUtils;

import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Four;
import com.suzhou.concept.lil.data.newDB.word.pass.WordEntity_Junior;
import com.suzhou.concept.lil.data.remote.bean.Concept_four_word;
import com.suzhou.concept.lil.data.remote.bean.Concept_junior_word;

import java.util.ArrayList;
import java.util.List;

/**
 * @desction: 接口转换类
 * @date: 2023/4/20 13:42
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class RemoteTransUtil {

    /********************转为数据库类******************/
    //将用户信息数据转换为数据库存储数据
    /*public static UserInfoEntity transUserInfoToDB(long uId, User_info info){
        return new UserInfoEntity(
                uId,
                info.getUsername(),
                info.getNickname(),
                FixUtil.fixUserPicUrl(info.getMiddle_url()),//拼接成完整的图片地址
                info.getMobile(),
                info.getEmail(),
                info.getIsteacher(),
                info.getVipStatus(),
                info.getExpireTime()* 1000L,
                info.getAmount(),
                TextUtils.isEmpty(info.getCredits())?0:Long.parseLong(info.getCredits()),
                info.getMoney(),
                TypeLibrary.UserActiveType.ACTIVE,//默认为激活状态
                TypeLibrary.UserWarnType.UN_WARN//默认可用
        );
    }*/

    //将单词闯关数据转为数据库类
    /*public static List<WordBreakEntity> transWordBreakToDB(long userId,Map<WordBean,WordBean> map){
        List<WordBreakEntity> temp = new ArrayList<>();

        for (WordBean key:map.keySet()){
            WordBean result = map.get(key);

            temp.add(new WordBreakEntity(
                    key.getTypes(),
                    key.getBookId(),
                    key.getVoaId(),
                    key.getId(),
                    TextUtils.isEmpty(key.getPosition())?0:Integer.parseInt(key.getPosition()),
                    key.getWord(),
                    result.getWord(),
                    key.getWord(),
                    userId
            ));
        }
        return temp;
    }*/

    //将单词闯关进度数据转换为数据库类
    /*public static WordBreakPassEntity transWordBreakProgressToDB(String types,String bookId,String id,long userId){
        return new WordBreakPassEntity(
                types,
                bookId,
                id,
                userId
        );
    }*/

    /**小说**/
    //将章节数据-小说转换为数据库数据
    /*public static List<ChapterEntity_novel> transNovelChapterToDB(String types,List<Novel_chapter> list){
        List<ChapterEntity_novel> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Novel_chapter novel = list.get(i);

            temp.add(new ChapterEntity_novel(
                    novel.getVoaid(),
                    novel.getOrderNumber(),
                    novel.getLevel(),
                    novel.getChapterOrder(),
                    novel.getSound(),
                    novel.getShow(),
                    novel.getCname_cn(),
                    novel.getCname_en(),
                    types
            ));
        }

        return temp;
    }*/

    //将章节详情数据-小说转换为数据库数据
    /*public static List<ChapterDetailEntity_novel> transNovelChapterDetailToDB(String types,List<Novel_chapter_detail> list){
        List<ChapterDetailEntity_novel> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Novel_chapter_detail detail = list.get(i);

            temp.add(new ChapterDetailEntity_novel(
                    detail.getBeginTiming(),
                    TextUtils.isEmpty(detail.getVoaid())?0:Long.parseLong(detail.getVoaid()),
                    detail.getChapter_order(),
                    TextUtils.isEmpty(detail.getParaid())?0:Integer.parseInt(detail.getParaid()),
                    detail.getEndTiming(),
                    detail.getImage(),
                    detail.getOrderNumber(),
                    detail.getSentence_audio(),
                    TextUtils.isEmpty(detail.getLevel())?0:Integer.parseInt(detail.getLevel()),
                    TextUtils.isEmpty(detail.getIndex())?0:Integer.parseInt(detail.getIndex()),
                    detail.getTextCH(),
                    detail.getTextEN(),
                    types
            ));
        }

        return temp;
    }*/

    /**新概念**/
    //将章节数据-新概念全四册转换为数据库数据
    /*public static List<ChapterEntity_conceptFour> transConceptFourChapterToDB(String types, String bookId,List<Concept_four_chapter> list){
        List<ChapterEntity_conceptFour> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Concept_four_chapter data = list.get(i);

            //英美音数据库结构一样, 区别在于
            //英音的voaid = 美音的voaid*10
            //这里如果是英音的话需要单独处理
            String voaId = data.getVoa_id();
            if (types.equals(TypeLibrary.BookType.conceptFourUK)){
                voaId = voaId+"0";
            }

            temp.add(new ChapterEntity_conceptFour(
                    voaId,
                    data.getListenPercentage(),
                    data.getText_num(),
                    data.getTotalTime(),
                    data.getTitleid(),
                    data.getEnd_time(),
                    data.getPackageid(),
                    data.getPic(),
                    data.getTitle(),
                    data.getOwnerid(),
                    data.getPrice(),
                    data.getPercentage(),
                    data.getTitle_cn(),
                    data.getChoice_num(),
                    data.getName(),
                    data.getWordNum(),
                    data.getCategoryid(),
                    data.getDesc(),
                    types,
                    bookId
            ));
        }

        return temp;
    }*/

    //将章节数据-新概念青少版转换为数据库数据
    /*public static List<ChapterEntity_conceptJunior> transConceptJuniorChapterToDB(String bookId, List<Concept_junior_chapter> list){
        List<ChapterEntity_conceptJunior> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Concept_junior_chapter junior = list.get(i);
            temp.add(new ChapterEntity_conceptJunior(
                    junior.getId(),
                    bookId,
                    junior.getCreatTime(),
                    junior.getListenPercentage(),
                    junior.getCategory(),
                    junior.getHavePractice(),
                    junior.getPackageid(),
                    junior.getTexts(),
                    junior.getVideo(),
                    junior.getPagetitle(),
                    junior.getUrl(),
                    junior.getPrice(),
                    junior.getPercentage(),
                    junior.getPublishTime(),
                    junior.getHotFlg(),
                    junior.getCategoryid(),
                    junior.getClickRead(),
                    junior.getIntroDesc(),
                    junior.getKeyword(),
                    junior.getTotalTime(),
                    junior.getTitle(),
                    junior.getSound(),
                    junior.getPic(),
                    junior.getOwnerid(),
                    junior.getFlag(),
                    junior.getDescCn(),
                    junior.getClassid(),
                    junior.getOutlineid(),
                    junior.getTitle_cn(),
                    junior.getSeries(),
                    junior.getName(),
                    junior.getWordNum(),
                    junior.getCategoryName(),
                    junior.getReadCount(),
                    junior.getDesc()
            ));
        }
        return temp;
    }*/

    //将章节详情数据-新概念全四册转换为数据库数据
    /*public static List<ChapterDetailEntity_conceptFour> transConceptFourChapterDetailToDB(String types, List<Concept_four_chapter_detail> list){
        List<ChapterDetailEntity_conceptFour> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Concept_four_chapter_detail detail = list.get(i);

            temp.add(new ChapterDetailEntity_conceptFour(
                    types,
                    TextUtils.isEmpty(detail.getVoaid())?0:Long.parseLong(detail.getVoaid()),
                    TextUtils.isEmpty(detail.getParaid())?0:Integer.parseInt(detail.getParaid()),
                    TextUtils.isEmpty(detail.getIdIndex())?0:Integer.parseInt(detail.getIdIndex()),
                    detail.getEndTiming(),
                    detail.getTiming(),
                    detail.getSentence_cn(),
                    detail.getSentence()
            ));
        }
        return temp;
    }*/

    //将章节详情数据-新概念青少版转换为数据库数据
    /*public static List<ChapterDetailEntity_conceptJunior> transConceptJuniorChapterDetailToDB(String voaId,List<Concept_junior_chapter_detail> list){
        List<ChapterDetailEntity_conceptJunior> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Concept_junior_chapter_detail detail = list.get(i);

            temp.add(new ChapterDetailEntity_conceptJunior(
                    TextUtils.isEmpty(voaId)?0:Long.parseLong(voaId),
                    TextUtils.isEmpty(detail.getParaId())?0:Integer.parseInt(detail.getParaId()),
                    TextUtils.isEmpty(detail.getIdIndex())?0:Integer.parseInt(detail.getIdIndex()),
                    detail.getImgPath(),
                    detail.getEndTiming(),
                    detail.getSentence_cn(),
                    detail.getImgWords(),
                    detail.getStart_x(),
                    detail.getEnd_y(),
                    detail.getTiming(),
                    detail.getEnd_x(),
                    detail.getSentence(),
                    detail.getStart_y()
            ));
        }
        return temp;
    }*/

    //将单词数据-新概念全四册转换为数据库数据
    public static List<WordEntity_Four> transConceptFourWordToDB(int bookId, List<Concept_four_word> list){
        List<WordEntity_Four> temp = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Concept_four_word word = list.get(i);

            temp.add(new WordEntity_Four(
                    word.getVoa_id(),
                    transWordToEntityData(word.getWord()),
                    word.getDef(),
                    word.getPron(),
                    word.getExamples(),
                    word.getAudio(),
                    word.getPosition(),
                    transWordToEntityData(word.getSentence()),
                    word.getSentence_cn(),
                    word.getTiming(),
                    word.getEnd_timing(),
                    word.getSentence_audio(),
                    word.getSentence_single_audio(),
                    String.valueOf(bookId)
            ));
        }
        return temp;
    }

    //将单词数据-新概念青少版转换为数据库数据
    public static List<WordEntity_Junior> transConceptJuniorWordToDB(List<Concept_junior_word> list){
        List<WordEntity_Junior> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Concept_junior_word word = list.get(i);

            temp.add(new WordEntity_Junior(
                    word.getDef(),
                    word.getUpdateTime(),
                    word.getBook_id(),
                    word.getVersion(),
                    word.getExamples(),
                    word.getVideoUrl(),
                    word.getPron(),
                    TextUtils.isEmpty(word.getVoa_id())?0:Long.parseLong(word.getVoa_id()),
                    TextUtils.isEmpty(word.getIdindex())?0:Integer.parseInt(word.getIdindex()),
                    word.getAudio(),
                    TextUtils.isEmpty(word.getPosition())?0:Integer.parseInt(word.getPosition()),
                    word.getSentence_cn(),
                    word.getPic_url(),
                    TextUtils.isEmpty(word.getUnit_id())?0:Integer.parseInt(word.getUnit_id()),
                    transWordToEntityData(word.getWord()),
                    transWordToEntityData(word.getSentence()),
                    word.getSentence_audio()
            ));
        }
        return temp;
    }

    //将评测单词数据-新概念转换为数据库数据
    /*public static EvalEntity_word transConceptEvalWordToDB(String types, String bookId, String voaId, String id, String position, String localPath, Concept_eval bean){
        if (bean==null){
            return null;
        }

        String words = null;
        if (bean.getWords()!=null&&bean.getWords().size()>0){
            words = GsonUtil.toJson(bean.getWords());
        }

        return new EvalEntity_word(
                types,
                bookId,
                voaId,
                position,
                transWordToEntityData(bean.getSentence()),
                bean.getTotal_score(),
                bean.getUrl(),
                words,
                id,
                localPath
        );
    }*/

    /****中小学****/
    //将书籍数据-中小学转换为数据库数据
    /*public static List<BookEntity_junior> transJuniorBookData(String types, List<Junior_book> list){
        List<BookEntity_junior> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Junior_book book = list.get(i);
                temp.add(new BookEntity_junior(
                        book.getCategory(),
                        book.getCreateTime(),
                        book.getIsVideo(),
                        book.getPic(),
                        book.getKeyWords(),
                        book.getVersion(),
                        book.getDescCn(),
                        book.getSeriesCount(),
                        book.getSeriesName(),
                        book.getUpdateTime(),
                        book.getHotFlg(),
                        book.getHaveMicro(),
                        book.getId(),
                        types
                ));
            }
        }
        return temp;
    }*/

    //将章节数据-中小学转换为数据库数据
    /*public static List<ChapterEntity_junior> transJuniorChapterData(String types, List<Junior_chapter> list){
        List<ChapterEntity_junior> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Junior_chapter chapter = list.get(i);
                temp.add(new ChapterEntity_junior(
                        chapter.getCreatTime(),
                        chapter.getListenPercentage(),
                        chapter.getCategory(),
                        chapter.getHavePractice(),
                        chapter.getPackageid(),
                        chapter.getTexts(),
                        chapter.getVideo(),
                        chapter.getPagetitle(),
                        chapter.getUrl(),
                        chapter.getPrice(),
                        chapter.getPercentage(),
                        chapter.getPublishTime(),
                        chapter.getHotFlg(),
                        chapter.getCategoryid(),
                        chapter.getClickRead(),
                        chapter.getIntroDesc(),
                        chapter.getKeyword(),
                        chapter.getTotalTime(),
                        chapter.getTitle(),
                        chapter.getSound(),
                        chapter.getPic(),
                        chapter.getOwnerid(),
                        chapter.getFlag(),
                        chapter.getDescCn(),
                        chapter.getClassid(),
                        chapter.getOutlineid(),
                        chapter.getTitle_cn(),
                        TextUtils.isEmpty(chapter.getSeries())?0:Integer.parseInt(chapter.getSeries()),
                        chapter.getName(),
                        chapter.getWordNum(),
                        chapter.getCategoryName(),
                        TextUtils.isEmpty(chapter.getId())?0:Long.parseLong(chapter.getId()),
                        chapter.getReadCount(),
                        chapter.getDesc(),
                        types
                ));
            }
        }
        return temp;
    }*/

    //将章节详情数据-中小学转换为数据库数据
    /*public static List<ChapterDetailEntity_junior> transJuniorChapterDetailData(String types, String voaId, List<Junior_chapter_detail> list){
        List<ChapterDetailEntity_junior> temp = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Junior_chapter_detail detail = list.get(i);

                temp.add(new ChapterDetailEntity_junior(
                        detail.getImgPath(),
                        detail.getEndTiming(),
                        TextUtils.isEmpty(detail.getParaId())?0:Integer.parseInt(detail.getParaId()),
                        TextUtils.isEmpty(detail.getIdIndex())?0:Integer.parseInt(detail.getIdIndex()),
                        detail.getSentence_cn(),
                        detail.getImgWords(),
                        detail.getStart_x(),
                        detail.getEnd_y(),
                        detail.getTiming(),
                        detail.getEnd_x(),
                        detail.getSentence(),
                        detail.getStart_y(),
                        types,
                        TextUtils.isEmpty(voaId)?0:Long.parseLong(voaId)
                ));
            }
        }
        return temp;
    }*/

    //将单词数据-中小学转换为数据库数据
    /*public static List<WordEntity_junior> transJuniorWordToDB(List<Junior_word> list){
        List<WordEntity_junior> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Junior_word word = list.get(i);

            temp.add(new WordEntity_junior(
                    word.getDef(),
                    word.getUpdateTime(),
                    word.getBook_id(),
                    word.getVersion(),
                    word.getExamples(),
                    word.getVideoUrl(),
                    word.getPron(),
                    TextUtils.isEmpty(word.getVoa_id())?0:Long.parseLong(word.getVoa_id()),
                    TextUtils.isEmpty(word.getIdindex())?0:Integer.parseInt(word.getIdindex()),
                    word.getAudio(),
                    TextUtils.isEmpty(word.getPosition())?0:Integer.parseInt(word.getPosition()),
                    word.getSentence_cn(),
                    word.getPic_url(),
                    TextUtils.isEmpty(word.getUnit_id())?0:Integer.parseInt(word.getUnit_id()),
                    transWordToEntityData(word.getWord()),
                    transWordToEntityData(word.getSentence()),
                    word.getSentence_audio()
            ));
        }
        return temp;
    }*/

    //将评测单词数据-新概念转换为数据库数据
    /*public static EvalEntity_word transJuniorEvalWordToDB(String types, String bookId, String voaId, String id, String position, String localPath, Junior_eval bean){
        if (bean==null){
            return null;
        }

        String words = null;
        if (bean.getWords()!=null&&bean.getWords().size()>0){
            words = GsonUtil.toJson(bean.getWords());
        }

        return new EvalEntity_word(
                types,
                bookId,
                voaId,
                position,
                transWordToEntityData(bean.getSentence()),
                String.valueOf(bean.getTotal_score()),
                bean.getUrl(),
                words,
                id,
                localPath
        );
    }*/

    /********************转为通用类*******************/
    /***中小学***/
    //中小学-转换类型数据
    /*public static List<Pair<String,List<Pair<String,String>>>> transJuniorTypeData(List<Junior_type> list){
        List<Pair<String,List<Pair<String,String>>>> temp = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Junior_type type = list.get(i);
//                if (type.getSourceType().equals("人教版")){
//                    continue;
//                }

                List<Pair<String,String>> typeList = new ArrayList<>();
                if (type.getSeriesData()!=null&&type.getSeriesData().size()>0){
                    for (int j = 0; j < type.getSeriesData().size(); j++) {
                        Junior_type.SeriesDataBean bean = type.getSeriesData().get(j);
                        typeList.add(new Pair<>(bean.getCategory(), bean.getSeriesName()));
                    }
                }

                temp.add(new Pair<>(type.getSourceType(), typeList));
            }
        }
        return temp;
    }*/

    /*********************辅助方法*****************/
    //将单词转为数据库存储的数据
    public static String transWordToEntityData(String word){
        if (TextUtils.isEmpty(word)){
            return word;
        }

        //因为部分特殊字符需要处理，这里进行转换处理
        if (word.contains("'")){
            word = word.replace("'","‘");
        }

        return word;
    }
}
