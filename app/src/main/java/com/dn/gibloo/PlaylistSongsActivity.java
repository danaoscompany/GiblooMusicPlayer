package com.dn.gibloo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.provider.MediaStore.Audio.Media;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

public class PlaylistSongsActivity extends AppCompatActivity {
    RecyclerView songList;
    long playlistId;
    ArrayList<Song> songs;
    PlaylistSongsAdapter adapter;
    MusicService service;
    boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        playlistId = getIntent().getLongExtra("playlist_id", 0L);
        setTitle(getIntent().getStringExtra("playlist_name"));
        songList = (RecyclerView) findViewById(R.id.song_list);
        songs = new ArrayList<>();
        new AsyncTask<String, Void, String>() {

            @Override
            public String doInBackground(String... params) {
                collectSongs();
                return "";
            }

            @Override
            public void onPostExecute(String result) {
                super.onPostExecute(result);
                adapter = new PlaylistSongsAdapter(PlaylistSongsActivity.this, songs);
                songList.setLayoutManager(new LinearLayoutManager(PlaylistSongsActivity.this));
                songList.setItemAnimator(new DefaultItemAnimator());
                songList.setAdapter(adapter);
            }

        }.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent i = new Intent(this, MusicService.class);
        if (!Tool.isServiceRunning(this, MusicService.class)) {
            startService(i);
        }
        bindService(i, conn, BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((MusicService.MusicBinder) iBinder).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public void collectSongs() {
        String[] projections = {
                MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.DATA, MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.ARTIST, MediaStore.Audio.Playlists.Members.ALBUM, MediaStore.Audio.Playlists.Members.ALBUM_ID, MediaStore.Audio.Playlists.Members.DURATION, MediaStore.Audio.Playlists.Members.SIZE,
                MediaStore.Audio.Playlists.Members.DATE_ADDED, MediaStore.Audio.Playlists.Members.DATE_MODIFIED, MediaStore.Audio.Playlists.Members.YEAR, MediaStore.Audio.Playlists.Members.PLAY_ORDER
        };
        Cursor c = getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId), projections, null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER);
        if (c != null && c.moveToFirst()) {
            int count = c.getCount();
            if (count > 0) {
                int nextCursor = 0;
                while (nextCursor < count) {
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID));
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DATA));
                    String title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE));
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST));
                    String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM));
                    long albumId = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID));
                    long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION));
                    long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.SIZE));
                    long dateAdded = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DATE_ADDED));
                    long dateModified = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DATE_MODIFIED));
                    int year = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.YEAR));
                    int playOrder = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAY_ORDER));
                    Tool.log("Play order: "+playOrder);
                    Song song = new Song();
                    song.setId(id);
                    song.setPath(path);
                    song.setTitle(title);
                    song.setArtist(artist);
                    song.setAlbum(album);
                    song.setAlbumId(albumId);
                    song.setDuration(duration);
                    song.setSize(size);
                    song.setDateAdded(dateAdded);
                    song.setDateModified(dateModified);
                    song.setYear(year);
                    songs.add(song);
                    c.moveToNext();
                    nextCursor++;
                }
            }
            c.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}