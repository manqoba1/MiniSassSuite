package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.ImageFragment;
import com.sifiso.codetribe.minisasslibrary.util.PhotoCache;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {

    TextView txtNumber, txtTitle, txtSubTitle, txtNext, txtPrev, txtDate;
    ImageView imageView;
    String url;
    ImagesDTO images;
    EvaluationDTO evaluation;
    EvaluationSiteDTO evaluationSite;
    EvaluationImageDTO evaluationImage;
    PhotoCache photoCache;
    int index;
    private boolean isLandscape;
    Context ctx;
    private static final Locale loc = Locale.getDefault();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", loc);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ctx = getApplicationContext();
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index");
            evaluationImage = (EvaluationImageDTO) savedInstanceState.getSerializable("evaluationImage");
            evaluation = (EvaluationDTO) savedInstanceState.getSerializable("evaluation");
        } else {
            evaluation = (EvaluationDTO) getIntent().getSerializableExtra("evaluation");
            evaluationImage = (EvaluationImageDTO) getIntent().getSerializableExtra("evaluationImage");
            index = getIntent().getIntExtra("index", 0);
        }

        setFields();
        txtNumber.setText(" " + (index + 1));

        photoCache = new PhotoCache();
        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL);
        if (evaluation != null) {
            photoCache.setImageUploadList(evaluation.getEvaluationimageList());
            EvaluationImageDTO dto = photoCache.getImageUploadList().get(index);
            sb.append(dto.getFileName());
            txtTitle.setText(evaluationImage.getEvaluationImageID());
            txtSubTitle.setText(evaluation.getRemarks());
            txtDate.setText(sdf.format(dto.getDateTaken()));
       }
       url = sb.toString();
        Picasso.with(ctx).load(url).into(imageView);
        Util.animateScaleY(imageView, 200);
        setHeader();

    }

    private void setHeader() {

    }

    private void setFields() {
        txtNumber = (TextView) findViewById(R.id.IMG_number);
        imageView = (ImageView) findViewById(R.id.IMG_image);
        txtTitle = (TextView) findViewById(R.id.IMG_title);
        txtSubTitle = (TextView) findViewById(R.id.IMG_subtitle);
        txtDate = (TextView) findViewById(R.id.IMG_date);
        txtNext = (TextView) findViewById(R.id.IMG_next);
        txtPrev = (TextView) findViewById(R.id.IMG_prev);

        Statics.setRobotoFontLight(ctx, txtTitle);
        Statics.setRobotoFontLight(ctx, txtSubTitle);
        Statics.setRobotoFontLight(ctx, txtDate);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        txtNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                index++;
                if (index == photoCache.getImageUploadList().size()) {
                    index = 0;
                }
                loadImage();
            }
        });
        txtPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0) return;
                index--;
                if (index < 0) {
                    index = 0;
                }
                loadImage();
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(ctx.getString(R.string.check_image))
                .setMessage(ctx.getString(R.string.check_image_text))
                .setPositiveButton(ctx.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        startMap();
                    }
                })
                        .setNegativeButton(ctx.getString(R.string.no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                            }
                        })
                        .show();

    }

    private void startMap() {
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("evaluationSite", evaluationSite);
        i.putExtra("index", index);
        startActivity(i);
    }

    private void loadImage() {
        StringBuilder sb = new StringBuilder();
        EvaluationImageDTO dto = photoCache.getImageUploadList().get(index);
        sb.append(Statics.IMAGE_URL)
                .append(dto.getFileName());
        Picasso.with(ctx).load(sb.toString()).into(imageView);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(sb.toString(),
                imageView, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            isLandscape = true;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                        } else {
                            isLandscape = false;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        Log.e(LOG, "onLoadingComplete - height: " + bitmap.getHeight() +
                        "width: " + bitmap.getWidth() + "isLandscape: " + isLandscape);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
        txtNumber.setText(" " + (index + 1));
        txtDate.setText(sdf.format(dto.getDateTaken()));
        animate();
        Util.animateRotationY(txtNext, 500);
    }


    private void animate () {
        Util.animateSlideRight(imageView, 500);
        Util.animateRotationY(txtNumber, 500);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_camera) {
            Intent i = new Intent(this, ImageFragment.class);
            if (evaluation != null) {
                i.putExtra("type", ImagesDTO.EVALUATION_IMAGE);
                i.putExtra("evaluation", evaluation);
            }

            startActivityForResult(i, Picture_REQ);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
static final int Picture_REQ = 221;
static final String LOG = ImageActivity.class.getSimpleName();
}
