package com.sifiso.codetribe.minisasslibrary.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodeTribe1 on 2015-02-16.
 */
public class DataUtil {
    private static List<TeamMemberDTO> teamMembers;
    static DataUtilInterface dataUtilInterface;

    public interface DataUtilInterface {
        public void onResponse(ResponseDTO r);

        public void onError(String error);

    }

    public static void AddTeamMembers(TeamMemberDTO teamMember) {
        if (teamMembers == null) {
            teamMembers = new ArrayList<TeamMemberDTO>();
        }
        teamMembers.add(teamMember);
    }

    public static List<TeamMemberDTO> getTeamMembers() {
        return teamMembers;
    }

    static ProgressBar progressBar;
    static Context ctx;

    public static void registerTeamMember(TeamDTO team, final DataUtilInterface listener) {
        dataUtilInterface = listener;

        RequestDTO req = new RequestDTO();
        req.setTeam(team);
        req.setRequestType(RequestDTO.REGISTER_TEAM);
        progressBar.setVisibility(View.VISIBLE);
        NetUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new NetUtil.NetListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Log.e(LOG, "## getCompanyData responded...statusCode: " + r.getStatusCode());
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }

                        CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached(ResponseDTO r) {

                            }

                            @Override
                            public void onError() {

                            }
                        });


                    }
                });

            }

            @Override
            public void onClose() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Util.showErrorToast(ctx, message);
                    }
                });
            }
        });
    }

    public static void login(Context ctx, String sufix, String email, String pin, GcmDeviceDTO dto, DataUtilInterface listener) {

        dataUtilInterface = listener;
        RequestDTO req = new RequestDTO();
        req.setEmail(email);
        req.setPassword(pin);
        req.setGcmDevice(dto);
        req.setRequestType(RequestDTO.SIGN_IN_MEMBER);

        try {
            NetUtil.sendRequest(ctx, sufix, req, new NetUtil.NetListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    dataUtilInterface.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    dataUtilInterface.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }

    private static String LOG = DataUtil.class.getSimpleName();
    static GCMRegisteredListener gcmRegisteredListener;

    public interface GCMRegisteredListener {
        public void onDeviceRegistered(String c);
    }

    public void registerGCMDevice(Context ctx, Activity act, GCMRegisteredListener registeredListener) {
        gcmRegisteredListener = registeredListener;
        GCMUtil.checkPlayServices(ctx, act);

        GCMUtil.startGCMRegistration(ctx, new GCMUtil.GCMUtilListener() {
            @Override
            public void onGCMError() {
                Log.e(LOG, "Error registering device for gcm");
            }

            @Override
            public void onDeviceRegistered(String regID) {
                gcmRegisteredListener.onDeviceRegistered(regID);
            }
        });


    }


    public static void getData() {
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_DATA);

        try {
            progressBar.setVisibility(View.VISIBLE);
            NetUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new NetUtil.NetListener() {
                @Override
                public void onMessage(final ResponseDTO r) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                            if (!ErrorUtil.checkServerError(ctx, r)) {
                                return;
                            }

                            CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {

                                }

                                @Override
                                public void onDataCached(ResponseDTO r) {

                                }

                                @Override
                                public void onError() {

                                }
                            });


                        }
                    });

                }

                @Override
                public void onClose() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onError(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Util.showErrorToast(ctx, message);
                        }
                    });
                }
            });
        } catch (Exception e) {

        }
    }

    public static void createEvaluation(Context ctx, String sufix, EvaluationDTO dto, DataUtilInterface listener) {
        dataUtilInterface = listener;
        RequestDTO req = new RequestDTO();
        req.setEvaluation(dto);
        try {
            NetUtil.sendRequest(ctx, sufix, req, new NetUtil.NetListener() {
                @Override
                public void onMessage(final ResponseDTO response) {

                    dataUtilInterface.onResponse(response);

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    dataUtilInterface.onError(message);
                }
            });
        } catch (Exception e) {

        }
    }

    static void runOnUiThread(Runnable runnable) {

    }

    public static void addTeam(TeamDTO team, final DataUtilInterface listener) {
        dataUtilInterface = listener;

        RequestDTO req = new RequestDTO();
        req.setTeam(team);
        req.setRequestType(RequestDTO.ADD_TEAM);

        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, req, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                listener.onResponse(r);
            }

            @Override
            public void onVolleyError(VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Util.showErrorToast(ctx, message);
                    }
                });
            }
        });
    }
}
