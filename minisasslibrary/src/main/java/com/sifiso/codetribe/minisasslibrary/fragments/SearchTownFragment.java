package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.SearchTownAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.TownDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;


public class SearchTownFragment extends Fragment implements PageFragment {
    private ImageView SLT_imgSearch2, SLT_hero;
    AutoCompleteTextView SLT_editSearch;
    ListView STF_list;
    SearchTownAdapter adapter;
    SearchTownFragmentListener mListener;
    Context ctx;
    Activity activity;
    View v;
    List<TownDTO> townList;
    ResponseDTO response;
    boolean isFound;
    String searchText;

    public SearchTownFragment() {
        // Required empty public constructor
    }

    private void setFields() {
        SLT_editSearch = (AutoCompleteTextView) v.findViewById(R.id.SLT_editSearch);
        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        SLT_hero = (ImageView) v.findViewById(R.id.SLT_hero);
        STF_list = (ListView) v.findViewById(R.id.STF_list);
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

    private void setList() {
        if (townList == null) {
            townList = new ArrayList<>();
        }
        townList = response.getTownList();
        adapter = new SearchTownAdapter(ctx, townList, new SearchTownAdapter.SearchTownAdapterListener() {
            @Override
            public void onTownClicked(TownDTO town) {

            }
        });
        STF_list.setAdapter(adapter);
        STF_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TownDTO town = (TownDTO) parent.getItemAtPosition(position);
                mListener.onTownSelected(town);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search_town, container, false);
        ctx = getActivity().getApplicationContext();
        response = (ResponseDTO) getArguments().getSerializable("response");
        activity = getActivity();
        setFields();
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SearchTownFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ObservationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void animateCounts() {

    }

    List<String> stringRiver;

    private void riverToSearch() {
        stringRiver = new ArrayList<>();
        for (int i = 0; i < response.getTownList().size(); i++) {

            TownDTO town = response.getTownList().get(i);
            stringRiver.add(town.getTownName());
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
        for (int i = 0; i < response.getTownList().size(); i++) {
            TownDTO searchRiver = response.getTownList().get(i);
            if (searchRiver.getTownName().contains(searchText)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            STF_list.setSelection(index);

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

    public interface SearchTownFragmentListener {
        // TODO: Update argument type and name
        public void onTownSelected(TownDTO town);
    }

}
