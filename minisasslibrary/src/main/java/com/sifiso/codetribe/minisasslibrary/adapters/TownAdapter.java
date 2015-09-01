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
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;

import java.util.List;

/**
 * Created by CodeTribe1 on 2015-03-13.
 */
public class TownAdapter extends BaseAdapter {
    public interface TownAdapterListener {
        public void onTeamClicked(List<TeamDTO> teamList);
        public void onMapClicked(TownDTO team);
    }

    Context mCtx;
    List<TownDTO> mList;
    TownAdapterListener mListener;

    public TownAdapter(Context mCtx, List<TownDTO> mList, TownAdapterListener mListener) {
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
            v = inflater.inflate(R.layout.town_list_item, parent, false);
            h.TLI_teamsCount = (TextView) v.findViewById(R.id.TLI_teamsCount);
            h.TLI_town_uri = (ImageView) v.findViewById(R.id.TLI_town_uri);
            h.TLI_townName = (TextView) v.findViewById(R.id.TLI_townName);
            h.TLI_click = v.findViewById(R.id.TLI_click);
            h.TLI_map = (ImageView) v.findViewById(R.id.TLI_map);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }

        final TownDTO town = mList.get(position);
        h.TLI_townName.setText(town.getTownName());
        h.TLI_teamsCount.setText(town.getTeamList().size() + "");
        h.TLI_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTeamClicked(town.getTeamList());
            }
        });
        h.TLI_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMapClicked(town);
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
    class Holder {
        ImageView TLI_town_uri,TLI_map;
        TextView TLI_townName, TLI_teamsCount;
        View TLI_click;
    }
}
