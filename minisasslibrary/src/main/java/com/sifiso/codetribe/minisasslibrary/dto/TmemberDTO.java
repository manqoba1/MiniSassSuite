package com.sifiso.codetribe.minisasslibrary.dto;

import java.io.Serializable;

/**
 * Created by sifiso on 2015-07-11.
 */
public class TmemberDTO implements Serializable, Comparable<TmemberDTO> {

    private Integer tmemberID, teamID, teamMemberID;
    private long dateCreated;
    private TeamDTO team;
    private TeamMemberDTO teamMember;
    private int acceptInvite;

    public TmemberDTO() {
    }

    public int getAcceptInvite() {
        return acceptInvite;
    }

    public void setAcceptInvite(int acceptInvite) {
        this.acceptInvite = acceptInvite;
    }

    public Integer getTmemberID() {
        return tmemberID;
    }

    public void setTmemberID(Integer tmemberID) {
        this.tmemberID = tmemberID;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public Integer getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public void setTeamMemberID(Integer teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public TeamMemberDTO getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(TeamMemberDTO teamMember) {
        this.teamMember = teamMember;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tmemberID != null ? tmemberID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TmemberDTO)) {
            return false;
        }
        TmemberDTO other = (TmemberDTO) object;
        if ((this.tmemberID == null && other.tmemberID != null) || (this.tmemberID != null && !this.tmemberID.equals(other.tmemberID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.rivers.data.Tmember[ tmemberID=" + tmemberID + " ]";
    }

    @Override
    public int compareTo(TmemberDTO another) {
        if (this.acceptInvite < another.acceptInvite) {
            return -1;
        }
        if (this.acceptInvite > another.acceptInvite) {
            return 1;
        }
        return 0;
    }
}
