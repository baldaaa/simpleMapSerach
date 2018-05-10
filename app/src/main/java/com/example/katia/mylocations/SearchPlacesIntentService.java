package com.example.katia.mylocations;

import android.app.IntentService;
import android.content.Intent;


import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.tasks.GoogleSearchPlaceTask;

import java.util.ArrayList;


public class SearchPlacesIntentService extends IntentService {
    public static final String ACTION_SEARCH = "com.example.katia.mylocations.action.SEARCH";
    public static final String EXTRA_PARAM = "com.example.katia.mylocations.extra.PARAM";

    public SearchPlacesIntentService() {
        super("SearchPlacesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
                final ArrayList<NameValuePair> params = intent.getParcelableArrayListExtra(EXTRA_PARAM);
                final String  searchType = intent.getStringExtra(GoogleSearchPlaceTask.PARAM_NAME_SEARCH_TYPE);
                handleActionSearch(searchType, params);
        }
    }


    private void handleActionSearch(String searchType,ArrayList<NameValuePair> params) {
        Intent intentToBroadcast = new Intent(ACTION_SEARCH);
        //exetute task, that search and save search results to DB
            new GoogleSearchPlaceTask(this,searchType,intentToBroadcast)
                    .execute(params.toArray(new NameValuePair[params.size()]));
    }
}
