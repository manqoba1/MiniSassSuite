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
public class TownDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer townID;
    private String townName,provinceName,countryName;;
    private Double latitude;
    private Double longitude;
    private Integer provinceID,countryID;
    private List<RiverTownDTO> riverTownList = new ArrayList<>();
    private List<TeamDTO> teamList;

    public TownDTO() {
    }

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(Integer provinceID) {
        this.provinceID = provinceID;
    }

    public List<RiverTownDTO> getRiverTownList() {
        return riverTownList;
    }

    public void setRiverTownList(List<RiverTownDTO> riverTownList) {
        this.riverTownList = riverTownList;
    }

    public List<TeamDTO> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<TeamDTO> teamList) {
        this.teamList = teamList;
    }


    public Integer getTownID() {
        return townID;
    }

    public void setTownID(Integer townID) {
        this.townID = townID;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
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


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (townID != null ? townID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TownDTO)) {
            return false;
        }
        TownDTO other = (TownDTO) object;
        if ((this.townID == null && other.townID != null) || (this.townID != null && !this.townID.equals(other.townID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.Town[ townID=" + townID + " ]";
    }


}
