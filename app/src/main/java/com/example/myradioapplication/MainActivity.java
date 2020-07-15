package com.example.myradioapplication;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.provider.SyncStateContract;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

public class MainActivity extends AppCompatActivity {

    //private MediaPlayer player=  new MediaPlayer();
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.putExtra("streamingUrl", "https://npr-ice.streamguys1.com/live.mp3");
                ContextCompat.startForegroundService(MainActivity.this, intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        getApplicationContext().stopService(intent);
        super.onDestroy();
    }

    /*
    private void initializeMediaPlayer() {
        try {
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
            player.setDataSource("https://npr-ice.streamguys1.com/live.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                fab.setVisibility(View.VISIBLE);
            }
        });
    }
     */
}