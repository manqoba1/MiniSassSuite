package com.sifiso.codetribe.minisasslibrary.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sifiso on 2015-06-03.
 */
public class OrganisationtypeDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private Integer organisationTypeID;
    private String organisationName;
    private List<TeamDTO> teamList = new ArrayList<>();

    public Integer getOrganisationTypeID() {
        return organisationTypeID;
    }

    public void setOrganisationTypeID(Integer organisationTypeID) {
        this.organisationTypeID = organisationTypeID;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<TeamDTO> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<TeamDTO> teamList) {
        this.teamList = teamList;
    }
}
