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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TmemberDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.List;

/**
 * Created by sifiso on 2015-07-23.
 */
public class InviteMemberAdapter extends BaseAdapter {
    public interface InviteMemberAdapterListener {
        public void onInviteAccepted(TeamMemberDTO teamMember);
    }

    Context mCtx;
    List<TeamMemberDTO> mList;
    InviteMemberAdapterListener mListener;
    Integer teamID;

    public InviteMemberAdapter(Context mCtx, List<TeamMemberDTO> mList, Integer teamID, InviteMemberAdapterListener mListener) {
        this.mCtx = mCtx;
        this.mList = mList;
        this.mListener = mListener;
        this.teamID = teamID;
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
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.team_member_invite_item, parent, false);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.txtName = (TextView) convertView
                    .findViewById(R.id.MEM_txtName);
            viewHolderItem.txtEmail = (TextView) convertView
                    .findViewById(R.id.MEM_txtEmail);
            viewHolderItem.txtCell = (TextView) convertView
                    .findViewById(R.id.MEM_txtCell);
            viewHolderItem.image = (ImageView) convertView
                    .findViewById(R.id.MEM_image);
            viewHolderItem.btnYes = (ImageView) convertView.findViewById(R.id.btn_yes);
            viewHolderItem.btnNo = (ImageView) convertView.findViewById(R.id.btn_no);
            viewHolderItem.txtMember = (TextView) convertView.findViewById(R.id.txtMember);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }
        final TeamMemberDTO tm = mList.get(position);
        viewHolderItem.txtName.setText(tm.getFirstName() + " " + tm.getLastName());
        viewHolderItem.txtCell.setText(tm.getCellphone());
        viewHolderItem.txtEmail
                .setText(tm.getEmail());

        if (tm.getTeamMemberImage() == null) {
            viewHolderItem.image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.boy));
        } else {
            ImageLoader.getInstance().displayImage(tm.getTeamMemberImage(), viewHolderItem.image);
        }
        viewHolderItem.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onInviteAccepted(tm);
            }
        });
        if (tm.getTeamID() == teamID) {
            viewHolderItem.txtMember.setVisibility(View.VISIBLE);
            viewHolderItem.btnYes.setVisibility(View.GONE);
            viewHolderItem.btnNo.setVisibility(View.GONE);
        } else {
            viewHolderItem.txtMember.setVisibility(View.GONE);
            viewHolderItem.btnYes.setVisibility(View.VISIBLE);
            viewHolderItem.btnNo.setVisibility(View.GONE);
        }
        for (TmemberDTO mt : tm.getTmemberList()) {
            if (mt.getTeamID() == teamID && mt.getAcceptInvite() == 1) {
                viewHolderItem.txtMember.setVisibility(View.VISIBLE);
                viewHolderItem.btnYes.setVisibility(View.GONE);
                viewHolderItem.btnNo.setVisibility(View.GONE);
            } else if(mt.getTeamID() == teamID && mt.getAcceptInvite() == 0){
                viewHolderItem.txtMember.setVisibility(View.VISIBLE);
                viewHolderItem.txtMember.setText("Padding request");
                viewHolderItem.txtMember.setTextColor(mCtx.getResources().getColor(R.color.blue_300));
                viewHolderItem.btnYes.setVisibility(View.GONE);
                viewHolderItem.btnNo.setVisibility(View.GONE);
            }else{
                viewHolderItem.txtMember.setVisibility(View.GONE);
                viewHolderItem.btnYes.setVisibility(View.VISIBLE);
                viewHolderItem.btnNo.setVisibility(View.GONE);
            }
        }
        Statics.setRobotoFontBold(mCtx, viewHolderItem.txtName);
        Statics.setRobotoFontBold(mCtx, viewHolderItem.txtMember);
        animateView(convertView);
        return convertView;
    }

    static class ViewHolderItem {
        TextView txtName, txtCell, txtMember;
        TextView txtEmail;
        ImageView image, btnYes, btnNo;
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(mCtx, R.anim.grow_fade_in_center);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }
}
