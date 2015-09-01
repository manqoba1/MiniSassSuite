package com.sifiso.codetribe.minisasslibrary.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.FullPictureAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FullPhotoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    int index;
    FullPictureAdapter adapter;
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", loc);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo);
        recyclerView = (RecyclerView) findViewById(R.id.FI_recyclerView);

        evaluationImage = (EvaluationImageDTO)getIntent().getSerializableExtra("evaluationImage");
        setTitle("evaluationImage fileName: " + evaluationImage.getFileName());
        getSupportActionBar().setSubtitle(evaluationImage.getFileName());

        int index = 0;
        for (ImagesDTO ei : evaluation.getImagesList()) {
            ei.setIndex(evaluation.getEvaluationimageList().size() - index);
            index++;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), RecyclerView.HORIZONTAL));
        adapter = new FullPictureAdapter(evaluation.getImagesList(), 1, getApplicationContext(), new FullPictureAdapter.PictureListener(){
            @Override
            public void onPictureClicked(int position) {

            }
        });

        recyclerView.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_photo, menu);
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
        if(id==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
