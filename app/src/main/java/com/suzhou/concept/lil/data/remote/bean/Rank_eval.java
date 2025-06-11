package com.suzhou.concept.lil.data.remote.bean;

import java.util.List;

/**
 * 学习界面-评测的排行数据
 */
public class Rank_eval {


    /**
     * result : 20
     * myimgSrc : http://static1.iyuba.cn/uc_server/head/2024/3/17/15/19/17/43cbd8cb-3d17-4830-9e73-fb80e0d28a31-m.jpg
     * myid : 15399731
     * myranking : 942
     * data : [{"uid":12647362,"scores":2100,"name":"果.","count":21,"ranking":1,"sort":1,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/4/8/21/33/46/de0362fa-ae89-4499-bd35-da3637769eaf-m.jpg"},{"uid":15272596,"scores":2000,"name":"iyuba31820268","count":20,"ranking":2,"sort":2,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":12556264,"scores":2000,"name":"user_68890331","count":20,"ranking":3,"sort":3,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":13027103,"scores":2000,"name":"李峻宸0925","count":20,"ranking":4,"sort":4,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":14391431,"scores":2000,"name":"就这个石粒","count":20,"ranking":5,"sort":5,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/3/6/20/55/31/4355df60-d2f0-4431-b9c5-4281cfce4cd3-m.jpg"},{"uid":11755523,"scores":1965,"name":"mmzayy","count":21,"ranking":6,"sort":6,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":15268806,"scores":1949,"name":"iyuba59658866","count":21,"ranking":7,"sort":7,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":15294070,"scores":1923,"name":"a0520","count":21,"ranking":8,"sort":8,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":15372918,"scores":1901,"name":"iyuba37332280","count":21,"ranking":9,"sort":9,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":15013896,"scores":1894,"name":"张艺眬","count":21,"ranking":10,"sort":10,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/2/25/21/17/20/d05efb11-f0fa-4dea-b6c0-357f41a04b1c-m.jpg"},{"uid":15344978,"scores":1893,"name":"iyuba05019511","count":21,"ranking":11,"sort":11,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/2/21/20/32/8/d5dca8a6-1f37-4adc-8388-2ea04453989a-m.jpg"},{"uid":15314748,"scores":1883,"name":"iyuba21177575","count":21,"ranking":12,"sort":12,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/2/22/13/20/8/41ae656f-b51b-48a8-98f0-1f04973773f2-m.jpg"},{"uid":15423653,"scores":1880,"name":"xixi-9","count":21,"ranking":13,"sort":13,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/4/3/19/40/33/8f823555-f54d-403a-b0a0-3a854939fbad-m.jpg"},{"uid":15244697,"scores":1878,"name":"iyuba97581956","count":21,"ranking":14,"sort":14,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":12993057,"scores":1875,"name":"iyuba96132270","count":21,"ranking":15,"sort":15,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":15352570,"scores":1874,"name":"依依很可爱","count":21,"ranking":16,"sort":16,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/2/24/12/51/11/58552ba9-a952-42e9-90af-15331ca45cec-m.jpg"},{"uid":12798010,"scores":1866,"name":"iyuba69595935","count":21,"ranking":17,"sort":17,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":14633935,"scores":1862,"name":"zhaoyvcheng赵","count":21,"ranking":18,"sort":18,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/head/2023/11/21/19/11/22/58152e93-42d4-4ba7-b49f-8a27d33d9d6f-m.jpg"},{"uid":13576497,"scores":1822,"name":"Lucy20150519","count":20,"ranking":19,"sort":19,"vip":"0","imgSrc":"http://static1.iyuba.cn/uc_server/images/noavatar_middle.jpg"},{"uid":15273425,"scores":1822,"name":"yanglailai0719","count":20,"ranking":20,"sort":20,"vip":"1","imgSrc":"http://static1.iyuba.cn/uc_server/head/2024/1/28/22/37/29/08efa1a0-d145-4412-a3bc-6b556461f8b3-m.jpg"}]
     * myname : test9653
     * myscores : 37
     * mycount : 1
     * vip : 0
     * message : Success
     */

    private int result;
    private String myimgSrc;
    private int myid;
    private int myranking;
    private String myname;
    private int myscores;
    private int mycount;
    private String vip;
    private String message;
    private List<DataDTO> data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMyimgSrc() {
        return myimgSrc;
    }

    public void setMyimgSrc(String myimgSrc) {
        this.myimgSrc = myimgSrc;
    }

    public int getMyid() {
        return myid;
    }

    public void setMyid(int myid) {
        this.myid = myid;
    }

    public int getMyranking() {
        return myranking;
    }

    public void setMyranking(int myranking) {
        this.myranking = myranking;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }

    public int getMyscores() {
        return myscores;
    }

    public void setMyscores(int myscores) {
        this.myscores = myscores;
    }

    public int getMycount() {
        return mycount;
    }

    public void setMycount(int mycount) {
        this.mycount = mycount;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * uid : 12647362
         * scores : 2100
         * name : 果.
         * count : 21
         * ranking : 1
         * sort : 1
         * vip : 0
         * imgSrc : http://static1.iyuba.cn/uc_server/head/2024/4/8/21/33/46/de0362fa-ae89-4499-bd35-da3637769eaf-m.jpg
         */

        private int uid;
        private int scores;
        private String name;
        private int count;
        private int ranking;
        private int sort;
        private String vip;
        private String imgSrc;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }
    }
}