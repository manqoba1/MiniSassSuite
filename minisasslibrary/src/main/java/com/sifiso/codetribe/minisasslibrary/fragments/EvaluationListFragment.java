package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.InsectBrowser;
import com.sifiso.codetribe.minisasslibrary.adapters.EvaluationAdapter;
import com.sifiso.codetribe.minisasslibrary.dialogs.EditEvaluationDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateEvaluationListener} interface
 * to handle interaction events.
 * Use the {@link EvaluationListFragment#} factory method to
 * create an instance of this fragment.
 */
public class EvaluationListFragment extends Fragment implements PageFragment {

    private CreateEvaluationListener mListener;
    private LinearLayout FEL_search;
    private ListView FEL_list;
    private TextView RL_add;
    private ImageView SLT_imgSearch2;
    private RelativeLayout SLT_hero;
    private EditText SLT_editSearch;
    private EditEvaluationDialog editEvaluationDialog;

    public static EvaluationListFragment newInstance(ResponseDTO resp, int index) {
        EvaluationListFragment fragment = new EvaluationListFragment();
        Bundle args = new Bundle();
        args.putSerializable("response", resp);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    public EvaluationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.evaluation_dialog_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void build() {

        FEL_list = (ListView) v.findViewById(R.id.FEL_list);
        FEL_search = (LinearLayout) v.findViewById(R.id.FEL_search);
        SLT_editSearch = (EditText) v.findViewById(R.id.SLT_editSearch);
        SLT_editSearch.setVisibility(View.GONE);
        SLT_hero = (RelativeLayout) v.findViewById(R.id.SLT_hero);
        SLT_imgSearch2 = (ImageView) v.findViewById(R.id.SLT_imgSearch2);
        SLT_imgSearch2.setVisibility(View.GONE);
        SLT_hero.setBackground(Util.getRandomHeroImage(ctx));


    }

    public void setEvaluationSiteList(List<EvaluationSiteDTO> evaluationSiteList) {
        this.evaluationSiteList = evaluationSiteList;
        List<EvaluationDTO> list = new ArrayList<>();
        for (EvaluationSiteDTO evalSite : evaluationSiteList) {
            for (EvaluationDTO eval : evalSite.getEvaluationList()) {
                list.add(eval);
            }
        }
        evaluationList = list;
        Log.i(LOG, "++ evaluation has been set: " + evaluationList.size());
        if (v != null) {
            setList();
        } else {
            Log.e(LOG, "$%#$## WTF?");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            evaluationSiteList = (List<EvaluationSiteDTO>) getArguments().getSerializable("evaluationSite");

        }
    }

    View v;
    EvaluationAdapter adapter;
    private Context ctx;
    private List<EvaluationDTO> evaluationList;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_evaluation_list, container, false);
        Bundle b = getArguments();
        ctx = getActivity().getApplicationContext();
        activity = getActivity();

        build();
        return v;
    }

    public String getRiverName() {
        return evaluationSiteList.get(0).getRiverName();
    }

    private void setList() {
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inf.inflate(R.layout.hero_layout, null);
        // FEL_list.addHeaderView(v);
        // FEL_list.addFooterView(v);
        adapter = new EvaluationAdapter(ctx, evaluationList, new EvaluationAdapter.EvaluationAdapterListener() {
            @Override
            public void onEvaluationContribute(EvaluationDTO evaluation) {
                ToastUtil.toast(ctx, "Contributing still under construction");
            }

            @Override
            public void onDirectionToSite(EvaluationSiteDTO evaluationSite) {
                mListener.onDirection(evaluationSite.getLatitude(), evaluationSite.getLongitude());
            }

            @Override
            public void onEvaluationEdit(EvaluationDTO evaluation) {
                editEvaluationDialog = new EditEvaluationDialog();

                editEvaluationDialog.show(getFragmentManager(), "Edit Evaluation");
                editEvaluationDialog.setEvaluation(evaluation);
                editEvaluationDialog.setListener(new EditEvaluationDialog.EditEvaluationDialogListener() {
                    @Override
                    public void onSaveUpdate(EvaluationDTO evaluation) {
                        if (evaluation.getEvaluationID() == null) {
                            ToastUtil.errorToast(ctx, "Evaluation can not be edited");
                            return;
                        }
                        editEvaluation(evaluation);
                    }
                });
            }

            @Override
            public void onViewInsect(List<EvaluationInsectDTO> insectImage) {

                Util.showPopupInsectsSelected(ctx, activity, insectImage, FEL_search, ctx.getResources().getString(R.string.insect_selected), new Util.UtilPopupInsectListener() {
                    @Override
                    public void onInsectSelected(InsectDTO insect) {
                        Intent intent = new Intent(activity.getApplicationContext(), InsectBrowser.class);
                        intent.putExtra("insect", insect);
                        startActivity(intent);
                        // Toast.makeText(ctx, insect.getGroupName(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        FEL_list.setAdapter(adapter);
    }

    static String LOG = EvaluationListFragment.class.getSimpleName();


    private void editEvaluation(EvaluationDTO dto) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_EVALUATION);
        w.setEvaluation(dto);
        Log.i(LOG, "Updated : " + new Gson().toJson(dto));
        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                if (!ErrorUtil.checkServerError(ctx, r)) {
                    return;
                }
                Log.i(LOG, "Updated : "+evaluationList.size() + new Gson().toJson(r.getEvaluation()));
                int i=0;
                for(EvaluationDTO ev : evaluationList){
                    if (ev.getEvaluationID() == r.getEvaluation().getEvaluationID()) {
                        Log.i(LOG, "Updated : " + ev.getEvaluationID() + " : " + r.getEvaluation().getEvaluationID());
                        evaluationList.set(i, r.getEvaluation());

                    }
                    i++;
                }

               setList();
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }

            @Override
            public void onError(String message) {

            }
        });
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
    public void animateCounts() {

    }

    private List<EvaluationSiteDTO> evaluationSiteList;
}
