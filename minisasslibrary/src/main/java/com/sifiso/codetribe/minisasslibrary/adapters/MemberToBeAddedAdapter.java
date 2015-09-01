package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.List;

/**
 * Created by sifiso on 2015-06-25.
 */
public class MemberToBeAddedAdapter extends BaseAdapter {
    public interface MemberToBeAddedAdapterListener {
        public void onRemoveMember(TeamMemberDTO dto);
    }

    Context mCtx;
    List<TeamMemberDTO> mList;
    MemberToBeAddedAdapterListener mListener;

    public MemberToBeAddedAdapter(Context mCtx, List<TeamMemberDTO> mList, MemberToBeAddedAdapterListener mListener) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder item;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.xxx_member_item, parent, false);
            item = new Holder();
            item.text1 = (TextView) convertView
                    .findViewById(R.id.text1);

            item.image1 = (ImageView) convertView
                    .findViewById(R.id.image1);
            item.remove = (ImageButton) convertView.findViewById(R.id.remove);
            convertView.setTag(item);
        } else {
            item = (Holder) convertView.getTag();
        }

        final TeamMemberDTO dto = mList.get(position);
        item.text1.setText(dto.getFirstName() + " " + dto.getLastName());
        item.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveMember(dto);
            }
        });
        Statics.setRobotoFontLight(mCtx, item.text1);
        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(mCtx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }

    class Holder {
        ImageView image1;
        TextView text1;
        ImageButton remove;
    }
}
