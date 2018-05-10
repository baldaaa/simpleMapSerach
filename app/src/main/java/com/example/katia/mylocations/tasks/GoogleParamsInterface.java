package com.example.katia.mylocations.tasks;

/**
 * Created by katia on 13/12/2016.
 */

public interface GoogleParamsInterface {
    public static String PARAM_NAME_SEARCH_TYPE = "searchtype";

    public static String PARAM_NAME_LOCATION = "location";
    public static String PARAM_NAME_RANKBY = "rankby";
    public static String PARAM_NAME_TYPES = "types";
    public static String PARAM_NAME_KEY = "key";
    public static String PARAM_NAME_RADIUS = "radius";
    public static String PARAM_NAME_KEYWORD = "keyword";
    public static String PARAM_NAME_QUERY = "query";
    public static String PARAM_NAME_PHOTOREFERENCE = "photoreference";
    public static String PARAM_NAME_MAXWIDTH = "maxwidth";
    public static String PARAM_NAME_MAXHEIGHT = "maxheight";
    public static String PARAM_NAME_PLACEID = "placeid";
    public static String PARAM_NAME_PAGETOKEN = "pagetoken";

    public static String PARAM_VALUE_RANKBY = "distance";


    public static String SEARCH_TYPE_TEXT = "textsearch";
    public static String SEARCH_TYPE_NEARBY = "nearbysearch";
    public static String SEARCH_TYPE_DETAILS = "details";
    public static String DOWNLOAD_GOOGLE_IMAGE = "image";
    public static String DOWNLOAD_PLACE_ICON = "icon";
}
