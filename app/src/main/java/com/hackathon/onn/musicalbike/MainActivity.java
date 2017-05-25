package com.hackathon.onn.musicalbike;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gemsense.common.GemSensorsData;
import com.gemsense.gemsdk.Gem;
import com.gemsense.gemsdk.GemListener;
import com.gemsense.gemsdk.GemManager;
import com.gemsense.gemsdk.GemSDKUtilityApp;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Gem gem;
    MediaPlayer mPlayer;
    int loseSound = 0;
    float threshold = 0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] whitelist = GemSDKUtilityApp.getWhiteList(this);
        initGem(whitelist[0]);
        loseSound = R.raw.sad;

        mPlayer = MediaPlayer.create(getApplicationContext(), loseSound);//Create MediaPlayer object with MP3 file under res/raw folder

      /*  Uri myUri = Uri.parse("file:///Internal storage/Download/John Legend  - All Of Me.mp3");
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        mediaPlayer.prepare();
        mediaPlayer.start();
*/

//        get list of files
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String[] extrenalFiles = file.list();
        if (extrenalFiles != null) {

        } else {
//        load files from the raw
            File internalFiles = Environment.getDataDirectory();
            if (internalFiles != null) {

            }

        }


//        get permission
    }

    private void initGem(String address) {
        //Get a gem
        gem = GemManager.getDefault().getGem(address, new GemListener() {
            @Override
            public void onSensorsChanged(GemSensorsData data) {

                float a[] = data.acceleration;
                double acc = Math.sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]);

                //filteredAcc[0] = (filteredAcc[0] + acc[0]) / 2f;
                //filteredAcc[1] = (filteredAcc[1] + acc[1]) / 2f;
                //filteredAcc[2] = (filteredAcc[2] + acc[2]) / 2f;

                Log.i("Acceleration", "Speed " + acc);

                if(acc < (1- threshold) || acc < (1+threshold)){
                    mPlayer.start();

                }

            }

            @Override
            public void onErrorOccurred(int i) {
                Toast.makeText(MainActivity.this, "Can't find a gem", Toast.LENGTH_SHORT).show();
            }

        });

    }
        @Override
    protected void onResume() {
        super.onResume();
        //Bind the Gem Service to the app
        GemManager.getDefault().bindService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Unbind Gem Service from the application
        GemManager.getDefault().unbindService(this);
    }

    public void onClick(View v)
    {
        String filePath = getExternalFilesDir(null) + "/p0000.mp3";
        File file = new File(filePath);
        file.setReadable(true);
        file.setWritable(true);

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(this, "Ext storage not available or read only", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Ext storage available", Toast.LENGTH_SHORT).show();
        }

        if (file.exists()){
            Toast.makeText(this, "File found", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, getExternalFilesDir(null) + "/p0000.mp3 - File not found", Toast.LENGTH_LONG).show();
        }


    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)     {
                    //Peform your task here if any
                } else {

                    checkPermission();
                }
                return;
            }
        }
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}

