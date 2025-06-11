package com.jn.yyz.practise.entity;

public class ListShowBean {

    private int unit;
    private String unitTitle;
    private String title;
    private boolean isPass;
    private String type;
    private int voaId;

    public ListShowBean(int unit, String unitTitle, String title, boolean isPass, String type, int voaId) {
        this.unit = unit;
        this.unitTitle = unitTitle;
        this.title = title;
        this.isPass = isPass;
        this.type = type;
        this.voaId = voaId;
    }

    public int getUnit() {
        return unit;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPass() {
        return isPass;
    }

    public String getType() {
        return type;
    }

    public int getVoaId() {
        return voaId;
    }
}
