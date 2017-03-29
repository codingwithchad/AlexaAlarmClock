package com.h.chad.alexaalarmclock;

import android.app.Application;

import java.text.DecimalFormat;

/**
 * Created by chad on 3/24/2017.
 */

public class AlarmUtils extends AlarmClockActivity{


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

    public static void setAllAlarms(){

    }
}
