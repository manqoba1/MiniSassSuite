package com.sifiso.codetribe.minisasslibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ImageAdapter extends ArrayAdapter<ImagesDTO> {

    private final LayoutInflater mInflator;
    private final int mLayoutRes;
    private List<ImagesDTO> mList;
    private Context ctx;
    View view;
    final static String LOG = ImageAdapter.class.getSimpleName();

    public ImageAdapter(Context context, int textViewResourceId,
                        List<ImagesDTO> list) {
        super(context, textViewResourceId);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (list != null)
            Log.e(LOG, "building imageAdapter list" + list.size());
    }

    @Override
    public int getCount() {
        Log.e(LOG, "items in ImageAdapter:" + mList.size());
        return mList.size();
    }

    static class ViewHolderItem {
        TextView txtNumber;
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem item;
        if (convertView == null) {
            convertView = mInflator.inflate(mLayoutRes, null);
            item = new ViewHolderItem();
            item.txtNumber = (TextView) convertView
                    .findViewById(R.id.number);
            item.img = (ImageView) convertView
                    .findViewById(R.id.image);

            convertView.setTag(item);
        } else {
            item = (ViewHolderItem) convertView.getTag();
        }

        ImagesDTO i = mList.get(position);

        item.txtNumber.setText(" " + (position + 1));
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL);
        sb.append(i.getUri());

        ImageLoader.getInstance().displayImage(sb.toString(), item.img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                Log.i(LOG, "FIRED onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Log.i(LOG, "FIRED onLoadingFailed");
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.i(LOG, "FIRED onLoadingComplete");
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                Log.i(LOG, "FIRED onLoadingCancelled");
            }
        });

        Statics.setRobotoFontLight(ctx, item.txtNumber);

        return (convertView);
    }


    static final Locale x = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", x);
    static final DecimalFormat df = new DecimalFormat("__0.0");

}
