package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.FileImportActivityFragment;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.List;

public class FileImportActivity extends AppCompatActivity implements FileImportActivityFragment.ImportListener {
    FileImportActivityFragment fileImportFragment;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_import);
        ctx = getApplicationContext();
        fileImportFragment = (FileImportActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_import, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Util.showToast(this, "under construction");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUsersImported(List<TeamMemberDTO> teamMemberList) {
        setRefreshActionButtonState(false);
        Util.showToast(ctx, "#### member import completed OK");
        response = new ResponseDTO();
        response.setTeamMemberList(teamMemberList);
    }

    @Override
    public void onError(String message) {
        setRefreshActionButtonState(false);
    }

    @Override
    public void setBusy(boolean busy) {
        setRefreshActionButtonState(busy);
    }

    Menu mMenu;
    ResponseDTO response;

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.action_info);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}
