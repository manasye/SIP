package com.example.sip;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryModel implements Parcelable {

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

    public HistoryModel(Parcel in) {
        date = in.readString();
        stepCount = in.readInt();
        target = in.readInt();
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(stepCount);
        dest.writeInt(target);
    }

    public String getDate(){
        return this.date;
    }

    public int getStepCount(){
        return this.stepCount;
    }
}
