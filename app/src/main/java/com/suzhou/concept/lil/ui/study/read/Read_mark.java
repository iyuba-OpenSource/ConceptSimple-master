package com.suzhou.concept.lil.ui.study.read;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @title:
 * @date: 2023/9/27 13:13
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "data",strict = false)
public class Read_mark {

    @Element(required = false)
    public String result;
    @Element(required = false)
    public String message;
    @Element(required = false)
    public String jifen;
    @Element(required = false)
    public String reward;
    @Element(required = false)
    public String rewardMessage;
}
