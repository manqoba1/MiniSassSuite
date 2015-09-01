package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.List;

/**
 * Created by sifiso on 4/18/2015.
 */
public class PopupInsectSelectedAdapter extends ArrayAdapter<EvaluationInsectDTO> {


    static final String LOG = PopupListAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    View view;
    private List<EvaluationInsectDTO> mList;
    private String[] mStrings;
    private Context ctx;
    private String title;
    private boolean showAlternateIcon;


    public PopupInsectSelectedAdapter(Context context, int textViewResourceId,
                                      List<EvaluationInsectDTO> list, boolean showAlternateIcon) {

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
            item.score_weight =(TextView) convertView.findViewById(R.id.score_weight);
            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        item.image.setColorFilter(ctx.getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
        item.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_crab));


        final EvaluationInsectDTO insect = mList.get(position);
        item.txtString.setText(insect.getInsect().getGroupName());
        item.score_weight.setText(insect.getInsect().getSensitivityScore()+"");
        Statics.setRobotoFontLight(ctx, item.txtString);
        Statics.setRobotoFontLight(ctx, item.score_weight);
        return (convertView);
    }

    static class ViewHolderItem {
        TextView txtString,score_weight;
        ImageView image;

    }
}
