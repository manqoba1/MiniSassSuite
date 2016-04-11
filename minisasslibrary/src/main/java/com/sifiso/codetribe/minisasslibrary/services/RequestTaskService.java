package com.sifiso.codetribe.minisasslibrary.services;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by aubreymalabie on 4/10/16.
 */
public class RequestTaskService extends GcmTaskService {
    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.w("RequestTaskService", "onRunTask ..........");
        Intent m = new Intent(getApplicationContext(),RequestSyncService.class);
        getApplicationContext().startService(m);
        return 0;
    }
}
