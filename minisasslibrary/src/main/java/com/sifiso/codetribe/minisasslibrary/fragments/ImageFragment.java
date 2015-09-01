package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Chris on 2015-03-09.
 */
public class ImageFragment extends Fragment implements PageFragment {

    private Context ctx;
    private String path;
    private ImageView image;
    private TextView txtCount, txtName;
    ImageLoader imageLoader;

    public static ImageFragment newInstance(String path) {
        ImageFragment imf = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        imf.setArguments(args);
        return imf;
    }


    /**
     * Empty constructor, It's a must to have. empty constructor enables fragment manager
     * to instantiate fragment
     * for instance if when the screen orientation changes/rotates*/

    public ImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            path = b.getString("path");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ctx = getActivity();

        imageLoader = ImageLoader.getInstance();
        image = (ImageView) view.findViewById(R.id.image);
        if (path != null) {
            File f = new File(path);
            imageLoader.displayImage(Uri.fromFile(f).toString(), image);

            Picasso.with(ctx).load(f).into(image);
            if (image.getWidth() > image.getHeight()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        return view;
    }


    @Override
    public void animateCounts() {
    Util.animateScaleY(image, 200);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public String getPath() {
        return path;
    }
}
