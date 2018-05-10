package com.example.katia.mylocations.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.katia.mylocations.PlaceDBHelper;
import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.utils.MyLocationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.example.katia.mylocations.utils.MyLocationUtils.FOLDER_NAME_ICONS;

/**
 * Created by jbt on 12/15/2016.
 */

public class DownloadAndSaveImageTask extends AsyncTask<String, Void, String> {
    static String TAG = DownloadAndSaveImageTask.class.getName();

    Context context;
    GooglePlace place;
    PlaceDBHelper helper;

    public DownloadAndSaveImageTask(Context context, GooglePlace place, PlaceDBHelper helper){
        this.context = context;
        this.place = place;
        this.helper = helper;
    }
    @Override
    protected String doInBackground(String... strings) {
        Bitmap icon = null;
        InputStream in = null;

        try {
            in = new URL(strings[0]).openStream();
            icon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.i(TAG,e.getMessage());
            Log.d(TAG,e.getMessage(),e);
        } finally {
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    Log.i(TAG,e.getMessage());
                    Log.d(TAG,e.getMessage(),e);
                }
        }

        String iconPath = MyLocationUtils.saveToInternalStorage(icon, context.getDir(FOLDER_NAME_ICONS, Context.MODE_PRIVATE),place.getId()+".png");
        helper.updateIconPath(iconPath,place.getId());
        return iconPath;
    }
}
