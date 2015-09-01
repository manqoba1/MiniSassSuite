package com.sifiso.codetribe.minisasslibrary.interfaces;

import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;

/**
 * Created by sifiso on 2015-07-09.
 */
public interface MarkerInfoWindowAdapterListener {
    public void onEditEvaluation(EvaluationDTO evaluation);
    public void onDirection(EvaluationDTO evaluation);
}
