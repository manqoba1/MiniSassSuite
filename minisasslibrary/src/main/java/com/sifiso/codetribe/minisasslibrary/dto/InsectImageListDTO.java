package com.sifiso.codetribe.minisasslibrary.dto;

import java.io.Serializable;

/**
 * Created by sifiso on 2015-06-03.
 */
public class InsectImageListDTO implements Serializable{
    private Integer insectimagelistID;
    private String imageName;
    private String url;
    private InsectImageDTO insectimage;

    public Integer getInsectimagelistID() {
        return insectimagelistID;
    }

    public void setInsectimagelistID(Integer insectimagelistID) {
        this.insectimagelistID = insectimagelistID;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InsectImageDTO getInsectimage() {
        return insectimage;
    }

    public void setInsectimage(InsectImageDTO insectimage) {
        this.insectimage = insectimage;
    }
}
