package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TeamListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TeamMemberListFragment;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamListener;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamMemberAddedListener;
import com.sifiso.codetribe.minisasslibrary.listeners.BusyListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamMemberActivity extends AppCompatActivity implements BusyListener, TeamMemberAddedListener, TeamListener {
    List<PageFragment> pageFragmentList;
    ViewPager mPager;
    Menu mMenu;
    PagerAdapter adapter;
    Context ctx;
    private TeamMemberDTO teamMember;
    private int pagerIdex;
    TeamListFragment teamListFragment;
    TeamMemberListFragment teamMemberListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
       // pagerIdex = getIntent().getIntExtra("index", 1);
        ctx = getApplicationContext();
        if (savedInstanceState != null) {
            teamMember = (TeamMemberDTO) savedInstanceState.getSerializable("teamMember");
            pagerIdex = savedInstanceState.getInt("index");
        } else {
            teamMember = (TeamMemberDTO) getIntent().getSerializableExtra("teamMember");
            pagerIdex = getIntent().getIntExtra("index", 1);

        }
        setFields();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("teamMember", teamMember);
        outState.putInt("index", pagerIdex);
        super.onSaveInstanceState(outState);
    }

    private void setFields() {
        mPager = (ViewPager) findViewById(R.id.SITE_pager);
        PagerTitleStrip strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        pageFragmentList = new ArrayList<PageFragment>();
        teamMemberListFragment = new TeamMemberListFragment();
        teamListFragment = new TeamListFragment();
        Bundle data = new Bundle();
//        Log.i(LOG, new Gson().toJson(teamMember.getTeam().getTeammemberList()));
        data.putSerializable("teamMemberList", (Serializable) teamMember.getTeam().getTeammemberList());
        data.putSerializable("tTeamList", (Serializable) teamMember.getTmemberList());

        teamMemberListFragment.setArguments(data);
        teamListFragment.setArguments(data);

        pageFragmentList.add(teamMemberListFragment);
        //pageFragmentList.add(teamListFragment);

        initializeAdapter();

    }
    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_member, menu);
        return true;
    }

    static String LOG = TeamMemberActivity.class.getSimpleName();

    private void initializeAdapter() {
        try {
            adapter = new PagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(adapter);
            mPager.setCurrentItem(pagerIdex);
            if(pagerIdex == 0){
                getSupportActionBar().setTitle("Team members");
            }else{
                getSupportActionBar().setTitle("Teams");
            }
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    PageFragment pf = pageFragmentList.get(arg0);
                    if (pf instanceof TeamMemberListFragment) {
                        getSupportActionBar().setTitle("Team members");
                    } else if (pf instanceof TeamListFragment) {
                        getSupportActionBar().setTitle("Teams");
                    }


                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            //ZoomOutPageTransformerImpl z = new ZoomOutPageTransformerImpl();
            // mPager.setPageTransformer(true, z);
        } catch (Exception e) {
            Log.e(LOG, "-- Some shit happened, probably IllegalState of some kind ..." + e.toString());
        }
    }

    @Override
    public void setBusy() {

    }

    @Override
    public void setNotBusy() {

    }

    @Override
    public void onTeamPicked(TeamDTO team) {

    }

    @Override
    public void onTeamMemberAdded(List<TeamMemberDTO> list) {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter implements PageFragment {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) pageFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return pageFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";

            switch (position) {
                case 0:
                    title = "Team members";
                    break;
                case 1:
                    title = "Teams";
                    break;

                default:
                    break;
            }
            return title;
        }

        @Override
        public void animateCounts() {

        }
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

        return super.onOptionsItemSelected(item);
    }
}
