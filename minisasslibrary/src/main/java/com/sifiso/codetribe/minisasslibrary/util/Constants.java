package com.sifiso.codetribe.minisasslibrary.util;

public interface Constants {
    static final int NETWORK_SOURCE = 0;
    static final int DATABASE_SOURCE = 1;

    //
    static final int ERROR_ENCODING = 900;
    static final int ERROR_DATABASE = 901;
    static final int ERROR_NETWORK_UNAVAILABLE = 902;
    static final int ERROR_SERVER_COMMS = 903;
    static final int ERROR_DUPLICATE = 904;

    static final int ERROR_COMPRESSION_FAILURE = 904;

    //server
    public static final int UNMODIFIED_NATURAL_SAND = 1;
    public static final int LARGELY_NATURAL_SAND = 6;
    public static final int MODERATELY_MODIFIED_SAND = 7;
    public static final int CRITICALLY_MODIFIED_SAND = 9;
    public static final int LARGELY_MODIFIED_SAND = 16;

    public static final int UNMODIFIED_NATURAL_ROCK = 13;
    public static final int LARGELY_NATURAL_ROCK = 14;
    public static final int MODERATELY_MODIFIED_ROCK = 15;
    public static final int CRITICALLY_MODIFIED_ROCK = 17;
    public static final int LARGELY_MODIFIED_ROCK = 8;

    //local
   /* public static final int UNMODIFIED_NATURAL_SAND = 1;
    public static final int LARGELY_NATURAL_SAND = 6;
    public static final int MODERATELY_MODIFIED_SAND = 7;
    public static final int CRITICALLY_MODIFIED_SAND = 9;
    public static final int LARGELY_MODIFIED_SAND = 8;

    public static final int UNMODIFIED_NATURAL_ROCK= 13;
    public static final int LARGELY_NATURAL_ROCK = 14;
    public static final int MODERATELY_MODIFIED_ROCK = 15;
    public static final int CRITICALLY_MODIFIED_ROCK = 16;
    public static final int LARGELY_MODIFIED_ROCK = 12;*/
    public static final int NOT_SPECIFIED = 0;

    public static final int PURPLE_CRAB = 1;
    public static final int GREEN_CRAB = 2;
    public static final int BLUE_CRAB = 3;
    public static final int CHOCOLATE_CRAB = 4;
    public static final int RED_CRAB = 5;

    static final String IMAGE_URI = "imageUri";
    static final String THUMB_URI = "thumbUri";


    //drafts
    static final String SP_RIVER_ID = "spRiverID";
    static final String SP_RIVER_NAME = "spRiverName";
    static final String SP_STREAM_ID = "spStreamID";
    static final String SP_STREAM_NAME = "spStreamName";
    static final String SP_CATEGORY_ID = "spCategoryID";
    static final String SP_CATEGORY_NAME = "spCategoryName";
    static final String SP_WC = "spWC";
    static final String SP_WT = "spWT";
    static final String SP_PH = "spPH";
    static final String SP_OL = "spOL";
    static final String SP_C = "spC";
    static final String SP_SELECTED_INSECTS_LIST = "spSELECTED_INSECTS_LIST";
    static final String SP_COMMENT = "spComment";
}
