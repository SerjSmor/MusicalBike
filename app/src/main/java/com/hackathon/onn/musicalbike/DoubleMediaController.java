package com.hackathon.onn.musicalbike;

import android.media.MediaPlayer;

/**
 * Created by sergey on 5/28/17.
 */

public class DoubleMediaController {
    private MusicController successPlayer;
    private MusicController encouragePlayer;
    private int goodIteration = 0; // will affect the speed of the sound

    public DoubleMediaController(MusicController successPlayer, MusicController failPlayer) {
        this.successPlayer = successPlayer;
        this.encouragePlayer = failPlayer;
    }

    public DoubleMediaController(MediaPlayer successPlayer, MediaPlayer failPlayer) {
        this.successPlayer = new MusicController(successPlayer);
        this.encouragePlayer = new MusicController(failPlayer);
    }


    public void playGood() {
//        goodIteration++;
//        if (goodIteration == FULL_SPEED_THRESHOLD) {
//            // play full speed
//        }

        if (encouragePlayer.isPlaying()) {
            encouragePlayer.stop();
        }

        if (!successPlayer.isPlaying()) {
            successPlayer.play();
        }
    }

    public void playEncourage() {
//        goodIteration = 0;

        if (successPlayer.isPlaying()) {
            successPlayer.stop();
        }

        if (!encouragePlayer.isPlaying()) {
            encouragePlayer.play();
        }
    }


}
