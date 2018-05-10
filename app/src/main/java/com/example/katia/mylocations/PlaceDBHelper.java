package com.example.katia.mylocations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.katia.mylocations.dataModel.google.GoogleGeo;
import com.example.katia.mylocations.dataModel.google.GoogleLocation;
import com.example.katia.mylocations.dataModel.google.GooglePhoto;
import com.example.katia.mylocations.dataModel.google.GooglePlace;

import java.util.ArrayList;

/**
 * Created by katia on 10/12/2016.
 *
 */

public class PlaceDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_places.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_FAVORITES = "favirites";
    public static final String TABLE_NAME_HISTORY = "history";
    public static final String TABLE_NAME_PHOTOS = "photos";
    public static final String TABLE_NAME_HISTORY_PHOTOS = "photoshistory";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_VICINITY = "vicinity";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_ICON_REF = "icon";
    private static final String COLUMN_INTERNAL_ICON_REF = "icon_ref";
    private static final String COLUMN_REFERENCE = "reference";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LON = "lon";
    private static final String COLUMN_PLACE_ID = "placeid";
    private static final String COLUMN_IMAGEREF = "imageref";
    private static final String COLUMN_IMAGEWIDTH = "imagewidth";
    private static final String COLUMN_IMAGEHIGH = "imagehigh";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s ( %s TEXT PRIMARY KEY , %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s REAL)";
    private static final String CREATE_IMAGE_TABLE = "CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER)";


    public PlaceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format(CREATE_TABLE, new String[]{TABLE_NAME_FAVORITES, COLUMN_ID, COLUMN_NAME, COLUMN_PHONE,
                COLUMN_VICINITY, COLUMN_ADDRESS, COLUMN_ICON_REF, COLUMN_INTERNAL_ICON_REF, COLUMN_REFERENCE, COLUMN_CATEGORY, COLUMN_RATING,
                COLUMN_LAT, COLUMN_LON}));
        sqLiteDatabase.execSQL(String.format(CREATE_TABLE, new String[]{TABLE_NAME_HISTORY, COLUMN_ID, COLUMN_NAME, COLUMN_PHONE,
                COLUMN_VICINITY, COLUMN_ADDRESS, COLUMN_ICON_REF, COLUMN_INTERNAL_ICON_REF, COLUMN_REFERENCE, COLUMN_CATEGORY, COLUMN_RATING,
                COLUMN_LAT, COLUMN_LON}));
        sqLiteDatabase.execSQL(String.format(CREATE_IMAGE_TABLE, new String[]{TABLE_NAME_PHOTOS, COLUMN_ID, COLUMN_PLACE_ID, COLUMN_IMAGEREF,
                COLUMN_IMAGEHIGH, COLUMN_IMAGEWIDTH}));
        sqLiteDatabase.execSQL(String.format(CREATE_IMAGE_TABLE, new String[]{TABLE_NAME_HISTORY_PHOTOS, COLUMN_ID, COLUMN_PLACE_ID, COLUMN_IMAGEREF,
                COLUMN_IMAGEHIGH, COLUMN_IMAGEWIDTH}));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<GooglePlace> getPlaces(String tableName){
        ArrayList<GooglePlace> places = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(tableName, null, null, null, null, null, null);
        while(c.moveToNext()){
            String id = c.getString( c.getColumnIndex(COLUMN_ID) );
            String name = c.getString(c.getColumnIndex(COLUMN_NAME));
            String phone = c.getString(c.getColumnIndex(COLUMN_PHONE));
            String place_id = c.getString(c.getColumnIndex(COLUMN_REFERENCE));
            String address = c.getString(c.getColumnIndex(COLUMN_ADDRESS));
            String category = c.getString(c.getColumnIndex(COLUMN_CATEGORY));
            ArrayList<GooglePhoto> photos = new ArrayList<>();

            Cursor cphotos;
            if(TABLE_NAME_FAVORITES.equals(tableName))
                cphotos = db.rawQuery("SELECT * FROM "+ TABLE_NAME_PHOTOS+" WHERE "+COLUMN_PLACE_ID + " = '" + id+"'", null);
            else
                cphotos = db.rawQuery("SELECT * FROM "+ TABLE_NAME_HISTORY_PHOTOS+" WHERE "+COLUMN_PLACE_ID + " = '" + id+"'", null);
            while(cphotos.moveToNext()){
                GooglePhoto photo = new GooglePhoto(cphotos.getString(cphotos.getColumnIndex(COLUMN_IMAGEREF)));
                photo.setHeight(cphotos.getInt(cphotos.getColumnIndex(COLUMN_IMAGEHIGH)));
                photo.setWidth(cphotos.getInt(cphotos.getColumnIndex(COLUMN_IMAGEWIDTH)));
                photos.add(photo);
            }

            double lat = c.getFloat(c.getColumnIndex(COLUMN_LAT));
            double lon = c.getFloat(c.getColumnIndex(COLUMN_LON));
            float rating = c.getFloat(c.getColumnIndex(COLUMN_RATING));
            String icon = c.getString(c.getColumnIndex(COLUMN_ICON_REF));
            String iconRef = c.getString(c.getColumnIndex(COLUMN_INTERNAL_ICON_REF));
            String vicinity = c.getString(c.getColumnIndex(COLUMN_VICINITY));
            GooglePlace p = new GooglePlace();
            p.setId(id);
            p.setFormatted_address(address);
            p.setName(name);
            p.setInternational_phone_number(phone);
            p.setPlace_id(place_id);
            p.setRating(rating);
            p.setIcon(icon);
            p.setIconRef(iconRef);
            p.setVicinity(vicinity);
            p.typesFromString(category);
            p.setPhotos(photos);
            p.setGeometry(new GoogleGeo(new GoogleLocation(lat,lon)));
            places.add(p);
        }
        db.close();
        return places;
    }

    public ArrayList<GooglePlace> getFavoritePlaces(){
        return getPlaces(TABLE_NAME_FAVORITES);

    }
    public ArrayList<GooglePlace> getHistoryPlaces(){
        return getPlaces(TABLE_NAME_HISTORY);

    }

    public void saveHistory(ArrayList<GooglePlace> places){
        SQLiteDatabase db = getWritableDatabase();
        for (GooglePlace place:places) {
            db.insert(TABLE_NAME_HISTORY, null, mapFavorite(place));
            if(place.getPhotos()!=null) {
                for (GooglePhoto photo : place.getPhotos()) {
                    db.insert(TABLE_NAME_PHOTOS, null, mapPhoto(place.getId(), photo));
                }
            }
        }
        db.close();
    }

    public void insertFavorite(GooglePlace place){

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME_FAVORITES, null, mapFavorite(place));
        if(place.getPhotos()!=null) {
            for (GooglePhoto photo : place.getPhotos())
                db.insert(TABLE_NAME_PHOTOS, null, mapPhoto(place.getId(), photo));
        }
        db.close();
    }
    public void updateIconPath(String newPath, String placeId){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INTERNAL_ICON_REF, newPath);
        db.update(TABLE_NAME_FAVORITES, values, COLUMN_ID+" = '"+placeId+"'",null);
        db.close();
    }

    public ContentValues mapFavorite(GooglePlace place){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, place.getId());
        values.put(COLUMN_NAME, place.getName());
        values.put(COLUMN_PHONE, place.getInternational_phone_number());
        values.put(COLUMN_VICINITY, place.getVicinity());
        values.put(COLUMN_ICON_REF,place.getIcon());
        values.put(COLUMN_INTERNAL_ICON_REF,place.getIconRef());
        values.put(COLUMN_REFERENCE, place.getPlace_id());
        values.put(COLUMN_ADDRESS, place.getFormatted_address());
        values.put(COLUMN_CATEGORY, place.typesToString());
        values.put(COLUMN_RATING,place.getRating());
        values.put(COLUMN_LAT, place.getGeometry().getLocation().getLat());
        values.put(COLUMN_LON, place.getGeometry().getLocation().getLng());
        return values;
    }

    public ContentValues mapPhoto(String placeId, GooglePhoto photo){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_ID, placeId);
        values.put(COLUMN_IMAGEREF, photo.getPhoto_reference());
        values.put(COLUMN_IMAGEWIDTH, photo.getWidth());
        values.put(COLUMN_IMAGEHIGH,photo.getHeight());

        return values;
    }


    public void clearTable(String tableName){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, null, null);
        if(TABLE_NAME_FAVORITES.equals(tableName))
            db.delete(TABLE_NAME_PHOTOS, null, null);
        else
            db.delete(TABLE_NAME_HISTORY_PHOTOS, null, null);
        db.close();
    }

    public void deleteFavoritePlace( String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_FAVORITES, COLUMN_ID + "='" + id+"'", null);
            db.delete(TABLE_NAME_PHOTOS, COLUMN_PLACE_ID + "='" + id+"'", null);
        db.close();

    }

    public void bulkHistoryInsert(GooglePlace[] results) {
        SQLiteDatabase db = getWritableDatabase();
        for(GooglePlace place: results) {
            db.insert(TABLE_NAME_HISTORY, null, mapFavorite(place));
            if (place.getPhotos() != null) {
                for (GooglePhoto photo : place.getPhotos())
                    db.insert(TABLE_NAME_HISTORY_PHOTOS, null, mapPhoto(place.getId(), photo));
            }
        }
        db.close();
    }
}
