package com.example.katia.mylocations;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.tasks.GetPlacesFromDBTask;

import static com.example.katia.mylocations.PlaceDBHelper.TABLE_NAME_FAVORITES;


public class FavoritesFragment extends Fragment{


    private OnPlaceFragmentInteractionListener mListener;
    private PlaceRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;
    private PlaceDBHelper helper;
    SharedPreferences sp;


    public FavoritesFragment() {
    }
    private static FavoritesFragment instance = null;
    public static FavoritesFragment getInstance(){
        if(instance == null)
            instance = new FavoritesFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        context = view.getContext();
        helper = new PlaceDBHelper(context);

        sp = PreferenceManager.getDefaultSharedPreferences(context);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PlaceRecyclerViewAdapter(context,mListener, sp);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                adapter.new MyItemTouchHelper(recyclerView,helper);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaceFragmentInteractionListener) {
            mListener = (OnPlaceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    void updateList() {
        new GetPlacesFromDBTask(context,adapter).execute(new String[]{TABLE_NAME_FAVORITES});
    }
    void addPlace(GooglePlace place){
        adapter.addItem(place);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
