package com.h.chad.alexaalarmclock.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.h.chad.alexaalarmclock.data.AlarmContract.AlarmEntry;
import static com.h.chad.alexaalarmclock.data.AlarmContract.CONTENT_AUTHORITY;
import static com.h.chad.alexaalarmclock.data.AlarmContract.PATH_ALARMS;

/**
 * Created by chad on 3/15/2017.
 */

public class AlarmProvider extends ContentProvider {

    private final static String LOG_TAG = AlarmProvider.class.getName();
    //database helper object
    private AlarmDbHelper mDBHelper;
    private final static int ALARMS = 100;
    private final static int ALARM_ID = 101;
    private final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ALARMS, ALARMS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_ALARMS +"/#", ALARM_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new AlarmDbHelper(getContext());
        return true;
    }

    /**
     * @param uri
     * @param projection
     * @param selection
     * @param args
     * @param sortOrder
     * @return cursor
     * */
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] args,
                        String sortOrder) {
        //get the database in readable mode
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        //Create the cursor
        Cursor cursor;
        //find the match to the URI
        int match = sUriMatcher.match(uri);
        //determine which matches
        switch (match){
            case ALARMS:
                cursor = db.query(AlarmEntry.TABLE_NAME,
                        projection,
                        selection,
                        args,
                        null,
                        null,
                        sortOrder);
                break;
            case ALARM_ID:
                selection = AlarmEntry._ID + "=?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(AlarmEntry.TABLE_NAME,
                        projection,
                        selection,
                        args,
                        null,
                        null,
                        sortOrder);
            default:
                throw new IllegalArgumentException(LOG_TAG + " can't query URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ALARMS:
                return AlarmEntry.CONTENT_LIST_TYPE;
            case ALARM_ID:
                return AlarmEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(LOG_TAG + " Unknown URI " + uri +
                        " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ALARMS:
                Uri returnURI = insertAlarm(uri, contentValues);
                return returnURI;
            default:
                throw new IllegalArgumentException();
        }
    }

    private Uri insertAlarm(Uri uri, ContentValues values) {
        String filename = values.getAsString(AlarmEntry.FILE_NAME);
        if(filename == null){
            throw new IllegalArgumentException("filename is null");
        }
        String userDescription = values.getAsString(AlarmEntry.USER_DESCRIPTION);
        if (userDescription == null){
            throw new IllegalArgumentException("description is null");
        }
        Integer isActive = values.getAsInteger(AlarmEntry.ALARM_ACTIVE);
        if(isActive == null){
            isActive = 0;
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insert(AlarmEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, " INSERTION FAILED for uri: " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] args) {
        int rowsDeleted = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ALARMS:
                rowsDeleted = db.delete(AlarmEntry.TABLE_NAME, selection, args);
                break;
            case ALARM_ID:  //delete a single row
                selection = AlarmEntry._ID + "=?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(AlarmEntry.TABLE_NAME, selection, args);
                break;
            default:
                throw new IllegalArgumentException("Deletion not available for uri: " + uri);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri,
                      ContentValues values,
                      String selection,
                      String[] args) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ALARMS:
                return updateAlarms(uri, values, selection, args);
            case ALARM_ID:
                selection = AlarmEntry._ID + "=?";
                args = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAlarms(uri, values, selection, args);
            default:
                throw new IllegalArgumentException("Update not available for uri: " + uri);
        }
    }

    private int updateAlarms(Uri uri, ContentValues values, String selection, String[] args) {
        int rowsAffected = 0;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        rowsAffected = db.update(AlarmEntry.TABLE_NAME, values, selection, args);
        if(rowsAffected != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAffected;
    }
}
