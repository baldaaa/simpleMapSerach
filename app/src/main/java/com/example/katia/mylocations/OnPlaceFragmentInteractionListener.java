package com.example.katia.mylocations;

import com.example.katia.mylocations.dataModel.google.GooglePlace;

/**
 * Created by katia on 10/12/2016.
 */
public interface OnPlaceFragmentInteractionListener {
    void onPlaceItemClickedFragmentInteraction(GooglePlace item);
    void onAddPlaceToFavoritesFragmentInteraction(GooglePlace item);
}
