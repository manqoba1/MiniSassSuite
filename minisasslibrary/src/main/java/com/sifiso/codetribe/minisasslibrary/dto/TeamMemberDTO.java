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
public class TeamMemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer teamMemberID, evaluationCount;
    private String firstName, teamName;
    private String lastName;
    private String email;
    private String cellphone;
    private long dateRegistered;
    private String pin;
    private Integer activeFlag;
    private List<EvaluationDTO> evaluationList;
    private Integer teamID;
    private String teamMemberImage;
    private List<GcmDeviceDTO> gcmdeviceList;
    private TeamDTO team;
    private List<TmemberDTO> tmemberList = new ArrayList<>();

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public TeamMemberDTO() {
    }

    public List<TmemberDTO> getTmemberList() {
        return tmemberList;
    }

    public void setTmemberList(List<TmemberDTO> tmemberList) {
        this.tmemberList = tmemberList;
    }

    public Integer getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(Integer evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public String getTeamMemberImage() {
        return teamMemberImage;
    }

    public void setTeamMemberImage(String teamMemberImage) {
        this.teamMemberImage = teamMemberImage;
    }

    public List<GcmDeviceDTO> getGcmdeviceList() {
        return gcmdeviceList;
    }

    public void setGcmdeviceList(List<GcmDeviceDTO> gcmdeviceList) {
        this.gcmdeviceList = gcmdeviceList;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public Integer getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamMemberID(Integer teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public List<EvaluationDTO> getEvaluationList() {
        return evaluationList;
    }

    public void setEvaluationList(List<EvaluationDTO> evaluationList) {
        this.evaluationList = evaluationList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (teamMemberID != null ? teamMemberID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TeamMemberDTO)) {
            return false;
        }
        TeamMemberDTO other = (TeamMemberDTO) object;
        if ((this.teamMemberID == null && other.teamMemberID != null) || (this.teamMemberID != null && !this.teamMemberID.equals(other.teamMemberID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.TeamMember[ teamMemberID=" + teamMemberID + " ]";
    }

}
