package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.List;

/**
 * Created by sifiso on 2015-05-22.
 */
public class PopupAdapter extends ArrayAdapter<String> {


    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<String> mList;
    private Context ctx;
    private String title;
    private boolean showAlternateIcon;
    public static final int ENGINEER_LIST = 1, TASK_LIST = 2,
            STAFF_ACTIONS = 3, INVOICE_ACTIONS = 4, SITE_LIST = 5, PROJECT_LIST = 6;

    static final String LOG = PopupListAdapter.class.getSimpleName();

    public PopupAdapter(Context context, int textViewResourceId,
                        List<String> list, boolean showAlternateIcon) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        this.showAlternateIcon = showAlternateIcon;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    View view;


    static class ViewHolderItem {
        TextView txtString, score_weight;
        ImageView image;
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
        if (showAlternateIcon) {
            item.score_weight.setVisibility(View.VISIBLE);
            item.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.xblue_oval_smaller));
        } else {
            item.score_weight.setVisibility(View.GONE);
            item.image.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.ic_input_add));
        }
        final String p = mList.get(position);

        item.txtString.setText(p);
        Statics.setRobotoFontLight(ctx, item.txtString);
        return (convertView);
    }


}
