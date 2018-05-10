package com.example.katia.mylocations.tasks;

import android.content.Context;

import com.example.katia.mylocations.PlaceRecyclerViewAdapter;
import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.dataModel.google.GoogleResponse;

import java.util.Arrays;

/**
 * Created by katia on 18/12/2016.
 */

public class GoogleGetNextPagePlaceTask extends GoogleSearchPlaceTask {
    PlaceRecyclerViewAdapter adapter;

    public GoogleGetNextPagePlaceTask(Context context, String searchType,PlaceRecyclerViewAdapter adapter) {
        super(context, searchType);
        this.adapter = adapter;

    }
    protected GoogleResponse doInBackground(NameValuePair... params) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return super.doInBackground(params);
    }
    @Override
    protected void onPostExecute(GoogleResponse googleResponse) {
        if(catchResponseError(googleResponse))
            return;
        adapter.addItems(Arrays.asList(googleResponse.getResults()));
        if(googleResponse.getNext_page_token()!= null)
            new GoogleGetNextPagePlaceTask(context,searchType,adapter)
                    .execute(new NameValuePair(PARAM_NAME_PAGETOKEN,googleResponse.getNext_page_token()));
    }
}
