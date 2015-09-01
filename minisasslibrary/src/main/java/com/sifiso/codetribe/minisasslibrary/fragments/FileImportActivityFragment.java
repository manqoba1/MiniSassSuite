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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.FileImportActivity;
import com.sifiso.codetribe.minisasslibrary.adapters.TeamMemberAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.ImportUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;
import com.sifiso.codetribe.minisasslibrary.util.bean.ImportException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileImportActivityFragment extends Fragment {

    public FileImportActivityFragment() {
    }

    public interface ImportListener {
        public void onUsersImported(List<TeamMemberDTO> teamMemberList);

        void onError(String message);

        void setBusy(boolean busy);
    }

    public static final int IMPORT_USERS = 1;
    View view;
    ImportListener listener;

    Context ctx;
    TextView txtTitle, txtCount, txtLabel;
    Spinner fileSpinner;
    Button btnImport;
    ListView list;
    List<TeamMemberDTO> teamMemberList;
    List<File> files = new ArrayList<>();
    ImageView image;

    TeamMemberAdapter teamMemberAdapter;

    public ListView getList() {
        return list;
    }

    int index = 0, pageCnt = 0, totalPages = 0;


    @Override
    public void onAttach(Activity a) {
        if (a instanceof ImportListener) {
            listener = (ImportListener) a;
        } else {
            throw new UnsupportedOperationException("Host activity " +
                    a.getLocalClassName() + " must implement ImportListener");
        }
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_file_import, container, false);
        ctx = getActivity();
        setFields();
        files = ImportUtil.getImportFilesOnSD();
        files.addAll(ImportUtil.getImportFiles());
        setImportType(IMPORT_USERS);
        setSpinner();
        return view;
    }

    static final int PAGE_SIZE = 1;

    private void setFields() {
        fileSpinner = (Spinner) view.findViewById(R.id.IMP_fileSpinner);
        btnImport = (Button) view.findViewById(R.id.IMP_btnImport);
        txtCount = (TextView) view.findViewById(R.id.IMP_count);
        txtTitle = (TextView) view.findViewById(R.id.IMP_countLabel);
        image = (ImageView) view.findViewById(R.id.IMP_image);
        list = (ListView) view.findViewById(R.id.IMP_list);

        Statics.setRobotoFontLight(ctx, txtTitle);


        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rem = 0;
                switch (importType) {
                    case IMPORT_USERS:
                        if (teamMemberList == null || teamMemberList.isEmpty()) {
                            Util.showErrorToast(ctx, "Tasks Import Not Found");
                            return;
                        }
                        totalPages = teamMemberList.size() / PAGE_SIZE;
                        rem = teamMemberList.size() % PAGE_SIZE;
                        break;

                }

                if (rem > 0) {
                    totalPages++;
                }
                sendData();
            }
        });


    }

    private void sendData() {
        btnImport.setEnabled(false);
        listener.setBusy(true);
        switch (importType) {
            case IMPORT_USERS:
                controlUserData();
                break;
        }
    }

    static String LOG = FileImportActivity.class.getSimpleName();

    private void controlUserData() {
        if (index < teamMemberList.size()) {
            sendUserData(teamMemberList.get(index));
        } else {
            Log.d(LOG, "*** team member import completed, members: " + teamMemberList.size());
            Util.showToast(ctx, "Team Members data import completed OK");
            listener.onUsersImported(importedResponse.getTeamMemberList());
        }
    }

    private ResponseDTO importedResponse;

    private void sendUserData(TeamMemberDTO teamMemberDTO) {
        Log.e(LOG, "### sendMemberData, Member: " + teamMemberDTO.getFirstName());
        RequestDTO w = new RequestDTO(RequestDTO.IMPORT_MEMBER_INFO);
        w.setTeamMember(teamMemberDTO);

        listener.setBusy(true);
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.setBusy(false);
                        if (response.getStatusCode() == 0) {
                            index++;
                            importedResponse = response;
                            controlUserData();
                        } else {
                            Util.showErrorToast(ctx, response.getMessage());
                            btnImport.setEnabled(true);
                        }
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                Log.e(LOG, "## ERROR websocket - " + message);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnImport.setEnabled(true);
                        listener.setBusy(true);
                        Util.showErrorToast(ctx, message);
                    }
                });
            }
        });
    }

    private int importType;

    public void setImportType(int type) {
        importType = type;
        switch (type) {
            case IMPORT_USERS:
                txtTitle.setText("Team Members");
                break;
        }

    }

    private void setTeamMemberList() {
        teamMemberAdapter = new TeamMemberAdapter(ctx, R.layout.team_member_item, teamMemberList);
        list.setAdapter(teamMemberAdapter);
        txtCount.setText("" + teamMemberList.size());
    }

    private boolean isEmptyLine(String line) {
        Pattern patt = Pattern.compile(";");
        boolean OK = false;
        String[] result = patt.split(line);
        try {
            if (result.length == 0) {
                OK = true;
            }
        } catch (Exception e) {
            Log.e(LOG, "--- ERROR parse fai;d", e);
        }
        return OK;
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", loc);
    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###,###,###.00");
    File selectedFile;

    private void setSpinner() {

        List<String> list = new ArrayList<>();
        list.add("Select File");
        for (File p : files) {
            list.add(p.getName() + " - " + sdf.format(new Date(p.lastModified())));
        }
        ArrayAdapter a = new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, list);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileSpinner.setAdapter(a);
        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedFile = null;
                    return;
                }
                try {
                    switch (importType) {
                        case IMPORT_USERS:
                            parseUsersFile(files.get(i - 1));
                            break;
                    }

                } catch (IOException e) {
                    Util.showErrorToast(ctx, "Import failed: " + e.getMessage());
                } catch (ImportException e) {
                    Util.showErrorToast(ctx, "Import failed: " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void parseUsersFile(File file) throws IOException, ImportException {
        listener.setBusy(true);
        teamMemberList = new ArrayList<>();

        BufferedReader brReadMe = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String strLine = brReadMe.readLine();
        while (strLine != null) {
            if (isEmptyLine(strLine)) {
                strLine = brReadMe.readLine();
                continue;
            }
            if (strLine.contains("firstName")) {
                strLine = brReadMe.readLine();
                continue;
            }
            TeamMemberDTO teamMember = parseTeamMember(strLine);
            if (teamMember != null) {
                teamMemberList.add(parseTeamMember(strLine));
            }
            strLine = brReadMe.readLine();
        }
        brReadMe.close();
        setTeamMemberList();
        listener.setBusy(false);
        Log.i(LOG, "#### team members have been imported into app, found: " + teamMemberList.size());
    }

    private TeamMemberDTO parseTeamMember(String line) {
        TeamMemberDTO dto = new TeamMemberDTO();

        Pattern patt = Pattern.compile(";");
        String[] result = patt.split(line);
        try {
            TeamDTO team = new TeamDTO();
            if(result[6].equals("")){
                team.setTeamName("MiniSASS");
            }else{
                team.setTeamName(result[6]);
            }

            team.setOrganisationTypeID(Integer.parseInt(result[5]));
            team.setCountryID(Integer.parseInt(result[7]));
            team.setDateRegistered(new Date().getTime());


            dto.setFirstName(result[0]);
            dto.setLastName(result[1]);
            dto.setEmail(result[2]);
            dto.setCellphone("");
            dto.setDateRegistered(new Date().getTime());
            dto.setActiveFlag(0);

            dto.setTeam(team);
        } catch (Exception e) {
            Log.e(LOG, "---- ERROR parseTeamMember failed", e);
            return null;
        }
        return dto;
    }

}
