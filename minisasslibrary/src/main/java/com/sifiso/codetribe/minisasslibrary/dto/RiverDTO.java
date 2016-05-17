/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;


import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author aubreyM
 */
public class RiverDTO implements Serializable, Comparable<RiverDTO> {

    private static final long serialVersionUID = 1L;
    private Integer riverID;
    private String riverName;
    private Date dateAdded;
    private boolean ignore;
    private Double nearestLatitude, nearestLongitude;
    private float distanceFromMe;
    private List<EvaluationSiteDTO> evaluationsiteList = new ArrayList<>();
    private List<RiverPartDTO> riverpartList = new ArrayList<>();
    private List<StreamDTO> streamList = new ArrayList<>();

    public RiverDTO() {
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public List<StreamDTO> getStreamList() {
        return streamList;
    }

    public void setStreamList(List<StreamDTO> streamList) {
        this.streamList = streamList;
    }

    public Integer getRiverID() {
        return riverID;
    }

    public List<EvaluationSiteDTO> getEvaluationsiteList() {
        return evaluationsiteList;
    }

    public void setEvaluationsiteList(List<EvaluationSiteDTO> evaluationsiteList) {
        this.evaluationsiteList = evaluationsiteList;
    }

    public void setRiverID(Integer riverID) {
        this.riverID = riverID;
    }

    public String getRiverName() {
        return riverName;
    }

    public void setRiverName(String riverName) {
        this.riverName = riverName;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Double getNearestLatitude() {
        return nearestLatitude;
    }

    public void setNearestLatitude(Double nearestLatitude) {
        this.nearestLatitude = nearestLatitude;
    }

    public Double getNearestLongitude() {
        return nearestLongitude;
    }

    public void setNearestLongitude(Double nearestLongitude) {
        this.nearestLongitude = nearestLongitude;
    }

    public float getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(float distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

    public List<RiverPartDTO> getRiverpartList() {
        return riverpartList;
    }

    public void setRiverpartList(List<RiverPartDTO> riverpartList) {
        this.riverpartList = riverpartList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riverID != null ? riverID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiverDTO)) {
            return false;
        }
        RiverDTO other = (RiverDTO) object;
        if ((this.riverID == null && other.riverID != null) || (this.riverID != null && !this.riverID.equals(other.riverID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.River[ riverID=" + riverID + " ]";
    }

    @Override
    public int compareTo(RiverDTO another) {
        if (this.distanceFromMe < another.distanceFromMe) {
            return -1;
        }
        if (this.distanceFromMe > another.distanceFromMe) {
            return 1;
        }
        return 0;
    }

    public float calculateDistance(double latitude, double longitude) {
        List<RiverPointDTO> points = new ArrayList<>();
        for (RiverPartDTO rp : riverpartList) {
            for (RiverPointDTO point : rp.getRiverpointList()) {
                point.calculateDistance(latitude,longitude);
                points.add(point);
            }
        }

        Collections.sort(points);
        if (!points.isEmpty()) {
            distanceFromMe = points.get(0).getDistanceFromMe();
            Log.d("RiverDTO","---------------------> distanceFromMe: " + distanceFromMe);
        } else {
            Log.e("RiverDTO","ERROR - no points for distance calc");
        }

        return distanceFromMe;
    }
}
