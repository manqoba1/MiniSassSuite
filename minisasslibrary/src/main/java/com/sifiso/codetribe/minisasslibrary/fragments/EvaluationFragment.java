package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;


public class EvaluationFragment extends Fragment implements PageFragment {
    static final String LOG = EvaluationFragment.class.getSimpleName();
   /* private TextView WC_minus, WC_add, WT_minus, WT_add,
            WP_minus, WP_add, WO_minus, WO_add, WE_minus, WE_add;
    private EditText WC_score, WP_score, WT_score, WO_score, WE_score;
    private TextView TV_total_score, TV_average_score, TV_avg_score, TV_score_status;
    private ImageView IMG_score_icon, AE_pin_point;
    private TextView WT_sp_river, EDT_comment;
    private Button WT_gps_picker, SL_show_insect, AE_create;
    private Context ctx;
    EvaluationDTO evaluationDTO;
    Integer teamMemberID, conditionID;
    ResponseDTO response;
    InsectSelectionDialog insectSelectionDialog;
    double wc = 0.0, wt = 0.0, we = 0.0, wp = 0.0, wo = 0.0;
    private Activity activity;
    List<String> categoryStr;
    private Spinner WT_sp_category;
    private ViewStub viewStub;
    private Integer categoryID;
    private Integer riverID;
    private List<InsectImageDTO> insectImageList;
    private EvaluationSiteDTO evaluationSite;
    View v;
    int codeStatus;
    RiverDTO river;
    String catType = "Select category";
    EvaluationFragmentListener mListener;
    static final int CREATE_EVALUATION = 108;*/

    public EvaluationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    /*public void setRiverField(RiverDTO r) {
        riverID = r.getRiverID();
        WT_sp_river.setText(r.getRiverName());
        buildUI();
    }*/
    View v;
    Context ctx;
    Activity activity;
    ResponseDTO response;
    EvaluationFragmentListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_evaluation, container, false);
        ctx = getActivity().getApplicationContext();
        activity = getActivity();
        Bundle b = getArguments();
        response = (ResponseDTO) getArguments().getSerializable("response");
        // setField();
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EvaluationFragmentListener) activity;
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


    public interface EvaluationFragmentListener {
        // TODO: Update argument type and name
        public void onScanGpsRequest();

        public void onSelectInsectsRequest();

        public void onDispatchTakeImage();

    }

}
