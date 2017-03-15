package com.h.chad.alexaalarmclock.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chad on 3/15/2017.
 */

public class AlarmDbHelper  extends SQLiteOpenHelper{

    private final static String LOG_TAG = AlarmDbHelper.class.getName();
    private final static String DATABASE_NAME = "alexaAlarm.db";
    private final static int DATABASE_VERSION = 1;

    public AlarmDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
