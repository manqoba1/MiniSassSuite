package com.sifiso.codetribe.minisasslibrary.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by CodeTribe1 on 2015-03-12.
 */
public class DistanceCalculator {
    public static double distance(LatLng orig, LatLng end, String NW) {

        double dDistance = Double.MIN_VALUE;
        double dLat1InRad = orig.latitude * (Math.PI / 180.0);
        double dLong1InRad = orig.longitude * (Math.PI / 180.0);
        double dLat2InRad = end.latitude * (Math.PI / 180.0);
        double dLong2InRad = end.longitude * (Math.PI / 180.0);

        double dLongitude = dLong2InRad - dLong1InRad;
        double dLatitude = dLat2InRad - dLat1InRad;

        // Intermediate result a.
        double a = Math.pow(Math.sin(dLatitude / 2.0), 2.0) +
                Math.cos(dLat1InRad) * Math.cos(dLat2InRad) *
                        Math.pow(Math.sin(dLongitude / 2.0), 2.0);

        // Intermediate result c (great circle distance in Radians).
        double c = 2.0 * Math.asin(Math.sqrt(a));

        // Distance.
        // const Double kEarthRadiusMiles = 3956.0;
        Double kEarthRadiusKms = 6376.5;
        dDistance = kEarthRadiusKms * c;
        dDistance = dDistance * 1.609344;
        return dDistance;
    }
}
