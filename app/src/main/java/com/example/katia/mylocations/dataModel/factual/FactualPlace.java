package com.example.katia.mylocations.dataModel.factual;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by katia on 03/12/2016.
 *
 * {
 "factual_id": "dab0005d-21d5-4c48-a9d6-02daa00479d3",
 "name": "Warszawa",
 "address": "1414 Lincoln Blvd",
 "locality": "Santa Monica",
 "region": "CA",
 "country": "US",
 "postcode": "90401",
 "tel": "(310) 393-8831",
 "category": "Food & Beverage > Restaurants",
 "latitude": 34.019207,
 "longitude": -118.49102,
 "status": "1",
 "resolved":true,
 "similarity":.99
 }
 */
public class FactualPlace implements Parcelable{
    private String factual_id;
    private String name;
    private String address;
    private String locality;
    private String region;
    private String country;
    private String category;

    private String postcode;
    private String phone;
    private String image;

    private double latitude;
    private double longitude;
    private boolean resolved;

    public FactualPlace(String name, String locality) {
        this.name = name;
        this.locality = locality;
    }

    public FactualPlace(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected FactualPlace(Parcel in) {
        factual_id = in.readString();
        name = in.readString();
        address = in.readString();
        locality = in.readString();
        region = in.readString();
        country = in.readString();
        category = in.readString();
        postcode = in.readString();
        phone = in.readString();
        image = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        resolved = in.readByte() != 0;
    }

    public static final Creator<FactualPlace> CREATOR = new Creator<FactualPlace>() {
        @Override
        public FactualPlace createFromParcel(Parcel in) {
            return new FactualPlace(in);
        }

        @Override
        public FactualPlace[] newArray(int size) {
            return new FactualPlace[size];
        }
    };

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getFactual_id() {
        return factual_id;
    }

    public void setFactual_id(String factual_id) {
        this.factual_id = factual_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaName() {
        return locality;
    }

    public void setAreaName(String locality) {
        this.locality = locality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(factual_id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(locality);
        parcel.writeString(region);
        parcel.writeString(country);
        parcel.writeString(category);
        parcel.writeString(postcode);
        parcel.writeString(phone);
        parcel.writeString(image);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeByte((byte) (resolved ? 1 : 0));
    }
}
