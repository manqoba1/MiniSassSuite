package com.sifiso.codetribe.minisasslibrary.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by aubreymalabie on 4/13/16.
 */
public class MapItem implements ClusterItem {
    public MapItem(LatLng latLng,Double distance) {
        this.latLng = latLng;
        this.distance = distance;
    }
    public MapItem(double latitude,double longitude,double distance) {
        this.latLng = new LatLng(latitude,longitude);
        this.distance = distance;
    }
    public MapItem(LatLng latLng,Double distance,String caption) {
        this.latLng = latLng;
        this.distance = distance;
        this.caption = caption;
    }
    public MapItem(LatLng latLng,Double distance,String caption,String subCaption) {
        this.latLng = latLng;
        this.distance = distance;
        this.caption = caption;
        this.subCaption = subCaption;
    }
    @Override
    public LatLng getPosition() {
        return latLng;
    }
    private LatLng latLng;
    private Double distance = 0.0;
    private String caption, subCaption;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
