package com.h.chad.alexaalarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by chad on 3/22/2017.
 */

public class AlarmReceiver extends BroadcastReceiver{
    public final static String LOG_TAG = AlarmReceiver.class.getSimpleName();
    //public MediaPlayer mediaPlayer;
    public Uri notification;
    @Override
    public void onReceive(Context context, Intent intent) {
        String fileName = intent.getStringExtra("extraString");
        Toast.makeText(context, "ALARM STARTED", Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Alarm Receiver received filename " + fileName);

        if(fileName == null)
            Log.e(LOG_TAG, "File name is null " + fileName);
        //mediaPlayer = new MediaPlayer();
        notification = Uri.parse("content://" +fileName);


        try{
            RingtoneManager.setActualDefaultRingtoneUri(
                    context, RingtoneManager.TYPE_ALARM, notification);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
            //mediaPlayer.setDataSource(fileName);
            //mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        //mediaPlayer.start();

    }
}
