package com.example.katia.mylocations.dataModel.google;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbt on 12/12/2016.
 */
public class GoogleGeo implements Parcelable{
    private GoogleLocation location;

    public GoogleGeo(GoogleLocation location) {
        this.location = location;
    }

    protected GoogleGeo(Parcel in) {
        location = in.readParcelable(GoogleLocation.class.getClassLoader());
    }

    public static final Creator<GoogleGeo> CREATOR = new Creator<GoogleGeo>() {
        @Override
        public GoogleGeo createFromParcel(Parcel in) {
            return new GoogleGeo(in);
        }

        @Override
        public GoogleGeo[] newArray(int size) {
            return new GoogleGeo[size];
        }
    };

    public GoogleLocation getLocation() {
        return location;
    }

    public void setLocation(GoogleLocation location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(location, i);
    }
}
