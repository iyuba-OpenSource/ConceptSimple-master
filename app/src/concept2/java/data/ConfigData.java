package data;

public class ConfigData {

    /********视频显示控制*****/
    //是否需要控制显示
    public static boolean videoVerifyCheck = true;

    //获取视频的控制渠道id
    public static int getVideoLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 8021;
            case "xiaomi":
                return 8022;
            case "oppo":
                return 8023;
            case "vivo":
                return 8024;
            case "honor":
                return 8025;
        }
        return 8020;
    }

    /********微课显示控制*****/
    //是否需要控制显示
    public static boolean mocVerifyCheck = true;

    //获取微课的控制渠道id
    public static int getMocLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 8001;
            case "xiaomi":
                return 8002;
            case "oppo":
                return 8003;
            case "vivo":
                return 8004;
            case "honor":
                return 8005;
        }
        return 8000;
    }

    /********单词显示控制*****/
    //是否需要控制显示
    public static boolean wordVerifyCheck = true;

    //获取单词的控制渠道id
    public static int getWordLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 8061;
            case "xiaomi":
                return 8062;
            case "oppo":
                return 8063;
            case "vivo":
                return 8064;
            case "honor":
                return 8065;
        }
        return 8060;
    }

    /****oaid升级****/
    //oaid的证书名称
    public static String oaid_pem = "com.iyuba.concept2.cert.pem";

    /***************广告的key************/
    //有道广告
    public static final String YOUDAO_AD_SPLASH_CODE = "9755487e03c2ff683be4e2d3218a2f2b";//开屏
    public static final String YOUDAO_AD_STEAM_CODE = "5542d99e63893312d28d7e49e2b43559";//信息流
    public static final String YOUDAO_AD_BANNER_CODE = "230d59b7c0a808d01b7041c2d127da95";//banner

    //爱语吧广告
    public static final String IYUBA_AD_SPLASH_CODE = "0013";
    public static final String IYUBA_AD_BANNER_CODE = "0014";

    /*******************mob的数据****************************/
    public static final String mobKey = "387636ce5cf38";
    public static final String mobSecret = "a51c6d507c607fb99787b44153789a76";

    /********************公司类型***************************/
    //北京爱语吧
    public static final String privacy_company_type = "1";
    public static final String vip_company_type = "1";
}
