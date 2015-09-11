package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

/**
 * Created by CodeTribe1 on 2015-07-07.
 */
public class AddMemberDialog extends DialogFragment {
    static final String LOG = AddMemberDialog.class.getSimpleName();
    View v;
    Context ctx;
    Activity activity;
    Button rsRegister;
    EditText rsMemberName, rsMemberSurname;
    EditText rsCellphone, rsPin, re_edtPassword;
    CheckBox cbMoreMember;
    AutoCompleteTextView rsMemberEmail;
    public ProgressBar progressBar3;
    private TeamMemberDTO teamMember;
    private AddMemberDialogListener listener;


    public interface AddMemberDialogListener {
        public void membersToBeRegistered(TeamMemberDTO teamMember);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        v = inflater.inflate(R.layout.add_member_layout, container, false);

        ctx = getActivity().getApplicationContext();
        //activity = this;
        setFields();
        return v;
    }

    TeamMemberDTO member;

    private void setFields() {
        progressBar3 = (ProgressBar) v.findViewById(R.id.progressBar3);
        re_edtPassword = (EditText) v.findViewById(R.id.re_edtPassword);
        rsMemberName = (EditText) v.findViewById(R.id.edtMemberName);
        rsMemberSurname = (EditText) v.findViewById(R.id.edtMemberLastNAme);
        rsPin = (EditText) v.findViewById(R.id.edtPassword);
        rsPin.setVisibility(View.GONE);
        re_edtPassword.setVisibility(View.GONE);
        rsMemberEmail = (AutoCompleteTextView) v.findViewById(R.id.edtMemberEmail);
        rsCellphone = (EditText) v.findViewById(R.id.edtMemberPhone);

        cbMoreMember = (CheckBox) v.findViewById(R.id.cbMoreMember);
        cbMoreMember.setVisibility(View.GONE);
        rsRegister = (Button) v.findViewById(R.id.btnLogSubmit);
        rsRegister.setText("Add member");
        getDialog().setTitle("Add Team Member");

        if (isFlag()) {
            rsMemberName.setText(teamMember.getFirstName());
            rsMemberSurname.setText(teamMember.getLastName());
            rsCellphone.setText(teamMember.getCellphone());
            rsMemberEmail.setText(teamMember.getEmail());
            rsMemberEmail.setEnabled(false);
            rsPin.setText(teamMember.getPin());
            rsPin.setVisibility(View.VISIBLE);
            re_edtPassword.setVisibility(View.GONE);
            rsRegister.setText("Submit");
            //member.setPin(rsPin.getText().toString());
            getDialog().setTitle("Edit Profile");
        }
        rsRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.flashOnce(rsRegister, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

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
                            rsMemberSurname.setError("Last name should be letters only ");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!Statics.rfc2822.matcher(rsMemberEmail.getText().toString()).matches()) {
                            rsMemberEmail.setError("Enter email address");
                            //Toast.makeText(ctx, "Enter Email Address", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (rsCellphone.getText().toString().length() != 10) {
                            rsCellphone.setError("Phone number must be 10 digits long");
                            // Toast.makeText(ctx, "Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        member = new TeamMemberDTO();
                        member.setActiveFlag(0);
                        member.setCellphone(rsCellphone.getText().toString());
                        member.setEmail(rsMemberEmail.getText().toString());
                        member.setFirstName(rsMemberName.getText().toString());
                        member.setLastName(rsMemberSurname.getText().toString());

                        member.setTeamID(teamMember.getTeamID());

                        // member.setTeamMemberImage(teamMember.getTeamMemberImage());
                        member.setTeamMemberID(teamMember.getTeamMemberID());
                        if (isFlag()) {

                            member.setTeamMemberImage(teamMember.getTeamMemberImage());
                            member.setTeamName(teamMember.getTeamName());
                            if (teamMember.getTmemberList() != null) {
                                member.setTmemberList(teamMember.getTmemberList());
                            }
                            if (teamMember.getTeam() != null) {
                                member.setTeam(teamMember.getTeam());
                            }
                            member.setPin(rsPin.getText().toString());
                        }

                        Log.d(LOG, new Gson().toJson(member));
                        progressBar3.setVisibility(View.VISIBLE);
                        listener.membersToBeRegistered(member);
                        // dismiss();
                    }
                });

            }
        });
    }


    boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setTeamMember(TeamMemberDTO teamMember) {
        Log.d(LOG, new Gson().toJson(teamMember));
        this.teamMember = teamMember;
    }

    public void setListener(AddMemberDialogListener listener) {
        this.listener = listener;
    }
}
