package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TeamMemberAdapter extends ArrayAdapter<TeamMemberDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<TeamMemberDTO> mList;
    private Context ctx;

    public TeamMemberAdapter(Context context, int textViewResourceId,
                             List<TeamMemberDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;

    static class ViewHolderItem {
        TextView txtName, txtCell;
        TextView txtEmail;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.txtName = (TextView) convertView
                    .findViewById(R.id.MEM_txtName);
            viewHolderItem.txtEmail = (TextView) convertView
                    .findViewById(R.id.MEM_txtEmail);
            viewHolderItem.txtCell = (TextView) convertView
                    .findViewById(R.id.MEM_txtCell);
            viewHolderItem.image = (ImageView) convertView
                    .findViewById(R.id.MEM_image);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }

        TeamMemberDTO tm = mList.get(position);
        viewHolderItem.txtName.setText(tm.getFirstName() + " " + tm.getLastName());
        viewHolderItem.txtCell.setText(tm.getCellphone());
        viewHolderItem.txtEmail
                .setText(tm.getEmail());

        if (tm.getTeamMemberImage() == null) {
            viewHolderItem.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.boy));
        } else {
            ImageLoader.getInstance().displayImage(tm.getTeamMemberImage(), viewHolderItem.image);
        }


        Statics.setRobotoFontBold(ctx, viewHolderItem.txtName);
        animateView(convertView);

        return (convertView);
    }

    static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
    static final DecimalFormat df2 = new DecimalFormat("###,###,###.00");
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat(
            "EEEE, dd MMMM yyyy HH:mm", loc);

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(1000);
        if (view == null)
            return;
        view.startAnimation(a);
    }

}
