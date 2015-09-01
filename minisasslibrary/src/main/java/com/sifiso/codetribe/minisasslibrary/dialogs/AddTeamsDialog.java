package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.CountryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.OrganisationtypeDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sifiso on 2015-07-11.
 */
public class AddTeamsDialog extends DialogFragment {
    static final String LOG = AddTeamsDialog.class.getSimpleName();
    View v;
    Context ctx;
    Activity activity;
    Button tnb_add_member;
    EditText edtRegTeamName;
    Spinner sp_org_type, sp_country;
    Integer countryID, orgaTypeID;

    private ResponseDTO response;
    private AddTeamDialogListener listener;

    private void setFields() {
        tnb_add_member = (Button) v.findViewById(R.id.tnb_add_member);
        edtRegTeamName = (EditText) v.findViewById(R.id.edtRegTeamName);
        sp_org_type = (Spinner) v.findViewById(R.id.sp_org_type);
        sp_country = (Spinner) v.findViewById(R.id.sp_country);
        setSpinners();
        tnb_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryID == null) {
                    Util.showErrorToast(ctx, "Choose a country");
                    //Toast.makeText(ctx, "Select Town", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (orgaTypeID == null) {
                    Util.showErrorToast(ctx, "Choose a Organisation type");
                    //Toast.makeText(ctx, "Select Town", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtRegTeamName.getText().toString().isEmpty()) {
                    //Toast.makeText(ctx, "Enter Team Name", Toast.LENGTH_SHORT).show();
                    edtRegTeamName.setError("Enter team name");
                    return;
                }

                TeamDTO team = new TeamDTO();
                team.setTeamName(edtRegTeamName.getText().toString());
                team.setDateRegistered(new Date().getTime());
                team.setCountryID(countryID);
                team.setOrganisationTypeID(orgaTypeID);
                listener.onAddTeamToMember(team);
                dismiss();
            }
        });
    }

    public interface AddTeamDialogListener {
        public void onAddTeamToMember(TeamDTO team);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        v = inflater.inflate(R.layout.add_team, container, false);
        ctx = getActivity().getApplicationContext();
        activity = getActivity();
        setFields();
        return v;
    }

    public void setTeamData(ResponseDTO response) {
        this.response = response;
    }

    List<String> countrySpinner, orgTypeSpinner;

    private void setSpinners() {

        countrySpinner = new ArrayList<>();
        orgTypeSpinner = new ArrayList<>();

        countrySpinner.add("Choose country");
        for (CountryDTO c : response.getCountryList()) {
            countrySpinner.add(c.getCountryName());
        }
        orgTypeSpinner.add("Choose organisation type");
        for (OrganisationtypeDTO c : response.getOrganisationtypeList()) {
            orgTypeSpinner.add(c.getOrganisationName());
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, countrySpinner);
        ArrayAdapter<String> orgtypeAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, orgTypeSpinner);
        sp_org_type.setAdapter(orgtypeAdapter);
        sp_org_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String oName = "";
                if (position > 0) {
                    orgaTypeID = response.getOrganisationtypeList().get(position - 1).getOrganisationTypeID();
                    oName = response.getOrganisationtypeList().get(position - 1).getOrganisationName();
                } else {
                    orgaTypeID = null;
                }
                Log.d(LOG, " the org id " + orgaTypeID + " : " + oName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_country.setAdapter(countryAdapter);
        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cName = "";
                if (position > 0) {
                    countryID = response.getCountryList().get(position - 1).getCountryID();
                    cName = response.getCountryList().get(position - 1).getCountryName();
                } else {
                    countryID = null;
                }
                Log.d(LOG, " the country id " + countryID + " : " + cName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setListener(AddTeamDialogListener listener) {
        this.listener = listener;
    }
}
