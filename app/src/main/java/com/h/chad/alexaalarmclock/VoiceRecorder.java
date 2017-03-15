package com.h.chad.alexaalarmclock;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.R.attr.button;
import static android.R.attr.category;
import static android.R.attr.packageNames;

/**
 * Created by chad on 3/14/2017.
 */

public class VoiceRecorder extends AppCompatActivity{

    private MediaRecorder voiceRecorder;
    private MediaPlayer testRecording;
    private Button startRecording;
    private Button playBack;
    private EditText recordingName;
    private String fileSavePath = null;

    public final static String LOG_TAG = VoiceRecorder.class.getSimpleName();
    public final static int REQUEST_PERMISSION_CODE = 1;
    private boolean recordingInProgress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder);
        startRecording = (Button) findViewById(R.id.record);
        playBack = (Button) findViewById(R.id.playBack);
        buttonRecordingPressed() ;
        playRecording();


    }

    private void playRecording() {
        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws
                    IllegalArgumentException,
                    SecurityException,
                    IllegalStateException {
                testRecording = new MediaPlayer();
                try{
                    testRecording.setDataSource(fileSavePath);
                    testRecording.prepare();

                }catch (IOException e){
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
                if(checkPermissions()) {
                    if (recordingInProgress == false) {
                        recordingName = (EditText) findViewById(R.id.alarm_title);
                        String nameForFile = recordingName.getText().toString();

                        if(nameForFile.isEmpty() || nameForFile.length() == 0){
                            Toast.makeText(getApplicationContext(), "Need a name", Toast.LENGTH_LONG).show();
                            return;
                        }else{
                            recordingInProgress = true;
                            startRecording.setBackgroundColor(Color.RED);
                            startRecording.setText(R.string.stop);
                        }

                        fileSavePath =
                                Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        "/" + timeforfile() + nameForFile + ".3gp";
                        Log.v(LOG_TAG, fileSavePath + " is the file save path");
                        mediaRecorder();

                        try{
                            voiceRecorder.prepare();
                            voiceRecorder.start();
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }catch(IOException x){
                            x.printStackTrace();
                        }

                    } else if (recordingInProgress == true) {
                        recordingInProgress = false;
                        startRecording.setBackgroundColor(Color.GREEN);
                        startRecording.setText(R.string.record);

                        if(voiceRecorder != null){
                            voiceRecorder.stop();
                            voiceRecorder.release();
                            mediaRecorder();
                        }
                    }

                }else{
                    requestPermission();
                }
            }
        });
    }

    private String timeforfile() {
        SimpleDateFormat format = new SimpleDateFormat("HH_mm_ss_dd_MM_yyyy", Locale.US);
        Date now = new Date();
        return format.format(now);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(VoiceRecorder.this, new
        String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                if(grantResults.length > 0){
                    boolean storagePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean recordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(storagePermission && recordPermission){
                        Toast.makeText(VoiceRecorder.this, "Permission Granted",
                                Toast.LENGTH_SHORT).show();
                    }else {
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


    public void mediaRecorder(){
        voiceRecorder = new MediaRecorder();
        voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        voiceRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        voiceRecorder.setAudioEncodingBitRate(16);
        voiceRecorder.setOutputFile(fileSavePath);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
