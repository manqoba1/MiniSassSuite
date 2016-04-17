package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.InsectSelectionAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class InsectSelectorFragment extends Fragment implements PageFragment {
    static final String LOG = InsectSelectorFragment.class.getSimpleName();
    View view;
    Context ctx;
    double total = 0.0;
    private InsectSelectionAdapter adapter;
    private RecyclerView SD_list;

    private Button SD_done;
    private TextView textView;
    private List<InsectImageDTO> mSites;
    private List<InsectImageDTO> listCal;
    InsectSelectorFragmentListener listener;

    public InsectSelectorFragment() {
        // Required empty public constructor
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
        Log.w(LOG, "###### onCreateView");

        view = inflater.inflate(R.layout.fragment_insect_selector, container, false);

        ctx = getActivity();

        setFields();
        setList();
        return view;

    }
    private void setList() {

        adapter = new InsectSelectionAdapter(ctx, mSites, new InsectSelectionAdapter.InsectPopupAdapterListener() {
            @Override
            public void onInsectSelected(InsectImageDTO insect, int index) {

                collectCheckedInsects(insect);

            }

            @Override
            public void onViewMore(InsectImageDTO insect, int index) {

            }

        });
        SD_list.setAdapter(adapter);
    }
    private void collectCheckedInsects(InsectImageDTO mDtos) {
        if (listCal == null) {
            listCal = new ArrayList<>(mSites.size());
        }


        if (mDtos.selected == true) {
            //if (listCal.contains(mDtos))
            listCal.add(mDtos);
            total = total + mDtos.getSensitivityScore();

        } else {
            mDtos.selected = true;
            listCal.remove(mDtos);
            total = total - mDtos.getSensitivityScore();
            mDtos.selected = false;
        }
        Log.e(LOG, listCal.size() + "");
        listener.onSelectDone(listCal);
    }
    private void setFields() {
        SD_list = (RecyclerView) view.findViewById(R.id.SD_list);
        SD_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        SD_list.setItemAnimator(new DefaultItemAnimator());
        SD_list.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.VERTICAL));
        textView = (TextView) view.findViewById(R.id.textView);
        SD_done = (Button) view.findViewById(R.id.SD_done);

        SD_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onSelectDone(listCal);


            }
        });
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InsectSelectorFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void animateCounts() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public void setmSites(List<InsectImageDTO> mSites) {
        this.mSites = mSites;
    }

    public void setListener(InsectSelectorFragmentListener listener) {
        this.listener = listener;
    }
    public interface InsectSelectorFragmentListener {
        public void onSelectDone(List<InsectImageDTO> insectImages);
    }

}
