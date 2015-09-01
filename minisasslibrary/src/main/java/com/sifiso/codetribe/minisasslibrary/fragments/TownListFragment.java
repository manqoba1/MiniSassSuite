package com.sifiso.codetribe.minisasslibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TownAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.RiverTownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class TownListFragment extends Fragment implements PageFragment {
    ListView FTL_townList;
    private AutoCompleteTextView SLT_editSearch;
    private ImageView SLT_imgSearch2, SLT_hero;
    boolean isFound;
    String searchText;

    public TownListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    private void setField() {
        FTL_townList = (ListView) v.findViewById(R.id.FTL_townList);
        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        SLT_editSearch = (AutoCompleteTextView) v.findViewById(R.id.SLT_editSearch);
        SLT_hero = (ImageView) v.findViewById(R.id.SLT_hero);
        SLT_imgSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchRiver();
            }
        });

        SLT_hero.setImageDrawable(Util.getRandomHeroImage(ctx));
        setList();
        riverToSearch();
    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_town_list, container, false);
        ctx = getActivity().getApplicationContext();
        Bundle b = getArguments();
        if (b != null) {
            riverTownList = (List<RiverTownDTO>) b.getSerializable("riverTown");

            getActivity().setTitle(riverTownList.get(0).getRiver().getRiverName() + " Towns");
        }
        setField();
        return v;
    }

    public String getTownName() {
        return riverTownList.get(0).getRiver().getRiverName();
    }

    private List<RiverTownDTO> riverTownList;
    private List<TownDTO> townList;
    private TownAdapter adapter;
    Context ctx;

    private void setList() {
        for (RiverTownDTO rt : riverTownList) {
            if (townList == null) {
                townList = new ArrayList<>();
            }
            townList.add(rt.getTown());
        }
        adapter = new TownAdapter(ctx, townList, new TownAdapter.TownAdapterListener() {
            @Override
            public void onTeamClicked(List<TeamDTO> teamList) {

            }

            @Override
            public void onMapClicked(TownDTO team) {

            }
        });

        FTL_townList.setAdapter(adapter);
    }

    List<String> stringRiver;

    private void riverToSearch() {
        stringRiver = new ArrayList<>();
        for (int i = 0; i < riverTownList.size(); i++) {

            RiverTownDTO riverText = riverTownList.get(i);
            stringRiver.add(riverText.getTown().getTownName());
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
        for (int i = 0; i < riverTownList.size(); i++) {
            RiverTownDTO searchRiver = riverTownList.get(i);
            if (searchRiver.getTown().getTownName().contains(searchText)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            FTL_townList.setSelection(index);


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

    @Override
    public void animateCounts() {

    }
}
