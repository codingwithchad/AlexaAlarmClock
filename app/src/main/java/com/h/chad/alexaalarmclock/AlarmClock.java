package com.h.chad.alexaalarmclock;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AlarmClock extends AppCompatActivity {

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        setupFAB(); //function for the FAB's onclick listener.

    }

    /*Pressing the FAB sends user to the voice recorder activity to create a new alarm*/
    private void setupFAB() {
        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRecorderIntent = new Intent(AlarmClock.this, VoiceRecorder.class);
                startActivity(toRecorderIntent);
            }
        });

    }
}
