package com.jn.yyz.practise.event;

public class BookChooseEventsbus {

    private String type;
    private int bookId;

    public BookChooseEventsbus(String type, int bookId) {
        this.type = type;
        this.bookId = bookId;
    }

    public String getType() {
        return type;
    }

    public int getBookId() {
        return bookId;
    }
}
