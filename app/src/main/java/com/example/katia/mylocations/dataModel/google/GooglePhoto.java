package com.example.katia.mylocations.dataModel.google;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbt on 12/12/2016.
 */
public class GooglePhoto implements Parcelable{
    private int height;
    private int width;
    private String photo_reference;

    protected GooglePhoto(Parcel in) {
        height = in.readInt();
        width = in.readInt();
        photo_reference = in.readString();
    }

    public GooglePhoto(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public static final Creator<GooglePhoto> CREATOR = new Creator<GooglePhoto>() {
        @Override
        public GooglePhoto createFromParcel(Parcel in) {
            return new GooglePhoto(in);
        }

        @Override
        public GooglePhoto[] newArray(int size) {
            return new GooglePhoto[size];
        }
    };

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeString(photo_reference);
    }
}
