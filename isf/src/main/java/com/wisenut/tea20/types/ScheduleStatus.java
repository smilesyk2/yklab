package com.wisenut.tea20.types;


public class ScheduleStatus {
    private int num;
    private String collectionId;
    private String endTimeAnalysis;
    private String runStatus;
    private String enableStatus;

    public ScheduleStatus() {
    }

    public ScheduleStatus(ScheduleStatus s) {
        this.num = s.num;
        this.collectionId = s.collectionId;
        this.endTimeAnalysis = s.endTimeAnalysis;
        this.runStatus = s.runStatus;
        this.enableStatus = s.enableStatus;
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

    public String getEndTimeAnalysis() {
        return endTimeAnalysis;
    }

    public void setEndTimeAnalysis(String endTimeAnalysis) {
        this.endTimeAnalysis = endTimeAnalysis;
    }

    public String getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }


}
