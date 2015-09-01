package com.sifiso.codetribe.minisasslibrary.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Chris on 2015-02-19.
 */
public class ImageLocation implements Serializable {

    private Integer evaluationImageID;
    private double latitude, longitude;
    private float accuracy;
    private Date dateTaken;

    public Integer getEvaluationImageID() {
        return evaluationImageID;
    }

    public void setEvaluationImageID(Integer evaluationImageID) {
        this.evaluationImageID = evaluationImageID;
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

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }
}
