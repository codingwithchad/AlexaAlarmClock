package com.h.chad.alexaalarmclock;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.AlarmClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.h.chad.alexaalarmclock.data.AlarmContract.AlarmEntry;

import com.h.chad.alexaalarmclock.data.AlarmContract;

public class AlarmClockActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = AlarmClock.class.getSimpleName();
    private static final int URL_LOADER_ID = 1;
    private AlarmCursorAdapter mAlarmCursorAdapter;
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
                Intent toRecorderIntent = new Intent(AlarmClockActivity.this, VoiceRecorder.class);
                startActivity(toRecorderIntent);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri base = AlarmEntry.CONTENT_URI;
        String [] projection = {
                AlarmEntry._ID,
                AlarmEntry.USER_DESCRIPTION,
                AlarmEntry.FILE_NAME,
                AlarmEntry.ALARM_ACTIVE,
                AlarmEntry.ALARM_ACTIVE,
                AlarmEntry.ALARM_HOUR,
                AlarmEntry.ALARM_MINUTE,
                AlarmEntry.ALARM_DAYS
        };
        return new CursorLoader(
                this,
                base,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAlarmCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmCursorAdapter.swapCursor(null);
    }
}
