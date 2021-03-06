package com.h.chad.alexaalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.io.IOException;
import java.util.Calendar;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;


import com.h.chad.alexaalarmclock.data.AlarmContract.AlarmEntry;
import com.h.chad.alexaalarmclock.data.AlarmProvider;


import static android.R.attr.id;
import static android.view.View.GONE;
import static com.h.chad.alexaalarmclock.VoiceRecorderActivity.LOG_TAG;

/**
 * Created by chad on 3/15/2017.
 */

public class AlarmCursorAdapter extends CursorAdapter{



    private MediaPlayer mediaPlayer;
    private  CheckBox alarmIsSet;
    private AudioManager audioManager;
    AudioManager.OnAudioFocusChangeListener afcl =
            new AudioManager.OnAudioFocusChangeListener(){
                public void onAudioFocusChange(int focusChange){
                    if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN){
                        mediaPlayer.start();
                    }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        relaseMediaPlayer();
                    }
                }
            };
    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener(){
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    relaseMediaPlayer();
                }
            };

    public AlarmCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView hours = (TextView) view.findViewById(R.id.textview_alarm_hour);
        TextView minutes = (TextView) view.findViewById(R.id.textview_alarm_minute);
        CheckBox alarmIsSet = (CheckBox) view.findViewById(R.id.checkbox_on_off);

        //Get the column index for each item
        final int idColumnIndex = cursor.getColumnIndex(AlarmEntry._ID);
        int descriptionColumnIndex = cursor.getColumnIndex(AlarmEntry.USER_DESCRIPTION);
        int filenameColumnIndex = cursor.getColumnIndex(AlarmEntry.FILE_NAME);
        int isActiveColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_ACTIVE);
        int hourColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_HOUR);
        int minuteColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_MINUTE);
        int daysColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_DAYS);

        //get the data from the database at that column
        final String alarmDescrtipion = cursor.getString(descriptionColumnIndex);
        final int active = cursor.getInt(isActiveColumnIndex);
        final int alarmHour = cursor.getInt(hourColumnIndex);
        final int alarmMinutes = cursor.getInt(minuteColumnIndex);
        final String fileName = cursor.getString(filenameColumnIndex);
        final int requestCode = cursor.getInt(idColumnIndex);



        alarmIsSet.setText(alarmDescrtipion);
        alarmIsSet.setChecked((active == 1));
        hours.setText(AlarmUtils.timeFormatter(alarmHour));
        minutes.setText(AlarmUtils.timeFormatter(alarmMinutes));

        String daysString = cursor.getString(daysColumnIndex);
        final int[] daysArray = AlarmUtils.StringToIntArray(daysString);
        daysOfWeekVisible(daysArray, view);

        ImageButton testSound = (ImageButton) view.findViewById(R.id.button_test_sound);
        testSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws
                    IllegalArgumentException,
                    SecurityException,
                    IllegalStateException {
                if (fileName == null)
                    Log.e(LOG_TAG, "File name is null " + fileName);
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });
        alarmIsSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ContentValues values = new ContentValues();

                Uri currentAlarmURI =
                        ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, requestCode);
                if(isChecked){

                    values.put(AlarmEntry.ALARM_ACTIVE, 1);
                    context.getContentResolver().update(
                            currentAlarmURI, values, null, null);
                    AlarmUtils.setNextAlarm(true, context, alarmHour, alarmMinutes,
                            daysArray, requestCode, fileName, 0);

                }
                else{
                    values.put(AlarmEntry.ALARM_ACTIVE, 0);
                    context.getContentResolver().update(
                            currentAlarmURI, values, null, null);
                    AlarmUtils.cancelAlarm();
                }

            }
        });
    }

    //Broke out the days of the week into its own method
    private void daysOfWeekVisible(int[] day, View view) {
        TextView sun = (TextView) view.findViewById(R.id.textview_Sunday);
        TextView mon = (TextView) view.findViewById(R.id.textview_Monday);
        TextView tue = (TextView) view.findViewById(R.id.textview_Tueday);
        TextView wed = (TextView) view.findViewById(R.id.textview_Wednesday);
        TextView thu = (TextView) view.findViewById(R.id.textview_Thursday);
        TextView fri = (TextView) view.findViewById(R.id.textview_Friday);
        TextView sat = (TextView) view.findViewById(R.id.textview_Saturday);

        if(day[0] == 1)
            sun.setVisibility(View.VISIBLE) ;
        else
            sun.setVisibility(GONE);
        if(day[1] == 1)
            mon.setVisibility(View.VISIBLE) ;
        else
            mon.setVisibility(GONE);
        if(day[2] == 1)
            tue.setVisibility(View.VISIBLE) ;
        else
            tue.setVisibility(GONE);
        if(day[3] == 1)
            wed.setVisibility(View.VISIBLE) ;
        else
            wed.setVisibility(GONE);
        if(day[4] == 1)
            thu.setVisibility(View.VISIBLE) ;
        else
            thu.setVisibility(GONE);
        if(day[5] == 1)
            fri.setVisibility(View.VISIBLE) ;
        else
            fri.setVisibility(GONE);
        if (day[6] == 1)
            sat.setVisibility(View.VISIBLE);
        else
            sat.setVisibility(GONE);


    }
    //Relase Media Player
    private void relaseMediaPlayer(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
