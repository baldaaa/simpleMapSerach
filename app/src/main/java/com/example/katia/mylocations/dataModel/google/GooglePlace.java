package com.example.katia.mylocations.dataModel.google;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by jbt on 12/12/2016.
 */
public class GooglePlace implements Parcelable{
    private String id;
    private GoogleGeo geometry;
    private String icon;
    private String iconRef;
    private String name;
    private ArrayList<GooglePhoto> photos;
    private String place_id;
    private String[] types;
    private String vicinity;
    private float rating;
    private String formatted_address;
    private boolean removeCandidate = false;

    private String international_phone_number;
    private String formatted_phone_number;
    private GoogleOpenHours opening_hours;

    public GooglePlace(){}


    protected GooglePlace(Parcel in) {
        id = in.readString();
        geometry = in.readParcelable(GoogleGeo.class.getClassLoader());
        icon = in.readString();
        iconRef = in.readString();
        name = in.readString();
        photos = in.createTypedArrayList(GooglePhoto.CREATOR);
        place_id = in.readString();
        types = in.createStringArray();
        vicinity = in.readString();
        rating = in.readFloat();
        formatted_address = in.readString();
        removeCandidate = in.readByte() != 0;
        international_phone_number = in.readString();
        formatted_phone_number = in.readString();
        opening_hours = in.readParcelable(GoogleOpenHours.class.getClassLoader());
    }

    public static final Creator<GooglePlace> CREATOR = new Creator<GooglePlace>() {
        @Override
        public GooglePlace createFromParcel(Parcel in) {
            return new GooglePlace(in);
        }

        @Override
        public GooglePlace[] newArray(int size) {
            return new GooglePlace[size];
        }
    };

    public boolean isRemoveCandidate() {
        return removeCandidate;
    }

    public void setRemoveCandidate(boolean removeCandidate) {
        this.removeCandidate = removeCandidate;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public GoogleOpenHours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(GoogleOpenHours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getId() {
        return id;
    }

    public String getIconRef() {
        return iconRef;
    }

    public void setIconRef(String iconRef) {
        this.iconRef = iconRef;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GoogleGeo getGeometry() {
        return geometry;
    }

    public void setGeometry(GoogleGeo geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<GooglePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<GooglePhoto> photos) {
        this.photos = photos;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }
    public String typesToString(){
        StringBuilder sb = new StringBuilder();
        for (String type:types) {
            sb.append(type);
            sb.append(",");
        }
        String temp = sb.toString();
        return temp.substring(0,temp.length()-2);
    }
    public void typesFromString(String typesStr){
        types = typesStr.split(",");
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(geometry, flags);
        dest.writeString(icon);
        dest.writeString(iconRef);
        dest.writeString(name);
        dest.writeTypedList(photos);
        dest.writeString(place_id);
        dest.writeStringArray(types);
        dest.writeString(vicinity);
        dest.writeFloat(rating);
        dest.writeString(formatted_address);
        dest.writeByte((byte) (removeCandidate ? 1 : 0));
        dest.writeString(international_phone_number);
        dest.writeString(formatted_phone_number);
        dest.writeParcelable(opening_hours, flags);
    }
}
