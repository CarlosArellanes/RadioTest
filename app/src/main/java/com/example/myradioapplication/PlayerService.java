package com.example.myradioapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PlayerService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private final IBinder iBinder = new LocalBinder();
    private MediaPlayer player = null;
    private String streamingUrl;
    private WifiManager.WifiLock wifiLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("streamingUrl"))
            streamingUrl = intent.getStringExtra("streamingUrl");

        initializeNotification();
        initializePlayer();
        return Service.START_NOT_STICKY;
    }

    private void initializeNotification() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("NPR is playing")
                .setSmallIcon(android.R.drawable.btn_radio)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void initializePlayer() {
        player = new MediaPlayer();
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            player.setDataSource(streamingUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnPreparedListener(this);
        player.prepareAsync();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        try {
            wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock");
            wifiLock.acquire();
        } catch (NullPointerException e) {
            Log.d("playerService", "WiFi lock failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int error, int extra) {
        Log.d("playerService", "Error: " + error + ", extra: " + extra);
        stopSelf();
        return false;
    }

    private void playMedia() {
        if (!player.isPlaying())
            player.start();
    }

    private void pauseMedia() {
        if (player.isPlaying())
            player.pause();
    }

    private void stopMedia() {
        if (player.isPlaying()) {
            player.stop();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("playerService", "Destroying service");
        if (player != null) {
            player.release();
            wifiLock.release();
        }
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
