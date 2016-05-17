package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.util.Constants;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by aubreyM on 14/12/17.
 */
public class ObservationListAdapter extends RecyclerView.Adapter<ObservationListAdapter.ObservationViewHolder> {

    public interface ObservationListListener {
         void onEvaluationContribute(EvaluationDTO evaluation);

         void onDirectionToSite();

         void onEvaluationEdit(EvaluationDTO evaluation);

        void onPictureRequired(EvaluationDTO evaluation);

         void onViewInsect(List<EvaluationInsectDTO> insectImage);

    }

    private ObservationListListener mListener;
    private List<EvaluationDTO> evaluationList;
    private Context ctx;
    int darkColor;

    public ObservationListAdapter(List<EvaluationDTO> evaluationList, int darkColor,
                                  Context context, ObservationListListener listener) {
        this.evaluationList = evaluationList;
        this.ctx = context;
        this.mListener = listener;
        this.darkColor = darkColor;
    }


    @Override
    public ObservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.eval_item_card, parent, false);
        return new ObservationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ObservationViewHolder holder, final int position) {

        final EvaluationDTO p = evaluationList.get(position);
        holder.txtTeamName.setText(p.getTeamName());
        holder.txtSite.setText(p.getSiteName());
        holder.txtNumber.setText("" + (position + 1));
        holder.txtDate.setText(sdf.format(p.getEvaluationDate()));
        if (p.getRemarks() == null) {
            holder.txtRemarks.setText("There are no remarks or comments recorded for this observation");
        } else {
            holder.txtRemarks.setText(p.getRemarks());
        }

        holder.txtScore.setText((Math.round(p.getScore() * 100.00) / 100.00) + "");
        holder.oxygen.setText((p.getOxygen() == null ? 0 : p.getOxygen()) + "");
        holder.waterClarity.setText((p.getWaterClarity() == null ? 0 : p.getWaterClarity()) + "");
        holder.waterTemp.setText((p.getWaterTemperature() == null ? 0 : p.getWaterTemperature()) + "");
        holder.ph.setText((p.getpH() == null ? 0 : p.getpH()) + "");

        setCrab(p,holder);

        if (p.getConditions() != null) {
            holder.txtCondition.setText(p.getConditions().getCategoryName());
            holder.txtCondition.setVisibility(View.VISIBLE);
            if (p.getConditions().getCategoryID() == 8) {
                holder.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.yellow_dark));
            } else {
                holder.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.Peru));
            }
        } else {
            holder.txtCondition.setVisibility(View.GONE);
        }
        //optionals
        if (p.getOxygen() == null || p.getOxygen().doubleValue() == 0) {
            holder.oxygenLayout.setVisibility(View.GONE);
        } else {
            holder.oxygenLayout.setVisibility(View.VISIBLE);
        }
        if (p.getWaterClarity() == null || p.getWaterClarity() == 0) {
            holder.waterClarityLayout.setVisibility(View.GONE);
        } else {
            holder.waterClarityLayout.setVisibility(View.VISIBLE);
        }
        if (p.getWaterTemperature() == null || p.getWaterTemperature() == 0) {
            holder.waterTempLayout.setVisibility(View.GONE);
        } else {
            holder.waterTempLayout.setVisibility(View.VISIBLE);
        }
        if (p.getpH() == null || p.getpH() == 0) {
            holder.phLayout.setVisibility(View.GONE);
        } else {
            holder.phLayout.setVisibility(View.VISIBLE);
        }
        //set action listeners
        holder.iconCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPictureRequired(p);
            }
        });
        holder.iconEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEvaluationEdit(p);
            }
        });
        holder.iconDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDirectionToSite();
            }
        });
        holder.iconCrab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewInsect(p.getEvaluationinsectList());
            }
        });
        holder.crab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewInsect(p.getEvaluationinsectList());
            }
        });
        

    }

    private void setCrab(EvaluationDTO evaluation,ObservationViewHolder h) {
        switch (evaluation.getConditionsID()) {
            case Constants.UNMODIFIED_NATURAL_SAND:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.blue_crap));
                break;
            case Constants.LARGELY_NATURAL_SAND:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.green_crap));
                break;
            case Constants.MODERATELY_MODIFIED_SAND:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.orange_crap));
                break;
            case Constants.LARGELY_MODIFIED_SAND:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.red_crap));

                break;
            case Constants.CRITICALLY_MODIFIED_SAND:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.purple_crap));
                break;
            case Constants.UNMODIFIED_NATURAL_ROCK:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.blue_crap));
                break;
            case Constants.LARGELY_NATURAL_ROCK:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.green_crap));
                break;
            case Constants.MODERATELY_MODIFIED_ROCK:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.orange_crap));
                break;
            case Constants.LARGELY_MODIFIED_ROCK:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.red_crap));
                break;
            case Constants.CRITICALLY_MODIFIED_ROCK:
                h.txtCondition.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                h.txtScore.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                h.crab.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.purple_crap));
                break;

        }

        h.txtCondition.setText(evaluation.getConditionName());
    }
    @Override
    public int getItemCount() {
        return evaluationList == null ? 0 : evaluationList.size();
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", loc);
    static final DecimalFormat df = new DecimalFormat("###,###,###,###");

    public class ObservationViewHolder extends RecyclerView.ViewHolder  {
        protected TextView txtTeamName, txtNumber, txtScore, txtCondition, txtDate, txtRemarks, txtSite;
        protected ImageView crab, iconCamera, iconDirections, iconEdit, iconCrab;
        protected TextView waterClarity, waterTemp, ph, oxygen;
        protected View optionals, waterClarityLayout, waterTempLayout, oxygenLayout, phLayout;


        public ObservationViewHolder(View itemView) {
            super(itemView);

            txtTeamName = (TextView) itemView
                    .findViewById(R.id.teamName);
            txtScore = (TextView) itemView
                    .findViewById(R.id.average);
            txtNumber = (TextView) itemView
                    .findViewById(R.id.number);
            txtCondition = (TextView) itemView
                    .findViewById(R.id.resultText);
            txtRemarks = (TextView) itemView
                    .findViewById(R.id.remarks);
            txtDate = (TextView) itemView
                    .findViewById(R.id.date);
            txtSite = (TextView) itemView
                    .findViewById(R.id.siteName);

            optionals = itemView
                    .findViewById(R.id.evalOptionals);
            waterClarityLayout = itemView
                    .findViewById(R.id.waterClarityLayout);
            waterTempLayout = itemView
                    .findViewById(R.id.waterTempLayout);
            phLayout = itemView
                    .findViewById(R.id.phLayout);
            oxygenLayout = itemView
                    .findViewById(R.id.oxygenLayout);
            //
            waterClarity = (TextView) itemView
                    .findViewById(R.id.waterClarity);
            waterTemp = (TextView) itemView
                    .findViewById(R.id.waterTemp);
            ph = (TextView) itemView
                    .findViewById(R.id.ph);
            oxygen = (TextView) itemView
                    .findViewById(R.id.oxygen);

            crab = (ImageView) itemView
                    .findViewById(R.id.imageCrab);
            iconCamera = (ImageView) itemView
                    .findViewById(R.id.iconCamera);
            iconDirections = (ImageView) itemView
                    .findViewById(R.id.iconDirections);
            iconEdit = (ImageView) itemView
                    .findViewById(R.id.iconEdit);
            iconCrab = (ImageView) itemView
                    .findViewById(R.id.iconCrab);


        }

    }

    static final String LOG = com.sifiso.codetribe.minisasslibrary.adapters.ObservationListAdapter.class.getSimpleName();
}
