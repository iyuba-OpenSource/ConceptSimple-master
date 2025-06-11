package data;

/**
 * 广告测试的key数据
 *
 * 自用数据
 */
public interface AdTestKeyData {

    /**
     * 广告位key配置如下
     *
     * 穿山甲
     * com.suzhou.concept 开屏 0048
     * com.suzhou.concept Banner 0049
     * com.suzhou.concept 插屏 0524
     * com.suzhou.concept 模版 0525
     * com.suzhou.concept DrawVideo 0526
     * com.suzhou.concept 激励视频 0527
     *
     * 优量汇
     * com.suzhou.concept 开屏 0528
     * com.suzhou.concept Banner 0529
     * com.suzhou.concept 插屏 0530
     * com.suzhou.concept 模版 0531
     * com.suzhou.concept DrawVideo 0532
     * com.suzhou.concept 激励视频 0533
     *
     * 百度
     * com.suzhou.concept 开屏 0534
     * com.suzhou.concept 插屏 0535
     * com.suzhou.concept 模版 0536
     * com.suzhou.concept 激励视频 0537
     *
     * 快手
     * com.suzhou.concept 开屏 0538
     * com.suzhou.concept 插屏 0539
     * com.suzhou.concept 模版 0540
     * com.suzhou.concept DrawVideo 0541
     * com.suzhou.concept 激励视频 0542
     *
     *
     * 接口数据请在浏览器中查看 http://ai.iyuba.cn/mediatom/server/adplace?placeid=0542
     */

    //key值信息
    interface  KeyData{
        class SpreadAdKey{
            /**
             * 穿山甲：0048
             * 优量汇：0528
             * 百度：0534
             * 快手：0538
             */
            public static final String spread_youdao = "9755487e03c2ff683be4e2d3218a2f2b";//有道
            public static final String spread_beizi = "0634";//倍孜
            public static final String spread_csj = "0048";//穿山甲
            public static final String spread_ylh = "0528";//优量汇
            public static final String spread_baidu = "0534";//百度
            public static final String spread_ks = "0538";//快手
        }

        class TemplateAdKey{
            /**
             * 穿山甲：0525
             * 优量汇：0531
             * 百度：0536
             * 快手：0540
             */
            public static final String template_youdao = "3438bae206978fec8995b280c49dae1e";//有道
            public static final String template_csj = "0525";//穿山甲
            public static final String template_ylh = "0531";//优量汇
            public static final String template_baidu = "0536";//百度
            public static final String template_ks = "0540";//快手
        }

        class BannerAdKey{
            /**
             * 穿山甲：0049
             * 优量汇：0529
             */
            public static final String banner_youdao = "230d59b7c0a808d01b7041c2d127da95";//有道
            public static final String banner_csj = "0049";//穿山甲
            public static final String banner_ylh = "0529";//优量汇
        }

        class InterstitialAdKey{
            /**
             * 穿山甲：0524
             * 优量汇：0530
             * 百度：0535
             * 快手：0539
             */
            public static final String interstitial_csj = "0524";//穿山甲
            public static final String interstitial_ylh = "0530";//优量汇
            public static final String interstitial_baidu = "0535";//百度
            public static final String interstitial_ks = "0539";//快手
        }

        class DrawVideoAdKey{
            /**
             * 穿山甲：0526
             * 优量汇：0532
             * 快手：0541
             */
            public static final String drawVideo_csj = "0526";//穿山甲
            public static final String drawVideo_ylh = "0532";//优量汇
            public static final String drawVideo_ks = "0541";//快手
        }

        class IncentiveAdKey{
            /**
             * 穿山甲：0527
             * 优量汇：0533
             * 百度：0537
             * 快手：0542
             */
            public static final String incentive_csj = "0527";//穿山甲
            public static final String incentive_ylh = "0533";//优量汇
            public static final String incentive_baidu = "0537";//百度
            public static final String incentive_ks = "0542";//快手
        }
    }
}
