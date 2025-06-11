package com.jn.yyz.practise.event;

/**
 * 完成试题的eventbus
 */
public class TestFinishEventbus {


    private String type;


    private String id;


    public TestFinishEventbus(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
