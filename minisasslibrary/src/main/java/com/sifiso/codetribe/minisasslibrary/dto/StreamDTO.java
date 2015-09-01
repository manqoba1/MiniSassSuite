package com.sifiso.codetribe.minisasslibrary.dto;

import java.io.Serializable;

/**
 * Created by sifiso on 2015-07-11.
 */
public class StreamDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer streamID, riverID;
    private String streamName;
    private double latitude;
    private double longitude;
    private RiverDTO river;

    public StreamDTO() {
    }


    public Integer getStreamID() {
        return streamID;
    }

    public void setStreamID(Integer streamID) {
        this.streamID = streamID;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
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

    public Integer getRiverID() {
        return riverID;
    }

    public void setRiverID(Integer riverID) {
        this.riverID = riverID;
    }

    public RiverDTO getRiver() {
        return river;
    }

    public void setRiver(RiverDTO river) {
        this.river = river;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (streamID != null ? streamID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StreamDTO)) {
            return false;
        }
        StreamDTO other = (StreamDTO) object;
        if ((this.streamID == null && other.streamID != null) || (this.streamID != null && !this.streamID.equals(other.streamID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.rivers.data.Stream[ streamID=" + streamID + " ]";
    }

}
