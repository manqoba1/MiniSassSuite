package com.sifiso.codetribe.minisasslibrary.dto;

import android.location.Location;
import android.location.LocationManager;

import java.io.Serializable;

/**
 * Created by sifiso on 2015-06-02.
 */
public class RiverPointDTO implements Serializable, Comparable<RiverPointDTO> {
    private static final long serialVersionUID = 1L;
    private Integer riverPointID;
    private Double distance;
    private double latitude;
    private double longitude;
    private float distanceFromMe;
    private int iprivID;
    private RiverPartDTO riverPart;

    public RiverPointDTO() {
    }

    public RiverPointDTO(Integer riverPointID) {
        this.riverPointID = riverPointID;
    }

    public float getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(float distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getRiverPointID() {
        return riverPointID;
    }

    public void setRiverPointID(Integer riverPointID) {
        this.riverPointID = riverPointID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIprivID() {
        return iprivID;
    }

    public void setIprivID(int iprivID) {
        this.iprivID = iprivID;
    }

    public RiverPartDTO getRiverPart() {
        return riverPart;
    }

    public void setRiverPart(RiverPartDTO riverPart) {
        this.riverPart = riverPart;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riverPointID != null ? riverPointID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiverPointDTO)) {
            return false;
        }
        RiverPointDTO other = (RiverPointDTO) object;
        if ((this.riverPointID == null && other.riverPointID != null) || (this.riverPointID != null && !this.riverPointID.equals(other.riverPointID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.rivers.data.RiverPoint[ riverPointID=" + riverPointID + " ]";
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(RiverPointDTO another) {
        if (this.distanceFromMe < another.distanceFromMe) {
            return -1;
        }
        if (this.distanceFromMe > another.distanceFromMe) {
            return 1;
        }
        return 0;
    }
    public float calculateDistance(double latitude, double longitude) {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        loc1.setLatitude(this.latitude);
        loc1.setLongitude(this.longitude);

        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
        loc2.setLatitude(latitude);
        loc2.setLongitude(longitude);

        distanceFromMe = loc1.distanceTo(loc2);
        return distanceFromMe;
    }
}
