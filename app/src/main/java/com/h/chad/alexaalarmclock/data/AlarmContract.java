package com.h.chad.alexaalarmclock.data;

import android.provider.BaseColumns;

/**
 * Created by chad on 3/15/2017.
 */

public class AlarmContract {
    //to prevent someone from creating the AlarmContract class
    private AlarmContract(){}

    public final static class AlarmEntry implements BaseColumns{

        public final static String TABLE_NAME = "alarms";

    }
}
