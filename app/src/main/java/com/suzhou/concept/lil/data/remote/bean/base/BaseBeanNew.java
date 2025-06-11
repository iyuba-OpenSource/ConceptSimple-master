package com.suzhou.concept.lil.data.remote.bean.base;

public class BaseBeanNew<T> {


    /**
     * ResultCode : 511
     * Message : OK
     * PageNumber : 1
     * TotalPage : 1
     * FirstPage : 1
     * PrevPage : 1
     * NextPage : 1
     * LastPage : 1
     * AddScore : 0
     * Counts : 4
     * data : []
     */

    private String ResultCode;
    private String Message;
    private int PageNumber;
    private int TotalPage;
    private int FirstPage;
    private int PrevPage;
    private int NextPage;
    private int LastPage;
    private int AddScore;
    private int Counts;
    private T data;

    public String getResultCode() {
        return ResultCode;
    }

    public void setResultCode(String ResultCode) {
        this.ResultCode = ResultCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public int getPageNumber() {
        return PageNumber;
    }

    public void setPageNumber(int PageNumber) {
        this.PageNumber = PageNumber;
    }

    public int getTotalPage() {
        return TotalPage;
    }

    public void setTotalPage(int TotalPage) {
        this.TotalPage = TotalPage;
    }

    public int getFirstPage() {
        return FirstPage;
    }

    public void setFirstPage(int FirstPage) {
        this.FirstPage = FirstPage;
    }

    public int getPrevPage() {
        return PrevPage;
    }

    public void setPrevPage(int PrevPage) {
        this.PrevPage = PrevPage;
    }

    public int getNextPage() {
        return NextPage;
    }

    public void setNextPage(int NextPage) {
        this.NextPage = NextPage;
    }

    public int getLastPage() {
        return LastPage;
    }

    public void setLastPage(int LastPage) {
        this.LastPage = LastPage;
    }

    public int getAddScore() {
        return AddScore;
    }

    public void setAddScore(int AddScore) {
        this.AddScore = AddScore;
    }

    public int getCounts() {
        return Counts;
    }

    public void setCounts(int Counts) {
        this.Counts = Counts;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
