package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;

import java.util.List;

public class Evaluation2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Evaluation2Activity","################ onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        startInsectPicker();
    }

    List<InsectImageDTO> insectImages;
    static final int INSECT_DATA = 188;

    private void startInsectPicker() {
        Intent intent = new Intent(this, InsectPickerActivity.class);
        intent.putExtra("insetImageList", (java.io.Serializable) insectImages);
        startActivityForResult(intent, INSECT_DATA);
    }
}
