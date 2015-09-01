package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.EvaluationAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Activities that contain this fragment must implement the
 * {@link EvaluationListDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class EvaluationListDialog extends DialogFragment {

    private OnFragmentInteractionListener mListener;


    public EvaluationListDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    View v;
    private LinearLayout FEL_search;
    private ListView FEL_list;
    private ImageView SLT_imgSearch2, SLT_hero;
    private EditText SLT_editSearch;
    EvaluationAdapter adapter;
    private List<EvaluationDTO> evaluationList;
    private List<EvaluationSiteDTO> evaluationSiteList;

    private Context ctx;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.evaluation_list_dialog, container, false);

        ctx = getActivity().getApplicationContext();
        setHasOptionsMenu(true);
        Bundle b = getArguments();
        setMenuVisibility(true);
        if (b != null) {
            evaluationSiteList = (List<EvaluationSiteDTO>) b.getSerializable("evaluationSite");
            for (EvaluationSiteDTO evalSite : evaluationSiteList) {

                for (EvaluationDTO eval : evalSite.getEvaluationList()) {
                    if (evaluationList == null) {
                        evaluationList = new ArrayList<EvaluationDTO>();
                    }
                    evaluationList.add(eval);
                }
            }
        }
        build();
        return v;
    }
    private void build() {
        FEL_list = (ListView) v.findViewById(R.id.FEL_list);
        FEL_search = (LinearLayout) v.findViewById(R.id.FEL_search);
        SLT_editSearch = (EditText) v.findViewById(R.id.SLT_editSearch);
        SLT_hero = (ImageView) v.findViewById(R.id.SLT_hero);
        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        setList();
    }
    private void setList() {
        if (evaluationList == null) evaluationList = new ArrayList<>();
        adapter = new EvaluationAdapter(ctx, evaluationList, new EvaluationAdapter.EvaluationAdapterListener() {
            @Override
            public void onEvaluationContribute(EvaluationDTO evaluation) {

            }

            @Override
            public void onDirectionToSite(EvaluationSiteDTO evaluationSite) {

            }

            @Override
            public void onEvaluationEdit(EvaluationDTO evaluation) {

            }

            @Override
            public void onViewInsect(List<EvaluationInsectDTO> insectImage) {

            }
        });
        FEL_list.setAdapter(adapter);
    }



   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.evaluation_dialog_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_menu_evaluation) {

            return true;
        }
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
