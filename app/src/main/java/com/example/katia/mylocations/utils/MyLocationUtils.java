package com.example.katia.mylocations.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.katia.mylocations.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by katia on 19/12/2016.
 */

public class MyLocationUtils {
    public static String FOLDER_NAME_ICONS = "icons";
    public static String FOLDER_NAME_SCREENSHORTS = "screenshorts";

    public static String saveToInternalStorage(Bitmap bitmapImage, File directory, String fileName){
        File filepath=new File(directory,fileName);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filepath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.i("DownloadImageTask:",e.getMessage());
            Log.d("DownloadImageTask:",e.getMessage(),e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.i("DownloadImageTask:",e.getMessage());
                Log.d("DownloadImageTask:",e.getMessage(),e);
            }
        }
        return filepath.getAbsolutePath();
    }

    public static boolean deleteFile(String absolutePath){
        File file = new File(absolutePath);
        return file.delete();
    }

    //calculates distance between two locations
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if ("km".equals(unit)) {
            dist = dist * 1.609344;
        } else if ("mi".equals(unit)) {
            dist = dist * 0.8684;
        }
        return (dist);
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
