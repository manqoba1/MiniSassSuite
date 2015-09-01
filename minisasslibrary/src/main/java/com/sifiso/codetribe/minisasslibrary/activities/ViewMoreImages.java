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
import com.sifiso.codetribe.minisasslibrary.adapters.ViewMoreImageAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageListDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;

import java.util.List;

public class ViewMoreImages extends AppCompatActivity {
    static final String LOG = InsectPicker.class.getSimpleName();
    Context ctx;
    double total = 0.0;
    private ViewMoreImageAdapter adapter;
    private RecyclerView SD_list;

    //  private List<InsectImageListDTO> mImageListDTOs;
    private List<InsectImageDTO> mSites;
    Intent intent;
    private InsectImageDTO insect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more_images);
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i(LOG, "onCreate more insect images");
        insect = (InsectImageDTO) getIntent().getSerializableExtra("insect");
        mSites = (List<InsectImageDTO>) getIntent().getSerializableExtra("insetImageList");
        Log.i(LOG, "onCreate select insect " + insect.getInsectimagelistList().size() + " : " + insect.getInsect().getGroupName());
        getSupportActionBar().setTitle(insect.getInsect().getGroupName());
        setFields();
        setList();
    }

    private void setFields() {
        SD_list = (RecyclerView) findViewById(R.id.SD_list);

        SD_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        //SD_list.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        SD_list.setItemAnimator(new DefaultItemAnimator());
        SD_list.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.HORIZONTAL));
    }

    private void setList() {

        adapter = new ViewMoreImageAdapter(ctx, insect.getInsectimagelistList(), R.layout.insect_select_item, new ViewMoreImageAdapter.ViewMoreImageAdapterListener() {
            @Override
            public void onInsectSelected(InsectImageListDTO imageListDTO, int index) {

            }
        });
        SD_list.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_more_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_list) {
            SD_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        }
        if (id == R.id.action_grid) {
            SD_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    static final int RETURN_PICKER = 300;
}
