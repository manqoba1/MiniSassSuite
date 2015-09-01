package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.List;

public class PopupListAdapter extends ArrayAdapter<CategoryDTO> {


    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<CategoryDTO> mList;
    private Context ctx;
    private String title;
    private boolean showAlternateIcon;


    static final String LOG = PopupListAdapter.class.getSimpleName();

    public PopupListAdapter(Context context, int textViewResourceId,
                            List<CategoryDTO> list, boolean showAlternateIcon) {

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
        item.score_weight.setVisibility(View.GONE);

        final CategoryDTO p = mList.get(position);
        if (p.getCategoryId() == 8) {
            item.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_sand));

        } else {
            item.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_rock));

        }
        item.txtString.setText(p.getCategoryName());
        Statics.setRobotoFontLight(ctx, item.txtString);
        return (convertView);
    }

}
