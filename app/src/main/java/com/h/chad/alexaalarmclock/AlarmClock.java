package com.h.chad.alexaalarmclock;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlarmClock extends AppCompatActivity {

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        setupFAB();

    }

    private void setupFAB() {
        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);

    }
}
