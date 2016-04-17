package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

/**
 * Created by aubreymalabie on 4/17/16.
 */
public class SiteEditorDialog extends DialogFragment {

    public interface SiteEditorListener {
        void onSiteAdded(EvaluationSiteDTO site);

        void onSiteUpdated();

        void onSiteRemoved();

        void onError(String message);
    }

    EditText eSiteName;
    TextView txtCount;
    RadioButton radioRocky, radioSandy;
    Button btnSave;
    View view;
    ImageView deleteIcon;
    EvaluationSiteDTO site;
    RiverDTO river;
    Context context;
    public static final int ADD_SITE = 1, UPDATE_SITE = 2, DELETE_SITE = 3;
    int type;
    SiteEditorListener listener;

    public void setListener(SiteEditorListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.site_editor, null);
        setFields();
        builder.setView(view);

        switch (type) {
            case ADD_SITE:

                break;
            case UPDATE_SITE:
                fill();
                break;
            case DELETE_SITE:
                fill();
                break;
        }

        return builder.create();
    }

    private void fill() {
        eSiteName.setText(site.getSiteName());
        if (site.getCategoryID().intValue() == ROCKY_TYPE_ID) {
            radioRocky.setChecked(true);
        }
        if (site.getCategoryID().intValue() == SANDY_TYPE_ID) {
            radioSandy.setChecked(true);
        }
        txtCount.setText("" + site.getEvaluationList().size());
        if (type == DELETE_SITE) {
            showConfirmDialog();
        }
    }

    private void setFields() {
        txtCount = (TextView) view.findViewById(R.id.evaluations);
        txtCount.setText("0");
        deleteIcon = (ImageView) view.findViewById(R.id.delete);
        eSiteName = (EditText) view.findViewById(R.id.editName);
        radioSandy = (RadioButton) view.findViewById(R.id.radioSandy);
        radioRocky = (RadioButton) view.findViewById(R.id.radioRocky);
        btnSave = (Button) view.findViewById(R.id.btnSave);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case ADD_SITE:
                        addSite();
                        break;
                    case UPDATE_SITE:
                        updateSite();
                        break;
                }
            }
        });
    }

    static final int ROCKY_TYPE_ID = 9, SANDY_TYPE_ID = 8;
    boolean isdeleteSite;

    private void showConfirmDialog() {
        if (isdeleteSite) {
            isdeleteSite = false;
            deleteSite();
            return;
        }
        isdeleteSite = true;
        Util.showToast(getContext(), "Please tap the Remove icon again to confirm site removal");
    }

    private void addSite() {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION_SITE);
        site.setRiverID(river.getRiverID());
        site.setRiverName(river.getRiverName());
        site.setLongitude(this.site.getLongitude());
        site.setLatitude(this.site.getLatitude());
        if (!eSiteName.getText().toString().isEmpty()) {
            site.setSiteName(eSiteName.getText().toString());
        }
        if (radioRocky.isChecked()) {
            site.setCategoryID(ROCKY_TYPE_ID);
        }
        if (radioSandy.isChecked()) {
            site.setCategoryID(SANDY_TYPE_ID);
        }
        w.setEvaluationSite(site);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, context, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                dismiss();
                if (!response.getEvaluationSiteList().isEmpty())
                    listener.onSiteAdded(response.getEvaluationSiteList().get(0));
            }

            @Override
            public void onVolleyError(VolleyError error) {
                dismiss();
                listener.onError(error.getMessage());
            }

            @Override
            public void onError(String message) {
                dismiss();
                listener.onError(message);
            }
        });
    }


    public void deleteSite() {
        RequestDTO w = new RequestDTO(RequestDTO.DELETE_EVALUATION_SITE);
        w.setEvaluationSiteID(site.getEvaluationSiteID());

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, context, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                dismiss();
                listener.onSiteRemoved();
            }

            @Override
            public void onVolleyError(VolleyError error) {
                dismiss();
                listener.onError(error.getMessage());
            }

            @Override
            public void onError(String message) {
                dismiss();
                listener.onError(message);
            }
        });
    }

    private void updateSite() {
        RequestDTO w = new RequestDTO(RequestDTO.UPDATE_EVALUATION_SITE);
        EvaluationSiteDTO es = new EvaluationSiteDTO();
        es.setEvaluationSiteID(site.getEvaluationSiteID());
        es.setLongitude(site.getLongitude());
        es.setLatitude(site.getLatitude());
        if (!eSiteName.getText().toString().isEmpty()) {
            es.setSiteName(eSiteName.getText().toString());
        }
        if (radioRocky.isChecked()) {
            es.setCategoryID(ROCKY_TYPE_ID);
        }
        if (radioSandy.isChecked()) {
            es.setCategoryID(SANDY_TYPE_ID);
        }
        w.setEvaluationSite(es);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, context, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                dismiss();
                listener.onSiteUpdated();
            }

            @Override
            public void onVolleyError(VolleyError error) {
                dismiss();
                listener.onError(error.getMessage());
            }

            @Override
            public void onError(String message) {
                dismiss();
                listener.onError(message);
            }
        });
    }

    public void setSite(EvaluationSiteDTO site) {
        this.site = site;
    }

    public void setRiver(RiverDTO river) {
        this.river = river;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setType(int type) {
        this.type = type;
    }
}
