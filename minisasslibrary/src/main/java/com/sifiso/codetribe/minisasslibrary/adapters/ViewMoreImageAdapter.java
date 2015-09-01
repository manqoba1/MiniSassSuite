package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageListDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sifiso on 2015-06-04.
 */
public class ViewMoreImageAdapter extends RecyclerView.Adapter<ViewMoreImageAdapter.Holder> {
    private Context mContext;
    private List<InsectImageListDTO> mList;
    private ArrayList<InsectImageListDTO> selectedInsects;
    private int rowLayout;
    ViewMoreImageAdapterListener listener;

    public interface ViewMoreImageAdapterListener {
        public void onInsectSelected(InsectImageListDTO imageListDTO, int index);
    }

    public ViewMoreImageAdapter(Context mContext, List<InsectImageListDTO> mList, int rowLayout, ViewMoreImageAdapterListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.rowLayout = rowLayout;
        this.listener = listener;
    }

    public ArrayList<InsectImageListDTO> getSelectedInsects() {

        return selectedInsects;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.insect_view_more_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int position) {
        final InsectImageListDTO insect = mList.get(position);

        int rID = mContext.getResources().getIdentifier(insect.getUrl(), "drawable", mContext.getPackageName());

        h.INSC_image.setImageResource(rID);
        h.INSC_name.setText(insect.getImageName());

       // h.INSC_box.setText(insect.getImageName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        protected ImageView INSC_image;
        protected CheckBox INSC_box;
        private TextView INSC_name;

        public Holder(View itemView) {
            super(itemView);
           // INSC_box = (CheckBox) itemView.findViewById(R.id.INSC_box);
            INSC_image = (ImageView) itemView.findViewById(R.id.INSC_image);
            INSC_name = (TextView) itemView.findViewById(R.id.INSC_name);
        }
    }
}
