/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto.tranfer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author CodeTribe1
 */
public class ImagesDTO implements Serializable {

    public interface PhotoUploadedListener {
        public void onPhotoUploaded();
        public void onPhotoUploadFailed();
    }

    public static final int TEAM_MEMBER_IMAGE = 1, TEAM_IMAGE = 2, EVALUATION_IMAGE = 3;
    private Integer teamMemberID,index, teamID, evaluationID, evaluationImageID, thumbFlag, pictureType;
    private boolean isFullPicture, isTeamPicture, isTeamMemberPicture, isEvaluationImagePicture;
    private double latitude, longitude;
    private Date dateTaken, dateUploaded, dateThumbUploaded, dateFullPictureUploaded;;
    private float accuracy;
    private String thumbFilePath, imageFilePath, uri;

    private List<String> tags;

    public ImagesDTO() {
    }

    public boolean isTeamMemberPicture() {
        return isTeamMemberPicture;
    }

    public void setTeamMemberPicture(boolean isTeamMemberPicture) {
        this.isTeamMemberPicture = isTeamMemberPicture;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEvaluationImagePicture() {
        return isEvaluationImagePicture;
    }

    public void setEvaluationImagePicture(boolean isEvaluationImagePicture) {
        this.isEvaluationImagePicture = isEvaluationImagePicture;
    }

    public boolean isTeamPicture() {
        return isTeamPicture;
    }

    public void setTeamPicture(boolean isTeamPicture) {
        this.isTeamPicture = isTeamPicture;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
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

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public Date getDateThumbUploaded() {
        return dateThumbUploaded;
    }

    public void setDateThumbUploaded(Date dateThumbUploaded) {
        this.dateThumbUploaded = dateThumbUploaded;
    }

    public Date getDateFullPictureUploaded() {
        return dateFullPictureUploaded;
    }

    public void setDateFullPictureUploaded(Date dateFullPictureUploaded) {
        this.dateFullPictureUploaded = dateFullPictureUploaded;
    }

    public String getThumbFilePath() {
        return thumbFilePath;
    }

    public void setThumbFilePath(String thumbFilePath) {
        this.thumbFilePath = thumbFilePath;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }



    public static int getTeamMemberImage() {
        return TEAM_MEMBER_IMAGE;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public static int getTeamImage() {
        return TEAM_IMAGE;
    }

    public static int getEvaluationImage() {
        return EVALUATION_IMAGE;
    }

    public boolean isFullPicture() {
        return isFullPicture;
    }

    public void setFullPicture(boolean isFullPicture) {
        this.isFullPicture = isFullPicture;
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

    public Integer getPictureType() {
        return pictureType;
    }

    public void setPictureType(Integer pictureType) {
        this.pictureType = pictureType;
    }

    public Integer getTeamMemberID() {
        return teamMemberID;
    }

    public void setTeamMemberID(Integer teamMemberID) {
        this.teamMemberID = teamMemberID;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public Integer getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Integer evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Integer getThumbFlag() {
        return thumbFlag;
    }

    public void setThumbFlag(Integer thumbFlag) {
        this.thumbFlag = thumbFlag;
    }

    public boolean isIsFullPicture() {
        return isFullPicture;
    }

    public void setIsFullPicture(boolean isFullPicture) {
        this.isFullPicture = isFullPicture;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
