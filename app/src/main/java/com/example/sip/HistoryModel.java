package com.example.sip;

public class HistoryModel {

    public String date;
    public int stepCount;
    public int target;

    public HistoryModel() {
        this.date = "01-01-0001";
        this.stepCount = 0;
        this.target = 0;
    }

    public HistoryModel(String date, int stepCount, int target) {
        this.date = date;
        this.stepCount = stepCount;
        this.target = target;
    }

}
