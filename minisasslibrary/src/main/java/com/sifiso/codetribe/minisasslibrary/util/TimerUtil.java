package com.sifiso.codetribe.minisasslibrary.util;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aubreyM on 2014/08/28.
 */
public class TimerUtil {

    public interface TimerListener {
        public void onSessionDisconnected();
    }

    public interface TimerFlashListener {
        public void onStartFlash();
    }

    static TimerListener listener;
    static Timer timer;
    static final long TEN_SECONDS = 10 * 1000;



    static TimerFlashListener timerFlashListener;
    static Timer timerFlash;

    public static void startFlashTime(final TimerFlashListener flashListener) {
        timerFlashListener = flashListener;
        timerFlash = new Timer();
        timerFlash.schedule(new TimerTask() {
            @Override
            public void run() {
                flashListener.onStartFlash();
            }
        }, 100, 5000);
    }

    public static void killFlashTimer() {
        if (timerFlash != null) {
            timerFlash.cancel();
            timerFlash = null;
            Log.w("TimerUtil", "########## Flash Timer KILLED");
        }
    }
}
