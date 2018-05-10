package com.example.katia.mylocations;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.tasks.GetPlacesFromDBTask;
import com.example.katia.mylocations.tasks.GoogleGetNextPagePlaceTask;
import com.example.katia.mylocations.tasks.GoogleSearchPlaceTask;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.katia.mylocations.LocationApplication.DEFAULT_LAT;
import static com.example.katia.mylocations.LocationApplication.DEFAULT_LNG;
import static com.example.katia.mylocations.PlaceDBHelper.TABLE_NAME_HISTORY;
import static com.example.katia.mylocations.SearchPlacesIntentService.EXTRA_PARAM;
import static com.example.katia.mylocations.SettingsFragment.SP_PARAM_NAME_DISTANCE_MEASURE;
import static com.example.katia.mylocations.tasks.GoogleParamsInterface.PARAM_NAME_PAGETOKEN;
import static com.example.katia.mylocations.tasks.GoogleParamsInterface.PARAM_NAME_SEARCH_TYPE;
import static com.example.katia.mylocations.utils.LocationAnimationUtils.setClickImageScaleAnimation;


public class SearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

private static String BUNDLE_PARAM_PLACES = "storedPlaces";
    private OnPlaceFragmentInteractionListener mListener;
    private PlaceRecyclerViewAdapter adapter;
    private ImageView searchImage;
    private EditText searchText;
    private RecyclerView recyclerView;
    private Spinner spinnerDist;
    private TextView distMeag;
    private Context context;
    private SharedPreferences sp;
    private String SP_PARAM_NAME_SEARCH_TEXT = "";
    private int distance = -1;
    private PlaceDBHelper helper;
    private ProgressDialog progress;
    ArrayAdapter<CharSequence> adapterSp;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }
    private static SearchFragment instance = null;
    public static SearchFragment getInstance(){
        if(instance == null)
            instance = new SearchFragment();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);

        searchImage = (ImageView) view.findViewById(R.id.imageSearch);
        searchText = (EditText) view.findViewById(R.id.editSearch);
        distMeag = (TextView) view.findViewById(R.id.textMeasure);
        spinnerDist = (Spinner) view.findViewById(R.id.spinnerDistance);

        context = view.getContext();
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        helper = new PlaceDBHelper(context);


        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);

        spinnerDist.setOnItemSelectedListener(this);
        searchImage.setOnClickListener(this);

        searchText.setText(sp.getString(SP_PARAM_NAME_SEARCH_TEXT, ""));
        distMeag.setText(sp.getString(SP_PARAM_NAME_DISTANCE_MEASURE, context.getResources().getString(R.string.km)));


        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState == null)
            //load last searches from DB(only first page of search result stored in DB)
            new GetPlacesFromDBTask(context,adapter).execute(new String[]{TABLE_NAME_HISTORY});
        if(savedInstanceState !=null) {
            //restore last searched, that contains "next pages"
            Parcelable[] parcelableArray = savedInstanceState.getParcelableArray(BUNDLE_PARAM_PLACES);

            GooglePlace[] resultArray = null;
            if (parcelableArray != null) {
                resultArray = Arrays.copyOf(parcelableArray, parcelableArray.length, GooglePlace[].class);
            }
            adapter.addItems(Arrays.asList(resultArray));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaceFragmentInteractionListener) {
            mListener = (OnPlaceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlaceFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // register the receiver
        IntentFilter filter = new IntentFilter(SearchPlacesIntentService.ACTION_SEARCH);
        SearchReceiver receiver = new SearchReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);

        // Set the adapters
        adapterSp = ArrayAdapter.createFromResource(context,
                R.array.distance, R.layout.mesure_spinner_item);
        adapterSp.setDropDownViewResource(R.layout.mesure_spinner_item);

        adapter = new PlaceRecyclerViewAdapter(context,mListener, sp);
        spinnerDist.setAdapter(adapterSp);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        spinnerDist.setAdapter(adapterSp);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageSearch:
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
                setClickImageScaleAnimation(view);

                String keyword = searchText.getText().toString().trim();

                double lat = sp.getFloat(LocationApplication.SP_PARAM_NAME_LAT, DEFAULT_LAT);
                double lng = sp.getFloat(LocationApplication.SP_PARAM_NAME_LNG, DEFAULT_LNG);

                Intent intent = new Intent(context, SearchPlacesIntentService.class);

                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_LOCATION, lat + "," + lng));

                //look for suitable search type according to search criteria(text and radius) specified by user
                if ((keyword != null && !keyword.equals("") && keyword.split(" ").length >1)) {
                    params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_QUERY, keyword.replace(" ", "+")));
                    if (distance != -1)
                        params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_RADIUS, distance * 1000 + ""));

                    intent.putExtra(PARAM_NAME_SEARCH_TYPE, GoogleSearchPlaceTask.SEARCH_TYPE_TEXT);
                    intent.putParcelableArrayListExtra(EXTRA_PARAM, params);

                }else {
                    params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_KEYWORD, keyword));
                    if(keyword!=null && !keyword.equals("") && distance == -1) {
                        params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_RANKBY, GoogleSearchPlaceTask.PARAM_VALUE_RANKBY));
                    }else if(distance == -1){
                        params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_RADIUS, "5000"));
                    }else {
                        params.add(new NameValuePair(GoogleSearchPlaceTask.PARAM_NAME_RADIUS, distance * 1000 + ""));
                    }

                    intent.putExtra(PARAM_NAME_SEARCH_TYPE, GoogleSearchPlaceTask.SEARCH_TYPE_NEARBY);
                    intent.putParcelableArrayListExtra(EXTRA_PARAM, params);

                }
                context.startService(intent);
                progress = new ProgressDialog(context);
                progress.setMessage(context.getResources().getString(R.string.looking_for_places));
                progress.setCancelable(false);
                progress.show();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        sp.edit().putString(SP_PARAM_NAME_SEARCH_TEXT, searchText.getText().toString()).apply();
        //save "next pages", that is not stored in DB
        outState.putParcelableArray(BUNDLE_PARAM_PLACES, adapter.getValues().toArray(new GooglePlace[adapter.getValues().size()]));
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            distance = Integer.parseInt(adapterView.getItemAtPosition(i).toString());
        } catch (NumberFormatException e) {
            distance = -1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
//receiver for service intent
    public class SearchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case SearchPlacesIntentService.ACTION_SEARCH:
                    new GetPlacesFromDBTask(context, adapter, progress).execute(TABLE_NAME_HISTORY);
                    sp.edit().putString(SP_PARAM_NAME_SEARCH_TEXT, searchText.getText().toString()).apply();
                    //retrieves next page of search data if exist
                    if (intent.getStringExtra(PARAM_NAME_PAGETOKEN) != null)
                        new GoogleGetNextPagePlaceTask(context, intent.getStringExtra(PARAM_NAME_SEARCH_TYPE), adapter).execute(
                                new NameValuePair(PARAM_NAME_PAGETOKEN, intent.getStringExtra(PARAM_NAME_PAGETOKEN)));
                    break;
            }
        }
    }
}
