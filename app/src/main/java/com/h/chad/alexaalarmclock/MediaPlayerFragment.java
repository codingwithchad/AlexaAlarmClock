package com.h.chad.alexaalarmclock;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * Created by chad on 3/23/2017.
 */

public class MediaPlayerFragment {
    public static MediaPlayer mMediaPlayer;


    public static void playRecording(Context context, String fileName){
        if(TextUtils.isEmpty(fileName) || fileName.length() <=0){
            return;
        }
        mMediaPlayer = new MediaPlayer();
        try{
            mMediaPlayer.setDataSource(fileName);
            mMediaPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }
        mMediaPlayer.start();
        Log.i("MediaPlayerFragment ", " Mediaplayer should have started");
        relaseMediaPlayer();



    }

    //Relase Media Player
    private static void relaseMediaPlayer(){
        if( mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


}
