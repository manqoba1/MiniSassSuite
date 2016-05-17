package com.sifiso.codetribe.minisasslibrary.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aubreymalabie on 4/26/16.
 */
public class Bounds {

    public static boolean isPointInBounds(LatLng nortEast, LatLng southWest, double latitude, double longitude) {

        boolean longitudeContained = false;
        boolean latitudeContained = false;

        if (southWest.longitude < nortEast.longitude) {
            if (southWest.longitude < longitude && longitude < nortEast.longitude) {
                longitudeContained = true;
            }
        } else {
            // Contains prime meridian.
            if ((0 < longitude && longitude < nortEast.longitude) ||
                    (southWest.longitude < longitude && longitude < 0)) {
                longitudeContained = true;
            }
        }

        if (southWest.latitude < nortEast.latitude) {
            if (southWest.latitude < latitude && latitude < nortEast.latitude) {
                latitudeContained = true;
            }
        } else {
            // The poles. Don't care.
        }
        if (latitudeContained && longitudeContained) {
            return true;
        } else {
            return false;
        }

    }

    
}
