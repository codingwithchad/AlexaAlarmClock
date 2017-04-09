package com.h.chad.alexaalarmclock;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;


/**
 * Created by chad on 3/24/2017.
 */

public class AlarmUtils extends AlarmClockActivity{

    private static AlarmManager alarmManager;
    private static PendingIntent alarmIntent;
    private static Calendar mCalendar = Calendar.getInstance();

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



    public static void setNextAlarm(boolean isActive, Context context,
                                    int alarmHour, int alarmMinutes, int[] day_checked,
                                    int alarmID, String fileSavePath, int nextAlarm){

        Calendar currrentTime = Calendar.getInstance();
        currrentTime.setTimeInMillis(System.currentTimeMillis());
        int today = currrentTime.get(Calendar.DAY_OF_WEEK) -1; //subtract 1 to match the array

        int daysToAdd = 0;

        //If the alarm already ran once per day, we need to find the NEXT occurance
        //not todays already ran occuracne
        if(nextAlarm ==1 ){
            daysToAdd = daysAdded(day_checked, today+1);
        }
        else{
            daysToAdd = daysAdded(day_checked, today);
        }
        Log.i(LOG_TAG, "Add " + daysToAdd + "  to the next alarm");

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("extraAlarmHour", alarmHour);
        intent.putExtra("extraAlarmMinute", alarmMinutes);
        intent.putExtra("extraDay_checked", day_checked);
        intent.putExtra("extraAlarmID", alarmID);
        intent.putExtra("extraString", fileSavePath);

        intent.putExtra("extraString", fileSavePath);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        //AlarmUtils.setNextAlarm(daysArray, today);
        Log.i(LOG_TAG, " ########## Current day is " + AlarmUtils.dayToString(today));

            mCalendar.set(Calendar.DAY_OF_WEEK, today);
            mCalendar.set(Calendar.HOUR_OF_DAY, alarmHour);
            mCalendar.set(Calendar.MINUTE, alarmMinutes);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
            Log.i(LOG_TAG, "Setting alarm for " + dayToString(today) + " time: " + alarmHour + ":" + alarmMinutes);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY + daysToAdd, alarmIntent);

    }

    //Add days until the next check is checked, rotating up to 7 days for the week.
    private static int daysAdded(int[] day_checked, int today){

        int k = 0;
        if(today == 7){
            today = 6;
        }
        for(int i = today; i <= day_checked.length; i++){
            if(day_checked[i] == 1){
                Log.i(LOG_TAG, "today is " + dayToString(today));
                Log.i(LOG_TAG, "There should be an alarm set for " + dayToString(i));
                Log.i(LOG_TAG, "i: " +i + " k: " + k);
                return k;
            }
            if(i >= 6){
                i = -1;
            }
            k++;
            if(k == 6) {
                //add 7 days if nothing is returned after six days
                //this means start the alarm for the same time one week from now
                Log.e(LOG_TAG, "########### add " + k + " days");
                return 7;

            }
        }
        return k;
    }
    private static void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
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
