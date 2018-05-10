package com.example.katia.mylocations;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.katia.mylocations.dataModel.google.GoogleLocation;
import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.utils.MyLocationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.katia.mylocations.LocationApplication.DEFAULT_LAT;
import static com.example.katia.mylocations.LocationApplication.DEFAULT_LNG;
import static com.example.katia.mylocations.SettingsFragment.SP_PARAM_NAME_DISTANCE_MEASURE;
import static com.example.katia.mylocations.utils.LocationAnimationUtils.setClickImageAlphaAnimation;

public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {

    static String TAG = PlaceRecyclerViewAdapter.class.getName();

    private final List<GooglePlace> mValues;
    private final OnPlaceFragmentInteractionListener mListener;
    private SharedPreferences sp;
    DecimalFormat df = new DecimalFormat("#.##");
    View lastClickedView;
    Context context;


    public PlaceRecyclerViewAdapter(Context context, OnPlaceFragmentInteractionListener listener, SharedPreferences sp) {
        this.context = context;
        mListener = listener;
        this.sp = sp;
        mValues = new ArrayList<GooglePlace>();

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_place, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        //set Title
        holder.titleView.setText(holder.mItem.getName());

        //set Content
        if (holder.mItem.getVicinity() != null)
            holder.contentView.setText(holder.mItem.getVicinity());
        else
            holder.contentView.setText(holder.mItem.getFormatted_address());

        //set phone number (favorites only)
        if (holder.mItem.getInternational_phone_number() != null)
            holder.phoneList.setText(holder.mItem.getInternational_phone_number());
        else
            holder.phoneList.setVisibility(View.GONE);

        //calculate distance
        GoogleLocation location = holder.mItem.getGeometry().getLocation();
        String unit = sp.getString(SP_PARAM_NAME_DISTANCE_MEASURE, context.getResources().getString(R.string.km));

        holder.distanceView.setText(df.format(
                MyLocationUtils.distance(location.getLat(), location.getLng(),
                        sp.getFloat(LocationApplication.SP_PARAM_NAME_LAT, DEFAULT_LAT),
                        sp.getFloat(LocationApplication.SP_PARAM_NAME_LNG, DEFAULT_LNG), unit)) + " \n" + unit);

        //set icon
        if (holder.mItem.getIconRef() != null)
            holder.imageView.setImageBitmap(loadImageFromStorage(holder.mItem.getIconRef()));
        else
            holder.imageView.setImageResource(R.drawable.bluemarker);

        //set on item click listener and pass item data to activity
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onPlaceItemClickedFragmentInteraction(holder.mItem);
                    if (holder.mItem.getIconRef() == null) {
                        //change icon for clicked item(searches only)
                        if(lastClickedView!=null)
                            setClickImageAlphaAnimation((ImageView) lastClickedView.findViewById(R.id.iconList), R.drawable.bluemarker);
                        setClickImageAlphaAnimation((ImageView) v.findViewById(R.id.iconList), R.drawable.redmarker);
                    }
                }
                setLastClicked(v);
            }
        });
    }

    private Bitmap loadImageFromStorage(String path) {

        try {
            File f = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            Log.d(TAG, e.getMessage(), e);
        }
        return null;
    }

    public ArrayList<GooglePlace> getValues(){
        return (ArrayList<GooglePlace>) mValues;
    }

    private void setLastClicked(View view){ lastClickedView = view; }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageView;
        public final TextView titleView;
        public final TextView contentView;
        public final TextView phoneList;
        public final TextView distanceView;
        public final LinearLayout itemCard;
        public GooglePlace mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = (ImageView) view.findViewById(R.id.iconList);
            titleView = (TextView) view.findViewById(R.id.titleList);
            contentView = (TextView) view.findViewById(R.id.contentList);
            phoneList = (TextView) view.findViewById(R.id.phoneList);
            distanceView = (TextView) view.findViewById(R.id.distanceList);
            itemCard = (LinearLayout) view.findViewById(R.id.itemCard);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + contentView.getText() + "'";
        }
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<GooglePlace> places) {
        mValues.addAll(places);
        notifyDataSetChanged();
    }
    public void addItem(GooglePlace place) {
        mValues.add(place);
        notifyDataSetChanged();
    }
    //implement delete item on swiped
    public class MyItemTouchHelper extends ItemTouchHelper.SimpleCallback {
        RecyclerView recyclerView;
        Snackbar snackbar;
        PlaceDBHelper helper;

        public MyItemTouchHelper(RecyclerView recyclerView, PlaceDBHelper helper) {
            super(0,ItemTouchHelper.RIGHT);
            this.recyclerView = recyclerView;
            this.helper = helper;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            final int adapterPosition = viewHolder.getAdapterPosition();
            final GooglePlace placeItem = mValues.get(adapterPosition);
            snackbar = Snackbar
                    .make(recyclerView, placeItem.getName()+" "+context.getResources().getString(R.string.removed), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mValues.add(adapterPosition, placeItem);
                            notifyItemInserted(adapterPosition);
                            recyclerView.scrollToPosition(adapterPosition);
                        }
                    })
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            Log.d(TAG, "SnackBar dismissed");
                            if (event != DISMISS_EVENT_ACTION) {
                                Log.d(TAG, "SnackBar not dismissed by click event");

                                helper.deleteFavoritePlace(placeItem.getId());
                                MyLocationUtils.deleteFile(placeItem.getIconRef());
                            }
                        }
                    });
            snackbar.show();
            mValues.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
        }
    }
}
