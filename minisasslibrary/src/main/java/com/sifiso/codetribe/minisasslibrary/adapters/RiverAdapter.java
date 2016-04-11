package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.List;

/**
 * Created by CodeTribe1 on 2015-03-05.
 */
public class RiverAdapter extends BaseAdapter {
    Context mCtx;
    List<RiverDTO> mList;
    RiverAdapterListener mListener;

    public RiverAdapter(List<RiverDTO> mList, Context mCtx, RiverAdapterListener listener) {
        this.mList = mList;
        this.mCtx = mCtx;
        mListener = listener;
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
        return 0;
    }

    class Holder {
        ImageView AR_image_folder, AR_imgMap, AR_refresh, AR_imgDirections, AR_imgSitesMap,logo_icon;
        TextView AR_totalEvaluation, AR_txtRiverName, AR_totalStreams, AR_percOverallEva, AR_totalTrLabel,
                AR_eva_date, AR_Eva_score, AR_conditionName;
        View scoreView, actionsView;

    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.river_list_item, parent,
                    false);
            h.scoreView = v.findViewById(R.id.score_lay);
            h.actionsView = v.findViewById(R.id.AR_actionLayout);
            h.AR_conditionName = (TextView) v.findViewById(R.id.AR_conditionName);
            h.logo_icon = (ImageView) v.findViewById(R.id.logo_icon);
           // h.AR_image_folder = (ImageView) v.findViewById(R.id.AR_image_folder);
            h.AR_imgMap = (ImageView) v.findViewById(R.id.AR_imgMap);
            // h.AR_refresh = (ImageView) v.findViewById(R.id.AR_refresh);
            h.AR_imgDirections = (ImageView) v.findViewById(R.id.AR_imgDirections);
            h.AR_imgSitesMap = (ImageView) v.findViewById(R.id.AR_imgSitesMap);
            h.AR_totalEvaluation = (TextView) v.findViewById(R.id.AR_totalEvaluation);
            h.AR_txtRiverName = (TextView) v.findViewById(R.id.AR_txtRiverName);
            h.AR_totalStreams = (TextView) v.findViewById(R.id.AR_totalStreams);
            h.AR_percOverallEva = (TextView) v.findViewById(R.id.AR_percOverallEva);
            h.AR_totalTrLabel = (TextView) v.findViewById(R.id.AR_totalTrLabel);
            h.AR_eva_date = (TextView) v.findViewById(R.id.AR_eva_date);
            h.AR_Eva_score = (TextView) v.findViewById(R.id.AR_Eva_score);

            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }
        final RiverDTO river = mList.get(position);


        double perc = 0.0;
        double total = 0.0;
        for (EvaluationSiteDTO es : river.getEvaluationsiteList()) {
            for (EvaluationDTO e : es.getEvaluationList()) {
                if (e.getScore() != null) {
                    if (e.getScore() != 0) {
                        total = total + e.getScore();
                    }
                }
            }
        }
        h.AR_totalStreams.setText("" + river.getStreamList().size());
        if (!river.getEvaluationsiteList().isEmpty()) {
            if (!river.getEvaluationsiteList().get(0).getEvaluationList().isEmpty()) {
                h.AR_percOverallEva.setText(river.getEvaluationsiteList().get(0).getEvaluationList().get(0).getScore() + "");
                h.AR_conditionName.setText(river.getEvaluationsiteList().get(0).getEvaluationList().get(0).getConditionName());
                setColorToText(river.getEvaluationsiteList().get(0).getEvaluationList().get(0).getConditionsID(), h);
                h.scoreView.setVisibility(View.VISIBLE);
                h.actionsView.setVisibility(View.VISIBLE);
            } else {
                h.scoreView.setVisibility(View.GONE);
                h.actionsView.setVisibility(View.GONE);
                h.AR_percOverallEva.setText(0 + "");
                h.AR_conditionName.setText("No Observation(s) yet");
                h.AR_percOverallEva.setTextColor(ContextCompat.getColor(mCtx, R.color.gray));
                h.AR_conditionName.setTextColor(ContextCompat.getColor(mCtx,R.color.gray));
                h.logo_icon.setImageDrawable(ContextCompat.getDrawable(mCtx,R.drawable.ic_action_edit));
            }
        } else {
            h.scoreView.setVisibility(View.GONE);
            h.actionsView.setVisibility(View.GONE);
            h.AR_percOverallEva.setText(0 + "");
            h.AR_conditionName.setText("No Observation(s) yet");
            h.AR_percOverallEva.setTextColor(ContextCompat.getColor(mCtx, R.color.gray));
            h.AR_conditionName.setTextColor(ContextCompat.getColor(mCtx, R.color.gray));
            h.logo_icon.setImageDrawable(ContextCompat.getDrawable(mCtx, R.drawable.ic_action_edit));
        }

        h.AR_imgDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.flashOnce(h.AR_imgDirections, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onDirectionRequired(river.getEvaluationsiteList());
                    }
                });
            }
        });
        h.AR_txtRiverName.setText(river.getRiverName().trim() + " River");
        h.AR_txtRiverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.AR_txtRiverName, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (river.getEvaluationsiteList() != null) {
                            mListener.onCreateEvaluation(river);
                        } else {
                            ToastUtil.toast(mCtx, "No Observation(s) yet");
                        }
                    }
                });
            }
        });
        h.AR_totalEvaluation.setText("" + river.getEvaluationsiteList().size());
        h.AR_totalEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.toast(mCtx,river.getRiverName());
                Util.flashOnce(h.AR_totalEvaluation, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (river.getEvaluationsiteList() != null) {
                            mListener.onEvaluationRequest(river.getEvaluationsiteList(), position, river.getRiverName().trim());
                        } else {
                            ToastUtil.toast(mCtx, "No Observation(s) yet");
                        }
                    }
                });
            }
        });
        h.AR_totalTrLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.toast(mCtx,river.getRiverName());
                Util.flashOnce(h.AR_totalTrLabel, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (river.getEvaluationsiteList() != null) {
                            mListener.onEvaluationRequest(river.getEvaluationsiteList(), position, river.getRiverName().trim());
                        } else {
                            ToastUtil.toast(mCtx, "No Observation(s) yet");
                        }
                    }
                });
            }

        });
        h.AR_imgSitesMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.AR_imgSitesMap, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onSitesMapRequested(river);
                    }
                });

            }
        });
        h.AR_imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(h.AR_imgMap, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        mListener.onMapRequest(river, RIVER_VIEW);
                    }
                });

            }
        });

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

    public interface RiverAdapterListener {
        public void onSitesMapRequested(RiverDTO river);

        public void onEvaluationRequest(List<EvaluationSiteDTO> siteList, int position, String riverName);

        public void onCreateEvaluation(RiverDTO river);

        public void onMapRequest(RiverDTO river, int result);

        public void onDirectionRequired(List<EvaluationSiteDTO> siteList);
    }

    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;

    private void setColorToText(int condition, Holder h) {
        switch (condition) {
            case Constants.UNMODIFIED_NATURAL_SAND:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.blue_900));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.blue_900));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                break;
            case Constants.LARGELY_NATURAL_SAND:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.green_700));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.green_700));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                break;
            case Constants.MODERATELY_MODIFIED_SAND:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.orange_400));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.orange_400));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                break;
            case Constants.LARGELY_MODIFIED_SAND:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.red_900));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.red_900));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));

                break;
            case Constants.CRITICALLY_MODIFIED_SAND:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.purple_800));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.purple_800));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                break;
            case Constants.UNMODIFIED_NATURAL_ROCK:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.blue_900));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.blue_900));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                break;
            case Constants.LARGELY_NATURAL_ROCK:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.green_700));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.green_700));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                break;
            case Constants.MODERATELY_MODIFIED_ROCK:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.orange_400));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.orange_400));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                break;
            case Constants.LARGELY_MODIFIED_ROCK:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.red_900));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.red_900));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));
                break;
            case Constants.CRITICALLY_MODIFIED_ROCK:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.purple_800));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.purple_800));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                break;
            case Constants.NOT_SPECIFIED:
                h.AR_percOverallEva.setTextColor(mCtx.getResources().getColor(R.color.gray));
                h.AR_conditionName.setTextColor(mCtx.getResources().getColor(R.color.gray));
                h.logo_icon.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.ic_launcher));
                break;

        }
    }

}
