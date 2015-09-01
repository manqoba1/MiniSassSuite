package com.sifiso.codetribe.minisasslibrary.util;


import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;

import java.util.regex.Pattern;


public class Statics {


    public static final String WEBSOCKET_URL = "ws://69.89.15.29:8080/ms2/";
    public static final String URL = "http://69.89.15.29:8080/ms2/";
    public static final String IMAGE_URL = "http://69.89.15.29::8080/";

    /*public static final String WEBSOCKET_URL = "ws://192.168.33.119:8080/ms2/";
    public static final String URL = "http://192.168.33.119:8080/ms2/";
    public static final String IMAGE_URL = "http://192.168.33.119:8080/";*/

    /*public static final String WEBSOCKET_URL = "ws://146.64.85.22:8080/ms2/";
    public static final String URL = "http://146.64.85.22:8080/ms2/";
    public static final String IMAGE_URL = "http://146.64.85.22:8080/";*/

    public static final String CRASH_REPORTS_URL = URL + "crash?";
    public static final String UPLOAD_URL_REQUEST = "uploadUrl?";
    public static final String
            REQUEST_ENDPOINT = "wsrequest",
            REQUEST_SERVLET = "rsrequest?JSON=",
            MINI_SASS_ENDPOINT = "wsmini",
            SERVLET_ENDPOINT = "list?JSON=",
            SERVLET_TEST = "test1?JSON=";

    public static final String SESSION_ID = "sessionID";
    public static final int CRASH_STRING = R.string.crash_toast;


    public static void setDroidFontBold(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "DroidSerif-Bold");
        txt.setTypeface(font);
    }

    public static void setRobotoFontBoldCondensed(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-BoldCondensed.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontRegular(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontLight(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Light.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoFontBold(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Bold.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoItalic(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Italic.ttf");
        txt.setTypeface(font);
    }

    public static void setRobotoRegular(Context ctx, TextView txt) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txt.setTypeface(font);
    }

    public static boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
    public static boolean isSpecial(String name) {
        return name.matches("[!#$%&'*+/=?^_`{|}~-]+");
    }

    public static boolean isLetterAndNumber(String l) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        String s = ".*[0-9!#$%&'*+/=?^_`{|}~-].*";

        return l.matches(s) ;

    }

    public static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );
}
