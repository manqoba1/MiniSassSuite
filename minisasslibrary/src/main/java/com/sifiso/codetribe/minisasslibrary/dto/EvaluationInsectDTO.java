/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sifiso.codetribe.minisasslibrary.dto;

import java.io.Serializable;

/**
 * @author aubreyM
 */
public class EvaluationInsectDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer evaluationInsectID;
    private int evaluationFlag;
    private Integer evaluationColor;
    private String remarks;
    private Integer evaluationID;
    private Integer insectID;
    private EvaluationDTO evaluation;
    private InsectDTO insect;

    public EvaluationInsectDTO() {
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public InsectDTO getInsect() {
        return insect;
    }

    public void setInsect(InsectDTO insect) {
        this.insect = insect;
    }

    public Integer getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(Integer evaluationID) {
        this.evaluationID = evaluationID;
    }

    public Integer getInsectID() {
        return insectID;
    }

    public void setInsectID(Integer insectID) {
        this.insectID = insectID;
    }


    public Integer getEvaluationInsectID() {
        return evaluationInsectID;
    }

    public void setEvaluationInsectID(Integer evaluationInsectID) {
        this.evaluationInsectID = evaluationInsectID;
    }

    public int getEvaluationFlag() {
        return evaluationFlag;
    }

    public void setEvaluationFlag(int evaluationFlag) {
        this.evaluationFlag = evaluationFlag;
    }

    public Integer getEvaluationColor() {
        return evaluationColor;
    }

    public void setEvaluationColor(Integer evaluationColor) {
        this.evaluationColor = evaluationColor;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (evaluationInsectID != null ? evaluationInsectID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EvaluationInsectDTO)) {
            return false;
        }
        EvaluationInsectDTO other = (EvaluationInsectDTO) object;
        if ((this.evaluationInsectID == null && other.evaluationInsectID != null) || (this.evaluationInsectID != null && !this.evaluationInsectID.equals(other.evaluationInsectID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.EvaluationInsect[ evaluationInsectID=" + evaluationInsectID + " ]";
    }

}
