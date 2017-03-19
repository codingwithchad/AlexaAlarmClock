package com.h.chad.alexaalarmclock;

import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.app.Dialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

/**
 * Created by chad on 3/16/2017.
 */

public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener{
    private TextView mTvHours;
    private TextView mTvMinutes;

    //constructor is empty
    public TimePickerFragment(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //User current time as default
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        //Create and return a new instace of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        mTvHours = (TextView) getActivity().findViewById(R.id.tv_hours);
        mTvMinutes = (TextView)getActivity().findViewById(R.id.tv_minutes);
        mTvHours.setText(String.valueOf(hour));
        mTvMinutes.setText(String.valueOf(min));
    }
}
