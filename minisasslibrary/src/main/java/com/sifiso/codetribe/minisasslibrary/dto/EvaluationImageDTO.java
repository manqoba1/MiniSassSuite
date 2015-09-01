/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;


import java.io.Serializable;

/**
 * @author aubreyM
 */
public class EvaluationImageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer evaluationImageID;
    private long dateTaken;
    private String fileName;
    private Integer evaluationID, index;
 
    private Double latitude;
    private Double longitude;
    private Float accuracy;

    public EvaluationImageDTO() {
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

    public Integer getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Integer evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Integer getEvaluationImageID() {
        return evaluationImageID;
    }

    public void setEvaluationImageID(Integer evaluationImageID) {
        this.evaluationImageID = evaluationImageID;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (evaluationImageID != null ? evaluationImageID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EvaluationImageDTO)) {
            return false;
        }
        EvaluationImageDTO other = (EvaluationImageDTO) object;
        if ((this.evaluationImageID == null && other.evaluationImageID != null) || (this.evaluationImageID != null && !this.evaluationImageID.equals(other.evaluationImageID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.EvaluationImage[ evaluationImageID=" + evaluationImageID + " ]";
    }

}
