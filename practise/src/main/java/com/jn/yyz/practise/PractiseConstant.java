package com.jn.yyz.practise;


public class PractiseConstant {


    public static String UID = "0";

    public static String APPID = "222";

    /**
     * 有power、concept、smallvideo
     */
    public static String TEST_TYPE = "smallvideo";

    public static String DOMAIN = "iyuba.cn";
    public static String DOMAIN_LONG = "iyuba.com.cn";


    public static String URL_CLASS = "http://class." + DOMAIN;


    public static String URL_AI = "http://ai." + DOMAIN;


    public static String IUSERSPEECH_URL = "http://iuserspeech." + DOMAIN + ":9001";

    //https://apps.iyuba.cn/credits/updateEXP.jsp
    public static String URL_APPS = "https://apps." + DOMAIN;

    /**
     * 获取错题本错题
     */
    public static String URL_GET_WRONG_EXAM_BY_UID = URL_APPS + "/getWrongExamByUid.jsp";

    /**
     * 练习题首页
     */
    public static String URL_GET_EXAM_TITLE_LIST = URL_CLASS + "/getExamTitleList.jsp";
    /**
     * 获取排行榜
     */
    public static String URL_GET_ENGLISH_TEST_RANKING = URL_AI + "/japanapi/getEnglishTestRanking.jsp";


    public static String URL_UPDATE_EXP = URL_APPS + "/credits/updateEXP.jsp";
    /**
     * 获取练习
     */
    public static String URL_GET_EXAM = URL_CLASS + "/getExam.jsp";


    /**
     * 评测
     */
    public static String EVAL_URL = IUSERSPEECH_URL + "/test/ai/";

    /**
     * 获取音标
     */
    public static String URL_GET_PRON_NEW = URL_AI + "/api/getPronNew.jsp";

    /**
     * 上传做题信息
     */
    public static String URL_UPDATE_ENGLISH_TEST_RECORD = URL_AI + "/japanapi/updateEnglishTestRecord.jsp";
}
