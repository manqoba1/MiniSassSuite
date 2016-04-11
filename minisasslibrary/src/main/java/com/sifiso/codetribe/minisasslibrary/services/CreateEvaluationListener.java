package com.sifiso.codetribe.minisasslibrary.services;

import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;

import java.util.List;

/**
 * Created by sifiso on 4/16/2015.
 */
public interface CreateEvaluationListener {
    public void onListEvaluationSites(List<EvaluationSiteDTO> siteList, int index);

    public void onRefreshMap(RiverDTO river, int result);

    public void onCreateEvaluation(RiverDTO river);

    public void onDirection(Double latitude, Double longitude);

    public void onSitesMapRequested(RiverDTO river);

    public void onNewEvaluation();

}
