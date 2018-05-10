package com.example.katia.mylocations.tasks;



import android.content.Context;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.katia.mylocations.R;
import com.example.katia.mylocations.dataModel.NameValuePair;
import com.example.katia.mylocations.dataModel.google.GooglePhoto;
import com.example.katia.mylocations.dataModel.google.GooglePlace;
import com.example.katia.mylocations.dataModel.google.GoogleResponse;
import com.example.katia.mylocations.utils.LocationAnimationUtils;

import java.util.ArrayList;


/**
 * Created by katia on 16/12/2016.
 */

public class GoogleGetPlaceDetailsTask extends GoogleSearchPlaceTask {
    ViewGroup details;
    ViewGroup images;
    TextView textTitle;
    TextView textVinicity;
    TextView textAddress;
    TextView textInternationalPhone;
    TextView textPhone;
    TextView textHours;
    RatingBar ratingBar;
    GooglePlace place;

    public GoogleGetPlaceDetailsTask(Context context, ViewGroup details, ViewGroup images, GooglePlace place) {
        super(context, SEARCH_TYPE_DETAILS);
        this.details = details;
        this.images = images;
        this.place = place;
        textTitle = (TextView) details.findViewById(R.id.textTitle);
        textVinicity = (TextView) details.findViewById(R.id.textVinicity);
        textAddress = (TextView) details.findViewById(R.id.textAddress);
        textInternationalPhone = (TextView) details.findViewById(R.id.textInternationalPhone);
        textPhone = (TextView) details.findViewById(R.id.textPhone);
        textHours = (TextView) details.findViewById(R.id.textHours);
        ratingBar = (RatingBar) details.findViewById(R.id.ratingBar);
    }

    @Override
    protected void onPostExecute(GoogleResponse googleResponse) {
        if (catchResponseError(googleResponse))
            return;
        fillDetails(googleResponse);
        updatePlaceData(googleResponse.getResult());
        addPhotos(googleResponse);

    }

    protected void fillDetails(GoogleResponse googleResponse) {
        textTitle.setText(googleResponse.getResult().getName());
        textVinicity.setText(googleResponse.getResult().getVicinity());
        textAddress.setText(googleResponse.getResult().getFormatted_address());
        ratingBar.setRating(googleResponse.getResult().getRating());
        textInternationalPhone.setText(googleResponse.getResult().getInternational_phone_number());
        textPhone.setText(googleResponse.getResult().getFormatted_phone_number());

        StringBuilder strBuilder = new StringBuilder();
        if (googleResponse.getResult().getOpening_hours() != null && googleResponse.getResult().getOpening_hours().getWeekday_text() != null) {
            for (String hours : googleResponse.getResult().getOpening_hours().getWeekday_text()) {
                strBuilder.append(hours);
                strBuilder.append("\n");
            }
            String str = strBuilder.toString();
            textHours.setVisibility(View.VISIBLE);
            textHours.setText(str.substring(0, str.lastIndexOf("\n")));
        } else {
            textHours.setText("");
            textHours.setVisibility(View.GONE);
        }
    }
    private void updatePlaceData(GooglePlace newData){
        place.setFormatted_phone_number(newData.getFormatted_phone_number());
        place.setInternational_phone_number(newData.getInternational_phone_number());
    }

    protected void addPhotos(GoogleResponse googleResponse) {
        ArrayList<GooglePhoto> photos = googleResponse.getResult().getPhotos();

        if (photos == null)
            return;

        for (int i = 0; i < photos.size() && i  < 10; i++) {//load place images but not more than 10
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams vp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            vp.gravity = Gravity.BOTTOM;
            imageView.setLayoutParams(vp);


            new GooglePhotoRequestTask(context, imageView).execute(
                    new NameValuePair[]{
                            new NameValuePair(GooglePhotoRequestTask.PARAM_NAME_PHOTOREFERENCE, photos.get(i).getPhoto_reference()),
                            new NameValuePair(GooglePhotoRequestTask.PARAM_NAME_MAXHEIGHT, "300")
                    });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            imageView.setLayoutParams(params);
            imageView.setVisibility(View.GONE);
            images.addView(imageView);
        }
        if (photos.size() >1)//if photos count is 1 animation not needed
            //set slideshow photos animation
            LocationAnimationUtils.setSlideShowAnimation(images);

    }


}
