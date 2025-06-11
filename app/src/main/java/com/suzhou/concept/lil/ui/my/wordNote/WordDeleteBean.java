package com.suzhou.concept.lil.ui.my.wordNote;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @title: 收藏/取消收藏的回调
 * @date: 2023/10/9 10:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "response",strict = false)
public class WordDeleteBean {

    @Element(name = "result")
    public int result;
    @Element(name = "word")
    public String word;
}
