package com.h.chad.alexaalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chad on 3/22/2017.
 */

public class AlarmReceiver extends BroadcastReceiver{
    public final static String LOG_TAG = AlarmReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARM STARTED", Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Alarm Receiver received the alarm");

    }
}
