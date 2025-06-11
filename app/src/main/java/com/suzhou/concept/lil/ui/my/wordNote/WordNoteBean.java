package com.suzhou.concept.lil.ui.my.wordNote;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @title:
 * @date: 2023/10/8 16:45
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "response", strict = false)
public class WordNoteBean {

    @Element(name = "counts")
    public int counts;
    @Element(name = "pageNumber")
    public int pageNumber;
    @Element(name = "totalPage")
    public int totalPage;
    @Element(name = "firstPage")
    public int firstPage;
    @Element(name = "prevPage")
    public int prevPage;
    @Element(name = "nextPage")
    public int nextPage;
    @Element(name = "lastPage")
    public int lastPage;
    @ElementList(required = false, inline = true)
    public List<TempWord> tempWords;

    @Root(name = "row", strict = false)
    public static class TempWord {
        @Element(name = "Word",required = false )
        public String word;
        @Element(name = "Audio", required = false)
        public String audioUrl;
        @Element(name = "Pron", required = false)
        public String pronunciation;
        @Element(name = "Def", required = false)
        public String definition;
    }
}
