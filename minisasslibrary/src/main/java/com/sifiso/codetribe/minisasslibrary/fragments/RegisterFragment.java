package com.sifiso.codetribe.minisasslibrary.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.ForgotPasswordActivity;
import com.sifiso.codetribe.minisasslibrary.adapters.MemberToBeAddedAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.CountryDTO;
import com.sifiso.codetribe.minisasslibrary.dto.OrganisationtypeDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterFragment extends Fragment implements PageFragment {
    public interface RegisterFragmentListener {
        // TODO: Update argument type and name
        public void onRegistered();

        public void onTownRequest();
    }

    Context ctx;
    Activity activity;
    Button rsRegister;
    EditText rsMemberName, rsMemberSurname;
    EditText rsCellphone, rsPin, re_edtPassword;
    CheckBox cbMoreMember;
    Spinner sp_org_type, sp_country;
    View v;
    String email;
    Integer countryID, orgaTypeID;
    AutoCompleteTextView rsMemberEmail, rsTeamName;
    ResponseDTO response;
    ListView lsMember;
    LinearLayout llMember;
    TextView textView13;
    RelativeLayout imgTopLgo;
    private RegisterFragmentListener mListener;
    Map<Integer, String> organisationType;

    public RegisterFragment() {
        // Required empty public constructor
    }

    static String LOG = RegisterFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_register, container, false);
        activity = getActivity();
        ctx = getActivity().getApplicationContext();
        setFields();

        return v;
    }

    List<String> countrySpinner, orgTypeSpinner, teamSpinner;
    ArrayAdapter<String> countryAdapter;
    ArrayAdapter<String> orgtypeAdapter;
    ArrayAdapter<String> teamAdapter;

    private void setOrganisationTypeHash() {
        organisationType = new HashMap<>();
        organisationType.put(3, "Conservancy");
        organisationType.put(6, "Consultancy");
        organisationType.put(5, "Government Department");
        organisationType.put(2, "NGO");
        organisationType.put(7, "Other");
        organisationType.put(4, "Private Individual");
        organisationType.put(1, "School");
    }

    private void setSpinners() {
        if (countrySpinner == null && orgTypeSpinner == null && teamSpinner == null) {
            countrySpinner = new ArrayList<>();
            orgTypeSpinner = new ArrayList<>();
            teamSpinner = new ArrayList<>();
        }

        for (CountryDTO c : response.getCountryList()) {
            countrySpinner.add(c.getCountryName());
        }
        Collections.sort(countrySpinner);
        countrySpinner.add(0,"Choose country");
        orgTypeSpinner.add("Choose organisation type");
        for (OrganisationtypeDTO c : response.getOrganisationtypeList()) {
            orgTypeSpinner.add(c.getOrganisationName());
        }
        for (TeamDTO t : response.getTeamList()) {
            teamSpinner.add(t.getTeamName());
        }

        teamAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, teamSpinner);
        countryAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, countrySpinner);
        orgtypeAdapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, orgTypeSpinner);
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
        rsTeamName.setAdapter(teamAdapter);
        rsTeamName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                indexS = searchRiver(parent.getItemAtPosition(position).toString());
                teamName = response.getTeamList().get(indexS).getTeamName();

                teamID = response.getTeamList().get(indexS).getTeamID();
                rsTeamName.setText(teamName);
                sp_country.setVisibility(View.GONE);
                sp_org_type.setVisibility(View.GONE);
                activateWatcher = true;
            }
        });
        rsTeamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (activateWatcher) {

                    sp_country.setVisibility(View.VISIBLE);
                    sp_org_type.setVisibility(View.VISIBLE);
                    activateWatcher = false;
                    teamID = null;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    boolean activateWatcher;
    String teamName;
    int indexS;
    Integer teamID;

    private Integer searchRiver(String text) {

        int index = 0;

        for (int i = 0; i < response.getTeamList().size(); i++) {
            TeamDTO searchRiver = response.getTeamList().get(i);
            if (searchRiver.getTeamName().contains(text)) {
                isFound = true;
                break;
            }
            index++;
        }
        if (isFound) {
            //STF_list.setSelection(index);
            Log.e(LOG, text + " Found River " + response.getTeamList().get(index).getTeamName() + " " + response.getTeamList().get(index).getTeamID());
        } else {
            // Util.showToast(ctx, ctx.getString(R.string.river_not_found) + " " + SLT_editSearch.getText().toString());
        }

        return index;
    }

    boolean isMoreMember, isFound;
    MemberToBeAddedAdapter memberToBeAddedAdapter;
    TextView SI_app;

    public void setFields() {
        SI_app = (TextView) v.findViewById(R.id.SI_app);
        SI_app.setText(ctx.getResources().getString(R.string.registration_message));
        imgTopLgo = (RelativeLayout) v.findViewById(R.id.imgTopLgo);
        imgTopLgo.setBackground(Util.getRandomHeroImage(ctx));
        imgTopLgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(imgTopLgo, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        imgTopLgo.setBackground(Util.getRandomHeroImage(ctx));
                    }
                });
            }
        });
        rsRegister = (Button) v.findViewById(R.id.btnLogSubmit);
        textView13 = (TextView) v.findViewById(R.id.textView13);
        llMember = (LinearLayout) v.findViewById(R.id.llMember);
        rsTeamName = (AutoCompleteTextView) v.findViewById(R.id.edtRegTeamName);
        sp_country = (Spinner) v.findViewById(R.id.sp_country);
        sp_org_type = (Spinner) v.findViewById(R.id.sp_org_type);
        rsMemberName = (EditText) v.findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) v.findViewById(R.id.edtMemberLastNAme);
        rsPin = (EditText) v.findViewById(R.id.edtPassword);
        re_edtPassword = (EditText) v.findViewById(R.id.re_edtPassword);
        rsMemberEmail = (AutoCompleteTextView) v.findViewById(R.id.edtMemberEmail);
        rsCellphone = (EditText) v.findViewById(R.id.edtMemberPhone);
        lsMember = (ListView) v.findViewById(R.id.lsMember);
        rsMemberEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!memberToBeRegistered.isEmpty()) {
                    memberToBeRegistered.clear();
                }
                Log.d(LOG, "List size" + memberToBeRegistered.size());
                //Backg
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lsMember.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        cbMoreMember = (CheckBox) v.findViewById(R.id.cbMoreMember);
        cbMoreMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isMoreMember = isChecked;
                    onCreateOptionsMenu(mMenu, inflater);
                    disableWhenMoreIsChecked();
                } else {
                    isMoreMember = isChecked;
                    onCreateOptionsMenu(mMenu, inflater);
                    enableWhenMoreIsNotChecked();
                }
            }
        });
        rsRegister.setText("Sign Up");
        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(rsRegister, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                        if (rsTeamName.getText().toString().isEmpty()) {
                            rsTeamName.setError("Enter team name");
                            return;
                        }
                        if (Statics.isSpecial(rsTeamName.getText().toString())) {
                            rsTeamName.setError("Team name should be letters and numbers only");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!activateWatcher) {
                            if (countryID == null) {
                                Util.showErrorToast(ctx, "Select a country");
                                //Toast.makeText(ctx, "Select Town", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (orgaTypeID == null) {
                                Util.showErrorToast(ctx, "Select an organisation type");
                                //Toast.makeText(ctx, "Select Town", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (rsMemberName.getText().toString().isEmpty()) {
                            rsMemberName.setError("Enter first name");

                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Statics.isLetterAndNumber(rsMemberName.getText().toString())) {
                            rsMemberName.setError("First name should be letters only");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (rsMemberSurname.getText().toString().isEmpty()) {
                            rsMemberSurname.setError("Enter last name");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Statics.isLetterAndNumber(rsMemberSurname.getText().toString())) {
                            rsMemberSurname.setError("Last name should be letters only");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (rsMemberEmail.getText().toString().isEmpty()) {
                            rsMemberEmail.setError("Enter email address");
                            //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!Statics.rfc2822.matcher(rsMemberEmail.getText().toString()).matches()) {
                            rsMemberEmail.setError("Incorrect email address format");
                            //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (rsCellphone.getText().toString().length() != 10) {
                            rsCellphone.setError("Phone number must be 10 digits long");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (rsPin.getText().toString().isEmpty()) {
                            rsPin.setError("Enter password");
                            //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!re_edtPassword.getText().toString().equals(rsPin.getText().toString())) {
                            re_edtPassword.setError("password mismatch");
                            rsPin.setError("password mismatch");
                            return;
                        }
                        sendRegistration();
                    }
                });


            }
        });


    }

    private void disableWhenMoreIsChecked() {
        rsTeamName.setEnabled(false);
        sp_country.setEnabled(false);
        sp_org_type.setEnabled(false);
        textView13.setText(rsTeamName.getText().toString().toUpperCase() + " Team members");
        rsRegister.setText("Add Member");
    }

    private void enableWhenMoreIsNotChecked() {
        rsTeamName.setEnabled(true);
        sp_country.setEnabled(true);
        sp_org_type.setEnabled(true);
        rsRegister.setText("REGISTER");
    }

    public void sendRegistration() {


        TeamMemberDTO t = new TeamMemberDTO();
        t.setEmail(rsMemberEmail.getText().toString());
        t.setFirstName(rsMemberName.getText().toString());
        t.setLastName(rsMemberSurname.getText().toString());
        t.setCellphone(rsCellphone.getText().toString());
        t.setPin(rsPin.getText().toString());
        t.setActiveFlag(0);
        t.setDateRegistered(new Date().getTime());
        if (!memberToBeRegistered.contains(t)) {
            memberToBeRegistered.add(0, t);
        }

        if (isMoreMember) {
            ToastUtil.toast(ctx, "Member is added to " + rsTeamName.getText().toString().toUpperCase() + ".");
            llMember.setVisibility(View.VISIBLE);
            memberToBeAddedAdapter = new MemberToBeAddedAdapter(ctx, memberToBeRegistered, new MemberToBeAddedAdapter.MemberToBeAddedAdapterListener() {
                @Override
                public void onRemoveMember(TeamMemberDTO dto) {
                    memberToBeRegistered.remove(dto);
                    memberToBeAddedAdapter.notifyDataSetChanged();
                    if (memberToBeRegistered.size() == 0) {
                        llMember.setVisibility(View.GONE);
                    }
                }
            });
            lsMember.setAdapter(memberToBeAddedAdapter);
            clearMemberInput();
            return;
        }
        registerRequest(memberToBeRegistered);

    }


    private void clearMemberInput() {
        ToastUtil.toast(ctx, "Next member please.");
        rsMemberEmail.setText(null);
        rsMemberName.setText(null);
        rsMemberSurname.setText(null);
        rsCellphone.setText(null);
        rsPin.setText(null);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.doneAddingMember);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    private List<TeamMemberDTO> memberToBeRegistered = new ArrayList<>();

    private void registerRequest(List<TeamMemberDTO> t) {
        final TeamDTO g = new TeamDTO();
        g.setTeamName(rsTeamName.getText().toString());
        g.setTeamID(teamID);
        g.setCountryID(countryID);
        g.setOrganisationTypeID(orgaTypeID);
        g.setTeammemberList(t);
        g.setDateRegistered(new Date().getTime());

        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.REGISTER_TEAM);
        r.setTeam(g);
        setRefreshActionButtonState(true);

        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, r, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO resp) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (resp.getStatusCode() == 222) {
                            Util.showErrorToast(ctx, resp.getMessage());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SharedUtil.storeEmail(ctx, rsMemberEmail.getText().toString());
                                    Intent forgot = new Intent(activity, ForgotPasswordActivity.class);
                                    startActivity(forgot);
                                }
                            }, 5000);
                            return;
                        }
                        if (resp.getStatusCode() == 223) {
                            Util.showErrorToast(ctx, resp.getMessage());
                            return;
                        }
                        if (!ErrorUtil.checkServerError(ctx, resp)) {
                            return;
                        }
                        SharedUtil.storeEmail(ctx, rsMemberEmail.getText().toString());
                        mListener.onRegistered();
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, "Your Entered email address already exist, Retrieve password");
                        setRefreshActionButtonState(false);
                    }
                });
            }

            @Override
            public void onError(final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!message.equals(null)) {

                            Log.e(LOG, message);
                            Util.showErrorToast(ctx, message);

                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegisterFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ObservationListener");
        }
    }

    Menu mMenu;
    MenuInflater inflater;

    public void setResponse(ResponseDTO response) {
        this.response = response;
        Log.i(LOG, "++ evaluation has been set");
        if (v != null) {
            setSpinners();
        } else {
            Log.e(LOG, "$%#$## WTF?");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.inflater = inflater;
        if (isMoreMember) {
            menu.clear();
            inflater.inflate(R.menu.menu_registration, menu);
            //menu.findItem(R.id.action_refresh).setVisible(false);
        } else {
            menu.clear();
            inflater.inflate(R.menu.menu_register, menu);
            //menu.findItem(R.id.action_refresh).setVisible(false);
            //menu.findItem(R.id.action_refresh).setVisible(false);
        }
        this.mMenu = menu;

    }

    private void checkEmpty() {
        if (memberToBeRegistered.size() == 0) {
            ToastUtil.toast(ctx, "Add member to group");
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.doneAddingMember) {
            checkEmpty();
            registerRequest(memberToBeRegistered);
            return true;
        }
        if (id == R.id.action_refresh) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    List<String> emailAccountList;

    public void getEmailx() {
        AccountManager am = AccountManager.get(ctx);
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            Toast.makeText(ctx, "No Accounts found. Please create one and try again", Toast.LENGTH_LONG).show();

            return;
        }

        emailAccountList = new ArrayList<String>();
        if (accts != null) {
            for (int i = 0; i < accts.length; i++) {
                emailAccountList.add(accts[i].name);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_item, emailAccountList);


            rsMemberEmail.setAdapter(adapter);
            rsMemberEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    email = emailAccountList.get(position);
                    rsMemberEmail.setText(email);
                }
            });
        }

    }

    @Override
    public void animateCounts() {

    }


}
