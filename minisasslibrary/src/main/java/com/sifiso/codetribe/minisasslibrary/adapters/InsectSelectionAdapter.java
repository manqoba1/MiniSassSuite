package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-02-27.
 */
public class InsectSelectionAdapter extends RecyclerView.Adapter<InsectSelectionAdapter.Holder> {

    private Context mContext;
    private List<InsectImageDTO> mList;
    private ArrayList<InsectImageDTO> selectedInsects;
    private InsectPopupAdapterListener listener;

    public InsectSelectionAdapter(Context mContext, List<InsectImageDTO> mList, InsectPopupAdapterListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        this.listener = listener;
    }

    public void selectInsect(InsectImageDTO insect, int index) {
        if (selectedInsects == null) {
            selectedInsects = new ArrayList<InsectImageDTO>();
        }

        selectedInsects.add(index, insect);
    }

    public void removeSelected(InsectImageDTO insect, int index) {
        if (selectedInsects == null) {
            selectedInsects = new ArrayList<InsectImageDTO>();
        }
        selectedInsects.remove(index);
    }

    public ArrayList<InsectImageDTO> getSelectedInsects() {

        return selectedInsects;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.insect_select_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder h, final int position) {
        final InsectImageDTO insect = mList.get(position);

        int rID = mContext.getResources().getIdentifier(insect.getUri(), "drawable", mContext.getPackageName());
        Log.d("TAG", new Gson().toJson(insect));
        h.INSC_image.setImageResource(rID);

        if (insect.getInsectimagelistList().size() > 1) {
            h.INSC_more.setVisibility(View.VISIBLE);
            h.INSC_more.setText("...more (" + insect.getInsectimagelistList().size() + ")");
        } else {
            h.INSC_more.setVisibility(View.GONE);
        }

        h.INSC_box.setText(insect.getInsect().getGroupName());
        h.INSC_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    insect.setSelected(isChecked);
                    listener.onInsectSelected(insect, position);
                } else {
                    insect.setSelected(isChecked);
                    listener.onInsectSelected(insect, position);
                }
            }
        });
        h.INSC_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insect.setSelected(true);
                listener.onViewMore(insect, position);
            }
        });
        h.INSC_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.INSC_box.setChecked(true);
            }
        });
        if (insect.isSelected()) {
            h.INSC_box.setChecked(true);
            h.footer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.amber_600));
        } else {
            h.INSC_box.setChecked(false);
            h.footer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_500));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface InsectPopupAdapterListener {
        void onInsectSelected(InsectImageDTO insect, int index);

        void onViewMore(InsectImageDTO insect, int index);
    }

    public class Holder extends RecyclerView.ViewHolder {
        protected ImageView INSC_image;
        protected CheckBox INSC_box;
        private TextView INSC_more;
        private View footer;

        public Holder(View itemView) {
            super(itemView);
            INSC_box = (CheckBox) itemView.findViewById(R.id.INSC_box);
            INSC_image = (ImageView) itemView.findViewById(R.id.INSC_image);
            INSC_more = (TextView) itemView.findViewById(R.id.INSC_more);
            footer = itemView.findViewById(R.id.footer);
        }
    }
}
