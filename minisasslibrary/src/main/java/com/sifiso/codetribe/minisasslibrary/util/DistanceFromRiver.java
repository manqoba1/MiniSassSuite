package com.sifiso.codetribe.minisasslibrary.util;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aubreymalabie on 4/17/16.
 */

public class DistanceFromRiver implements Comparable<DistanceFromRiver> {
    Double latitude, longitude;
    float distance;
    LatLng latLng;

    public DistanceFromRiver(Double latitude, Double longitude, LatLng latLng) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.latLng = latLng;
        calculateDistance();
    }

    public void calculateDistance() {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);

        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
        loc2.setLatitude(latLng.latitude);
        loc2.setLongitude(latLng.longitude);
        distance = loc1.distanceTo(loc2);
    }

    @Override
    public int compareTo(DistanceFromRiver another) {
        if (distance < another.distance) {
            return -1;
        }
        if (distance > another.distance) {
            return 1;
        }
        return 0;
    }

    public float getDistance() {
        return distance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}