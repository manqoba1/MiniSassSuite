/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aubreyM
 */
public class EvaluationSiteDTO implements Serializable, Comparable<EvaluationSiteDTO> {

    private static final long serialVersionUID = 1L;
    private Integer evaluationSiteID, locationConfirmed;
    private Double latitude;
    private Double longitude;
    private Float accuracy, distanceFromMe;
    private long dateRegistered;
    private Integer categoryID;
    private Integer riverID;
    private String riverName, categoryName;
    private List<EvaluationDTO> evaluationList;
    private RiverDTO river;
    private CategoryDTO category;
    private StreamDTO stream;
    private Boolean confirmed;
    private String siteName;
    private Integer gID;
    private String description;
    private String riverName2;
    private Marker marker;


    @Override
    public int compareTo(EvaluationSiteDTO another) {
        if (this.distanceFromMe < another.distanceFromMe) {
            return -1;
        }
        if (this.distanceFromMe > another.distanceFromMe) {
            return 1;
        }
        return 0;
    }

    public void calculateDistance(double latitude,double longitude) {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        loc1.setLatitude(getLatitude());
        loc1.setLongitude(getLongitude());

        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
        loc2.setLatitude(latitude);
        loc2.setLongitude(longitude);

        distanceFromMe = loc1.distanceTo(loc2);
    }
    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getgID() {
        return gID;
    }

    public void setgID(Integer gID) {
        this.gID = gID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRiverName2() {
        return riverName2;
    }

    public void setRiverName2(String riverName2) {
        this.riverName2 = riverName2;
    }

    public StreamDTO getStream() {
        return stream;
    }

    public void setStream(StreamDTO stream) {
        this.stream = stream;
    }

    public EvaluationSiteDTO() {
    }

    public Integer getEvaluationSiteID() {
        return evaluationSiteID;
    }

    public void setEvaluationSiteID(Integer evaluationSiteID) {
        this.evaluationSiteID = evaluationSiteID;
    }

    public Integer getLocationConfirmed() {
        return locationConfirmed;
    }

    public void setLocationConfirmed(Integer locationConfirmed) {
        this.locationConfirmed = locationConfirmed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(Float distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getRiverID() {
        return riverID;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<EvaluationDTO> getEvaluationList() {
        if (evaluationList == null)
            evaluationList = new ArrayList<>();
        return evaluationList;
    }

    public void setEvaluationList(List<EvaluationDTO> evaluationList) {
        this.evaluationList = evaluationList;
    }

    public RiverDTO getRiver() {
        return river;
    }

    public void setRiver(RiverDTO river) {
        this.river = river;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (evaluationSiteID != null ? evaluationSiteID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EvaluationSiteDTO)) {
            return false;
        }
        EvaluationSiteDTO other = (EvaluationSiteDTO) object;
        if ((this.evaluationSiteID == null && other.evaluationSiteID != null) || (this.evaluationSiteID != null && !this.evaluationSiteID.equals(other.evaluationSiteID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.EvaluationSite[ evaluationSiteID=" + evaluationSiteID + " ]";
    }

}
