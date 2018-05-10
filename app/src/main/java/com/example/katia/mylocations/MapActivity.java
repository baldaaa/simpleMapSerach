package com.example.katia.mylocations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.tasks.DownloadAndSaveImageTask;

public class MapActivity extends AppCompatActivity implements OnPlaceFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onPlaceItemClickedFragmentInteraction(GooglePlace item) {

    }

    @Override
    public void onAddPlaceToFavoritesFragmentInteraction(GooglePlace item) {
        saveLocation(item);
        Toast t = Toast.makeText(this,item.getName() + "\n"+getResources().getString(R.string.addedToFavorites), Toast.LENGTH_SHORT);
        t.show();
    }

    private void saveLocation(GooglePlace place) {
        PlaceDBHelper helper = new PlaceDBHelper(this);
        helper.insertFavorite(place);
        new DownloadAndSaveImageTask(this, place, helper).execute(place.getIcon());
    }
}
