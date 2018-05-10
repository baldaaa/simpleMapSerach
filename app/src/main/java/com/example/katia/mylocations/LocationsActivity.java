package com.example.katia.mylocations;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.tasks.DownloadAndSaveImageTask;

import java.util.concurrent.ConcurrentHashMap;

public class LocationsActivity extends AppCompatActivity implements OnPlaceFragmentInteractionListener {

    private final static int FAVOR_FRAG_POSITION = 0;
    private final static int SEARCH_FRAG_POSITION = 1;
    private final static int SETTINGS_FRAG_POSITION = 2;
    public static String INTENT_EXTRA_PARAM_NAME_PLACE = "place";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(FAVOR_FRAG_POSITION).setIcon(R.drawable.favor_menu);
        tabLayout.getTabAt(SEARCH_FRAG_POSITION).setIcon(android.R.drawable.ic_menu_search);
        tabLayout.getTabAt(SETTINGS_FRAG_POSITION).setIcon(android.R.drawable.ic_menu_preferences);
    }

    @Override
    public void onPlaceItemClickedFragmentInteraction(GooglePlace item) {
        //different process for one or dual panel device
        if(getResources().getString(R.string.is_tablet).equals("false")) {
            Intent in = new Intent(this, MapActivity.class);
            in.putExtra(INTENT_EXTRA_PARAM_NAME_PLACE, item);
            startActivity(in);
        }else{
            MapFragment frag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
            frag.upatePlace(item);
        }
    }

    @Override
    public void onAddPlaceToFavoritesFragmentInteraction(GooglePlace item) {
        saveLocation(item);
        mSectionsPagerAdapter.getFavorFragment().addPlace(item);
        Toast.makeText(this,item.getName() + "\n"+getResources().getString(R.string.addedToFavorites), Toast.LENGTH_SHORT).show();
    }

    private void saveLocation(GooglePlace place) {
        PlaceDBHelper helper = new PlaceDBHelper(this);
        helper.insertFavorite(place);
        new DownloadAndSaveImageTask(this, place, helper).execute(place.getIcon());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * additional implementation for HashMap, that contains active fragments to retreive
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ConcurrentHashMap<Integer, Fragment> fragReferenceMap = new ConcurrentHashMap<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case FAVOR_FRAG_POSITION:
                    FavoritesFragment favorF = FavoritesFragment.getInstance();
                    fragReferenceMap.put(FAVOR_FRAG_POSITION, favorF);
                    return favorF;

                case SEARCH_FRAG_POSITION:
                    Fragment searchF = SearchFragment.getInstance();
                    fragReferenceMap.put(SEARCH_FRAG_POSITION, searchF);
                    return searchF;
                case SETTINGS_FRAG_POSITION:
                    Fragment settingsF = SettingsFragment.getInstance();
                    fragReferenceMap.put(SETTINGS_FRAG_POSITION, settingsF);
                    return settingsF;

                default:
                    return null;
            }
        }
        public FavoritesFragment getFavorFragment() {
            return (FavoritesFragment)fragReferenceMap.get(FAVOR_FRAG_POSITION);
        }
        public SearchFragment getSearchFragment() {
            return (SearchFragment)fragReferenceMap.get(SEARCH_FRAG_POSITION);
        }
        public SettingsFragment getSettingsFragment() {
            return (SettingsFragment)fragReferenceMap.get(SETTINGS_FRAG_POSITION);
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment frag = (Fragment)super.instantiateItem(container, position);
            fragReferenceMap.put(position, frag);
            return frag;

        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            fragReferenceMap.remove(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case FAVOR_FRAG_POSITION:
                    return getResources().getString(R.string.favorites);
                case SEARCH_FRAG_POSITION:
                    return getResources().getString(R.string.searches);
                case SETTINGS_FRAG_POSITION:
                    return getResources().getString(R.string.settings);
            }
            return null;
        }
    }
}
