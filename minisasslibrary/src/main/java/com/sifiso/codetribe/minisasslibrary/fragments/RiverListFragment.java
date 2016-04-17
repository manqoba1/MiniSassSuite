package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.RiverAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RiverListFragment extends Fragment implements PageFragment, SwipeRefreshLayout.OnRefreshListener {

    ResponseDTO response;
    RiverAdapter riverAdapter;
    boolean isFound;
    String searchText;
    CreateEvaluationListener mListener;
    private Context ctx;
    private AutoCompleteTextView SLT_editSearch;
    private TextView RL_add, SI_welcome;
    private ImageView SLT_imgSearch2;
    private RelativeLayout SLT_hero;
    private ListView RL_riverList;
    TextView txtCount;
    View handle;

    //public SwipeRefreshLayout refreshLayout;
    private int index2;

    public static RiverListFragment newInstance(ResponseDTO resp, int indexList) {
        RiverListFragment fragment = new RiverListFragment();
        Bundle args = new Bundle();
        args.putSerializable("response", resp);
        args.putInt("index", indexList);
        fragment.setArguments(args);
        return fragment;
    }

    public RiverListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            response = (ResponseDTO) getArguments().getSerializable("response");
            index2 = savedInstanceState.getInt("index");
        }
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.river_list, container, false);
        ctx = getActivity().getApplicationContext();
        setField();
        return v;
    }

    public void setResponse(ResponseDTO resp, int index) {
        this.response = resp;
        this.index2 = index;

        Log.i(LOG, "++ rivers has been set index : " + index);
        if (v != null) {
            setListView();
            riverToSearch();
        } else {
            Log.e(LOG, "$%#$## WTF?");
        }

    }

    private void setField() {
        handle = v.findViewById(R.id.handle);
        SLT_editSearch = (AutoCompleteTextView) v.findViewById(R.id.SLT_editSearch);
        SLT_hero = (RelativeLayout) v.findViewById(R.id.SLT_hero);
        SI_welcome = (TextView) v.findViewById(R.id.SI_welcome);
        txtCount = (TextView) v.findViewById(R.id.SI_count);
        txtCount.setText("0");
        SI_welcome.setText("Observations");
        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        //refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refreshLayout);
        //refreshLayout.setOnRefreshListener(this);

        SLT_imgSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchRiver();
            }
        });
        RL_riverList = (ListView) v.findViewById(R.id.RL_riverList);
        SLT_hero.setBackground(Util.getRandomHeroImage(ctx));

    }


    List<String> stringRiver;

    private void riverToSearch() {
        stringRiver = new ArrayList<>();
        for (int i = 0; i < response.getRiverList().size(); i++) {
            RiverDTO riverText = response.getRiverList().get(i);
            stringRiver.add(riverText.getRiverName().trim());
        }
        ArrayAdapter<String> riverSearchAdapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_dropdown_item, stringRiver);
        SLT_editSearch.setAdapter(riverSearchAdapter);
    }

    private void searchRiver() {
        if (SLT_editSearch.getText().toString().isEmpty()) {
            return;
        }
        int index = 0;
        searchText = SLT_editSearch.getText().toString();
        for (int i = 0; i < response.getRiverList().size(); i++) {
            RiverDTO searchRiver = response.getRiverList().get(i);
            String str = searchRiver.getRiverName() + " River";
            if (searchRiver.getRiverName().contains(searchText)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            RL_riverList.setSelection(index);


        } else {
            Util.showToast(ctx, ctx.getString(R.string.river_not_found) + " " + SLT_editSearch.getText().toString());
        }
        hideKeyboard();
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(SLT_editSearch.getWindowToken(), 0);
    }



    static String LOG = RiverListFragment.class.getSimpleName();

    public void setResponse(ResponseDTO response) {
        this.response = response;
        if (txtCount != null) {
            setListView();
        }
    }

    private void setListView() {

        txtCount.setText("" + response.getRiverList().size());
        Collections.sort(response.getRiverList());

        riverAdapter = new RiverAdapter(response.getRiverList(), ctx, new RiverAdapter.RiverAdapterListener() {
            @Override
            public void onDirectionRequired(final List<EvaluationSiteDTO> siteList) {
                if (siteList.size() == 1) {
                    mListener.onDirection(siteList.get(0).getLatitude(), siteList.get(0).getLongitude());
                    return;
                }
                List<String> list = new ArrayList<>();
                for (EvaluationSiteDTO x : siteList) {

                    list.add((x.getSiteName() == null ? "Site # " + x.getEvaluationSiteID() : x.getSiteName()));
                }
                Util.showPopupBasicWithHeroImage(ctx, getActivity(), list, handle,
                        "Select Observation Site", new Util.UtilPopupListener() {
                            @Override
                            public void onItemSelected(int index) {
                                EvaluationSiteDTO site = siteList.get(index);
                                mListener.onDirection(site.getLatitude(), site.getLongitude());
                            }
                        });
            }

            @Override
            public void onSitesMapRequested(RiverDTO river) {

                mListener.onSitesMapRequested(river);
            }

            @Override
            public void onEvaluationRequest(List<EvaluationSiteDTO> siteList, int position, String riverName) {
                mListener.onListEvaluationSites(siteList, position);
            }

            @Override
            public void onCreateEvaluation(RiverDTO river) {
                mListener.onCreateEvaluation(river);
            }

            @Override
            public void onMapRequest(RiverDTO river, int result) {
                mListener.onRefreshMap(river, result);
            }
        });

        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inf.inflate(R.layout.hero_layout, null);
        RL_riverList.setDividerHeight(2);
        RL_riverList.setAdapter(riverAdapter);
        RL_riverList.setVerticalScrollbarPosition(index2);
        RL_riverList.setSelection(index2);
        RL_riverList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mListener.onCreateEvaluation((RiverDTO) parent.getItemAtPosition(position));
                return false;
            }
        });
    }

    static final int CREATE_EVALUATION = 108;
    @Override
    public void animateCounts() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CreateEvaluationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
//        refreshLayout.setRefreshing(true);
//        mListener.onPullRefresh();

    }

    public void refreshListStop() {
        //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
        //when your data has finished loading, cset the refresh state of the view to false
        //refreshLayout.setRefreshing(false);
    }

    public void refreshListStart() {
        //refreshLayout.setRefreshing(true);
        //mListener.onPullRefresh();
    }
}
