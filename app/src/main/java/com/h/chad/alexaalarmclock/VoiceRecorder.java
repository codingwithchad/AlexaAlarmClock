package com.h.chad.alexaalarmclock;


import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.SystemClock;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.h.chad.alexaalarmclock.data.AlarmContract;
import com.h.chad.alexaalarmclock.data.AlarmContract.AlarmEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by chad on 3/14/2017.
 */

public class VoiceRecorder extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>{

    private MediaRecorder voiceRecorder;
    private MediaPlayer testRecording;
    private Button startRecording;
    private Button playBack;
    private String fileSavePath = null;

    private Chronometer recordingLength;

    private LinearLayout mTime_button;

    protected int mHoursForDB;
    protected int mMinutesForDB;
    private Button mSaveButton;
    private Uri mCurrentAlarmUri;

    private EditText recordingName;
    private TextView mHours;
    private TextView mMinutes;

    public final static String LOG_TAG = VoiceRecorder.class.getSimpleName();
    public final static int REQUEST_PERMISSION_CODE = 1;
    private boolean recordingInProgress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder);
        startRecording = (Button) findViewById(R.id.record);
        playBack = (Button) findViewById(R.id.playBack);
        recordingLength = (Chronometer) findViewById(R.id.record_timer);
        mMinutes = (TextView)findViewById(R.id.tv_minutes);
        mHours = (TextView) findViewById(R.id.tv_hours);
        mSaveButton = (Button) findViewById(R.id.button_save);
        buttonRecordingPressed();
        playRecording();
        timeClicked();
        saveButtonClicked();


    }



    private void playRecording() {
        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws
                    IllegalArgumentException,
                    SecurityException,
                    IllegalStateException {
                testRecording = new MediaPlayer();
                try {
                    testRecording.setDataSource(fileSavePath);
                    testRecording.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                testRecording.start();
                Toast.makeText(VoiceRecorder.this, "Playing Back Recording", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buttonRecordingPressed() {
        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    if (recordingInProgress == false) {
                        recordingName = (EditText) findViewById(R.id.alarm_title);
                        String nameForFile = recordingName.getText().toString();

                        if (nameForFile.isEmpty() || nameForFile.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Need a name", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            recordingInProgress = true;
                            startRecording.setBackgroundColor(Color.RED);
                            startRecording.setText(R.string.stop);
                            recordingLength.setBase(SystemClock.elapsedRealtime());
                            recordingLength.start();
                        }

                        fileSavePath =
                                Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        "/" + timeforfile() + nameForFile + ".m4a";
                        Log.v(LOG_TAG, fileSavePath + " is the file save path");
                        mediaRecorder();

                        try {
                            voiceRecorder.prepare();
                            voiceRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException x) {
                            x.printStackTrace();
                        }

                    } else if (recordingInProgress == true) {
                        recordingInProgress = false;
                        startRecording.setBackgroundColor(Color.GREEN);
                        startRecording.setText(R.string.record);
                        recordingLength.stop();

                        if (voiceRecorder != null) {
                            voiceRecorder.stop();
                            voiceRecorder.release();
                            mediaRecorder();
                        }
                    }
                } else {
                    requestPermission();
                }
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(VoiceRecorder.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean storagePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean recordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (storagePermission && recordPermission) {
                        Toast.makeText(VoiceRecorder.this, "Permission Granted",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VoiceRecorder.this, "Permission Denied",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermissions() {
        int result0 = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result0 == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    /*
    * settings for the MediaRecoder()
    * https://developer.android.com/guide/topics/media/mediarecorder.html
    *help from StackOverflow http://stackoverflow.com/questions/6600364/android-mediarecorder-getmaxamplitude-always-returns-0-on-lg-optimus/33992633#33992633
    * */
    public void mediaRecorder() {
        voiceRecorder = new MediaRecorder();
        voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        voiceRecorder.setAudioSamplingRate(44100);
        voiceRecorder.setAudioEncodingBitRate(96000);
        voiceRecorder.setOutputFile(fileSavePath);

    }

    //Takes no input
    //Returns the string of teh current time so each filename is different
    protected static String timeforfile() {
        SimpleDateFormat format = new SimpleDateFormat("HH_mm_ss_dd_MM_yyyy", Locale.US);
        Date now = new Date();
        return format.format(now);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void timeClicked() {
        mTime_button = (LinearLayout)findViewById(R.id.time_selected);
        mTime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }
    private void saveButtonClicked() {
        mHoursForDB = Integer.parseInt(mHours.getText().toString().trim());
        mMinutesForDB = Integer.parseInt(mMinutes.getText().toString().trim());
        Arrays.toString(daysOfTheWeek());

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                int duration = Toast.LENGTH_SHORT;
                Context c = VoiceRecorder.this;


            }
        });

    }

    public int[] daysOfTheWeek(){
        int[] days = new int[7];
        CheckBox mon = (CheckBox) findViewById(R.id.cb_monday);
        CheckBox tue = (CheckBox) findViewById(R.id.cb_tuesday);
        CheckBox wed = (CheckBox) findViewById(R.id.cb_wednesday);
        CheckBox thu = (CheckBox) findViewById(R.id.cb_thursday);
        CheckBox fri = (CheckBox) findViewById(R.id.cb_friday);
        CheckBox sat = (CheckBox) findViewById(R.id.cb_saturday);
        CheckBox sun = (CheckBox) findViewById(R.id.cb_sunday);
        days[0] = (mon.isChecked()) ? 1 : 0;
        days[1] = (tue.isChecked()) ? 1 : 0;
        days[2] = (wed.isChecked()) ? 1 : 0;
        days[3] = (thu.isChecked()) ? 1 : 0;
        days[4] = (fri.isChecked()) ? 1 : 0;
        days[5] = (sat.isChecked()) ? 1 : 0;
        days[6] = (sun.isChecked()) ? 1 : 0;
        return days;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                AlarmEntry._ID,
                AlarmEntry.USER_DESCRIPTION,
                AlarmEntry.FILE_NAME,
                AlarmEntry.ALARM_ACTIVE,
                AlarmEntry.ALARM_HOUR,
                AlarmEntry.ALARM_MINUTE,
                AlarmEntry.ALARM_DAYS
        };
        return new CursorLoader(this, mCurrentAlarmUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null || data.getCount() < 1){return;}
        if(data.moveToFirst()){

            int user_descriptionColumnIndex = data.getColumnIndex(AlarmEntry.USER_DESCRIPTION);
            int file_nameColumnIndex = data.getColumnIndex(AlarmEntry.FILE_NAME);
            int alarm_activeColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_ACTIVE);
            int alarm_hourColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_HOUR);
            int alarm_minuteColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_MINUTE);
            int days_of_weekColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_DAYS);

            String user_Description = data.getString(user_descriptionColumnIndex);
            String fileName = data.getString(file_nameColumnIndex);
            int isAlarmActive = data.getInt(alarm_activeColumnIndex);
            int alarmHour = data.getInt(alarm_hourColumnIndex);
            int alarmMinute = data.getInt(alarm_minuteColumnIndex);
            String alarmDaysOfWeek = data.getString(days_of_weekColumnIndex);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recordingName.setText("");
        mHours.setText("");
        mMinutes.setText("");

    }
}
