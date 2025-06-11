package com.jn.yyz.practise.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeTestTitleBean {


    @SerializedName("result")
    private int result;
    @SerializedName("totalNumber")
    private int totalNumber;
    @SerializedName("winningDays")
    private int winningDays;
    @SerializedName("data")
    private List<Unity> data;
    @SerializedName("isContinueNextLevel")
    private int isContinueNextLevel;
    @SerializedName("unitSize")
    private int unitSize;
    @SerializedName("message")
    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getWinningDays() {
        return winningDays;
    }

    public void setWinningDays(int winningDays) {
        this.winningDays = winningDays;
    }

    public List<Unity> getData() {
        return data;
    }

    public void setData(List<Unity> data) {
        this.data = data;
    }

    public int getIsContinueNextLevel() {
        return isContinueNextLevel;
    }

    public void setIsContinueNextLevel(int isContinueNextLevel) {
        this.isContinueNextLevel = isContinueNextLevel;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Unity {
        @SerializedName("unit")
        private int unit;
        @SerializedName("data")
        private List<Level> data;
        @SerializedName("size")
        private int size;
        @SerializedName("desc")
        private String desc;
        private boolean isShowTitle = false;

        //关卡的开始的位置 0.5 0.7 0.9 0.3 0.1
        private int biasPos;


        public int getBiasPos() {
            return biasPos;
        }

        public void setBiasPos(int biasPos) {
            this.biasPos = biasPos;
        }

        public boolean isShowTitle() {
            return isShowTitle;
        }

        public void setShowTitle(boolean showTitle) {
            isShowTitle = showTitle;
        }

        public int getUnit() {
            return unit;
        }

        public void setUnit(int unit) {
            this.unit = unit;
        }

        public List<Level> getData() {
            return data;
        }

        public void setData(List<Level> data) {
            this.data = data;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static class Level {
            @SerializedName("voaid")
            private int voaid;
            @SerializedName("titleCn")
            private String titleCn;
            @SerializedName("isPass")
            private int isPass;
            @SerializedName("title")
            private String title;
            @SerializedName("type")
            private String type;

            private float bias;

            private boolean isUnlock = false;


            public float getBias() {
                return bias;
            }

            public void setBias(float bias) {
                this.bias = bias;
            }

            public boolean isUnlock() {
                return isUnlock;
            }

            public void setUnlock(boolean unlock) {
                isUnlock = unlock;
            }

            public int getVoaid() {
                return voaid;
            }

            public void setVoaid(int voaid) {
                this.voaid = voaid;
            }

            public String getTitleCn() {
                return titleCn;
            }

            public void setTitleCn(String titleCn) {
                this.titleCn = titleCn;
            }

            public int getIsPass() {
                return isPass;
            }

            public void setIsPass(int isPass) {
                this.isPass = isPass;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
