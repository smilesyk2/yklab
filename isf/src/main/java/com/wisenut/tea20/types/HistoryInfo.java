package com.wisenut.tea20.types;


public class HistoryInfo {
    private int num;
    private String collectionId;
    private String beginTimeAnalysis;
    private String endTimeAnalysis;
    private String resultAnalysis;

    public HistoryInfo() {
    }

    public HistoryInfo(int num, String collectionId, String beginTimeAnalysis, String endTimeAnalysis, String resultAnalysis) {
        this.num = num;
        this.collectionId = collectionId;
        this.beginTimeAnalysis = beginTimeAnalysis;
        this.endTimeAnalysis = endTimeAnalysis;
        this.resultAnalysis = resultAnalysis;
    }

    public HistoryInfo(String key, String value) {

    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getBeginTimeAnalysis() {
        return beginTimeAnalysis;
    }

    public void setBeginTimeAnalysis(String beginTimeAnalysis) {
        this.beginTimeAnalysis = beginTimeAnalysis;
    }

    public String getEndTimeAnalysis() {
        return endTimeAnalysis;
    }

    public void setEndTimeAnalysis(String endTimeAnalysis) {
        this.endTimeAnalysis = endTimeAnalysis;
    }

    public String getResultAnalysis() {
        return resultAnalysis;
    }

    public void setResultAnalysis(String resultAnalysis) {
        this.resultAnalysis = resultAnalysis;
    }

}
