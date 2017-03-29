package com.h.chad.alexaalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;
import com.h.chad.alexaalarmclock.data.AlarmContract.AlarmEntry;

/**
 * Created by chad on 3/22/2017.
 */

public class DeviceBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Log.v("DEVICE BOOT RECEIVER", "SET ALARMS");

        }
    }



}
