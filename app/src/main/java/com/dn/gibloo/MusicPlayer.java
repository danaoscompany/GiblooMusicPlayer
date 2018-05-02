package com.dn.gibloo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class MusicPlayer extends AppCompatActivity {
    private static MusicPlayer instance;
    TextView title, artist;
    ImageView albumCover;
    ImageView playButton;
    SeekBar songProgress;
    Song song;
    MusicService service;
    boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_music_player);
        setTitle("");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        albumCover = (ImageView) findViewById(R.id.album_cover);
        title = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);
        playButton = (ImageView) findViewById(R.id.play);
        songProgress = (SeekBar) findViewById(R.id.song_progress);
        songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int position = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
                this.position = position;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (bound) {
                    service.mp.seekTo(position);
                }
            }
        });
        song = (Song) getIntent().getSerializableExtra("song");
        if (song == null) {
            song = (Song) Tool.readObject(new File(getFilesDir(), "song"));
        }
        if (song != null) {
            String albumCoverPath = Tool.getAlbumCoverPath(this, song.getAlbumId());
            if (albumCoverPath != null) {
                Picasso.with(this).load(new File(albumCoverPath)).placeholder(R.drawable.song_icon).into(albumCover);
            }
            title.setText(song.getTitle());
            artist.setText(song.getArtist());
            setTitle(song.getTitle());
            Tool.writeObject(new File(getFilesDir(), "song"), song);
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!service.started) {
                    Intent i = new Intent("PLAY_SONG");
                    i.putExtra("song", song);
                    sendBroadcast(i);
                    playButton.setImageResource(R.drawable.pause_icon);
                } else {
                    if (service.mp.isPlaying()) {
                        sendBroadcast(new Intent("PAUSE_SONG"));
                        playButton.setImageResource(R.drawable.play_icon);
                    } else {
                        sendBroadcast(new Intent("CONTINUE_SONG"));
                        playButton.setImageResource(R.drawable.pause_icon);
                    }
                }
            }
        });
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bound) {
                    songProgress.setProgress(service.mp.getCurrentPosition());
                }
                h.postDelayed(this, 100);
            }
        }, 100);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);
        bindService(musicServiceIntent, conn, BIND_AUTO_CREATE);
    }

    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((MusicService.MusicBinder) iBinder).getService();
            bound = true;
            if (service.mp.isPlaying()) {
                playButton.setImageResource(R.drawable.pause_icon);
            } else {
                playButton.setImageResource(R.drawable.play_icon);
            }
            if (service.started) {
                songProgress.setMax(service.mp.getDuration());
            }
            if (!service.started) {
                Intent i = new Intent("PLAY_SONG");
                i.putExtra("song", song);
                sendBroadcast(i);
                playButton.setImageResource(R.drawable.pause_icon);
            } else {
                if (!service.currentSongPath.equals(song.getPath())) {
                    Intent i = new Intent("PLAY_NEW_SONG");
                    i.putExtra("song", song);
                    sendBroadcast(i);
                    playButton.setImageResource(R.drawable.pause_icon);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    public static MusicPlayer getInstance() {
        return instance;
    }
}
