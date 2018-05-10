package com.example.katia.mylocations.dataModel.google;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by katia on 16/12/2016.
 */
public class GoogleOpenHours implements Parcelable {
    private String[] weekday_text;
    private boolean open_now;

    protected GoogleOpenHours(Parcel in) {
        weekday_text = in.createStringArray();
        open_now = in.readByte() != 0;
    }

    public static final Creator<GoogleOpenHours> CREATOR = new Creator<GoogleOpenHours>() {
        @Override
        public GoogleOpenHours createFromParcel(Parcel in) {
            return new GoogleOpenHours(in);
        }

        @Override
        public GoogleOpenHours[] newArray(int size) {
            return new GoogleOpenHours[size];
        }
    };

    public String[] getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(String[] weekday_text) {
        this.weekday_text = weekday_text;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(weekday_text);
        dest.writeByte((byte) (open_now ? 1 : 0));
    }
}
