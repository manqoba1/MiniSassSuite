package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sifiso.codetribe.minisasslibrary.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 2015-03-01.
 */
public class LocalPictureRecyclerAdapter extends RecyclerView.Adapter<LocalPictureRecyclerAdapter.PhotoViewHolder> {

    private List<String> photoList;
    private Context ctx;
    private int rowLayout;

    public LocalPictureRecyclerAdapter(List<String> photos, int rowLayout,
                                       Context context) {
        this.photoList = photos;
        this.ctx = context;
        this.rowLayout = rowLayout;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {

        String st = photoList.get(position);
        holder.number.setText(" " + (position + 1));
        holder.caption.setText(" ");
        holder.date.setText(" ");

        File f = new File(photoList.get(position));

        ImageLoader.getInstance().displayImage(Uri.fromFile(f).toString(),holder.image, new ImageLoadingListener(){
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

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

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", loc);

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        protected ImageView image;
        protected TextView caption, number, date;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.PHOTO_image);
            number = (TextView) itemView.findViewById(R.id.PHOTO_number);
            caption = (TextView) itemView.findViewById(R.id.PHOTO_caption);
            date = (TextView) itemView.findViewById(R.id.PHOTO_date);
        }
    }

}
