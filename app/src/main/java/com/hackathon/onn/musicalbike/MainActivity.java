package com.hackathon.onn.musicalbike;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.gemsense.common.GemSensorsData;
import com.gemsense.gemsdk.Gem;
import com.gemsense.gemsdk.GemListener;
import com.gemsense.gemsdk.GemManager;
import com.gemsense.gemsdk.GemSDKUtilityApp;
import com.gemsense.gemsdk.OnSensorsAbstractListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Gem gem;
    MediaPlayer mPlayerSuccess;
    MediaPlayer mPlayerFail;

    int loseSound = 0;
    float threshold = 0.02f; //acceleration must change at least this figure in ABS within the time interval.
    long lastTime = System.currentTimeMillis();
    long interval = 1000 ; // the interval between time samples
    float prevAcc = 0f;

    private static final String FILES = "FILES";
    private static final String TAG = "MainActivity";
    private static final int ENCOURAGE_MAX = 3;
    private static final int FULL_SPEED_THRESHOLD = 3;
    private SharedPreferences prefs;

    private ArrayList<String> songs = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Button chooseSongsButton;
    private MediaPlayer playerS;
    private MediaPlayer playerF;

    private MusicController controllerS;
    private MusicController controllerF;
    private DoubleMediaController mediaController;
    private int encourageIterations = 0;
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] whitelist = GemSDKUtilityApp.getWhiteList(this);
        if (whitelist.length > 0) {
            initGem(whitelist[0]);
        }

        loseSound = R.raw.sad;

        mPlayerSuccess = MediaPlayer.create(getApplicationContext(), R.raw.a3);
        mPlayerFail = MediaPlayer.create(getApplicationContext(), R.raw.b1);

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String[] extrenalFiles = file.list();
        if (extrenalFiles != null) {

        } else {
//        load files from the raw
            File internalFiles = Environment.getDataDirectory();
            if (internalFiles != null) {

            }

        }



        long lastTime = 0;
        playerS = MediaPlayer.create(getApplicationContext(), R.raw.a1);
        playerF = MediaPlayer.create(getApplicationContext(), R.raw.b1);

        controllerS = new MusicController(playerS);
        controllerF = new MusicController(playerF);
        mediaController = new DoubleMediaController(controllerS, controllerF);
        Random random = new Random();


//        get permission
    }

    private void initGem(String address) {
        //Get a gem
        gem = GemManager.getDefault().getGem(address, new GemListener() {
            @Override
            public void onStateChanged(int state) {
                //States handling
                switch (state) {
                    case Gem.STATE_CONNECTED:
                        Log.d("GemDemo", "Connected to a gem");
                        break;
                    case Gem.STATE_DISCONNECTED:
                        Log.d("GemDemo", "Gem was disconnected");
                        break;
                }
            }

            @Override
            public void onErrorOccurred(int i) {
                Toast.makeText(MainActivity.this, "Can't find a gem", Toast.LENGTH_SHORT).show();
            }

        });

        gem.setSensorsListener(new OnSensorsAbstractListener() {
            @Override
            public void onSensorsChanged(GemSensorsData data) {
                super.onSensorsChanged(data);

                float q[] = data.quaternion;
                long now = System.currentTimeMillis();

                if (now - lastTime > interval) {
                    if((q[0]) - prevAcc > threshold) { // success
                        encourageIterations = 0; // reset the encourage
                        mediaController.playGood();
                    }
                    else { // encourage
                        encourageIterations++;
                        if (encourageIterations == ENCOURAGE_MAX) {
                            encourageIterations = 0;
                            mediaController.playEncourage();
                        }
                    }
//                    prevAcc = (q[0]);
//                    Log.d(TAG, "value:" + (q[0]));
                    lastTime = now;

                }
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

