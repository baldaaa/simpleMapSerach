package com.example.katia.mylocations.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.katia.mylocations.PlaceDBHelper;
import com.example.katia.mylocations.PlaceRecyclerViewAdapter;
import com.example.katia.mylocations.dataModel.google.GooglePlace;

import java.util.ArrayList;

import static com.example.katia.mylocations.PlaceDBHelper.TABLE_NAME_FAVORITES;
import static com.example.katia.mylocations.PlaceDBHelper.TABLE_NAME_HISTORY;

/**
 * Created by katia on 13/12/2016.
 */

public class GetPlacesFromDBTask extends AsyncTask<String, Void,ArrayList<GooglePlace>> {
    private PlaceDBHelper helper;
    private PlaceRecyclerViewAdapter adapter;
    Context context;
    ProgressDialog progress;

    public GetPlacesFromDBTask(Context context, PlaceRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        this.context = context;
        helper = new PlaceDBHelper(context);
    }
    public GetPlacesFromDBTask(Context context, PlaceRecyclerViewAdapter adapter, ProgressDialog progress) {
        this.adapter = adapter;
        this.context = context;
        helper = new PlaceDBHelper(context);
        this.progress = progress;
    }
    @Override
    protected ArrayList<GooglePlace> doInBackground(String... strings) {
        if(TABLE_NAME_FAVORITES.equals(strings[0]))
            return helper.getFavoritePlaces();
        else if(TABLE_NAME_HISTORY.equals(strings[0]))
            return helper.getHistoryPlaces();
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<GooglePlace> googlePlaces) {
        adapter.clear();
        adapter.addItems(googlePlaces);
        if(progress != null)
            progress.dismiss();
    }
}

