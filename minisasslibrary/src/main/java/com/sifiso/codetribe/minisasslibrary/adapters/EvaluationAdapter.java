package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.Date;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-03-09.
 */
public class EvaluationAdapter extends BaseAdapter {
    Context mCtx;
    List<EvaluationDTO> mList;
    EvaluationAdapterListener mListener;

    public EvaluationAdapter(Context mCtx, List<EvaluationDTO> mList, EvaluationAdapterListener mListener) {
        this.mCtx = mCtx;
        this.mList = mList;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getEvaluationID();
    }

    class Holder {
        TextView ELI_txtCount, ELI_team, ELI_date, ELI_wc, ELI_pH, ELI_wt, ELI_oxygen, ELI_score, ELI_condition, ELI_remarks, ELI_envType;
        ImageView ELI_condition_image, ELI_contribute, ELI_edit, ELI_directions;

        RelativeLayout AR_traineeLayout;
        RelativeLayout AR_traineeLayout2;
        RelativeLayout AR_insLayout;
        RelativeLayout AR_insLayout2;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.evaluation_list_item, parent, false);
            h.AR_insLayout2 = (RelativeLayout) v.findViewById(R.id.AR_insLayout2);
            h.AR_insLayout = (RelativeLayout) v.findViewById(R.id.AR_insLayout);
            h.AR_traineeLayout2 = (RelativeLayout) v.findViewById(R.id.AR_traineeLayout2);
            h.AR_traineeLayout = (RelativeLayout) v.findViewById(R.id.AR_traineeLayout);
            h.ELI_condition = (TextView) v.findViewById(R.id.ELI_condition);
            h.ELI_envType = (TextView) v.findViewById(R.id.ELI_envType);
            h.ELI_date = (TextView) v.findViewById(R.id.ELI_date);
            h.ELI_oxygen = (TextView) v.findViewById(R.id.ELI_oxygen);
            h.ELI_pH = (TextView) v.findViewById(R.id.ELI_pH);
            h.ELI_score = (TextView) v.findViewById(R.id.ELI_score);
            h.ELI_team = (TextView) v.findViewById(R.id.ELI_team);
            h.ELI_wc = (TextView) v.findViewById(R.id.ELI_wc);
            h.ELI_wt = (TextView) v.findViewById(R.id.ELI_wt);
            h.ELI_remarks = (TextView) v.findViewById(R.id.ELI_remarks);
            h.ELI_condition_image = (ImageView) v.findViewById(R.id.ELI_condition_image);
            //h.ELI_contribute = (ImageView) v.findViewById(R.id.ELI_contribute);
            h.ELI_edit = (ImageView) v.findViewById(R.id.ELI_edit);
            h.ELI_directions = (ImageView) v.findViewById(R.id.ELI_directions);
            h.ELI_txtCount = (TextView) v.findViewById(R.id.ELI_txtCount);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        final EvaluationDTO evaluation = mList.get(position);

        h.ELI_team.setText(evaluation.getTeamName());
        h.ELI_txtCount.setText((position + 1) + "");
        h.ELI_date.setText(Util.getLongDate(new Date(evaluation.getEvaluationDate())));
        h.ELI_score.setText((Math.round(evaluation.getScore() * 100.00) / 100.00) + "");
        h.ELI_oxygen.setText((evaluation.getOxygen() == null ? 0 : evaluation.getOxygen()) + "");
        h.ELI_wc.setText((evaluation.getWaterClarity() == null ? 0 : evaluation.getWaterClarity()) + "");
        h.ELI_wt.setText((evaluation.getWaterTemperature() == null ? 0 : evaluation.getWaterTemperature()) + "");
        h.ELI_pH.setText((evaluation.getpH() == null ? 0 : evaluation.getpH()) + "");
        Log.i("LOG", new Gson().toJson(evaluation));
        if (evaluation.getConditions() != null) {
            h.ELI_envType.setText(evaluation.getConditions().getCategoryName());
        } else {
            h.ELI_envType.setVisibility(View.GONE);
        }

        if (evaluation.getConditions().getCategoryID() == 8) {
            h.ELI_envType.setTextColor(mCtx.getResources().getColor(R.color.yellow_dark));
        } else {
            h.ELI_envType.setTextColor(mCtx.getResources().getColor(R.color.Peru));
        }

        if (evaluation.getRemarks() == null) {
            h.ELI_remarks.setVisibility(View.GONE);
        } else {
            h.ELI_remarks.setVisibility(View.VISIBLE);
        }

        h.ELI_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_score, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onViewInsect(evaluation.getEvaluationinsectList());
                    }
                });

            }
        });
        h.ELI_condition_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_condition_image, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onViewInsect(evaluation.getEvaluationinsectList());
                    }
                });

            }
        });

        h.ELI_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_edit, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onEvaluationEdit(evaluation);
                    }
                });

            }
        });
        h.ELI_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.ELI_directions, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onDirectionToSite(evaluation.getEvaluationSite());
                    }
                });

            }
        });
        if (evaluation.getOxygen() == null || evaluation.getOxygen().doubleValue() == 0) {
            h.AR_insLayout.setVisibility(View.GONE);
        } else {
            h.AR_insLayout.setVisibility(View.VISIBLE);
        }
        if (evaluation.getWaterClarity() == null || evaluation.getWaterClarity() == 0) {
            h.AR_traineeLayout.setVisibility(View.GONE);
        } else {
            h.AR_traineeLayout.setVisibility(View.VISIBLE);
        }
        if (evaluation.getWaterTemperature() == null || evaluation.getWaterTemperature() == 0) {
            h.AR_traineeLayout2.setVisibility(View.GONE);
        } else {
            h.AR_traineeLayout2.setVisibility(View.VISIBLE);
        }
        if (evaluation.getpH() == null || evaluation.getpH() == 0) {
            h.AR_insLayout2.setVisibility(View.GONE);
        } else {
            h.AR_insLayout2.setVisibility(View.VISIBLE);
        }
        h.ELI_remarks.setText(evaluation.getRemarks());
        switch (evaluation.getConditionsID()) {
            case Constants.UNMODIFIED_NATURAL_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.blue));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.blue));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                break;
            case Constants.LARGELY_NATURAL_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                break;
            case Constants.MODERATELY_MODIFIED_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                break;
            case Constants.LARGELY_MODIFIED_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));

                break;
            case Constants.CRITICALLY_MODIFIED_SAND:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                break;
            case Constants.UNMODIFIED_NATURAL_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.blue));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.blue));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                break;
            case Constants.LARGELY_NATURAL_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                break;
            case Constants.MODERATELY_MODIFIED_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                break;
            case Constants.LARGELY_MODIFIED_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));
                break;
            case Constants.CRITICALLY_MODIFIED_ROCK:
                h.ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                h.ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                break;

        }

        h.ELI_condition.setText(evaluation.getConditionName());
        animateView(v);
        return v;
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(mCtx, R.anim.grow_fade_in_center);

        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }


    public interface EvaluationAdapterListener {
        public void onEvaluationContribute(EvaluationDTO evaluation);

        public void onDirectionToSite(EvaluationSiteDTO evaluationSite);

        public void onEvaluationEdit(EvaluationDTO evaluation);

        public void onViewInsect(List<EvaluationInsectDTO> insectImage);
    }
}
