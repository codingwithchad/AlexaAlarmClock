package com.h.chad.alexaalarmclock.data;

import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.content.ContentResolver;
import android.support.compat.BuildConfig;

/**
 * Created by chad on 3/15/2017.
 *
 */

public class AlarmContract {
    //to prevent someone from creating the AlarmContract class
    private AlarmContract(){}

    public final static String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static String PATH_ALARMS = "alarms";

    public final static class AlarmEntry implements BaseColumns{

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALARMS);

        public final static String TABLE_NAME = "alarms";               //table name
        public final static String _ID = BaseColumns._ID;               //ID
        public final static String USER_DESCRIPTION = "description";    //Description from user
        public final static String FILE_NAME = "filename";              //Name of the file
        public final static String ALARM_ACTIVE = "active";             //is the alarm active
        public final static String ALARM_HOUR = "alarmhour";            //Description from user
        public final static String ALARM_MINUTE = "alarmminute";
        public final static String ALARM_DAYS = "alarmdays";            //Description from user

        //return value for getType in AlarmProvider
        public final static String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_ALARMS;
        public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_ALARMS;

    }
}
