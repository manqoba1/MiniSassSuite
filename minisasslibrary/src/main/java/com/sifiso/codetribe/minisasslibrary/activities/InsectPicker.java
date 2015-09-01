package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.InsectSelectionAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class InsectPicker extends AppCompatActivity {
    static final String LOG = InsectPicker.class.getSimpleName();
    Context ctx;
    double total = 0.0;
    private InsectSelectionAdapter adapter;
    private RecyclerView SD_list;

    private List<InsectImageDTO> mRawImages;
    private List<InsectImageDTO> mSelectedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_picker);
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.i(LOG, "onCreate select insect");
        //mRawImages = new ArrayList<>();
        mRawImages = (List<InsectImageDTO>) getIntent().getSerializableExtra("insetImageList");
        Log.i(LOG, "onCreate select insect " + mRawImages.size());
        setFields();
        setList();
    }

    private void setList() {

        adapter = new InsectSelectionAdapter(ctx, mRawImages, R.layout.insect_select_item, new InsectSelectionAdapter.InsectPopupAdapterListener() {
            @Override
            public void onInsectSelected(InsectImageDTO insect, int index) {

                collectCheckedInsects(insect);

            }

            @Override
            public void onViewMoreImages(InsectImageDTO insect, int index) {
                intent = new Intent(InsectPicker.this, ViewMoreImages.class);
                intent.putExtra("insetImageList", (java.io.Serializable) mRawImages);
                intent.putExtra("insect", insect);
                startActivityForResult(intent, RETURN_PICKER);
            }
        });
        SD_list.setAdapter(adapter);

    }


    private void collectCheckedInsects(InsectImageDTO mDtos) {
        if (mSelectedImages == null) {
            mSelectedImages = new ArrayList<InsectImageDTO>(mRawImages.size());
        }


        if (mDtos.selected == true) {
            //if (listCal.contains(mDtos))
            mSelectedImages.add(mDtos);
            total = total + mDtos.getSensitivityScore();

        } else {
            mDtos.selected = true;
            mSelectedImages.remove(mDtos);
            total = total - mDtos.getSensitivityScore();
            mDtos.selected = false;
        }
        Log.e(LOG, mSelectedImages.size() + " count");
        intent = new Intent(InsectPicker.this, EvaluationActivity.class);
        intent.putExtra("overallInsect", (java.io.Serializable) mRawImages);
        intent.putExtra("selectedInsects", (java.io.Serializable) mSelectedImages);
        setResult(INSECT_DATA, intent);
        //listener.onSelectDone(listCal);
    }

    Intent intent;
    static final int INSECT_DATA = 103;

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(InsectPicker.this, EvaluationActivity.class);
        intent.putExtra("overallInsect", (java.io.Serializable) mRawImages);
        intent.putExtra("selectedInsects", (java.io.Serializable) mSelectedImages);
        setResult(INSECT_DATA, intent);
        super.onBackPressed();
    }

    private void setFields() {
        SD_list = (RecyclerView) findViewById(R.id.SD_list);
        SD_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //SD_list.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        SD_list.setItemAnimator(new DefaultItemAnimator());
        SD_list.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.HORIZONTAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insect_picker, menu);
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
        if (id == android.R.id.home) {
            intent = new Intent(InsectPicker.this, EvaluationActivity.class);
            intent.putExtra("overallInsect", (java.io.Serializable) mRawImages);
            intent.putExtra("selectedInsects", (java.io.Serializable) mSelectedImages);
            setResult(INSECT_DATA, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    static final int RETURN_PICKER = 300;

}
