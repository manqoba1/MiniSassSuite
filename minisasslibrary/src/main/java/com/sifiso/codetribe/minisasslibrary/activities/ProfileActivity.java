package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dialogs.AddMemberDialog;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TeamListFragment;
import com.sifiso.codetribe.minisasslibrary.fragments.TeamMemberListFragment;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamListener;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamMemberAddedListener;
import com.sifiso.codetribe.minisasslibrary.listeners.BitmapListener;
import com.sifiso.codetribe.minisasslibrary.listeners.BusyListener;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.Bitmaps;
import com.sifiso.codetribe.minisasslibrary.util.CloudinaryUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.ImageTask;
import com.sifiso.codetribe.minisasslibrary.util.ImageUtil;
import com.sifiso.codetribe.minisasslibrary.util.PictureUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements BusyListener, TeamMemberAddedListener, TeamListener {

    TextView P_TNAME, P_name, P_phone, P_email, P_EVN_count, textView7;
    Spinner P_sp_team;
    ImageView AP_PP, P_ICON, P_edit;
    ListView P_membersList;
    Button P_add_member, P_inviteMember;

    List<PageFragment> pageFragmentList;
    ViewPager mPager;
    Menu mMenu;
    PagerAdapter adapter;
    Context ctx;
    private TeamMemberDTO teamMember;
    static String LOG = ProfileActivity.class.getSimpleName();
    private AddMemberDialog addMemberDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctx = getApplicationContext();
        getSupportActionBar().setTitle("Profile");

        setFields();
        setRefreshActionButtonState(true);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    private void checkConnection() {
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
        if (wcr.isWifiConnected()) {
            P_ICON.setVisibility(View.VISIBLE);

        }
    }

    private void setFields() {
        mPager = (ViewPager) findViewById(R.id.SITE_pager);
        PagerTitleStrip strip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        P_name = (TextView) findViewById(R.id.P_name);
        // textView7 = (TextView) findViewById(R.id.textView7);
        P_phone = (TextView) findViewById(R.id.P_phone);
        P_email = (TextView) findViewById(R.id.P_email);
        P_EVN_count = (TextView) findViewById(R.id.P_EVN_count);
        AP_PP = (ImageView) findViewById(R.id.AP_PP);
        AP_PP.setDrawingCacheEnabled(true);
        P_TNAME = (TextView) findViewById(R.id.P_TNAME);
        P_edit = (ImageView) findViewById(R.id.P_edit);
        P_ICON = (ImageView) findViewById(R.id.P_ICON);
        P_add_member = (Button) findViewById(R.id.P_add_member);
        P_inviteMember = (Button) findViewById(R.id.P_inviteMember);
        //P_sp_team = (Spinner) findViewById(R.id.P_sp_team);

        P_TNAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(P_TNAME, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {


                    }
                });

            }
        });

        buildPages();
    }

    boolean isEdited;

    private void updateProfilePicture(final TeamMemberDTO tm) {
        Log.e(LOG + " Updated", new Gson().toJson(tm));
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_PROFILE);
        w.setTeamMember(tm);
        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                if (!ErrorUtil.checkServerError(ctx, r)) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOG + " Check", new Gson().toJson(r));

                        if (isEdited) {
                            addMemberDialog.dismiss();
                            Util.showToast(ctx, r.getMessage());
                            isEdited = false;
                        }
                        SharedUtil.saveTeamMember(ctx, tm);
                        teamMember = tm;
                        buildPages();
                    }
                });
            }

            @Override
            public void onVolleyError(final VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showErrorToast(ctx, "Problem: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onError(String message) {

            }
        });
    }


    private void registerMember(final TeamMemberDTO dto) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.REGISTER_TEAM_MEMBER);
        w.setTeamMember(dto);

        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }
                        Util.showToast(ctx, r.getMessage());
                        addMemberDialog.dismiss();
                        teamMember = r.getTeamMember();
                        teamMember.getTeam().getTeammemberList().add(0, dto);
                        SharedUtil.saveTeamMember(ctx, teamMember);
                        buildPages();
                    }
                });

            }

            @Override
            public void onVolleyError(VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showErrorToast(ctx, dto.getEmail() + " already exists in out database");
                    }
                });
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        setRefreshActionButtonState(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_refresh) {
            getProfileData();
            return true;
        } else if (id == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * Start the device camera
     */
    private void startCameraIntent() {
        fileUri = PictureUtil.getImageFileUri();
        Intent cameraIntent = PictureUtil.getCameraIntent(AP_PP, fileUri);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE);
    }

    private void startGalleryIntent() {
        fileUri = PictureUtil.getImageFileUri();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void uploadProfileImageToCDN(final TeamMemberDTO tn, File imgUrl) {
        Log.e(LOG, imgUrl.toString());
        CloudinaryUtil.uploadImagesToCDN(ctx, imgUrl, new CloudinaryUtil.CloudinaryUtilListner() {
            @Override
            public void onSuccessUpload(Map uploadResult) {
                pictureChanged = false;
                String url = (String) uploadResult.get("url");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showToast(ctx, "Profile picture uploaded");
                    }
                });

                tn.setTeamMemberImage(url);
                updateProfilePicture(tn);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onProgress(Integer upload) {

            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    ImageTask.getResizedBitmaps(fileUri, ctx, new BitmapListener() {
                        @Override
                        public void onError() {
                            ToastUtil.errorToast(
                                    ctx,
                                    ctx.getResources().getString(
                                            R.string.error_image_get));
                        }

                        @Override
                        public void onBitmapsResized(Bitmaps bitmaps) {
                            AP_PP.setImageBitmap(bitmaps.getLargeBitmap());
                            // Bitmap bitmap = AP_PP.getDrawingCache();
                            Palette.from(bitmaps.getLargeBitmap()).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch aSwitch = palette.getMutedSwatch();

                                    try {
                                        textView7.setTextColor(aSwitch.getRgb());
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            try {
                                fImage = ImageUtil.getFileFromBitmap(bitmaps.getLargeBitmap(), "picM" + System.currentTimeMillis() + ".jpg");
                                uploadProfileImageToCDN(teamMember, fImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    pictureChanged = true;
                }
                break;
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    ImageTask.getResizedBitmaps(imageUri, ctx,
                            new BitmapListener() {

                                @Override
                                public void onError() {
                                    ToastUtil.errorToast(ctx, ctx.getResources()
                                            .getString(R.string.error_image_get));
                                }

                                @Override
                                public void onBitmapsResized(Bitmaps bitmaps) {
                                    AP_PP.setImageBitmap(bitmaps.getLargeBitmap());
                                    Palette.from(bitmaps.getLargeBitmap()).generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch aSwitch = palette.getMutedSwatch();

                                            try {
                                                textView7.setTextColor(aSwitch.getRgb());
                                            } catch (Exception e) {

                                            }
                                        }
                                    });
                                    try {
                                        fImage = ImageUtil.getFileFromBitmap(bitmaps.getLargeBitmap(), "picM" + System.currentTimeMillis() + ".jpg");
                                        uploadProfileImageToCDN(teamMember, fImage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                    pictureChanged = true;

                } else {
                    ToastUtil.toast(
                            ctx,
                            ctx.getResources().getString(
                                    R.string.image_pick_cancelled));
                }
                break;
        }
        //TODO not belonging here


        super.onActivityResult(requestCode, resultCode, data);
    }

    File fImage;

    private void selectImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(ctx.getResources().getString(R.string.image_source));
        builder.setItems(
                new CharSequence[]{
                        ctx.getResources().getString(R.string.gallery),
                        ctx.getResources().getString(R.string.camera)},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startGalleryIntent();
                                break;
                            case 1:
                                startCameraIntent();
                                break;

                            default:
                                break;
                        }

                    }
                });

        builder.show();
    }

    boolean pictureChanged;
    static final int CAPTURE_IMAGE = 3, PICK_IMAGE = 4;
    private static Uri fileUri;

    @Override
    public void onTeamPicked(TeamDTO team) {

    }

    @Override
    public void onTeamMemberAdded(List<TeamMemberDTO> list) {

    }

    @Override
    public void setBusy() {

    }

    @Override
    public void setNotBusy() {

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

    TeamListFragment teamListFragment;
    TeamMemberListFragment teamMemberListFragment;

    private void buildPages() {
//        calculateDistances();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                teamMember = SharedUtil.getTeamMember(ctx);
                P_name.setText(teamMember.getFirstName() + " " + teamMember.getLastName());
                P_phone.setText((teamMember.getCellphone().equals("") ? "cell not specified" : teamMember.getCellphone()));
                P_email.setText(teamMember.getEmail());

                P_EVN_count.setText((teamMember.getEvaluationCount() == null ? 0 : teamMember.getEvaluationCount()) + "");
                P_TNAME.setText("Team " + teamMember.getTeamName());
                if (teamMember.getTeamMemberImage() == null) {
                    AP_PP.setImageDrawable(ctx.getResources().getDrawable(R.drawable.boy));
                } else {
                    ImageLoader.getInstance().displayImage(teamMember.getTeamMemberImage(), AP_PP);
                    ImageLoader.getInstance().displayImage(teamMember.getTeamMemberImage(), AP_PP, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch aSwitch = palette.getMutedSwatch();
                                    try {
                                        textView7.setTextColor(aSwitch.getRgb());
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                }

                P_add_member.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addMemberDialog = new AddMemberDialog();
                        addMemberDialog.show(getFragmentManager(), LOG);
                        addMemberDialog.setTeamMember(teamMember);
                        addMemberDialog.setFlag(false);
                        addMemberDialog.setListener(new AddMemberDialog.AddMemberDialogListener() {
                            @Override
                            public void membersToBeRegistered(TeamMemberDTO tm) {
                                registerMember(tm);
                            }
                        });
                    }
                });
                P_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMemberDialog = new AddMemberDialog();
                        addMemberDialog.show(getFragmentManager(), LOG);
                        addMemberDialog.setTeamMember(teamMember);
                        addMemberDialog.setFlag(true);
                        addMemberDialog.setListener(new AddMemberDialog.AddMemberDialogListener() {
                            @Override
                            public void membersToBeRegistered(TeamMemberDTO tm) {
                                Log.d(LOG, new Gson().toJson(tm));
                                isEdited = true;
                                updateProfilePicture(tm);
                            }
                        });
                    }
                });
                P_inviteMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, InviteMemberActivity.class);
                        startActivity(intent);
                    }
                });
                AP_PP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImageSource();
                    }
                });

            }
        });
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
        pageFragmentList.add(teamListFragment);

        initializeAdapter();


    }

    private void initializeAdapter() {
        try {
            adapter = new PagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(adapter);
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    PageFragment pf = pageFragmentList.get(arg0);


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

    private void getProfileData() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_MEMBER);
        w.setTeamMemberID(teamMember.getTeamMemberID());
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setRefreshActionButtonState(true);
            }
        });
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }
                        teamMember = r.getTeamMember();
                        SharedUtil.saveTeamMember(ctx, teamMember);

                        buildPages();
                    }
                });

            }

            @Override
            public void onVolleyError(final VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.showErrorToast(ctx, "Problem: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onError(String message) {

            }
        });

    }
}
