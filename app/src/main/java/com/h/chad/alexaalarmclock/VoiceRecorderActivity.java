package com.h.chad.alexaalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.h.chad.alexaalarmclock.data.AlarmProvider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;


import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * Created by Chad H. Glaser on 3/14/2017.
 *
 */

public class VoiceRecorderActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    
    private MediaRecorder voiceRecorder;
    private MediaPlayer testRecording;
    private Button startRecording;
    private Button playBack;
    private String mFileSavePath = null;

    private Chronometer recordingLength;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    public  Calendar mCalendar = Calendar.getInstance();

    private LinearLayout mTime_button;

    protected int mHoursForDB;
    protected int mMinutesForDB;
    private Button mSaveButton;
    private Button mCancelButton;
    private Uri mCurrentAlarmUri;

    private TextView mAlarmId;
    private EditText mRecordingName;
    private TextView mHours;
    private TextView mMinutes;
    private CheckBox mon;
    private CheckBox tue;
    private CheckBox wed;
    private CheckBox thu;
    private CheckBox fri;
    private CheckBox sat;
    private CheckBox sun;

    public final static String LOG_TAG = VoiceRecorderActivity.class.getSimpleName();
    public final static int REQUEST_PERMISSION_CODE = 1;
    private static final int EXISTING_ALARM_LOADER = 0;
    private static final String FILE_SAVE_PATH_STRING = "fileSavePathKey";
    private boolean recordingInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFileSavePath = savedInstanceState.getString(FILE_SAVE_PATH_STRING);
        }
        setContentView(R.layout.activity_voice_recorder);
        
        Intent receivedIntent = getIntent();        //Receive the intent from the AlarmClockActivity
        mCurrentAlarmUri = receivedIntent.getData();    //If there 
        
        linkVariables();            //Get all the member variables set attached from the view
        
        if(mCurrentAlarmUri == null){
            setTitle("Record a new alarm");
        }else{
            setTitle("Edit Alarm" );
            getLoaderManager().initLoader(EXISTING_ALARM_LOADER, null, this);
        }
        
        setCurrentTime();           //Set the clock to the current time
        buttonRecordingPressed();   //When the recording button is pressed
        playRecording();            //When the playback button is pressed
        timeClicked();              //When time is clicked, opens TimePickerFragment
        saveButtonClicked();        //When save is pressed
        canelButtonClicked();
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString(FILE_SAVE_PATH_STRING, mFileSavePath);

    }

    private void canelButtonClicked() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void linkVariables() {
        mRecordingName = (EditText)findViewById(R.id.alarm_title);
        startRecording = (Button) findViewById(R.id.record);
        playBack = (Button) findViewById(R.id.playBack);
        recordingLength = (Chronometer) findViewById(R.id.record_timer);
        mMinutes = (TextView)findViewById(R.id.tv_minutes);
        mHours = (TextView) findViewById(R.id.tv_hours);
        mAlarmId = (TextView)findViewById(R.id.tv_alarmID);
        mSaveButton = (Button) findViewById(R.id.button_save);
        mCancelButton = (Button) findViewById(R.id.button_cancel);
        sun = (CheckBox) findViewById(R.id.cb_sunday);
        mon = (CheckBox) findViewById(R.id.cb_monday);
        tue = (CheckBox) findViewById(R.id.cb_tuesday);
        wed = (CheckBox) findViewById(R.id.cb_wednesday);
        thu = (CheckBox) findViewById(R.id.cb_thursday);
        fri = (CheckBox) findViewById(R.id.cb_friday);
        sat = (CheckBox) findViewById(R.id.cb_saturday);

    }

    private void setCurrentTime() {
        Calendar c = Calendar.getInstance();
        mHours.setText(AlarmUtils.timeFormatter(c.get(Calendar.HOUR_OF_DAY)));
        mMinutes.setText(AlarmUtils.timeFormatter(c.get(Calendar.MINUTE)));
    }

    private void buttonRecordingPressed() {
        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    if (!recordingInProgress) {
                        String nameForFile = mRecordingName.getText().toString();

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
                        if(mFileSavePath == null) {


                            mFileSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + timeforfile() + nameForFile + ".m4a";
                        }
                        ///storage/emulated/0/15_27_05_18_03_2017ggh.m4a is the file save path
                        //Keeping log message here to track file sizes later.
                        Log.e("***** " +LOG_TAG, "FileSavePath: " +mFileSavePath + "***** ");
                        mediaRecorder();

                        try {
                            voiceRecorder.prepare();
                            voiceRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException x) {
                            x.printStackTrace();
                        }

                    } else if (recordingInProgress) {
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
    //Ability to test the recording before putting it in the database
    private void playRecording() {
        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws
                    IllegalArgumentException,
                    SecurityException,
                    IllegalStateException {
                testRecording = new MediaPlayer();
                try {
                    testRecording.setDataSource(mFileSavePath);
                    testRecording.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                testRecording.start();
                Toast.makeText(VoiceRecorderActivity.this, "Playing Back Recording", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(VoiceRecorderActivity.this, new
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
                        Toast.makeText(getApplicationContext(), "Permission Granted",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied",
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
        voiceRecorder.setOutputFile(mFileSavePath);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voice_recorder_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.deleteButton:
                delete();
                break;
        }
        return true;
    }

    private void delete() {
        if(mCurrentAlarmUri != null){
            int rows_deleted = getContentResolver().delete(mCurrentAlarmUri, null, null);
            int len = Toast.LENGTH_SHORT;
            Context c = getApplicationContext();
            if(rows_deleted == 0){
                Toast.makeText(c, getText(R.string.error_deleting_alarm), len).show();
            }else{
                if (mFileSavePath != null) {
                    goDeleteTheFile();
                }
                Toast.makeText(c, getText(R.string.alarm_deleted), len).show();
            }
        }
        finish();
    }

    private void goDeleteTheFile() {
        if (mFileSavePath != null) {
            String path = mFileSavePath;
            String file = path.substring(path.lastIndexOf(File.separator) + 1);
            boolean isDeleted = new File(mFileSavePath).delete();
            Log.e(LOG_TAG, "File name is:" + file + " " + isDeleted);
        }
    }

    private void saveButtonClicked() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                int duration = Toast.LENGTH_SHORT;
                Context c = getApplicationContext();
                boolean isActive = false;
                boolean success = false;
                int alarmID = -1;
                int [] alarmDays = new int[7];
                alarmDays = daysOfTheWeek();

                String user_description = mRecordingName.getText().toString().trim();
                if(TextUtils.isEmpty(user_description) || user_description.length() <=0 ){
                    Toast.makeText(c, getText(R.string.need_description), duration).show();
                    return;
                }else{
                    values.put(AlarmEntry.USER_DESCRIPTION, user_description);
                }

                String fileNameForDb = mFileSavePath;
                if(TextUtils.isEmpty(fileNameForDb) || fileNameForDb.length() <= 0){
                    Toast.makeText(c, getText(R.string.needfilesavepath), duration).show();
                    return;
                }else {
                    values.put(AlarmEntry.FILE_NAME, fileNameForDb);
                }
                values.put(AlarmEntry.ALARM_ACTIVE, 1);

                mHoursForDB = Integer.parseInt(mHours.getText().toString().trim());
                if(mHoursForDB < 0 || mHoursForDB > 24){
                    Toast.makeText(c, getText(R.string.houroutofbounds), duration).show();
                }else {
                 values.put(AlarmEntry.ALARM_HOUR, mHoursForDB);
                }
                mMinutesForDB = Integer.parseInt(mMinutes.getText().toString().trim());
                if(mMinutesForDB < 0 || mMinutesForDB > 59){
                    Toast.makeText(c, getText(R.string.minuteoutofbounds), duration).show();
                }else{
                    values.put(AlarmEntry.ALARM_MINUTE, mMinutesForDB);
                }
                String daysForDatabase = Arrays.toString(alarmDays);
                if(TextUtils.isEmpty(daysForDatabase) || daysForDatabase.length() <= 0){
                    Toast.makeText(c, getText(R.string.days_of_week_error), duration).show();
                }else{
                    values.put(AlarmEntry.ALARM_DAYS, daysForDatabase);
                }

                //Adding a new alarm if mCurrentAlarmUri is null
                if(mCurrentAlarmUri == null){
                    Uri newUri = getContentResolver().insert(
                            AlarmEntry.CONTENT_URI, values);

                    if(newUri == null){
                        Toast.makeText(c, getText(R.string.fail_inserting), duration).show();
                    }else{
                        success = true;
                         alarmID = (int) AlarmProvider.mThisIsMyAlarmId;
                        Toast.makeText(c, getText(R.string.success_inserting), duration).show();
                    }
                }else{
                    int rowsAffected = getContentResolver().update(
                            mCurrentAlarmUri, values, null, null);
                    if(rowsAffected == 0){
                        Toast.makeText(c, getText(R.string.fail_updating), duration).show();
                    }else{
                        success = true;
                        alarmID = Integer.valueOf(mAlarmId.getText().toString().trim());
                        Log.i(LOG_TAG, "ALARM ID: " + alarmID);
                        Toast.makeText(c, getText(R.string.success_updating), duration).show();
                    }
                }
                //The alarm was successfully added or updates, we should set the alarm
                if(success && alarmID >= 0 ){

                    AlarmUtils.setNextAlarm(true, c, mHoursForDB, mMinutesForDB,
                            alarmDays, alarmID, mFileSavePath, 0);
                }

            finish();
            }
        });
    }
    //https://developer.android.com/reference/java/util/Calendar.html
    //Day of week is 1 to 7 and this array is 0 to 6.
    //Sunday is 1, Monday 2, Tuesday 3, Wedensday 4, Thursday 5, Friday 6, Saturday 7
    public int[] daysOfTheWeek(){
        int[] days = new int[7];
        days[0] = (sun.isChecked()) ? 1 : 0;
        days[1] = (mon.isChecked()) ? 1 : 0;
        days[2] = (tue.isChecked()) ? 1 : 0;
        days[3] = (wed.isChecked()) ? 1 : 0;
        days[4] = (thu.isChecked()) ? 1 : 0;
        days[5] = (fri.isChecked()) ? 1 : 0;
        days[6] = (sat.isChecked()) ? 1 : 0;

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
        return new CursorLoader(
                this,
                mCurrentAlarmUri, 
                projection, 
                null, 
                null, 
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        if(data == null || data.getCount() < 1){return;}
        if(data.moveToFirst()){
            int _IDColumnIndex = data.getColumnIndex(AlarmEntry._ID);
            int user_descriptionColumnIndex = data.getColumnIndex(AlarmEntry.USER_DESCRIPTION);
            int file_nameColumnIndex = data.getColumnIndex(AlarmEntry.FILE_NAME);
            int alarm_activeColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_ACTIVE);
            int alarm_hourColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_HOUR);
            int alarm_minuteColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_MINUTE);
            int days_of_weekColumnIndex = data.getColumnIndex(AlarmEntry.ALARM_DAYS);

            String user_Description = data.getString(user_descriptionColumnIndex);
            mFileSavePath = data.getString(file_nameColumnIndex);
            int isAlarmActive = data.getInt(alarm_activeColumnIndex);
            int alarmHour = data.getInt(alarm_hourColumnIndex);
            int alarmMinute = data.getInt(alarm_minuteColumnIndex);
            String alarmDaysOfWeek = data.getString(days_of_weekColumnIndex);
            int thisAlarmID = (int) data.getLong(_IDColumnIndex);

            mRecordingName.setText(user_Description);
            mHours.setText(AlarmUtils.timeFormatter(alarmHour));
            mMinutes.setText(AlarmUtils.timeFormatter(alarmMinute));
            mAlarmId.setText(Integer.toString(thisAlarmID));


            int[] days = AlarmUtils.StringToIntArray(alarmDaysOfWeek);
            sun.setChecked(days[0] == 1);
            mon.setChecked(days[1] == 1);
            tue.setChecked(days[2] == 1);
            wed.setChecked(days[3] == 1);
            thu.setChecked(days[4] == 1);
            fri.setChecked(days[5] == 1);
            sat.setChecked(days[6] == 1);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mRecordingName.setText("");
        mHours.setText("");
        mMinutes.setText("");
        mon.setChecked(false);
        tue.setChecked(false);
        wed.setChecked(false);
        thu.setChecked(false);
        fri.setChecked(false);
        sat.setChecked(false);
        sun.setChecked(false);
    }

}
