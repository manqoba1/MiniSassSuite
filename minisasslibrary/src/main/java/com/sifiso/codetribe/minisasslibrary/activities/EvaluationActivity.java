package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
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
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.dto.StreamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.PageFragment;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class EvaluationActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String LOG = EvaluationActivity.class.getSimpleName();
    private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private ProgressBar progressBar;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon, AE_pin_point, ivCancel;
    private TextView WT_sp_riverConnected, WT_sp_category, EDT_comment, AE_down_up;
    private Button WT_gps_picker, SL_show_insect, AE_create, AE_find_near_sites;
    private AutoCompleteTextView WT_sp_river, WT_sp_stream;

    //layouts
    RelativeLayout result2, result3;
    LinearLayout llSiteLayout;
    EvaluationDTO evaluationDTO;
    Integer teamMemberID, conditionID = null;
    double wc = 0.0, wt = 0.0, we = 0.0, wp = 0.0, wo = 0.0;
    private Activity activity;
    List<String> categoryStr;
    private ViewStub viewStub;
    private Integer categoryID;
    private Integer riverID;
    private TeamMemberDTO teamMember;
    String catType = "Select category";

    static final int ACCURACY_THRESHOLD = 10;
    static final int MAP_REQUESTED = 9007;
    static final int STATUS_CODE = 220;
    static final int INSECT_DATA = 103;
    public static final int CAPTURE_IMAGE = 1001;
    static final int CREATE_EVALUATION = 108;

    static final int GPS_DATA = 102;

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    ViewPager mPager;
    Menu mMenu;
    List<PageFragment> pageFragmentList;
    ResponseDTO response;
    boolean mBound, mShowMore;
    RequestSyncService mService;

    private Context ctx;
    RelativeLayout GPS_layout4;
    private TextView txtAccuracy, txtLat, txtLng, txtSiteName;

    private void setField() {
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
                        WT_sp_category.setEnabled(true);
                    }
                });
            }
        });

        GPS_layout4 = (RelativeLayout) findViewById(R.id.GPS_layout4);
        txtAccuracy = (TextView) findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) findViewById(R.id.GPS_latitude);
        txtLng = (TextView) findViewById(R.id.GPS_longitude);
        Statics.setRobotoFontBold(ctx, txtLat);
        Statics.setRobotoFontBold(ctx, txtLng);
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
        TV_total_score = (TextView) findViewById(R.id.TV_total_score);
        TV_average_score = (TextView) findViewById(R.id.TV_average_score);
        TV_avg_score = (TextView) findViewById(R.id.TV_avg_score);
        TV_score_status = (TextView) findViewById(R.id.TV_score_status);
        IMG_score_icon = (ImageView) findViewById(R.id.IMG_score_icon);
        WT_sp_river = (AutoCompleteTextView) findViewById(R.id.WT_sp_river);
        WT_sp_stream = (AutoCompleteTextView) findViewById(R.id.WT_sp_stream);
        WT_sp_riverConnected = (TextView) findViewById(R.id.WT_sp_riverConnected);
        WT_sp_category = (TextView) findViewById(R.id.WT_sp_category);
        EDT_comment = (EditText) findViewById(R.id.EDT_comment);
        AE_pin_point = (ImageView) findViewById(R.id.AE_pin_point);
        AE_pin_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });
        SL_show_insect = (Button) findViewById(R.id.SL_show_insect);
        AE_create = (Button) findViewById(R.id.AE_create);
        AE_down_up = (TextView) findViewById(R.id.AE_down_up);
        AE_down_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(AE_down_up, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (mShowMore) {
                            mShowMore = false;
                            result2.setVisibility(View.VISIBLE);
                            // AE_down_up.setCompoundDrawables(ctx.getResources().getDrawable(android.R.drawable.arrow_down_float), null, null, null);
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
        AE_find_near_sites = (Button) findViewById(R.id.AE_find_near_sites);
        AE_find_near_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(AE_find_near_sites, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (riverID == null) {
                            Toast.makeText(ctx, "Please choose the river first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getEvaluationsByRiver();
                    }
                });

            }
        });
        viewStub = (ViewStub) findViewById(R.id.viewStub);
        codeStatus = getIntent().getIntExtra("statusCode", 0);
        if (codeStatus == CREATE_EVALUATION) {
            //fieldPicker();
            river = (RiverDTO) getIntent().getSerializableExtra("riverCreate");
            response = (ResponseDTO) getIntent().getSerializableExtra("response");
            WT_sp_river.setText(river.getRiverName());
            WT_sp_riverConnected.setText(river.getRiverName());
            setStreamSpinner(river.getStreamList());
            Util.expand(WT_sp_stream, 2000, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_stream.setVisibility(View.VISIBLE);
                }
            });
            riverID = river.getRiverID();
            Log.e(LOG, CREATE_EVALUATION + " River ID : " + riverID);

            //buildUI();
        }
        //buildUI();
    }

    /*  if (getIntent().getSerializableExtra("riverCreate") != null) {
                river = (RiverDTO) getIntent().getSerializableExtra("riverCreate");
                setRiverField(river);
            }*/
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
                        if (goodResponses > 0) {
                            // getRiversAroundMe();
                        }
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
        //  setTheme(R.style.EvalListTheme);
        setContentView(R.layout.activity_evaluation);
        ctx = getApplicationContext();
        activity = this;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Observation");
        getSupportActionBar().setSubtitle(sdf.format(new Date()));
        teamMember = SharedUtil.getTeamMember(ctx);

        setField();
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState.getSerializable("response");
            buildUI();
        }
        // (RiverDTO) data.getSerializableExtra("riverCreate");


    }


    private int currentView;


    int codeStatus;
    RiverDTO river;

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        mMenu = menu;
        WebCheckResult wr = WebCheck.checkNetworkAvailability(ctx);
        fieldPicker();

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
            finish();
            stopLocationUpdates();
        }
        if (id == R.id.action_refresh) {
            WebCheckResult wr = WebCheck.checkNetworkAvailability(ctx);
            fieldPicker();

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
                    setRiverField(riv);
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
                if (resultCode == INSECT_DATA) {
                    isInsectsPickerBack = true;
                    isBusy = true;
                    if ((List<InsectImageDTO>) data.getSerializableExtra("selectedInsects") != null) {

                        insectImageList = (List<InsectImageDTO>) data.getSerializableExtra("overallInsect");
                        scoreUpdater((List<InsectImageDTO>) data.getSerializableExtra("selectedInsects"));
                        List<InsectImageDTO> list = (List<InsectImageDTO>) data.getSerializableExtra("selectedInsects");
                        //Log.w(LOG, "### insect ui has returned with data?" + list.size());

                        result3.setVisibility(View.VISIBLE);
                        teamMember = SharedUtil.getTeamMember(ctx);
                    }
                }
                break;
        }
    }

    public void setRiverField(RiverDTO r) {
        // ToastUtil.toast(ctx, "yes");
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiverName());

    }

    public void setFieldsFromVisitedSites(EvaluationSiteDTO r) {
        evaluationSite = r;
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiver().getRiverName());
        WT_sp_river.setEnabled(false);
        categoryID = r.getCategoryID();
        WT_sp_category.setText(r.getCategory().getCategoryName());
        WT_sp_category.setEnabled(false);
        catType = r.getCategory().getCategoryName();
        txtSiteName.setText("Site #" + r.getEvaluationSiteID());
        Util.expandOrCollapse(llSiteLayout, 100, true, new Util.UtilAnimationListener() {
            @Override
            public void onAnimationEnded() {

            }
        });
    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,##0.0");

    private String getDistance(float mf) {
        if (mf < 1000) {
            return df.format(mf) + " metres";
        }
        Double m = Double.parseDouble("" + mf);
        Double n = m / 1000;

        return df.format(n) + " kilometres";

    }

    private void calculateDistances() {
        if (location != null) {
            List<RiverPointDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);

            for (RiverDTO w : response.getRiverList()) {
                for (RiverPartDTO x : w.getRiverpartList()) {
                    for (RiverPointDTO y : x.getRiverpointList()) {
                        spot.setLatitude(y.getLatitude());
                        spot.setLongitude(y.getLongitude());
                        y.setDistanceFromMe(location.distanceTo(spot));
                    }
                    Collections.sort(x.getRiverpointList());
                    x.setNearestLatitude(x.getRiverpointList().get(0).getLatitude());
                    x.setNearestLongitude(x.getRiverpointList().get(0).getLongitude());
                    x.setDistanceFromMe(x.getRiverpointList().get(0).getDistanceFromMe());
                }
                Collections.sort(w.getRiverpartList());
                w.setNearestLatitude(w.getRiverpartList().get(0).getNearestLatitude());
                w.setNearestLongitude(w.getRiverpartList().get(0).getNearestLongitude());
                w.setDistanceFromMe(w.getRiverpartList().get(0).getDistanceFromMe());


            }
            Collections.sort(response.getRiverList());
        }
    }

    private void setSpinner() {

        //calculateDistances();
        evaluationSite = new EvaluationSiteDTO();
        Log.d(LOG, "category size " + response.getCategoryList().size());
        WT_sp_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(WT_sp_category, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        hideKeyboard();
                        Util.showPopupCategoryBasicWithHeroImage(ctx, EvaluationActivity.this, response.getCategoryList(), WT_sp_category, "", new Util.UtilPopupListener() {
                            @Override
                            public void onItemSelected(int index) {
                                indexCat = index;
                                WT_sp_category.setText(response.getCategoryList().get(indexCat).getCategoryName());
                                categoryID = response.getCategoryList().get(indexCat).getCategoryId();
                                catType = (response.getCategoryList().get(indexCat).getCategoryName());
                                evaluationSite.setCategoryID(categoryID);
                                Log.e(LOG, categoryID + "");
                            }
                        });
                    }
                });

            }
        });
        Log.d(LOG, "river size " + response.getRiverList().size());
        WT_sp_riverConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(WT_sp_riverConnected, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        Util.showPopupRiverWithHeroImage(ctx, EvaluationActivity.this, response.getRiverList(), WT_sp_river, "", new Util.UtilPopupListener() {
                            @Override
                            public void onItemSelected(int position) {
                                // WT_sp_river.setTextColor(getResources().getColor(R.color.gray));
                                indexRiv = position;
                                WT_sp_riverConnected.setText(response.getRiverList().get(position).getRiverName());
                                riverID = response.getRiverList().get(position).getRiverID();

                                evaluationSite.setRiverID(riverID);

                                setStreamSpinner(response.getRiverList().get(position).getStreamList());
                                Util.expand(WT_sp_stream, 200, new Util.UtilAnimationListener() {
                                    @Override
                                    public void onAnimationEnded() {
                                        WT_sp_stream.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        });
                    }
                });


            }
        });
        setAutoTextData();


    }

    List<String> riverTiles, streamTiles;

    private void setAutoTextData() {
        riverTiles = new ArrayList<>();
        for (RiverDTO r : response.getRiverList()) {
            riverTiles.add(r.getRiverName().trim());
        }
        ArrayAdapter<String> riverSpinner = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_dropdown_item, riverTiles);
        WT_sp_river.setAdapter(riverSpinner);
        WT_sp_river.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
                indexRiv = searchRiver(parent.getItemAtPosition(position).toString());
                WT_sp_river.setText(response.getRiverList().get(indexRiv).getRiverName().trim());
                riverID = response.getRiverList().get(indexRiv).getRiverID();
                evaluationSite.setRiverID(riverID);
                Log.e(LOG, riverID + " " + parent.getItemAtPosition(position).toString());
                setStreamSpinner(response.getRiverList().get(indexRiv).getStreamList());
                Util.expand(WT_sp_stream, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        WT_sp_stream.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


    }

    private void setStreamSpinner(List<StreamDTO> streamList) {
        streamTiles = new ArrayList<>();
        for (StreamDTO s : streamList) {
            streamTiles.add(s.getStreamName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(ctx, R.layout.xsimple_spinner_dropdown_item, streamTiles);
        WT_sp_stream.setAdapter(arrayAdapter);
        WT_sp_stream.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(WT_sp_river.getWindowToken(), 0);
        //imm.hideSoftInputFromWindow(WT_sp_river.getWindowToken(), 0);
    }

    private String riverToBeCreated;
    private Integer indexCat, indexRiv;

    private void startGPSDialog() {


        AE_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(AE_create, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (riverID == null) {
                            ToastUtil.toast(ctx, "Please select river");
                            return;
                        }
                        if (categoryID == null) {
                            ToastUtil.toast(ctx, "Please select category");
                            return;
                        }

                        if (evaluationSite == null || (accuracy == null || accuracy==0)) {
                            ToastUtil.toast(ctx, "Observation site not defined, Once the site is define, the red WORLD icon will change to blue. Make sure you LOCATION setting is enabled");

                            AE_pin_point.setVisibility(View.VISIBLE);
                            AE_pin_point.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                            Util.flashOnce(AE_pin_point, 200, new Util.UtilAnimationListener() {
                                @Override
                                public void onAnimationEnded() {

                                }
                            });
                            /*GPS_layout4.setVisibility(View.VISIBLE);
                            AE_pin_point.setVisibility(View.VISIBLE);
                            startLocationUpdates();*/
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

                        AE_create.setEnabled(false);
                        localSaveRequests();

                    }
                });

            }
        });

    }

    private void setLocationTextviews(Location loc) {
        txtLat.setText("" + loc.getLatitude());
        txtLng.setText("" + loc.getLongitude());
        txtAccuracy.setText("" + loc.getAccuracy());
    }

    private void startSelectionDialog() {

        insectImageList = new ArrayList<InsectImageDTO>();
        for (InsectDTO i : response.getInsectList()) {

            for (InsectImageDTO ii : i.getInsectimageDTOList()) {
                insectImageList.add(ii);
            }
        }
        SL_show_insect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryID == null) {
                    ToastUtil.toast(ctx, "Select category, before choosing macroinvertebrates");
                } else {

                    Log.d(LOG, "Insect Select " + insectImageList.size());
                    Intent intent = new Intent(EvaluationActivity.this, InsectPickerActivity.class);
                    intent.putExtra("insetImageList", (java.io.Serializable) response.getInsectimageDTOList());
                    startActivityForResult(intent, INSECT_DATA);
                }
            }
        });
    }

    public void scoreUpdater(List<InsectImageDTO> insectImages) {
        selectedInsects = new ArrayList<>();
        if (insectImages == null) {
            result3.setVisibility(View.GONE);
            return;
        } else {
            result3.setVisibility(View.VISIBLE);
        }
        TV_avg_score.setText(calculateAverage(insectImages) + "");
        TV_average_score.setText(((insectImages != null) ? insectImages.size() : 0.0) + "");
        selectedInsects = insectImages;
        statusScore(insectImages, catType);
        TV_total_score.setText(calculateScore(insectImages) + "");
        Log.e(LOG, calculateScore(insectImages) + "Yes");
    }

    private List<InsectImageDTO> selectedInsects;

    private void cleanForm() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TV_total_score.setText("0.0");
                TV_average_score.setText("0.0");
                TV_avg_score.setText("0.0");
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.gray_crap));
                WT_sp_river.setText(null);
                //WT_sp_river.setHint("What is the current river are you at?");
                WT_sp_category.setText(null);
                // WT_sp_category.setHint("What environment are you at? Rocky or Sandy?");
                EDT_comment.setText(null);
                WT_sp_stream.setText(null);
                WT_sp_stream.setVisibility(View.GONE);
                WT_sp_riverConnected.setText(null);
                //WT_sp_riverConnected.setHint("What is the current river are you at?");
                AE_pin_point.setVisibility(View.GONE);
                result3.setVisibility(View.GONE);

                evaluationSite = new EvaluationSiteDTO();
                evaluationDTO = new EvaluationDTO();

                TV_score_status.setText("not specified");
                TV_score_status.setTextColor(getResources().getColor(R.color.gray));
                IMG_score_icon.setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.MULTIPLY);
                TV_total_score.setTextColor(getResources().getColor(R.color.gray));
                WE_score.setText("");
                WO_score.setText("");
                WC_score.setText("");
                WP_score.setText("");
                WT_score.setText("");
                categoryID = null;
                riverID = null;
                conditionID = null;
                AE_create.setEnabled(true);
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
                TV_score_status.setTextColor(getResources().getColor(R.color.blue_900));
                TV_avg_score.setTextColor(getResources().getColor(R.color.blue_900));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.blue_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.8 && average < 6.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.green_700));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green_700));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.9 && average < 5.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.orange_400));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange_400));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.orange_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.3 && average < 4.9) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.red_900));
                TV_avg_score.setTextColor(getResources().getColor(R.color.red_900));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 4.3) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_SAND;
                TV_score_status.setTextColor(getResources().getColor(R.color.purple_800));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple_800));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.purple_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            }
        } else if (categoryName.equalsIgnoreCase("Rocky Type")) {
            categoryID = 9;
            if (average > 7.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.blue_900));
                TV_avg_score.setTextColor(getResources().getColor(R.color.blue_900));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.blue_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.8 && average < 7.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.green_700));
                TV_avg_score.setTextColor(getResources().getColor(R.color.green_700));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.1 && average < 6.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.orange_400));
                TV_avg_score.setTextColor(getResources().getColor(R.color.orange_400));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.orange_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.1 && average < 6.1) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.red_900));
                TV_avg_score.setTextColor(getResources().getColor(R.color.red_900));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_crap));
                // IMG_score_icon.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 5.1) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_ROCK;
                TV_score_status.setTextColor(getResources().getColor(R.color.purple_800));
                TV_avg_score.setTextColor(getResources().getColor(R.color.purple_800));
                IMG_score_icon.setImageDrawable(getResources().getDrawable(R.drawable.purple_crap));
                //IMG_score_icon.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
            }
        }
        TV_score_status.setText(status);

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
        setSpinner();
        Log.d(LOG, "******* getting cached data");
        startSelectionDialog();
        //addMinus();
        startGPSDialog();
    }

    private void setProgressDialog() {
        pd = new ProgressDialog(EvaluationActivity.this);
        pd.setMessage("Saving Observation...");
        pd.setCancelable(false);
        pd.show();
    }

    private void sendRequest(final RequestDTO request) {
        //setProgressDialog();
        /*BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, request, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    return;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // cleanForm();
                            //setRefreshActionButtonState(false);
                            isInsectsPickerBack = true;
                            isBusy = false;
                            pd.dismiss();
                            ToastUtil.toast(ctx, "Observation successfully saved");
                            cleanForm();
                            getRiversAroundMe();
                            //showImageDialog();
                        }
                    });
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {

                //addRequestToCache(request);
            }

            @Override
            public void onError(String message) {
                addRequestToCache(request);

            }
        });*/
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                if (response.getStatusCode() > 0) {
                    //addRequestToCache(request);
                    return;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // cleanForm();
                            //setRefreshActionButtonState(false);
                            isInsectsPickerBack = true;
                            isBusy = false;
                            pd.dismiss();
                            Util.showToast(ctx, "Observation successfully saved");
                            cleanForm();
                            //getRiversAroundMe();
                            //showImageDialog();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                addRequestToCache(request);
            }
        });
    }

    private void addRequestToCache(RequestDTO request) {
        //setRefreshActionButtonState(true);
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Util.showToast(ctx, "Observation successfully saved on device, will be uploaded when connectivity is available");
                        cleanForm();
//                        showImageDialog();
                    }
                });


            }

            @Override
            public void onRequestCacheReturned(RequestCache cache) {

            }

            @Override
            public void onError() {
                pd.dismiss();
                //setRefreshActionButtonState(false);
            }
        });
    }

    public void setEvaluationSiteData(EvaluationSiteDTO site) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AE_pin_point.setVisibility(View.VISIBLE);
            }
        });
        evaluationSite = new EvaluationSiteDTO();
        evaluationSite = site;
        /*evaluationSite.setCategoryID(categoryID);
        evaluationSite.setRiverID(riverID);*/

    }

    GregorianCalendar c;

    private void localSaveRequests() {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION);
        c = new GregorianCalendar();
        evaluationDTO = new EvaluationDTO();
        if (!WT_sp_stream.getText().toString().equals("") && (!WT_sp_stream.getText().toString().equals(null))) {
            StreamDTO stream = new StreamDTO();
            stream.setStreamName(WT_sp_stream.getText().toString());
            stream.setRiverID(riverID);
            evaluationSite.setStream(stream);
        } else {
            evaluationSite.setStream(null);
        }
        Log.d(LOG, "" + selectedInsects.size());


        evaluationDTO.setConditionsID(conditionID);
        evaluationDTO.setTeamMemberID(teamMember.getTeamMemberID());
        evaluationSite.setDateRegistered(new Date().getTime());
        evaluationSite.setCategoryID(categoryID);
        evaluationSite.setRiverID(riverID);
        evaluationSite.setLatitude(latitude);
        evaluationSite.setLongitude(longitude);
        evaluationSite.setAccuracy(accuracy);
        evaluationDTO.setEvaluationSite(evaluationSite);


        evaluationDTO.setRemarks(EDT_comment.getText().toString());
        evaluationDTO.setpH(Double.parseDouble((WP_score.getText().toString().isEmpty()) ? 0.0 + "" : WP_score.getText().toString()));

        evaluationDTO.setOxygen(Double.parseDouble((WO_score.getText().toString().isEmpty()) ? 0.0 + "" : WO_score.getText().toString()));

        evaluationDTO.setWaterClarity(Double.parseDouble((WC_score.getText().toString().isEmpty()) ? 0.0 + "" : WC_score.getText().toString()));

        evaluationDTO.setWaterTemperature(Double.parseDouble((WT_score.getText().toString().isEmpty()) ? 0.0 + "" : WT_score.getText().toString()));

        evaluationDTO.setElectricityConductivity(Double.parseDouble((WE_score.getText().toString().isEmpty()) ? 0.0 + "" : WE_score.getText().toString()));

        evaluationDTO.setEvaluationDate(new Date().getTime());
        evaluationDTO.setScore(Double.parseDouble(TV_avg_score.getText().toString()));
        Log.i(LOG, new Date(evaluationDTO.getEvaluationDate()).toString());
        evaluationDTO.setEvaluationimageList(takenImages);

        w.setInsectImages(selectedInsects);
        w.setEvaluation(evaluationDTO);

        //ToastUtil.errorToast(ctx, c.getTime().getTime() + " : " + c.getTime());
        //Log.i(LOG, (new Gson()).toJson(w));
        System.out.println((new Gson()).toJson(w));
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);

        setProgressDialog();
        if (wcr.isWifiConnected()) {
            sendRequest(w);
            //addRequestToCache(w);
        } else if (wcr.isMobileConnected()) {
            sendRequest(w);
        } else {
            addRequestToCache(w);
        }

    }

    List<EvaluationImageDTO> takenImages;

    public void setTakenImage(List<EvaluationImageDTO> takenImages) {
        if (takenImages == null) {
            takenImages = new ArrayList<>();
        }
        this.takenImages = takenImages;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        if (!mResolvingError) {  // more about this later
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


    EvaluationSiteDTO evaluationSite;


    List<InsectImageDTO> insectImageList;


    private void showImageDialog() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                EvaluationActivity.this);

        // set title
        alertDialogBuilder.setTitle("Observation Created");

        // set dialog message
        alertDialogBuilder
                .setMessage("Create another observation?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Intent intent = new Intent(ctx, PictureActivity.class);
                        // intent.putExtra("takenImages", )
                        // startActivityForResult(intent, CAPTURE_IMAGE);
                        cleanForm();

                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        finish();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    ProgressDialog pd;

    private void showEvaluationDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        EvaluationActivity.this);

                // set title
                alertDialogBuilder.setTitle("Upload Results");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Create a new Observation!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Intent intent = new Intent(ctx, PictureActivity.class);
                                // intent.putExtra("takenImages", )
                                // startActivityForResult(intent, CAPTURE_IMAGE);
                                cleanForm();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                finish();
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder
                        .setMessage("Contribute to Existing Site!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Intent intent = new Intent(ctx, PictureActivity.class);
                                // intent.putExtra("takenImages", )
                                // startActivityForResult(intent, CAPTURE_IMAGE);
                                cleanForm();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        finish();
                                        dialog.cancel();
                                    }
                                }

                        );

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


    }


    boolean isBusy, isFromAddingEvaluation;

    private void getRiversAroundMe() {

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

//
//
//        if (location == null) {
//            Toast.makeText(ctx, "Invalid Location ...", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (isBusy) {
//            Toast.makeText(ctx, "Rivers Loaded ...", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        RequestDTO w = new RequestDTO();
//        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
//        w.setLatitude(location.getLatitude());
//        w.setLongitude(location.getLongitude());
//        w.setRadius(1);
//        isBusy = true;
//        //Util.showToast(ctx, "Loading new data");
//        if (!isFromAddingEvaluation) {
//            setRefreshActionButtonState(true);
//        }
//
//        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                isBusy = false;
////                progressBar.setVisibility(View.GONE);
//                setRefreshActionButtonState(false);
//                if (r.getStatusCode() > 0) {
//                    Toast.makeText(ctx, r.getMessage(), Toast.LENGTH_LONG).show();
//                    return;
//                }
//                response = new ResponseDTO();
//                response = r;
//                Log.d(LOG, new Gson().toJson(r));
//                buildUI();
//                CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
//                    @Override
//                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                    }
//
//                    @Override
//                    public void onDataCached(ResponseDTO response) {
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                isBusy = false;
//                setRefreshActionButtonState(false);
//                //Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                Util.collapse(WT_sp_riverConnected, 200, new Util.UtilAnimationListener() {
//                    @Override
//                    public void onAnimationEnded() {
//                        WT_sp_riverConnected.setVisibility(View.GONE);
//                    }
//                });
//                Util.expand(WT_sp_river, 200, new Util.UtilAnimationListener() {
//                    @Override
//                    public void onAnimationEnded() {
//                        WT_sp_river.setVisibility(View.VISIBLE);
//                    }
//                });
//                getCachedRiverData();
//            }
//
//            @Override
//            public void onError(String message) {
//                isBusy = false;
//                setRefreshActionButtonState(false);
//            }
//        });
//

    }


    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    boolean isInsectsPickerBack;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        setGPSLocation(location);
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            setGPSLocation(location);
            AE_pin_point.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
            if (!isInsectsPickerBack) {

                //fieldPicker();
            }
        }
        Log.e(LOG, "####### onLocationChanged");
    }

    Float accuracy = null;
    Double latitude, longitude;

    private void setGPSLocation(Location loc) {

        this.location = loc;

       /* evaluationSite.setLatitude(location.getLatitude());
        evaluationSite.setLongitude(location.getLongitude());
        evaluationSite.setAccuracy(location.getAccuracy());*/
        setLocationTextviews(loc);
        accuracy = location.getAccuracy();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.w(LOG, "### Passing " + loc.getAccuracy());
        if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {

            location = loc;
            setLocationTextviews(loc);
            Log.w(LOG, "### Passing location2");
           /* evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());*/
            accuracy = location.getAccuracy();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            stopLocationUpdates();
            setEvaluationSiteData(evaluationSite);

            //finish();
            Log.e(LOG, "+++ best accuracy found: " + location.getAccuracy());
        }
    }


    //getting evaluations by distance
    boolean isBusy2;


    private void getEvaluationsByRiver() {
        if (isBusy2) {
            Toast.makeText(ctx, "Observations Loaded ...", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_EVALUATION_BY_RIVER_ID);
        w.setRiverID(riverID);
        isBusy2 = true;

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy2 = false;
//                progressBar.setVisibility(View.GONE);
                if (r.getStatusCode() > 0) {
                    Toast.makeText(ctx, r.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!r.getEvaluationSiteList().isEmpty()) {
                    setPopupList(r);
                } else {
                    ToastUtil.toast(ctx, "Unfortunately there are no observations made yet on " + WT_sp_river.getText().toString().trim() + " river");
                }

            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy2 = false;
                //Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                isBusy2 = false;
            }
        });
    }

    private void setPopupList(ResponseDTO r) {
        resp = r;
        calculateDistancesForSite();
        Util.showPopupEvaluationSite(ctx, activity, resp.getEvaluationSiteList(), AE_find_near_sites, "Near Observations Site", new Util.PopupSiteListener() {
            @Override
            public void onEvaluationClicked(EvaluationSiteDTO evaluation) {
                // showEvaluationDialog();
                Log.i(LOG, new Gson().toJson(evaluation));
                setFieldsFromVisitedSites(evaluation);
            }
        });
    }

    ResponseDTO resp;

    private void calculateDistancesForSite() {
        try {
            if (location != null) {
                List<EvaluationSiteDTO> criverPoints = new ArrayList<>();
                Location spot = new Location(LocationManager.GPS_PROVIDER);

                for (EvaluationSiteDTO w : resp.getEvaluationSiteList()) {
                    spot.setLatitude(w.getLatitude());
                    spot.setLongitude(w.getLongitude());
                    w.setDistanceFromMe(location.distanceTo(spot));
                }
                Collections.sort(resp.getEvaluationSiteList());
            }
        } catch (Exception e) {

        }
    }

    static final int ACCURACY_LIMIT = 20;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
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
            } else {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                accuracy = location.getAccuracy();
                WebCheckResult wr = WebCheck.checkNetworkAvailability(ctx);
                if (wr.isMobileConnected() || wr.isWifiConnected()) {
                    getRiversAroundMe();
                }
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

    /* Creates a dialog for an error message */
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

    private void fieldPicker() {
        w = WebCheck.checkNetworkAvailability(ctx);

        if (w.isWifiConnected()) {
            // Util.showToast(ctx, "System detected network connectivity and is going to load only the nearest rivers");
            Log.d(LOG, "Wifi setup");
            response = new ResponseDTO();
            Util.collapse(WT_sp_river, 200, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_river.setVisibility(View.GONE);
                }
            });
            Util.expand(WT_sp_riverConnected, 200, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_riverConnected.setVisibility(View.VISIBLE);
                }
            });

            //f (!isBusy) {
            getRiversAroundMe();
            // }

        } else if (w.isMobileConnected()) {
            // Util.showToast(ctx, "System detected network connectivity and is going to load only the nearest rivers");
            Log.d(LOG, "Mobile setup");
            response = new ResponseDTO();
            Util.collapse(WT_sp_river, 200, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_river.setVisibility(View.GONE);
                }
            });
            Util.expand(WT_sp_riverConnected, 200, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_riverConnected.setVisibility(View.VISIBLE);
                }
            });

            //f (!isBusy) {
            getRiversAroundMe();
            // }

        } else {
            response = new ResponseDTO();
            Util.collapse(WT_sp_riverConnected, 200, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_riverConnected.setVisibility(View.GONE);
                }
            });
            Util.expand(WT_sp_river, 200, new Util.UtilAnimationListener() {
                @Override
                public void onAnimationEnded() {
                    WT_sp_river.setVisibility(View.VISIBLE);
                }
            });
            getRiversAroundMe();
        }
    }

    private void getCachedRiverData() {

        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_RIVER_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO respond) {

                response = new ResponseDTO();
                response = respond;
                buildUI();
            }

            @Override
            public void onDataCached(ResponseDTO r) {

            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }
        });


    }

    public void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EvaluationActivity.this);

        builder.setTitle("GPS settings");
        builder.setMessage("GPS is not enabled. Do you want to go to settings menu, to search for location?");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    boolean isFound;

    private Integer searchRiver(String text) {

        int index = 0;

        for (int i = 0; i < response.getRiverList().size(); i++) {
            RiverDTO searchRiver = response.getRiverList().get(i);
            if (searchRiver.getRiverName().contains(text)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            //STF_list.setSelection(index);
            Log.e(LOG, text + " Found River " + response.getRiverList().get(index).getRiverName() + " " + response.getRiverList().get(index).getRiverID());
        } else {
            // Util.showToast(ctx, ctx.getString(R.string.river_not_found) + " " + SLT_editSearch.getText().toString());
        }
        hideKeyboard();
        return index;
    }

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

    private void createDraft() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(Constants.SP_RIVER_ID, riverID);
        //ed.putString(Constants.SP_RIVER_NAME,setSpinner();)
    }
}