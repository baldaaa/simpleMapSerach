package com.example.katia.mylocations.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.katia.mylocations.PlaceDBHelper;
import com.example.katia.mylocations.R;
import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.dataModel.google.GoogleMapsCredentials;
import com.example.katia.mylocations.dataModel.google.GoogleResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.net.ssl.HttpsURLConnection;

import static com.example.katia.mylocations.PlaceDBHelper.TABLE_NAME_HISTORY;

/**
 * Created by jbt on 12/12/2016.
 */

public class GoogleSearchPlaceTask extends AsyncTask<NameValuePair, Void, GoogleResponse> implements GoogleParamsInterface {
    static String TAG = GoogleSearchPlaceTask.class.getName();
    protected String searchType;
    Context context;
    Intent intent;

    public GoogleSearchPlaceTask(Context context, String searchType) {
        this.searchType = searchType;
        this.context = context;
    }

    public GoogleSearchPlaceTask(Context context, String searchType, Intent intent) {
        this(context, searchType);
        this.intent = intent;
    }

    @Override
    protected GoogleResponse doInBackground(NameValuePair... params) {
        GoogleResponse response = null;
        HttpsURLConnection connection = null;
        BufferedReader reader;
        StringBuilder builder;
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("maps.googleapis.com")
                .path("maps/api/place")
                .appendPath(searchType)
                .appendPath("json")
                .appendQueryParameter(PARAM_NAME_KEY, GoogleMapsCredentials.AUTH_KEY);
        for (NameValuePair pair : params) {
            uriBuilder.appendQueryParameter(pair.getName(), pair.getValue().toString());
        }
        try {
            URL url = new URL(URLDecoder.decode(uriBuilder.build().toString()).replace(" ","%20"));
            Log.d(TAG, url.toString());
            connection = (HttpsURLConnection) url.openConnection();
            if (((int) (connection.getResponseCode() / 100)) != 2) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            builder = new StringBuilder();

            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            Log.d(TAG, builder.toString());
            response = convert(builder.toString());

        } catch (MalformedURLException e) {
            Log.d(TAG, e.getMessage(), e);
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            Log.e(TAG, e.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }

    @Override
    protected void onPostExecute(GoogleResponse googleResponse) {
        if (catchResponseError(googleResponse)) {

        }
        else if (googleResponse.getResults() != null) {
            PlaceDBHelper helper = new PlaceDBHelper(context);
            helper.clearTable(TABLE_NAME_HISTORY);
            helper.bulkHistoryInsert(googleResponse.getResults());
            intent.putExtra(PARAM_NAME_SEARCH_TYPE, searchType);
            intent.putExtra(PARAM_NAME_PAGETOKEN, googleResponse.getNext_page_token());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    protected boolean catchResponseError(GoogleResponse googleResponse) {
        if (googleResponse == null) {
            Toast.makeText(context, context.getResources().getString(R.string.serviceUnavalableLong), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (googleResponse.getStatus() != GoogleResponse.StatusEnum.OK) {
            Toast.makeText(context, context.getResources().getString(R.string.serviceUnavalable)+": "+googleResponse.getError_message(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Response status: " + googleResponse.getStatus().toString());
            Log.e(TAG, "Response message: " + googleResponse.getError_message());
            return true;
        }
        return false;
    }

    protected GoogleResponse convert(String response) {
        Gson gson = new GsonBuilder().disableInnerClassSerialization().setPrettyPrinting().create();
        return gson.fromJson(response, GoogleResponse.class);
    }
}
