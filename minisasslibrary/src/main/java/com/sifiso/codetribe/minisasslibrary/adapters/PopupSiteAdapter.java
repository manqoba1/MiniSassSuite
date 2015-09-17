package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-06-22.
 */
public class PopupSiteAdapter extends ArrayAdapter<EvaluationSiteDTO> {
    static final String LOG = PopupListAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    View view;
    private List<EvaluationSiteDTO> mList;
    private String[] mStrings;
    private Context ctx;
    private String title;
    private boolean showAlternateIcon;

    public PopupSiteAdapter(Context context, int textViewResourceId,
                            List<EvaluationSiteDTO> list, boolean showAlternateIcon) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.showAlternateIcon = showAlternateIcon;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtString = (TextView) convertView
                    .findViewById(R.id.text1);

            item.image = (ImageView) convertView
                    .findViewById(R.id.image1);
            item.score_weight = (TextView) convertView.findViewById(R.id.score_weight);
            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }
        //item.score_weight.setVisibility(View.GONE);

        item.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_directions));


        //final TownDTO p = mList.get(position);
        //String string = mStrings[position];
        final EvaluationSiteDTO river = mList.get(position);

        item.txtString.setText((river.getSiteName()== null ? "Site # " + river.getEvaluationSiteID() : river.getSiteName()));
        item.score_weight.setText(getDistance(river.getDistanceFromMe()));
        Statics.setRobotoFontLight(ctx, item.txtString);
        return (convertView);
    }


    static class ViewHolderItem {
        TextView txtString, score_weight;
        ImageView image;
    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,##0.0");

    private String getDistance(float mf) {
        if (mf < 1000) {
            return df.format(mf) + " metres";
        }
        Double m = Double.parseDouble("" + mf);
        Double n = m / 1000;

        return df.format(n) + " kilometres";

    }
}
