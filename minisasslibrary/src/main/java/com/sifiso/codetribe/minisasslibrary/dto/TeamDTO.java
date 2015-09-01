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
public class TeamDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer teamID, countryID, organisationTypeID;
    private String teamName;
    private long dateRegistered;
    private String teamImage;
    private CountryDTO country;
    private OrganisationtypeDTO organisationType;
    private List<TeamMemberDTO> teammemberList = new ArrayList<>();
    private List<TmemberDTO> tmemberList = new ArrayList<>();

    public TeamDTO() {
    }

    public List<TmemberDTO> getTmemberList() {
        return tmemberList;
    }

    public void setTmemberList(List<TmemberDTO> tmemberList) {
        this.tmemberList = tmemberList;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public Integer getOrganisationTypeID() {
        return organisationTypeID;
    }

    public void setOrganisationTypeID(Integer organisationTypeID) {
        this.organisationTypeID = organisationTypeID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(String teamImage) {
        this.teamImage = teamImage;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public OrganisationtypeDTO getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationtypeDTO organisationType) {
        this.organisationType = organisationType;
    }

    public List<TeamMemberDTO> getTeammemberList() {
        return teammemberList;
    }

    public void setTeammemberList(List<TeamMemberDTO> teammemberList) {
        this.teammemberList = teammemberList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (teamID != null ? teamID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TeamDTO)) {
            return false;
        }
        TeamDTO other = (TeamDTO) object;
        if ((this.teamID == null && other.teamID != null) || (this.teamID != null && !this.teamID.equals(other.teamID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.Team[ teamID=" + teamID + " ]";
    }

}
