package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 2015-02-28.
 */
public class PictureRecyclerAdapter extends RecyclerView.Adapter<PictureRecyclerAdapter.PhotoViewHolder>{

    public PictureRecyclerAdapter(List<ImagesDTO> imagesList, Context ctx, PictureListener evaluationImage) {
    }

    public interface PictureListener {
        public void onPictureClicked(int position);
    }

    private PictureListener listener;
    private List<ImagesDTO> photoList;
    private Context ctx;
    private int rowLayout;

    public PictureRecyclerAdapter(List<ImagesDTO> photos, int rowLayout,
                                  Context context, PictureListener listener) {
        this.ctx = context;
        this.photoList = photos;
        this.listener = listener;
        this.rowLayout = rowLayout;
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", loc);

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView caption, number, date;
        protected int position;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.PHOTO_image);
            caption = (TextView) itemView.findViewById(R.id.PHOTO_caption);
            number = (TextView) itemView.findViewById(R.id.PHOTO_number);
            date = (TextView) itemView.findViewById(R.id.PHOTO_date);
        }
    }

    @Override
    public PictureRecyclerAdapter.PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        ImagesDTO i = photoList.get(position);
        int num = photoList.size() - (position);
        holder.number.setText(" " + num);
        holder.caption.setVisibility(View.GONE);
        holder.date.setText(sdf.format(i.getDateTaken()));
        holder.position = position;

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPictureClicked(position);
            }
        });

        String url = Statics.IMAGE_URL + i.getUri();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, holder.image, new ImageLoadingListener(){
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                holder.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.under_construction));
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return photoList == null ? 0 : photoList.size();
    }



    static final String LOG = PictureRecyclerAdapter.class.getSimpleName();
}
