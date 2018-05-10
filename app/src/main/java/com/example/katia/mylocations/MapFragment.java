package com.example.katia.mylocations;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v7.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.tasks.GoogleGetPlaceDetailsTask;
import com.example.katia.mylocations.utils.MyLocationUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Date;

import static com.example.katia.mylocations.LocationApplication.DEFAULT_LAT;
import static com.example.katia.mylocations.LocationApplication.DEFAULT_LNG;
import static com.example.katia.mylocations.LocationApplication.SP_PARAM_NAME_LAT;
import static com.example.katia.mylocations.LocationApplication.SP_PARAM_NAME_LNG;
import static com.example.katia.mylocations.SettingsFragment.SP_PARAM_NAME_DISTANCE_MEASURE;
import static com.example.katia.mylocations.tasks.GoogleParamsInterface.PARAM_NAME_PLACEID;
import static com.example.katia.mylocations.utils.LocationAnimationUtils.setClickImageScaleAnimation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static int SHOW_HIDE_FULL = 0;
    private static int SHOW_HIDE_DETAILS_IMAGES = 1;
    private GoogleMap mMap;
    private GooglePlace place;
    private LinearLayout imageLayout;
    private LinearLayout controlLayout;
    private LinearLayout detailsLayout;
    private RelativeLayout stepsLayout;
    private OnPlaceFragmentInteractionListener mListener;
    LayoutInflater inflater;
    private SharedPreferences sp;
    View rootView;

    boolean isLeft = true;
    RatingBar ratingBar;


    public MapFragment() {
        // Required empty public constructor
    }

    private static MapFragment instance = null;

    public static MapFragment getInstance() {
        if (instance == null)
            instance = new MapFragment();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        rootView = view;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        view.setDrawingCacheEnabled(true);

        place = getActivity().getIntent().getParcelableExtra(LocationsActivity.INTENT_EXTRA_PARAM_NAME_PLACE);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        view.findViewById(R.id.btnAddToFavorites).setOnClickListener(this);
        view.findViewById(R.id.btnShare).setOnClickListener(this);
        view.findViewById(R.id.btnFun).setOnClickListener(this);
        imageLayout = (LinearLayout) view.findViewById(R.id.imageLayout);
        controlLayout = (LinearLayout) view.findViewById(R.id.funkLayout);
        detailsLayout = (LinearLayout) view.findViewById(R.id.dataLayout);
        stepsLayout = (RelativeLayout) view.findViewById(R.id.stepsLayout);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        //change rating bar stars color
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        if (place != null)
            getAdditionalPlaceData();
        if (place == null) {
            detailsLayout.setVisibility(View.GONE);
            controlLayout.setVisibility(View.GONE);
            imageLayout.setVisibility(View.GONE);
        }
        //prints step images on touch
        stepsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ImageView step = new ImageView(getContext());
                    step.setTranslationX(event.getX());
                    step.setTranslationY(event.getY());
                    step.setMaxHeight(70);
                    if (isLeft) {
                        step.setImageResource(R.drawable.leftstep);
                    } else {
                        step.setImageResource(R.drawable.rightstep);
                    }
                    isLeft = !isLeft;
                    stepsLayout.addView(step);
                }

                return false;
            }
        });
        //block click for this view
        stepsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;
    }

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        if (place != null) {
            updateCameraMarker();
        }

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    boolean isSteps = true;

    @Override
    public void onClick(View view) {
        sp.edit().putFloat(SP_PARAM_NAME_LAT, (float) getMyCurrentLocation().latitude).putFloat(SP_PARAM_NAME_LNG, (float) getMyCurrentLocation().longitude).apply();
        setClickImageScaleAnimation(view);
        switch (view.getId()) {

            case R.id.btnAddToFavorites:
                mListener.onAddPlaceToFavoritesFragmentInteraction(place);
                break;

            case R.id.btnShare:
                shareScreenshot();
                break;

            case R.id.btnFun:
                if (isSteps) {
                    showBoundedLocations();
                    mMap.addMarker(makeMyLocationMarkerOptions());
                    hideDetails(SHOW_HIDE_DETAILS_IMAGES);
                    stepsLayout.addView(createDistanceView());
                    stepsLayout.setVisibility(View.VISIBLE);
                } else {
                    stepsLayout.removeAllViews();
                    stepsLayout.setVisibility(View.GONE);
                    showDetails(SHOW_HIDE_DETAILS_IMAGES);
                }
                isSteps = !isSteps;
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    boolean isVisible = true;

    @Override
    public void onMapClick(LatLng latLng) {

        if (isVisible) {
            hideDetails(SHOW_HIDE_FULL);
        } else {
            showDetails(SHOW_HIDE_FULL);
        }
        isVisible = !isVisible;
    }

    public void upatePlace(GooglePlace gPlace) {
        this.place = gPlace;
        imageLayout.clearAnimation();
        imageLayout.removeAllViews();
        mMap.clear();

        updateCameraMarker();
        getAdditionalPlaceData();
        showDetails(SHOW_HIDE_FULL);
    }
    DecimalFormat df = new DecimalFormat("#.###");
    private String getDistance(){
        return df.format(
                MyLocationUtils.distance(place.getGeometry().getLocation().getLat(),
                        place.getGeometry().getLocation().getLng(),
                        getMyCurrentLocation().latitude,getMyCurrentLocation().longitude,
                        sp.getString(SP_PARAM_NAME_DISTANCE_MEASURE, getContext().getResources().getString(R.string.km))));
    }

    private void updateCameraMarker() {
        mMap.addMarker(makeMarkerOptions(place));
        mMap.animateCamera(setCameraUpdate(place));
    }

    private View createDistanceView(){

        TextView dist = new TextView(getContext());
        dist.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
        dist.setTextColor(getContext().getResources().getColor(R.color.cast_expanded_controller_progress_text_color));
        dist.setTextSize(18f);
        dist.setPadding(10,10,10,10);
        dist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
        dist.setText(getDistance()+" "+sp.getString(SP_PARAM_NAME_DISTANCE_MEASURE, getContext().getResources().getString(R.string.km)));
        return dist;
    }

    private MarkerOptions makeMarkerOptions(GooglePlace place) {
        return new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.redmarker, 45, 80))).
                position(new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng())).
                title(place.getName());
    }

    private MarkerOptions makeMyLocationMarkerOptions() {
            return new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.person, 30, 70))).
                    position(getMyCurrentLocation()).
                    title("Me");
    }
    public Bitmap resizeMapIcons(int iconRef, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), iconRef);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void showBoundedLocations() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(getMyCurrentLocation());
        builder.include(new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng()));
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
        mMap.animateCamera(cu);
    }

    private LatLng getMyCurrentLocation() {
        if (mMap.getMyLocation() != null)
            return new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());

        double lat = sp.getFloat(SP_PARAM_NAME_LAT, DEFAULT_LAT);
        double lng = sp.getFloat(SP_PARAM_NAME_LNG, DEFAULT_LNG);
        return new LatLng(lat, lng);
    }

    private CameraUpdate setCameraUpdate(GooglePlace place) {
        return CameraUpdateFactory.newLatLngZoom(
                new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng()), 16);

    }

    private void getAdditionalPlaceData() {
        new GoogleGetPlaceDetailsTask(getContext(), detailsLayout, imageLayout, place)
                .execute(new NameValuePair(PARAM_NAME_PLACEID, place.getPlace_id()));
    }

    private void hideDetails(final int option) {
        ObjectAnimator right = ObjectAnimator.ofFloat(detailsLayout, "translationY", -1000);
        ObjectAnimator right2 = ObjectAnimator.ofFloat(imageLayout, "translationX", 800);
        ObjectAnimator top = ObjectAnimator.ofFloat(controlLayout, "translationX", 200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        if (SHOW_HIDE_DETAILS_IMAGES == option)
            animatorSet.playTogether(right, right2);
        if (SHOW_HIDE_FULL == option)
            animatorSet.playTogether(right, right2, top);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                detailsLayout.setVisibility(View.GONE);
                imageLayout.setVisibility(View.GONE);
                if (SHOW_HIDE_FULL == option)
                    controlLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();

    }

    private void showDetails(final int option) {
        ObjectAnimator right = ObjectAnimator.ofFloat(detailsLayout, "translationY", 0);
        ObjectAnimator right2 = ObjectAnimator.ofFloat(imageLayout, "translationX", 0);
        ObjectAnimator top = ObjectAnimator.ofFloat(controlLayout, "translationX", 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        if (SHOW_HIDE_DETAILS_IMAGES == option)
            animatorSet.playTogether(right, right2);
        if (SHOW_HIDE_FULL == option)
            animatorSet.playTogether(right, right2, top);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                detailsLayout.setVisibility(View.VISIBLE);
                imageLayout.setVisibility(View.VISIBLE);
                if (SHOW_HIDE_FULL == option)
                    controlLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    public void shareScreenshot() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                Date now = new Date();
                DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                rootView.setDrawingCacheEnabled(true);
                Bitmap backBitmap = rootView.getDrawingCache();
                Bitmap bmOverlay = Bitmap.createBitmap(
                        backBitmap.getWidth(), backBitmap.getHeight(),
                        backBitmap.getConfig());
                Canvas canvas = new Canvas(bmOverlay);
                canvas.drawBitmap(snapshot, new Matrix(), null);
                canvas.drawBitmap(backBitmap, 0, 0, null);
                String imagePath = MyLocationUtils.saveToInternalStorage
                        (bmOverlay, getContext().getExternalCacheDir(), now + ".jpeg");

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = new Uri.Builder().path(imagePath).build();
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.shareLocation)));
            }
        };
        mMap.snapshot(callback);
    }
}
