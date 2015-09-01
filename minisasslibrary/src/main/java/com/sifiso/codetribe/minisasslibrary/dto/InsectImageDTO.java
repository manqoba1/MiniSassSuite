/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author aubreyM
 */
public class InsectImageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    public boolean selected = false;
    private Integer insectImageID;
    private String uri, groupName;
    private long dateRegistered;
    private Integer insectID;
    private InsectDTO insect;
    private int sensitivityScore;
    private List<InsectImageListDTO> insectimagelistList = new ArrayList<>();

    public List<InsectImageListDTO> getInsectimagelistList() {
        return insectimagelistList;
    }

    public void setInsectimagelistList(List<InsectImageListDTO> insectimagelistList) {
        this.insectimagelistList = insectimagelistList;
    }

    public InsectImageDTO() {
    }

    public InsectDTO getInsect() {
        return insect;
    }

    public void setInsect(InsectDTO insect) {
        this.insect = insect;
    }

    public int getSensitivityScore() {
        return sensitivityScore;
    }

    public void setSensitivityScore(int sensitivityScore) {
        this.sensitivityScore = sensitivityScore;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Integer getInsectImageID() {
        return insectImageID;
    }

    public void setInsectImageID(Integer insectImageID) {
        this.insectImageID = insectImageID;
    }

    public Integer getInsectID() {
        return insectID;
    }

    public void setInsectID(Integer insectID) {
        this.insectID = insectID;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (insectImageID != null ? insectImageID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InsectImageDTO)) {
            return false;
        }
        InsectImageDTO other = (InsectImageDTO) object;
        if ((this.insectImageID == null && other.insectImageID != null) || (this.insectImageID != null && !this.insectImageID.equals(other.insectImageID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.InsectImage[ insectImageID=" + insectImageID + " ]";
    }

}
