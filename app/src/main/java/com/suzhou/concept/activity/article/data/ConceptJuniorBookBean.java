package com.suzhou.concept.activity.article.data;

import com.suzhou.concept.bean.YoungItem;

import java.util.List;

public class ConceptJuniorBookBean {

    /**
     * result : 1
     * total : 12
     * data : [{"Category":"321","CreateTime":"2020-05-26 03:05:39.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/278.jpg","KeyWords":"少儿英语","version":"8","DescCn":"StarterA","SeriesCount":"15","SeriesName":"新概念英语青少版StarterA","UpdateTime":"2020-05-26 03:05:39.0","HotFlg":"0","haveMicro":"0","Id":"278"},{"Category":"321","CreateTime":"2020-05-26 03:05:16.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/279.jpg","KeyWords":"少儿英语","version":"16","DescCn":"StarterB","SeriesCount":"15","SeriesName":"新概念英语青少版StarterB","UpdateTime":"2020-05-26 03:05:38.0","HotFlg":"0","haveMicro":"0","Id":"279"},{"Category":"321","CreateTime":"2020-05-26 03:05:11.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/280.jpg","KeyWords":"少儿英语","version":"251","DescCn":"1A","SeriesCount":"15","SeriesName":"新概念英语青少版1A","UpdateTime":"2020-05-26 03:05:37.0","HotFlg":"0","haveMicro":"0","Id":"280"},{"Category":"321","CreateTime":"2020-05-26 03:05:15.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/281.jpg","KeyWords":"少儿英语","version":"144","DescCn":"1B","SeriesCount":"15","SeriesName":"新概念英语青少版1B","UpdateTime":"2020-05-26 03:05:36.0","HotFlg":"0","haveMicro":"0","Id":"281"},{"Category":"321","CreateTime":"2020-05-26 03:05:56.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/282.jpg","KeyWords":"少儿英语","version":"78","DescCn":"2A","SeriesCount":"15","SeriesName":"新概念英语青少版2A","UpdateTime":"2020-05-26 03:05:35.0","HotFlg":"0","haveMicro":"0","Id":"282"},{"Category":"321","CreateTime":"2020-05-26 03:05:10.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/283.jpg","KeyWords":"少儿英语","version":"174","DescCn":"2B","SeriesCount":"15","SeriesName":"新概念英语青少版2B","UpdateTime":"2020-05-26 03:05:34.0","HotFlg":"0","haveMicro":"0","Id":"283"},{"Category":"321","CreateTime":"2020-05-26 03:05:11.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/284.jpg","KeyWords":"少儿英语","version":"200","DescCn":"3A","SeriesCount":"15","SeriesName":"新概念英语青少版3A","UpdateTime":"2020-05-26 03:05:33.0","HotFlg":"0","haveMicro":"0","Id":"284"},{"Category":"321","CreateTime":"2020-05-26 03:05:59.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/285.jpg","KeyWords":"少儿英语","version":"212","DescCn":"3B","SeriesCount":"15","SeriesName":"新概念英语青少版3B","UpdateTime":"2020-05-26 03:05:32.0","HotFlg":"0","haveMicro":"0","Id":"285"},{"Category":"321","CreateTime":"2020-05-26 03:05:53.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/286.jpg","KeyWords":"少儿英语","version":"349","DescCn":"4A","SeriesCount":"24","SeriesName":"新概念英语青少版4A","UpdateTime":"2020-05-26 03:05:31.0","HotFlg":"0","haveMicro":"0","Id":"286"},{"Category":"321","CreateTime":"2020-05-26 03:05:41.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/287.jpg","KeyWords":"少儿英语","version":"379","DescCn":"4B","SeriesCount":"24","SeriesName":"新概念英语青少版4B","UpdateTime":"2020-05-26 03:05:30.0","HotFlg":"0","haveMicro":"0","Id":"287"},{"Category":"321","CreateTime":"2020-05-26 03:05:09.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/288.jpg","KeyWords":"少儿英语","version":"151","DescCn":"5A","SeriesCount":"24","SeriesName":"新概念英语青少版5A","UpdateTime":"2020-05-26 03:05:29.0","HotFlg":"0","haveMicro":"0","Id":"288"},{"Category":"321","CreateTime":"2020-05-26 03:05:27.0","isVideo":"1","pic":"http://static2.iyuba.cn/images/voaseries/289.jpg","KeyWords":"少儿英语","version":"156","DescCn":"5B","SeriesCount":"24","SeriesName":"新概念英语青少版5B","UpdateTime":"2020-05-26 03:05:28.0","HotFlg":"0","haveMicro":"0","Id":"289"}]
     */

    private String result;
    private int total;
    private List<YoungItem> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<YoungItem> getData() {
        return data;
    }

    public void setData(List<YoungItem> data) {
        this.data = data;
    }
}
