package com.dn.gibloo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;

public class MusicService extends Service {
    MusicBinder binder = new MusicBinder();
    MediaPlayer mp;
    boolean started = false;
    String currentSongPath = "";
    NotificationCompat.Builder builder;
    ArrayList<Song> songs = null;
    int currentSongIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY_SONG");
        filter.addAction("PAUSE_SONG");
        filter.addAction("CONTINUE_SONG");
        filter.addAction("STOP_SONG");
        filter.addAction("PLAY_NEW_SONG");
        filter.addAction("TRANSFER_LIST_OF_SONGS");
        filter.addAction("com.dn.gibloo.PlaySong");
        filter.addAction("com.dn.gibloo.NextSong");
        filter.addAction("com.dn.gibloo.PrevSong");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("PLAY_SONG")) {
                    Song song = (Song) intent.getSerializableExtra("song");
                    try {
                        mp.setDataSource(song.getPath());
                        mp.prepare();
                    } catch (Exception exp) {
                    }
                    mp.start();
                    started = true;
                    MusicPlayer player = MusicPlayer.getInstance();
                    if (player != null) {
                        player.songProgress.setMax(mp.getDuration());
                    }
                    currentSongPath = song.getPath();
                    builder.setContentTitle(song.getArtist());
                    builder.setContentText(song.getTitle());
                    updateNotification();
                } else if (intent.getAction().equals("PAUSE_SONG")) {
                    mp.pause();
                    updateNotification();
                } else if (intent.getAction().equals("CONTINUE_SONG")) {
                    mp.start();
                    updateNotification();
                } else if (intent.getAction().equals("STOP_SONG")) {
                    mp.stop();
                    mp.release();
                    updateNotification();
                } else if (intent.getAction().equals("PLAY_NEW_SONG")) {
                    mp.stop();
                    mp.release();
                    mp = new MediaPlayer();
                    Song song = (Song) intent.getSerializableExtra("song");
                    try {
                        mp.setDataSource(song.getPath());
                        mp.prepare();
                    } catch (Exception exp) {
                    }
                    mp.start();
                    started = true;
                    MusicPlayer player = MusicPlayer.getInstance();
                    if (player != null) {
                        player.songProgress.setMax(mp.getDuration());
                    }
                    currentSongPath = song.getPath();
                    updateNotification();
                } else if (intent.getAction().equals("com.dn.gibloo.PlaySong")) {
                    if (!started) {
                        Song latestSongFile = (Song) Tool.readObject(new File(getFilesDir(), "song"));
                        try {
                            mp.setDataSource(latestSongFile.getPath());
                            mp.prepare();
                        } catch (Exception exp) {
                        }
                        mp.start();
                        started = true;
                    } else {
                        if (mp.isPlaying()) {
                            mp.pause();
                        } else {
                            mp.start();
                        }
                    }
                    updateNotification();
                } else if (intent.getAction().equals("com.dn.gibloo.PrevSong")) {
                    playPrevSong();
                    updateNotification();
                } else if (intent.getAction().equals("com.dn.gibloo.NextSong")) {
                    playNextSong();
                    updateNotification();
                } else if (intent.getAction().equals("TRANSFER_LIST_OF_SONGS")) {
                    songs = (ArrayList<Song>)intent.getSerializableExtra("list_of_songs");
                    currentSongIndex = intent.getIntExtra("song_index", 0);
                }
            }
        }, filter);
        Song latestSongFile = (Song)Tool.readObject(new File(getFilesDir(), "song"));
        if (latestSongFile != null) {
            builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            PendingIntent prev = PendingIntent.getBroadcast(this, 0, new Intent("com.dn.gibloo.PrevSong"), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent play = PendingIntent.getBroadcast(this, 1, new Intent("com.dn.gibloo.PlaySong"), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent next = PendingIntent.getBroadcast(this, 2, new Intent("com.dn.gibloo.NextSong"), PendingIntent.FLAG_UPDATE_CURRENT);
            Intent mainIntent = new Intent(this, MusicPlayer.class);
            mainIntent.putExtra("just_open", true);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(mainPendingIntent);
            builder.addAction(R.drawable.ic_stat_skip_previous, "", prev);
            builder.addAction(R.drawable.ic_stat_play_arrow, "", play);
            builder.addAction(R.drawable.ic_stat_next, "", next);
            builder.setStyle(new android.support.v7.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));
            builder.setWhen(0);
            builder.setOngoing(true);
            try {
                String albumCoverPath = Tool.getAlbumCoverPath(this, latestSongFile.getAlbumId());
                if (albumCoverPath != null) {
                    Bitmap albumArt = BitmapFactory.decodeFile(albumCoverPath);
                    builder.setLargeIcon(albumArt);
                }
            } catch (Exception exp) {
            }
            builder.setContentTitle(latestSongFile.getArtist());
            builder.setContentText(latestSongFile.getTitle());
            NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification not = builder.build();
            mgr.notify(1, not);
            startForeground(1, not);
        }
    }

    public void playNextSong() {
        if (currentSongIndex < songs.size()) {
            currentSongIndex++;
        } else {
            currentSongIndex = 0;
        }
        playSong(currentSongIndex);
    }

    public void playPrevSong() {
        if (currentSongIndex >= 0) {
            currentSongIndex--;
        } else {
            currentSongIndex = songs.size()-1;
        }
        playSong(currentSongIndex);
    }

    public void playSong(int index) {
        if (started) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource(songs.get(index).getPath());
            mp.prepare();
        } catch (Exception exp) {}
        mp.start();
    }

    public void updateNotification() {
        Song currentSong = songs.get(currentSongIndex);
        builder.setContentTitle(currentSong.getArtist());
        builder.setContentText(currentSong.getTitle());
        String albumCoverPath = Tool.getAlbumCoverPath(this, currentSong.getAlbumId());
        if (albumCoverPath != null) {
            Bitmap albumArt = BitmapFactory.decodeFile(albumCoverPath);
            builder.setLargeIcon(albumArt);
        }
        builder.mActions.clear();
        PendingIntent prev = PendingIntent.getBroadcast(this, 0, new Intent("com.dn.gibloo.PrevSong"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent play = PendingIntent.getBroadcast(this, 1, new Intent("com.dn.gibloo.PlaySong"), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent next = PendingIntent.getBroadcast(this, 2, new Intent("com.dn.gibloo.NextSong"), PendingIntent.FLAG_UPDATE_CURRENT);
        Intent mainIntent = new Intent(this, MusicPlayer.class);
        mainIntent.putExtra("just_open", true);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(mainPendingIntent);
        builder.addAction(R.drawable.ic_stat_skip_previous, "", prev);
        if (mp.isPlaying()) {
            builder.addAction(R.drawable.ic_stat_pause, "", play);
        } else {
            builder.addAction(R.drawable.ic_stat_play_arrow, "", play);
        }
        builder.addAction(R.drawable.ic_stat_next, "", next);
        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification not = builder.build();
        mgr.notify(1, not);
        startForeground(1, not);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }
}
