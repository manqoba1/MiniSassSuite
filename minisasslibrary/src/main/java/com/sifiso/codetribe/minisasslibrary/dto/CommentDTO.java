/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;


import java.io.Serializable;
import java.util.List;

/**
 * @author aubreyM
 */
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer commentID;
    private String remarks;
    private List<EvaluationCommentDTO> evaluationcommentList;

    public CommentDTO() {
    }


    public List<EvaluationCommentDTO> getEvaluationcommentList() {
        return evaluationcommentList;
    }

    public void setEvaluationcommentList(List<EvaluationCommentDTO> evaluationcommentList) {
        this.evaluationcommentList = evaluationcommentList;
    }

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
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
        hash += (commentID != null ? commentID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CommentDTO)) {
            return false;
        }
        CommentDTO other = (CommentDTO) object;
        if ((this.commentID == null && other.commentID != null) || (this.commentID != null && !this.commentID.equals(other.commentID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.Comment[ commentID=" + commentID + " ]";
    }

}
