package com.example.katia.mylocations.dataModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbt on 12/12/2016.
 */

public class NameValuePair implements Parcelable{
    private String name;
    private String value;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    protected NameValuePair(Parcel in) {
        name = in.readString();
        value = in.readString();
    }

    public static final Creator<NameValuePair> CREATOR = new Creator<NameValuePair>() {
        @Override
        public NameValuePair createFromParcel(Parcel in) {
            return new NameValuePair(in);
        }

        @Override
        public NameValuePair[] newArray(int size) {
            return new NameValuePair[size];
        }
    };

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(value);
    }
}
