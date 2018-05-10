package com.example.katia.mylocations;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by jbt on 12/11/2016.
 */

public class LocationApplication extends Application implements LocationListener{

    public static String SP_PARAM_NAME_LAT = "currentLat";
    public static String SP_PARAM_NAME_LNG = "currentLon";
    public static float DEFAULT_LAT = 32.167590f;
    public static float DEFAULT_LNG = 34.803837f;

    LocationManager locationManager;
    SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 50, this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLocationChanged(Location location) {
        sp.edit().putFloat(SP_PARAM_NAME_LAT, (float) location.getLatitude()).putFloat(SP_PARAM_NAME_LNG, (float) location.getLongitude()).apply();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
