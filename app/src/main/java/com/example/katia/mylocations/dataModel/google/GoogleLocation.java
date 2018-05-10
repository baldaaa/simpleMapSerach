package com.example.katia.mylocations.dataModel.google;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbt on 12/12/2016.
 */
public class GoogleLocation implements Parcelable{
    private double lat;
    private double lng;

    public GoogleLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    protected GoogleLocation(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<GoogleLocation> CREATOR = new Creator<GoogleLocation>() {
        @Override
        public GoogleLocation createFromParcel(Parcel in) {
            return new GoogleLocation(in);
        }

        @Override
        public GoogleLocation[] newArray(int size) {
            return new GoogleLocation[size];
        }
    };

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }

    @Override
    public String toString() {
        return lat+","+lng;
    }
}
