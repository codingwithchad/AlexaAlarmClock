package com.h.chad.alexaalarmclock;

import android.app.AlarmManager;
import android.app.Application;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;

import static android.os.Build.VERSION_CODES.M;


/**
 * Created by chad on 3/24/2017.
 */

public class AlarmUtils extends AlarmClockActivity{

    private final static int SUNDAY    = 0;
    private final static int MONDAY    = 1;
    private final static int TUESDAY   = 2;
    private final static int WEDNESDAY = 3;
    private final static int THURSDAY  = 4;
    private final static int FRIDAY    = 5;
    private final static int SATURDAY  = 6;

    public static final String LOG_TAG = AlarmUtils.class.getSimpleName();


    public static int[] StringToIntArray(String daysString) {
        int[] numbersArray = new int[7];
        String[] parts = daysString.split(",");
        for(int i = 0; i < parts.length; i++){
            String cleaned = parts[i].replaceAll("[^\\d.]", "");
            numbersArray[i] = Integer.valueOf(cleaned);
        }
        return numbersArray;
    }
    public static String timeFormatter(int unformatted){
        DecimalFormat formatTime = new DecimalFormat("00");
        return formatTime.format(unformatted);
    }

    public static void setNextAlarm(int [] day_checked, int today){


        int daysToAdd = daysAdded(day_checked, today);
        Log.i(LOG_TAG, "Add " + daysToAdd + "  to the next alarm");
    }

    private static int daysAdded(int[] day_checked, int today){
        int i = 0;
        int k = 0;
        for(i = today; i <= day_checked.length; i++){
            if(day_checked[i] == 1){
                Log.i(LOG_TAG, "today is " + dayToString(today));
                Log.i(LOG_TAG, "There should be an alarm set for " + dayToString(i));
                return k;
            }
            if(i == 6){
                i = 0;
            }
            k++;
            if(k>=7) {
                //cancel alarm, no alarm on for 7 days
                return 0;
            }

        }

        return k;
    }

    public static String dayToString(int day){
        switch (day){
            case SUNDAY:
                return "Sunday";
            case MONDAY:
                return "Monday";
            case TUESDAY:
                return "Tuesday";
            case WEDNESDAY:
                return "Wednesday";
            case THURSDAY:
                return "Thursday";
            case FRIDAY:
                return "Friday";
            case SATURDAY:
                return "Saturday";
            default:
                Log.e(LOG_TAG, "Day not in range of 0 - 6");
        }
        return "Error";
    }
}
