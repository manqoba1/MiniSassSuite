package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by aubreymalabie on 4/26/16.
 */
public class DropDownAdapter extends ArrayAdapter<RiverDTO> {

    public interface DropDownListener {
        void onRiverSelected(RiverDTO river);
    }
    List<RiverDTO> rivers;
    DropDownListener listener;
    LayoutInflater mInflater;

    public DropDownAdapter(Context context, List<RiverDTO> rivers, DropDownListener listener) {
        super(context, 0, rivers);
        this.rivers = rivers;
        this.listener = listener;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    static class ViewHolderItem {
        TextView txtName, txtDistance;
        ImageView icon;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dropdown, null);
            item = new ViewHolderItem();
            item.txtName = (TextView) convertView
                    .findViewById(R.id.label);
            item.txtDistance = (TextView) convertView
                    .findViewById(R.id.distance);

            item.icon = (ImageView) convertView
                    .findViewById(R.id.icon);
            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        final RiverDTO p = rivers.get(position);
        item.txtName.setText(p.getRiverName());
        item.txtDistance.setText(df.format(p.getDistanceFromMe()));

        return (convertView);
    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,##0.0");

}
