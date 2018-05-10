package com.example.katia.mylocations;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class SettingsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String SP_PARAM_NAME_DISTANCE_MEASURE = "distance_measure";

    SharedPreferences sp;
    private Spinner spinnerDist;
    private OnPlaceFragmentInteractionListener mListener;

    public SettingsFragment() {
    }
    private static SettingsFragment instance = null;
    public static SettingsFragment getInstance(){
        if(instance == null)
            instance = new SettingsFragment();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.findViewById(R.id.btnDeleteHistory).setOnClickListener(this);
        view.findViewById(R.id.btnDeleteFavorites).setOnClickListener(this);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        spinnerDist = (Spinner) view.findViewById(R.id.spinnerKmOrMiles);

        ArrayAdapter<CharSequence> adapterSpSet = ArrayAdapter.createFromResource(view.getContext(),
                R.array.distanceMeasures, R.layout.mesure_spinner_item);
        adapterSpSet.setDropDownViewResource(R.layout.mesure_spinner_item);
        spinnerDist.setAdapter(adapterSpSet);
        spinnerDist.setOnItemSelectedListener(this);
        String measure = sp.getString(SP_PARAM_NAME_DISTANCE_MEASURE,getContext().getResources().getString(R.string.km));

        int spinnerPosition = adapterSpSet.getPosition(measure);

        spinnerDist.setSelection(spinnerPosition);
        return view;
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
    public void onClick(View view) {

        final String table;
        switch (view.getId()){
            case R.id.btnDeleteFavorites:
                table = PlaceDBHelper.TABLE_NAME_FAVORITES;
                break;
            case R.id.btnDeleteHistory:
                table = PlaceDBHelper.TABLE_NAME_HISTORY;
                break;
            default:
                table = null;

        }
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.continue_delete)
                .setCancelable(true)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PlaceDBHelper(getContext()).clearTable(table);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO NOTHING
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String meg = sp.getString(SP_PARAM_NAME_DISTANCE_MEASURE, getContext().getResources().getString(R.string.km));
        sp.edit().putString(SP_PARAM_NAME_DISTANCE_MEASURE,adapterView.getItemAtPosition(i).toString()).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        sp.edit().putString(SP_PARAM_NAME_DISTANCE_MEASURE,getContext().getResources().getString(R.string.km)).apply();

    }
}
