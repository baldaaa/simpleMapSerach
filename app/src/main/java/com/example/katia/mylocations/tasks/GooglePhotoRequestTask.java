package com.example.katia.mylocations.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.dataModel.google.GoogleMapsCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by katia on 13/12/2016.
 * //https://maps.googleapis.com/maps/api/place/photo
 */

public class GooglePhotoRequestTask extends AsyncTask<NameValuePair,Void,Bitmap> implements GoogleParamsInterface {
    static String TAG = GooglePhotoRequestTask.class.getName();
    Context context;
    ImageView view;

    public GooglePhotoRequestTask(Context context, ImageView view) {
        this.context = context;
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(NameValuePair... params) {

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("maps.googleapis.com")
                .path("maps/api/place/photo")
                .appendQueryParameter(PARAM_NAME_KEY, GoogleMapsCredentials.AUTH_KEY);
        for (NameValuePair pair:params) {
            uriBuilder.appendQueryParameter(pair.getName(), pair.getValue().toString());
        }

        Bitmap icon = null;
        InputStream in = null;
        try {
            String uriStr = uriBuilder.build().toString();
            in = new URL(URLDecoder.decode(uriStr)).openStream();
            Log.d(TAG,uriStr);
            icon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.i(TAG,e.getMessage());
            Log.d(TAG,e.getMessage(),e);
        } finally {
            if(in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d(TAG,e.getMessage(),e);
                    Log.e(TAG,e.getMessage());
                }
        }
        return icon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}
