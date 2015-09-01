/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.codetribe.minisasslibrary.dto;


import java.io.Serializable;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer categoryId;
    private String categoryName;
    private List<EvaluationSiteDTO> evaluationSiteList;
    private List<ConditionsDTO> conditionsList;

    public CategoryDTO() {
    }


    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<ConditionsDTO> getConditionsList() {
        return conditionsList;
    }

    public void setConditionsList(List<ConditionsDTO> conditionsList) {
        this.conditionsList = conditionsList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<EvaluationSiteDTO> getEvaluationSiteList() {
        return evaluationSiteList;
    }

    public void setEvaluationSiteList(List<EvaluationSiteDTO> evaluationSiteList) {
        this.evaluationSiteList = evaluationSiteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryId != null ? categoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CategoryDTO)) {
            return false;
        }
        CategoryDTO other = (CategoryDTO) object;
        if ((this.categoryId == null && other.categoryId != null) || (this.categoryId != null && !this.categoryId.equals(other.categoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.minisass.data.Category[ categoryId=" + categoryId + " ]";
    }

}
