package com.hackathon.onn.musicalbike;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by sergey on 5/27/17.
 */

public class MusicController extends MediaPlayer {

//    is playing is not the opposite of is stopped
    private MediaPlayer player;
    private boolean isStopped;
    private boolean isPlaying = false;

    public MusicController(MediaPlayer player) {
        this.player = player;
        player.setLooping(true);
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void play() {
        isPlaying = true;

        if (isStopped) {
            try {
                player.prepare();
                isStopped = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        player.start();
    }

    public void stop() {
        player.stop();
        isStopped = true;
        isPlaying = false;
    }
}
