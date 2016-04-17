package com.sifiso.codetribe.minisasslibrary.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;

import java.util.Collection;
import java.util.List;

/**
 * Created by aubreymalabie on 4/13/16.
 */
public class MapCluster implements Cluster<MapItem> {

    List<MapItem> mapItems;
    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public Collection<MapItem> getItems() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
