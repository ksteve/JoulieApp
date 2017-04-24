package com.example.kyle.joulieapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Usage extends BaseEntry implements Parcelable {

    private float value;
    private float timestamp;

    public  Usage(){

    }

    public Usage(int asdfa, float label) {
    }

    public Usage(float x, float y, Object data) {
        super(y, data);
        this.value = x;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    protected Usage(Parcel in) {
        this.value = in.readFloat();
        this.setY(in.readFloat());
        if (in.readInt() == 1) {
            this.setData(in.readParcelable(Object.class.getClassLoader()));
        }
    }

    public static final Parcelable.Creator<Usage> CREATOR = new Parcelable.Creator<Usage>() {
        public Usage createFromParcel(Parcel source) {
            return new Usage(source);
        }

        public Usage[] newArray(int size) {
            return new Usage[size];
        }
    };
}
