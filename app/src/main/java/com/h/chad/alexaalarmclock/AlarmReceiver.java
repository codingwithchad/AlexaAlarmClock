package com.h.chad.alexaalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static android.media.CamcorderProfile.get;

/**
 * Created by chad on 3/22/2017.
 */

public class AlarmReceiver extends BroadcastReceiver{
    public final static String LOG_TAG = AlarmReceiver.class.getSimpleName();
    public MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmHour = intent.getIntExtra("extraAlarmHour", 0);
        int alarmMinute = intent.getIntExtra("extraAlarmMinute", 0);
        int alarmID = intent.getIntExtra("extraAlarmMinute", 0);
        int [] day_checked = intent.getIntArrayExtra("extraDay_checked");
        String fileName = intent.getStringExtra("extraString");
        Toast.makeText(context, "ALARM STARTED", Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Alarm Receiver received time " + alarmHour + ":" + alarmMinute);
        java.util.Calendar rightNow = java.util.Calendar.getInstance();

        int nowHour = rightNow.get(java.util.Calendar.HOUR_OF_DAY);
        int nowMinute = rightNow.get(java.util.Calendar.MINUTE);
        int today = rightNow.get(java.util.Calendar.DAY_OF_WEEK) -1;
        int nowSeconds = rightNow.get(java.util.Calendar.SECOND);

        if(fileName == null)
            Log.e(LOG_TAG, "File name is null " + fileName);
        mediaPlayer = new MediaPlayer();



        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }
        if( day_checked[today]== 1 && nowHour == alarmHour && nowMinute == alarmMinute){

            mediaPlayer.start();
            //Set alarm for future days
            AlarmUtils.setNextAlarm(true, context, alarmHour, alarmMinute, day_checked, alarmID, fileName, 1);
        }



    }
}
