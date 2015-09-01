package com.sifiso.codetribe.minisasslibrary.util;

import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2015-03-01.
 */
public class PhotoCache implements Serializable {
    private List<EvaluationImageDTO> imageUploadList = new ArrayList<>();

    public List<EvaluationImageDTO> getImageUploadList() {
        return imageUploadList;
    }

    public void setImageUploadList(List<EvaluationImageDTO> imageUploadList) {
        this.imageUploadList = imageUploadList;
    }


}
