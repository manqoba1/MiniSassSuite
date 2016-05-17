package com.sifiso.codetribe.minisasslibrary.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.ObservationListAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.util.SpacesItemDecoration;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ObservationListener} interface
 * to handle interaction events.
 * Use the {@link ObservationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObservationListFragment extends Fragment {

    private ObservationListener mListener;
    private RiverDTO river;
    private EvaluationSiteDTO selectedSite;
    private View view;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private CheckBox chkBoxAll;
    private boolean isFirstTimeIn = true;
    private TextView txtTotalEvals, txtTotalSites;
    private ObservationListAdapter adapter;
    static final String LOG = ObservationListFragment.class.getSimpleName();

    public ObservationListFragment() {
    }

    public static ObservationListFragment newInstance(RiverDTO river) {
        ObservationListFragment fragment = new ObservationListFragment();
        Bundle args = new Bundle();
        args.putSerializable("river", river);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            river = (RiverDTO) getArguments().getSerializable("river");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_observation_list, container, false);
        setFields();
        return view;
    }

    List<EvaluationDTO> riverEvaluations;

    private void setFields() {
        spinner = (Spinner) view.findViewById(R.id.spinner);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(lm);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));

        chkBoxAll = (CheckBox) view.findViewById(R.id.chkBoxAll);
        txtTotalEvals = (TextView) view.findViewById(R.id.totalObserv);
        txtTotalSites = (TextView) view.findViewById(R.id.totalSites);

        chkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    listRiverObservations();
                } else {
                    listSiteObservations();
                }
            }
        });
    }

    private void listRiverObservations() {
        riverEvaluations = new ArrayList<>();
        for (EvaluationSiteDTO site : river.getEvaluationsiteList()) {
            riverEvaluations.addAll(site.getEvaluationList());
        }
        Collections.sort(riverEvaluations);
        setList(riverEvaluations);
    }


    private void listSiteObservations() {

        Collections.sort(selectedSite.getEvaluationList());
        setList(selectedSite.getEvaluationList());

    }

    private void setList(List<EvaluationDTO> list) {

        txtTotalEvals.setText("" + list.size());
        adapter = new ObservationListAdapter(list, 0, getContext(), new ObservationListAdapter.ObservationListListener() {
            @Override
            public void onEvaluationContribute(EvaluationDTO evaluation) {

            }

            @Override
            public void onDirectionToSite() {
                getDirections();
            }

            @Override
            public void onEvaluationEdit(EvaluationDTO evaluation) {
                Util.showToast(getContext(), "EvaluationEdit: Under Construction");
            }

            @Override
            public void onPictureRequired(EvaluationDTO evaluation) {
                    mListener.onPictureRequired(evaluation);
            }

            @Override
            public void onViewInsect(List<EvaluationInsectDTO> insectImage) {
                Util.showToast(getContext(), "Under Construction");
            }
        });

        recyclerView.setAdapter(adapter);
    }
    private void getDirections() {
        String url = "http://maps.google.com/maps?saddr="
                + selectedSite.getLatitude() + "," + selectedSite.getLongitude()
                + "&daddr=" + latitude + "," + longitude + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
    private void setSiteSpinner() {
        Log.e(LOG,"--------- setSiteSpinner #########");
        List<String> list = new ArrayList<>();
        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
            list.add(s.getSiteName());
        }
        txtTotalSites.setText("" + list.size());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.spinner_text_black, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSite = river.getEvaluationsiteList().get(position);
                listSiteObservations();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (selectedSite != null) {
            int i = 0;
            for (String s : list) {
                if (s.equalsIgnoreCase(selectedSite.getSiteName())) {
                    spinner.setSelection(i, true);
                    break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ObservationListener) {
            mListener = (ObservationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ObservationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ObservationListener {

        void onPictureRequired(EvaluationDTO eval);

        void onEvaluationEdit(EvaluationDTO eval);

        void onInsectView(List<EvaluationInsectDTO> insects);
    }

    private double latitude, longitude;

    public void setLocation(double latitude, double longitude) {

        float dist = Util.getDistanceBetween(
                this.latitude,this.longitude,latitude,longitude);
        if (dist < 3000) {
            Log.d(LOG,"still in same place ... no need to calculate order of sites");
            return;
        }


        this.latitude = latitude;
        this.longitude = longitude;
        int i = 1;
        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
            s.calculateDistance(latitude, longitude);
        }
        Collections.sort(river.getEvaluationsiteList());
        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
            String name = "Unnamed Site " + i;
            if (s.getSiteName() != null) {
                name = s.getSiteName();
            }
            s.setSiteName(name);
            i++;
        }
        setSiteSpinner();
    }

    public void setRiver(RiverDTO river, EvaluationSiteDTO selectedSite) {
        this.river = river;
        this.selectedSite = selectedSite;
        if (spinner != null) {
            setSiteSpinner();
            listSiteObservations();
        }

    }

}
