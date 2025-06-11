package data;

public class ConfigData {

    /********视频显示控制*****/
    //是否需要控制显示
    public static boolean videoVerifyCheck = true;

    //获取视频的控制渠道id
    public static int getVideoLimitChannelId(String channel){
        switch (channel){
            case "huawei":
                return 8421;
            case "xiaomi":
                return 8422;
            case "oppo":
                return 8423;
            case "vivo":
                return 8424;
            case "honor":
                return 8425;
        }
        return 8420;
    }

    /********微课显示控制*****/
    //是否需要控制显示
    public static boolean mocVerifyCheck = false;

    //获取微课的控制渠道id
    public static int getMocLimitChannelId(String channel){
        return 0;
    }

    /********单词显示控制*****/
    //是否需要控制显示
    public static boolean wordVerifyCheck = false;

    //获取单词的控制渠道id
    public static int getWordLimitChannelId(String channel){
        return 0;
    }

    /****oaid升级****/
    //oaid的证书名称
    public static String oaid_pem = "com.suzhou.concept.cert.pem";

    /*******************mob的数据****************************/
    public static final String mobKey = "389f2cfa2acd5";
    public static final String mobSecret = "ca7a24a01ffcdfb8ea977b4d8c8688f4";

    /********************公司类型***************************/
    //爱语言北京
    public static final String privacy_company_type = "3";
    public static final String vip_company_type = "2";
}
