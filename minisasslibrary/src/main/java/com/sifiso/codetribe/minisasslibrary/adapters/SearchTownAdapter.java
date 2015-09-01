package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;

import java.util.List;

/**
 * Created by sifiso on 3/22/2015.
 */
public class SearchTownAdapter extends BaseAdapter {
    public interface SearchTownAdapterListener {
        public void onTownClicked(TownDTO town);

    }

    Context mCtx;
    List<TownDTO> mList;
    SearchTownAdapterListener mListener;

    public SearchTownAdapter(Context mCtx, List<TownDTO> mList, SearchTownAdapterListener mListener) {
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
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Holder h;
        if (v == null) {
            h = new Holder();
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.xxsimple_spinner_item2, parent, false);
            h.txtString = (TextView) v
                    .findViewById(R.id.text1);

            h.image = (ImageView) v
                    .findViewById(R.id.image1);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }

        final TownDTO town = mList.get(position);
        h.txtString.setText(town.getTownName()+", "+town.getProvinceName());


        return v;

    }
    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(mCtx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }
    class Holder {
        TextView txtString;
        ImageView image;
    }
}
