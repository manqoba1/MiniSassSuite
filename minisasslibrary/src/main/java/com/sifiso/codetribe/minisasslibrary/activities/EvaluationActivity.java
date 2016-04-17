package com.sifiso.codetribe.minisasslibrary.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.CategoryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.ConditionsDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.StreamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class EvaluationActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    static final String LOG = EvaluationActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView txtTotal, txtNumber, txtAverage, txtScoreStatus;
    private ImageView scoreIcon, AE_pin_point, ivCancel;
    private TextView WT_sp_riverConnected, EDT_comment, AE_down_up;
    private Button btnShowInsects, btnSave, btnNearbySites;
    private AutoCompleteTextView WT_sp_river, WT_sp_stream;
    FloatingActionButton fab;
    //layouts
    RelativeLayout result2, result3;
    LinearLayout llSiteLayout;
    EvaluationDTO evaluationDTO;
    Integer conditionID = null;
    private Activity activity;
    private TeamMemberDTO teamMember;
    public static final int MAP_REQUESTED = 9007;
    public static final int STATUS_CODE = 220;
    public static final int INSECT_DATA = 103;
    public static final int CREATE_EVALUATION = 108;

    public static final int GPS_DATA = 102;

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    boolean mResolvingError;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";

    Menu mMenu;
    ResponseDTO response;
    boolean mBound, mShowMore;
    RequestSyncService mService;

    private Context ctx;
    RelativeLayout GPS_layout4;
    private TextView txtLat, txtLng, txtSiteName;

    private void setField() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        radioRocky = (RadioButton) findViewById(R.id.radioRocky);
        radioSandy = (RadioButton) findViewById(R.id.radioSandy);

        radioRocky.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    indexCat = 0;
                    try {
                        category = response.getCategoryList().get(indexCat);
                        evaluationSite.setCategoryID(category.getCategoryId());

                        Log.e(LOG, "category selected: " + (category.getCategoryName()));
                    } catch (Exception e) {
                        Log.e(LOG, "Failed", e);
                    }
                }
            }
        });
        radioSandy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    indexCat = 1;
                    try {
                        category = response.getCategoryList().get(indexCat);
                        evaluationSite.setCategoryID(category.getCategoryId());
                        Log.e(LOG, "category selected: " + (category.getCategoryName()));
                    } catch (Exception e) {
                        Log.e(LOG, "Failed 2", e);
                    }
                }
            }
        });
        llSiteLayout = (LinearLayout) findViewById(R.id.llSiteLayout);
        llSiteLayout.setVisibility(View.GONE);
        txtSiteName = (TextView) findViewById(R.id.txtSiteName);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.expandOrCollapse(llSiteLayout, 100, false, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        llSiteLayout.setVisibility(View.GONE);
                        WT_sp_river.setEnabled(true);
                    }
                });
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        WC_score = (EditText) findViewById(R.id.WC_score);
        WT_score = (EditText) findViewById(R.id.WT_score);
        WP_score = (EditText) findViewById(R.id.WP_score);
        WO_score = (EditText) findViewById(R.id.WO_score);
        WE_score = (EditText) findViewById(R.id.WE_score);
        result3 = (RelativeLayout) findViewById(R.id.result3);
        result3.setVisibility(View.GONE);
        result2 = (RelativeLayout) findViewById(R.id.result2);
        txtTotal = (TextView) findViewById(R.id.TV_total_score);
        txtNumber = (TextView) findViewById(R.id.TV_average_score);
        txtAverage = (TextView) findViewById(R.id.TV_avg_score);
        txtScoreStatus = (TextView) findViewById(R.id.txtScoreStatus);
        scoreIcon = (ImageView) findViewById(R.id.scoreIcon);
        WT_sp_river = (AutoCompleteTextView) findViewById(R.id.WT_sp_river);
        WT_sp_stream = (AutoCompleteTextView) findViewById(R.id.WT_sp_stream);
        WT_sp_riverConnected = (TextView) findViewById(R.id.WT_sp_riverConnected);
        EDT_comment = (EditText) findViewById(R.id.EDT_comment);
        AE_pin_point = (ImageView) findViewById(R.id.AE_pin_point);
        AE_pin_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });
        btnShowInsects = (Button) findViewById(R.id.btnShowInsect);
        btnSave = (Button) findViewById(R.id.AE_create);
        AE_down_up = (TextView) findViewById(R.id.AE_down_up);
        btnShowInsects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(LOG, "%%%%%%%%%%%%%%%%%%%%%%%%%%% btnShowInsects onClick -------->");
                if (category == null) {
                    Log.w(LOG, "%%%%%%%%%%%%%%%%%%%%%%%%%%% btnShowInsects onClick - category is null");
                    Snackbar.make(btnSave, "Select Rocky or Sandy river types first, " +
                            "before choosing macroinvertebrates", Snackbar.LENGTH_LONG).show();
                    return;
                } else {
                    Log.d(LOG, "InsectPickerActivity about to start, insectImageList: " + response.getInsectimageDTOList().size());
                    Intent intent = new Intent(EvaluationActivity.this, InsectPickerActivity.class);
                    intent.putExtra("insetImageList", (java.io.Serializable) response.getInsectimageDTOList());
                    startActivityForResult(intent, INSECT_DATA);
                    return;
                }
            }
        });
        AE_down_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(AE_down_up, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (mShowMore) {
                            mShowMore = false;
                            result2.setVisibility(View.VISIBLE);
                            AE_down_up.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                        } else {
                            mShowMore = true;
                            result2.setVisibility(View.GONE);
                            AE_down_up.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                        }
                    }
                });

            }
        });
        btnNearbySites = (Button) findViewById(R.id.AE_find_near_sites);
        btnNearbySites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnNearbySites, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        getEvaluationsByRiver();
                    }
                });

            }
        });
        //codeStatus = getIntent().getIntExtra("statusCode", 0);
        setMoreButtons();

    }

    private void getCachedData() {
        String path = getIntent().getStringExtra("riverCreate");
        if (path != null) {
            File file = new File(path);
            try {
                String json = FileUtils.readFileToString(file);
                Gson gson = new Gson();
                river = gson.fromJson(json, RiverDTO.class);
                WT_sp_riverConnected.setText(river.getRiverName());
                WT_sp_riverConnected.setVisibility(View.VISIBLE);

                setStreamSpinner(river.getStreamList());
                Util.expand(WT_sp_stream, 2000, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        WT_sp_stream.setVisibility(View.VISIBLE);
                    }
                });
                Log.e(LOG, CREATE_EVALUATION + "****************************Evaluation River: " + river.getRiverName());
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                response = r;
                river = response.getRiverList().get(0);
                WT_sp_riverConnected.setText(river.getRiverName());
                WT_sp_riverConnected.setVisibility(View.VISIBLE);

                if (!river.getStreamList().isEmpty()) {
                    setStreamSpinner(river.getStreamList());
                    Util.expand(WT_sp_stream, 2000, new Util.UtilAnimationListener() {
                        @Override
                        public void onAnimationEnded() {
                            WT_sp_stream.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {
                Log.e(LOG, "Failed to get cached data");
            }
        });

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.w(LOG, "## RequestSyncService ServiceConnection: onServiceConnected");
            RequestSyncService.LocalBinder binder = (RequestSyncService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
            if (wcr.isWifiConnected() || wcr.isMobileConnected()) {
                mService.startSyncCachedRequests(new RequestSyncService.RequestSyncListener() {
                    @Override
                    public void onTasksSynced(int goodResponses, int badResponses) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });

               /* Util.pretendFlash(progressBar, 1000, 2, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });*/
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w(LOG, "## RequestSyncService onServiceDisconnected");
            mBound = false;
        }
    };

    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG, "*********** onCreate **********************");
        setContentView(R.layout.activity_evaluation);
        ctx = getApplicationContext();
        activity = this;

        teamMember = SharedUtil.getTeamMember(ctx);

        setField();
        getCachedData();
        getSupportActionBar().setTitle("");
        Util.setCustomActionBar(ctx, getSupportActionBar(),
                "New Observation", "Collect and record data",
                ContextCompat.getDrawable(ctx, R.drawable.ic_launcher), null);

    }


    private RiverDTO river;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        location = savedInstanceState.getParcelable("location");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        outState.putParcelable("location", location);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
//        mMenu = menu;
//        cleanForm();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            stopLocationUpdates();
        }
        if (id == R.id.action_refresh) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        switch (reqCode) {
            case MAP_REQUESTED:
                Log.w(LOG, "### map has returned with data?");
                if (resultCode == RESULT_OK) {
                    response = (ResponseDTO) data.getSerializableExtra("response");
                    teamMember = SharedUtil.getTeamMember(ctx);
                }
                break;
            case STATUS_CODE:
                Log.w(LOG, "### setting ui has returned with data?");
                if (resultCode == RESULT_OK) {
                    response = (ResponseDTO) data.getSerializableExtra("response");
                }
                break;
            case CREATE_EVALUATION:
                Log.w(LOG, "### setting ui has returned with data?");
                if (resultCode == CREATE_EVALUATION) {
                    RiverDTO riv = (RiverDTO) data.getSerializableExtra("riverCreate");
                    response = (ResponseDTO) data.getSerializableExtra("response");
                    river = riv;
                    WT_sp_river.setText(riv.getRiverName());
                }
                break;
            case GPS_DATA:
                Log.w(LOG, "### gps ui has returned with data?");
                if (resultCode == GPS_DATA) {
                    Log.w(LOG, "### gps ui has returned with data?");

                    setEvaluationSiteData((EvaluationSiteDTO) data.getSerializableExtra("siteData"));
                    // evaluationFragment.setResponse(response);
                    teamMember = SharedUtil.getTeamMember(ctx);
                }
                break;
            case INSECT_DATA:
                Log.w(LOG, "### insects ui has returned with data?");
                if (resultCode == RESULT_OK) {
                    isInsectsPickerBack = true;
                    if ((List<InsectImageDTO>) data.getSerializableExtra("selectedInsects") != null) {

                        insectImageList = (List<InsectImageDTO>) data.getSerializableExtra("overallInsect");
                        scoreUpdater((List<InsectImageDTO>) data.getSerializableExtra("selectedInsects"));

                        result3.setVisibility(View.VISIBLE);
                        teamMember = SharedUtil.getTeamMember(ctx);
                    }
                }
                break;
        }
    }

    CategoryDTO category;

    public void setFieldsFromSelectedSite() {
        category = evaluationSite.getCategory();
        if (category.getCategoryName().contains("Rocky")) {
            radioRocky.setChecked(true);
        }
        if (category.getCategoryName().contains("Sandy")) {
            radioSandy.setChecked(true);
        }

        txtSiteName.setText("Site #" + evaluationSite.getEvaluationSiteID());
        Util.expandOrCollapse(llSiteLayout, 100, true, new Util.UtilAnimationListener() {
            @Override
            public void onAnimationEnded() {

            }
        });
    }

    RadioButton radioRocky, radioSandy;
    List<String> streamTitles;


    private void setStreamSpinner(List<StreamDTO> streamList) {
        streamTitles = new ArrayList<>();
        for (StreamDTO s : streamList) {
            streamTitles.add(s.getStreamName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(ctx, R.layout.xsimple_spinner_dropdown_item, streamTitles);
        WT_sp_stream.setAdapter(arrayAdapter);
        WT_sp_stream.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private Integer indexCat;

    private void setMoreButtons() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(btnSave, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                        if (category == null) {
                            ToastUtil.toast(ctx, "Please select category");
                            return;
                        }

                        if (evaluationSite == null || (accuracy == null || accuracy == 0)) {
                            ToastUtil.toast(ctx, "Observation site not defined, Once the site is defined, the red WORLD icon will change to blue. Make sure you LOCATION setting is enabled");

                            AE_pin_point.setVisibility(View.VISIBLE);
                            AE_pin_point.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                            Util.flashOnce(AE_pin_point, 200, new Util.UtilAnimationListener() {
                                @Override
                                public void onAnimationEnded() {

                                }
                            });

                            return;
                        }
                        if (selectedInsects == null) {
                            //selectedInsects = new ArrayList<>();
                            ToastUtil.toast(ctx, "Select macroinvertebrates to score your observation (Hint: Select macroinvertebrates)");
                            return;
                        }
                        if (conditionID == null) {
                            ToastUtil.toast(ctx, "Condition not specified, Please select macroinvertebrates found");
                            return;
                        }

                        btnSave.setEnabled(false);
                        localSaveRequests();

                    }
                });

            }
        });

    }

    private void setImageList() {
        insectImageList = new ArrayList<>();
        for (InsectDTO i : response.getInsectList()) {
            for (InsectImageDTO ii : i.getInsectimageDTOList()) {
                insectImageList.add(ii);
            }
        }

    }

    public void scoreUpdater(List<InsectImageDTO> insectImages) {
        selectedInsects = new ArrayList<>();
        if (insectImages == null) {
            result3.setVisibility(View.GONE);
            return;
        } else {
            result3.setVisibility(View.VISIBLE);
        }
        txtAverage.setText(calculateAverage(insectImages) + "");
        txtNumber.setText(((insectImages != null) ? insectImages.size() : 0.0) + "");
        selectedInsects = insectImages;
        statusScore(insectImages, category.getCategoryName());
        txtTotal.setText(calculateScore(insectImages) + "");
        Log.e(LOG, calculateScore(insectImages) + "Yes");
    }

    private List<InsectImageDTO> selectedInsects;

    private void cleanForm() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTotal.setText("0.0");
                txtNumber.setText("0.0");
                txtAverage.setText("0.0");
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.gray_crap));
                WT_sp_river.setText(null);
                EDT_comment.setText(null);
                WT_sp_stream.setText(null);
                WT_sp_stream.setVisibility(View.GONE);
                AE_pin_point.setVisibility(View.GONE);
                result3.setVisibility(View.GONE);

                evaluationSite = new EvaluationSiteDTO();
                evaluationDTO = new EvaluationDTO();

                txtScoreStatus.setText("not specified");
                txtScoreStatus.setTextColor(getResources().getColor(R.color.gray));
                scoreIcon.setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY);
                txtTotal.setTextColor(getResources().getColor(R.color.gray));
                WE_score.setText("");
                WO_score.setText("");
                WC_score.setText("");
                WP_score.setText("");
                WT_score.setText("");
                category = null;
                conditionID = null;
                radioRocky.setChecked(false);
                radioSandy.setChecked(false);
                btnSave.setEnabled(true);
            }
        });


    }

    private Integer conditionIDFunc(List<CategoryDTO> categorys, int statusCondition, int categoryID) {
        Integer cond = null;
        Log.i(LOG, categorys.size() + " " + categoryID);
        for (CategoryDTO c : categorys) {
            Log.e(LOG, categorys.size() + " " + categoryID);
            if (categoryID == c.getCategoryId()) {
                Log.d(LOG, categorys.size() + " " + categoryID);
                for (ConditionsDTO cd : c.getConditionsList()) {
                    Log.w(LOG, categorys.size() + " " + cd.getConditionName() + " ");
                    if (statusCondition == cd.getConditionsID()) {
                        Log.i(LOG, cd.getConditionName() + " " + cd.getConditionsID());
                        cond = cd.getConditionsID();
                    }

                }

            }
        }
        return cond;
    }


    private void statusScore(List<InsectImageDTO> dtos, String categoryName) {
        String status = "";
        int statusCondition = 0;
        int categoryID = 0;
        double average = 0.0, total = 0.0;
        if (dtos == null || dtos.size() == 0) {
            average = 0.0;

        } else {
            for (InsectImageDTO ii : dtos) {
                total = total + ii.getInsect().getSensitivityScore();
            }
            average = total / dtos.size();
        }
        average = Math.round(average * 100.0) / 100.0;
        Log.d(LOG, average + "");
        if (categoryName.equalsIgnoreCase("Sandy Type")) {
            categoryID = 8;
            if (average > 6.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_SAND;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.blue_900));
                txtAverage.setTextColor(getResources().getColor(R.color.blue_900));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.blue_crap));
                //scoreIcon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.8 && average < 6.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_SAND;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.green_700));
                txtAverage.setTextColor(getResources().getColor(R.color.green_700));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.green_crap));
                // scoreIcon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.9 && average < 5.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_SAND;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.orange_400));
                txtAverage.setTextColor(getResources().getColor(R.color.orange_400));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.orange_crap));
                // scoreIcon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.3 && average < 4.9) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_SAND;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.red_900));
                txtAverage.setTextColor(getResources().getColor(R.color.red_900));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.red_crap));
                // scoreIcon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 4.3) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_SAND;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.purple_800));
                txtAverage.setTextColor(getResources().getColor(R.color.purple_800));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.purple_crap));
                //scoreIcon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            }
        } else if (categoryName.equalsIgnoreCase("Rocky Type")) {
            categoryID = 9;
            if (average > 7.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_ROCK;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.blue_900));
                txtAverage.setTextColor(getResources().getColor(R.color.blue_900));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.blue_crap));
                //scoreIcon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.8 && average < 7.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_ROCK;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.green_700));
                txtAverage.setTextColor(getResources().getColor(R.color.green_700));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.green_crap));
                // scoreIcon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.1 && average < 6.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_ROCK;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.orange_400));
                txtAverage.setTextColor(getResources().getColor(R.color.orange_400));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.orange_crap));
                // scoreIcon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.1 && average < 6.1) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_ROCK;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.red_900));
                txtAverage.setTextColor(getResources().getColor(R.color.red_900));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.red_crap));
                // scoreIcon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 5.1) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_ROCK;
                txtScoreStatus.setTextColor(getResources().getColor(R.color.purple_800));
                txtAverage.setTextColor(getResources().getColor(R.color.purple_800));
                scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.purple_crap));
                //scoreIcon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            }
        }
        txtScoreStatus.setText(status);

        Log.e(LOG, "Check conditionID : " + conditionID + " " + statusCondition + " " + categoryID);
        conditionID = statusCondition;//conditionIDFunc(response.getCategoryList(), statusCondition, categoryID);

        Log.e(LOG, "Check conditionID : " + conditionID);
    }

    private double calculateScore(List<InsectImageDTO> dtos) {
        double score = 0.0;

        if (dtos == null || dtos.size() == 0) {
            return 0.0;
        } else {
            for (InsectImageDTO ii : dtos) {

                score = score + ii.getInsect().getSensitivityScore();
            }
        }
        return score;
    }

    private double calculateAverage(List<InsectImageDTO> dtos) {
        double average = 0.0, total = 0.0;
        if (dtos == null || dtos.size() == 0) {
            return 0.0;
        }
        for (InsectImageDTO ii : dtos) {
            total = total + ii.getInsect().getSensitivityScore();
        }
        average = total / dtos.size();
        average = Math.round(average * 100.0) / 100.0;
        return average;
    }

    public void buildUI() {
        evaluationSite = new EvaluationSiteDTO();
        setImageList();
    }

    private void addRequestToCache(RequestDTO request) {
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w(LOG, "+++++++++++++ request cached, starting bound service");
                        if (mBound) {
                            mService.startSyncCachedRequests(new RequestSyncService.RequestSyncListener() {
                                @Override
                                public void onTasksSynced(int goodResponses, int badResponses) {
                                    Log.w(LOG, "--- onTasksSynced goodResponses: " + goodResponses + " bad: " + badResponses);
                                }

                                @Override
                                public void onError(String message) {
                                    Log.e(LOG, message);
                                }
                            });
                        }
                        cleanForm();
                        Snackbar.make(WT_sp_riverConnected, "Observation has been sent to the server", Snackbar.LENGTH_LONG).show();
                        finish();
                    }
                });


            }

            @Override
            public void onRequestCacheReturned(RequestCache cache) {

            }

            @Override
            public void onError() {
            }
        });
    }

    private void setEvaluationSiteData(EvaluationSiteDTO site) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AE_pin_point.setVisibility(View.VISIBLE);
            }
        });
        evaluationSite = site;

    }

    private void localSaveRequests() {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION);

        if (!WT_sp_stream.getText().toString().equals("") && (!WT_sp_stream.getText().toString().equals(null))) {
            StreamDTO stream = new StreamDTO();
            stream.setStreamName(WT_sp_stream.getText().toString());
            stream.setRiverID(river.getRiverID());
            evaluationSite.setStream(stream);
        } else {
            evaluationSite.setStream(null);
        }
        Log.d(LOG, "selected insects: " + selectedInsects.size());
//
        evaluationSite.setDateRegistered(new Date().getTime());
        evaluationSite.setCategoryID(category.getCategoryId());
        evaluationSite.setRiverID(river.getRiverID());
        evaluationSite.setLatitude(latitude);
        evaluationSite.setLongitude(longitude);
        evaluationSite.setAccuracy(accuracy);
//
        evaluationDTO = new EvaluationDTO();
        evaluationDTO.setConditionsID(conditionID);
        evaluationDTO.setTeamMemberID(teamMember.getTeamMemberID());
        evaluationDTO.setEvaluationSiteID(evaluationSite.getEvaluationSiteID());
        evaluationDTO.setRemarks(EDT_comment.getText().toString());
        evaluationDTO.setpH(Double.parseDouble((WP_score.getText().toString().isEmpty()) ? 0.0 + "" : WP_score.getText().toString()));
        evaluationDTO.setOxygen(Double.parseDouble((WO_score.getText().toString().isEmpty()) ? 0.0 + "" : WO_score.getText().toString()));
        evaluationDTO.setWaterClarity(Double.parseDouble((WC_score.getText().toString().isEmpty()) ? 0.0 + "" : WC_score.getText().toString()));
        evaluationDTO.setWaterTemperature(Double.parseDouble((WT_score.getText().toString().isEmpty()) ? 0.0 + "" : WT_score.getText().toString()));
        evaluationDTO.setElectricityConductivity(Double.parseDouble((WE_score.getText().toString().isEmpty()) ? 0.0 + "" : WE_score.getText().toString()));
        evaluationDTO.setEvaluationDate(new Date().getTime());
        evaluationDTO.setScore(Double.parseDouble(txtAverage.getText().toString()));

        evaluationDTO.setEvaluationimageList(takenImages);

        w.setInsectImages(selectedInsects);
        w.setEvaluation(evaluationDTO);
        addRequestToCache(w);

    }

    private List<EvaluationImageDTO> takenImages;


    @Override
    public void onStart() {
        super.onStart();
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        Log.i(LOG,
                "### onStart, binding RequestSyncService and PhotoUploadService");
        Intent intent = new Intent(this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG,
                "#################### onStop");

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        Log.w(LOG, "############## onStop stopping google service clients");
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }

        super.onStop();
    }


    private List<InsectImageDTO> insectImageList;

    private void getRiversAroundMex() {

        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                response = r;
                buildUI();
            }

            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {

            }
        });

    }


    protected void stopLocationUpdates() {
        Log.d(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(LOG, "###### removeLocationUpdates fired OK");
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }


    boolean isInsectsPickerBack;

    @Override
    public void onLocationChanged(Location location) {
        Log.e(LOG, "####### onLocationChanged, accuracy: " + location.getAccuracy());
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            this.location = location;
            accuracy = location.getAccuracy();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            stopLocationUpdates();
        }

    }

    Float accuracy;
    Double latitude, longitude;

    private void getEvaluationsByRiver() {
        if (!river.getEvaluationsiteList().isEmpty()) {
            ResponseDTO w = new ResponseDTO();
            w.setEvaluationSiteList(river.getEvaluationsiteList());
            setPopupList();
            return;
        }
    }

    private void setPopupList() {
        calculateDistancesForSites();
        Util.showPopupEvaluationSite(ctx, activity, river.getEvaluationsiteList(), btnNearbySites, "Near Observations Site", new Util.PopupSiteListener() {
            @Override
            public void onEvaluationClicked(EvaluationSiteDTO evaluation) {
                Log.i(LOG, "++++++++++++++++EvaluationSite selected: \n" + new Gson().toJson(evaluation));
                evaluationSite = evaluation;
                setFieldsFromSelectedSite();
            }
        });
    }

    EvaluationSiteDTO evaluationSite;

    private void calculateDistancesForSites() {
        try {
            if (location != null) {
                Location spot = new Location(LocationManager.GPS_PROVIDER);
                for (EvaluationSiteDTO w : river.getEvaluationsiteList()) {
                    spot.setLatitude(w.getLatitude());
                    spot.setLongitude(w.getLongitude());
                    w.setDistanceFromMe(location.distanceTo(spot));
                }
                Collections.sort(river.getEvaluationsiteList());
            }
        } catch (Exception e) {

        }
    }

    static final int ACCURACY_LIMIT = 50;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
        }
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        if (location != null) {
            if (location.getAccuracy() > ACCURACY_LIMIT) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            Toast.makeText(ctx, "Show", Toast.LENGTH_LONG).show();
            ;
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    WebCheckResult w;


    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
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