package com.h.chad.alexaalarmclock;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.h.chad.alexaalarmclock.data.AlarmContract.AlarmEntry;

/**
 * Created by chad on 3/15/2017.
 */

public class AlarmCursorAdapter extends CursorAdapter{

    public AlarmCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView description = (TextView) view.findViewById(R.id.textview_user_description);

        //Get the column index for each item
        int idColumnIndex = cursor.getColumnIndex(AlarmEntry._ID);
        int descriptionColumnIndex = cursor.getColumnIndex(AlarmEntry.USER_DESCRIPTION);
        int filenameColumnIndex = cursor.getColumnIndex(AlarmEntry.FILE_NAME);
        int isActiveColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_ACTIVE);
        int hourColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_HOUR);
        int minuteColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_MINUTE);
        int daysColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_DAYS);

        //get the data from the database at that column
        final String alarmDescrtipion = cursor.getString(descriptionColumnIndex);

        description.setText(alarmDescrtipion);


    }
}
