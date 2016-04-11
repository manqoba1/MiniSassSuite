package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TeamAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TmemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamListener;
import com.sifiso.codetribe.minisasslibrary.listeners.BusyListener;
import com.sifiso.codetribe.minisasslibrary.listeners.PageInterface;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.NetUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class TeamListFragment extends Fragment implements PageInterface, PageFragment {
    View view;
    Context ctx;
    ResponseDTO response;
    BusyListener busyListener;
    TeamListener teamListener;

    List<TmemberDTO> tmemberList;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof BusyListener) {
            busyListener = (BusyListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host must implement BusyListener");
        }
        if (a instanceof TeamListener) {
            teamListener = (TeamListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host must implement TeamListener");
        }
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        Log.i(LOG, "##### onCreateView");
        Log.e(LOG, "TeamID = "
                + SharedUtil.getTeamMember(ctx).getTeamMemberID());

        inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.fragment_team_list, container, false);
        setFields();
        Bundle b = getArguments();
        if (b != null) {
            tmemberList = (List<TmemberDTO>) b.getSerializable("tTeamList");
            teamList = new ArrayList<>();
            for (TmemberDTO tm : tmemberList) {
                teamList.add(tm.getTeam());
            }
            setList();
        }

        return view;
    }

    List<TeamDTO> teammList;

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.w(LOG, "## onSaveInstanceState");
        if (response != null) {
            b.putSerializable("response", response);
        }
        super.onSaveInstanceState(b);
    }

    @Override
    public void onResume() {
        Log.w(LOG, "## onResume");

        super.onResume();

    }

    public void getTeams() {
        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.GET_DATA);
        r.setTeam(SharedUtil.getTeamMember(ctx)
                .getTeam());
        //
        if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

        busyListener.setBusy();
        NetUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, r, new NetUtil.NetListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();

                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        response = r;
                        teamList = response.getTeamList();
                        setList();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_TEAM, r, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						busyListener.setNotBusy();
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						busyListener.setNotBusy();
//
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						response = r;
//						teamList = response.getTeamList();
//						setList();
//					}
//				});

    }

    private void setList() {

        adapter = new TeamAdapter(ctx, R.layout.team_item, teamList);
        if (teamList == null || teamList.isEmpty()) {
            Log.e(LOG, "TeamList is empty or null!");
            return;
        }

        txtCount.setText("" + teamList.size());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                team = teamList.get(arg2);
                teamListener.onTeamPicked(team);
            }
        });
    }

    public void addTeamMembers(List<TeamMemberDTO> list) {
        Log.w(LOG, "addTeamMembers, list : " + list.size());
        if (!list.isEmpty()) {
            for (TeamDTO t : teamList) {
                if (list.get(0).getTeamID() == t.getTeamID()
                        ) {
                    t.getTeammemberList().addAll(0, list);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setFields() {
        editTeamName = (EditText) view.findViewById(R.id.TEAM_editTeamName);
        btnSave = (Button) view.findViewById(R.id.TEAM_btnSave);
        txtCount = (TextView) view.findViewById(R.id.TEAM_count);
        listView = (ListView) view.findViewById(R.id.TEAM_list);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        if (editTeamName.getText().toString().isEmpty()) {
            ToastUtil.toast(ctx,
                    ctx.getResources().getString(R.string.enter_team_name));
            return;
        }
        TeamDTO t = new TeamDTO();
        t.setTeamName(editTeamName.getText().toString());
        t.setTeammemberList(new ArrayList<TeamMemberDTO>());

        TeamMemberDTO tm = new TeamMemberDTO();
        tm.setTeam(SharedUtil.getTeamMember(ctx).getTeam());
        t.getTeammemberList().add(tm);

        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.REGISTER_TEAM);
        r.setTeam(t);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
        busyListener.setBusy();
        BaseVolley.getRemoteData(Statics.MINI_SASS_ENDPOINT, r, ctx,
                new BaseVolley.BohaVolleyListener() {

                    @Override
                    public void onVolleyError(VolleyError error) {
                        busyListener.setNotBusy();
                        ToastUtil.errorToast(
                                ctx,
                                ctx.getResources().getString(
                                        R.string.error_server_comms));

                    }

                    @Override
                    public void onError(String message) {
                        busyListener.setNotBusy();
                        ToastUtil.errorToast(
                                ctx,
                                message);
                    }

                    @Override
                    public void onResponseReceived(ResponseDTO r) {
                        busyListener.setNotBusy();
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        TeamDTO t = r.getTeam();

                        if (teamList == null) {
                            teamList = new ArrayList<TeamDTO>();
                        }
                        teamList.add(0, t);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        } else {
                            setList();
                        }

                    }
                });
    }

    List<TeamDTO> teamList;
    TeamDTO team;
    TeamAdapter adapter;
    EditText editTeamName;
    Button btnSave;
    TextView txtCount;
    ListView listView;

    static final String LOG = "TeamListFragment";

    @Override
    public void animateCounts() {

    }
}
