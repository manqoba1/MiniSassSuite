package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.InviteMemberAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.listeners.BusyListener;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

public class InviteMemberActivity extends AppCompatActivity {

    View view;
    Context ctx;
    ResponseDTO response;
    TeamMemberDTO teamMember;
    CheckBox MSG_allCheck;
    Button MSG_btnSend;
    AutoCompleteTextView SLT_editSearch;
    TextView MSG_countLabel, MSG_selectedLabel, MSG_selected, MSG_count;
    BusyListener busyListener;
    ListView MSG_list;
    SwipeRefreshLayout refreshLayout;
    InviteMemberAdapter adapter;
    private String searchText;

    private void setFields() {
        MSG_allCheck = (CheckBox) findViewById(R.id.MSG_allCheck);
        MSG_btnSend = (Button) findViewById(R.id.MSG_btnSend);
        MSG_selected = (TextView) findViewById(R.id.MSG_selected);
        SLT_editSearch = (AutoCompleteTextView) findViewById(R.id.SLT_editSearch);
        SLT_editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(LOG, "change " + s.toString());
                if (s.toString().length() == 0) {
                    return;
                }
                searchText = s.toString();
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        MSG_countLabel = (TextView) findViewById(R.id.MSG_countLabel);
        MSG_selectedLabel = (TextView) findViewById(R.id.MSG_selectedLabel);
        MSG_count = (TextView) findViewById(R.id.MSG_count);
        MSG_list = (ListView) findViewById(R.id.MSG_list);
        //refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        // refreshLayout.setOnRefreshListener(this);
        hideKeyboard();
    }

    private void setList(final String label) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MSG_selected.setText(response.getTeamMemberList().size() + "");
                MSG_selectedLabel.setText(label);
            }
        });
        adapter = new InviteMemberAdapter(ctx, response.getTeamMemberList(), teamMember.getTeam().getTeamID(), new InviteMemberAdapter.InviteMemberAdapterListener() {
            @Override
            public void onInviteAccepted(TeamMemberDTO ten) {
                sendInvite(ten.getTeamMemberID(), teamMember.getTeam().getTeamID());
            }
        });
        MSG_list.setAdapter(adapter);
    }

    private void sendInvite(Integer teamMemberID, Integer teamID) {
        RequestDTO w = new RequestDTO(RequestDTO.SEND_INVITE_TO_TEAM_MEMBER);
        w.setTeamID(teamID);
        w.setTeamMemberID(teamMemberID);

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {

                search(searchText);
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void search(String search) {
        RequestDTO w = new RequestDTO(RequestDTO.SEARCH_MEMBERS);
        w.setSearch(search);
        w.setEmail(teamMember.getEmail());
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }
                        response = r;
                        setList("Search results");
                        CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_SEARCH_DATA, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(final ResponseDTO resp) {

                            }

                            @Override
                            public void onDataCached(ResponseDTO r) {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                            /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                            startService(intent);*/
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void getSearchCacheData() {
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_SEARCH_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                response = r;
                setList("Previous search results");
            }

            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {

            }
        });
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(SLT_editSearch.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member);
        ctx = getApplicationContext();
        teamMember = SharedUtil.getTeamMember(ctx);
        setFields();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teammember, menu);
        getSearchCacheData();
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_bar) {
            onBackPressed();
            return true;
        } else if (i == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_logo);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    Menu mMenu;

    @Override
    public void onBackPressed() {
        Log.w(LOG, "onBackPressed------------");

        finish();

        super.onBackPressed();
    }

    static final String LOG = "InviteMemberActivity";


    private void refreshList() {
        //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
        //when your data has finished loading, cset the refresh state of the view to false
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 5000);

    }
}
