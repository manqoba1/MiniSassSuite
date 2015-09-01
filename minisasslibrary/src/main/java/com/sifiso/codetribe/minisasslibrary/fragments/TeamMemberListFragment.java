package com.sifiso.codetribe.minisasslibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.TeamMemberAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.interfaces.TeamMemberAddedListener;
import com.sifiso.codetribe.minisasslibrary.listeners.BusyListener;
import com.sifiso.codetribe.minisasslibrary.listeners.PageInterface;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.BohaVolley;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class TeamMemberListFragment extends Fragment implements PageInterface,PageFragment{

	Context ctx;
	List<TeamMemberDTO> teamMemberList;
	TeamDTO team;
	BusyListener busyListener;
	TeamMemberAddedListener teamMemberAddedListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host "
					+ a.getLocalClassName() + " must implement BusyListener");
		}
		if (a instanceof TeamMemberAddedListener) {
			teamMemberAddedListener = (TeamMemberAddedListener) a;
		} else {
			throw new UnsupportedOperationException("Host "
					+ a.getLocalClassName()
					+ " must implement TeamMemberAddedListener");
		}

		super.onAttach(a);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		Log.i(LOG, "#### onCreateView");
		ctx = getActivity();
		teamMember = SharedUtil.getTeamMember(ctx);
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_teammember_list, container,
				false);
		setFields();
		Bundle b = getArguments();
		if (b != null) {
			teamMemberList = (List<TeamMemberDTO>) b.getSerializable("teamMemberList");
			setList();
		}
		return view;
	}

    public void refresh(List<TeamMemberDTO> list) {
        team.setTeammemberList(list);
        setList();
    }
	private void setList() {
		adapter = new TeamMemberAdapter(ctx, R.layout.team_member_item,
				teamMemberList);
		//btnSave.setText(team.getTeamName());
		txtCount.setText("" + teamMemberList.size());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//teamMember = teamMember..get(arg2);

			}
		});
	}

	private void setFields() {
		imageLoader = BohaVolley.getImageLoader(ctx);
		listView = (ListView) view.findViewById(R.id.TMM_list);
		txtCount = (TextView) view.findViewById(R.id.TEAM_count);

		txtLabel = (TextView) view.findViewById(R.id.TMM_joinLabel);
		btnSave = (Button) view.findViewById(R.id.TMM_btnSave);
		btnAddMember = (Button) view.findViewById(R.id.TMM_btnAdd);
		joinLayout = view.findViewById(R.id.TMM_joinLayout);
		classmateLayout = view.findViewById(R.id.TMM_classmateLayout);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addSelfAsMember();
			}
		});
		btnAddMember.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addMember();
			}
		});
	}

	private void addMember() {


		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_TEAM_MEMBER);
		 TeamMemberDTO tm = new TeamMemberDTO();
		//tm.setTeamID(team.getTeamID());
		r.setTeamMember(tm);

		if (!BaseVolley.checkNetworkOnDevice(ctx))
			return;
		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
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
						//team.setTeammemberList(r.getTeamMemberList());
						adapter.notifyDataSetChanged();
						Log.i(LOG,
								"tell teamMemberAddedListener, member just added");
						teamMemberAddedListener.onTeamMemberAdded(r.getTeamMemberList());
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
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						team.setTeamMemberList(r.getTeamMemberList());
//						adapter.notifyDataSetChanged();
//						Log.i(LOG,
//								"tell teamMemberAddedListener, member just added");
//						teamMemberAddedListener.onTeamMemberAdded(r.getTeamMemberList());
//
//					}
//				});

	}

	private void addSelfAsMember() {
		for (TeamMemberDTO tm : team.getTeammemberList()) {
			if (team.getTeammemberList().add(tm))
			{
				ToastUtil.toast(ctx,
						ctx.getResources().getString(R.string.already_in_team));
				return;
			}
		}
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_TEAM_MEMBER);
		TeamMemberDTO tm = new TeamMemberDTO();
		tm.setTeamID(team.getTeamID());
		r.setTeamMember(tm);
		if (!BaseVolley.checkNetworkOnDevice(ctx))
			return;
		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
			@Override
			public void onMessage(final ResponseDTO r) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (r.getStatusCode() > 0) {
							ToastUtil.errorToast(ctx, r.getMessage());
							return;
						}
						Log.i(LOG,
								"tell teamMemberAddedListener, member just added");
						team.setTeammemberList(r.getTeamMemberList());
						adapter.notifyDataSetChanged();
						animateJoinLayoutOut();
						Log.i(LOG,
								"tell teamMemberAddedListener, member just added");
						teamMemberAddedListener.onTeamMemberAdded(r.getTeamMemberList());
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
//						ToastUtil.toast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						Log.i(LOG,
//								"tell teamMemberAddedListener, member just added");
//						team.setTeamMemberList(r.getTeamMemberList());
//						adapter.notifyDataSetChanged();
//						animateJoinLayoutOut();
//						Log.i(LOG,
//								"tell teamMemberAddedListener, member just added");
//						teamMemberAddedListener.onTeamMemberAdded(r.getTeamMemberList());
//
//					}
//				});
	}

	private void animateJoinLayoutOut() {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.push_up_out);
		a.setDuration(1000);
		a.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				joinLayout.setVisibility(View.GONE);

			}
		});
		joinLayout.startAnimation(a);
	}

	private void animateButtonIn() {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.push_up_in);
		a.setDuration(1000);
		btnAddMember.setVisibility(View.VISIBLE);
		btnAddMember.startAnimation(a);

	}

	private void animateButtonOut() {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.push_down_out);
		a.setDuration(1000);
		a.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				btnAddMember.setVisibility(View.GONE);

			}
		});
		btnAddMember.startAnimation(a);
	}

	TeamMemberDTO teamMember;
	ImageLoader imageLoader;
	TeamMemberAdapter adapter;
	static final String LOG = "TeamMemberListFragment";
	View view;
	Button btnSave, btnAddMember;
	TextView txtCount, txtLabel;
	ListView listView;
	View joinLayout, classmateLayout;
	Spinner spinner;
	public void setTeam(TeamDTO team) {
		this.team = team;
		setList();
		if (team.getTeammemberList() != null) {
			boolean found = false;
			for (TeamMemberDTO tm : team.getTeammemberList()) {
				if (tm.getTeamID() == tm.getTeamMemberID()
						) {
					found = true;
					break;
				}
			}
			if (!found) {
				Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
				a.setDuration(1000);
				joinLayout.setVisibility(View.VISIBLE);
				joinLayout.startAnimation(a);
			}
		}
	}

	private void setSpinner() {
		if (spinner == null)
			spinner = (Spinner) view.findViewById(R.id.TMM_spinner);
		final ArrayList<String> tarList = new ArrayList<String>();

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 0) {
						teamMember = null;
						animateButtonOut();
						return;
					}
					//team = teamMemberList.get(arg2 - 1);
					animateButtonIn();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}

	@Override
	public void animateCounts() {

	}
}
