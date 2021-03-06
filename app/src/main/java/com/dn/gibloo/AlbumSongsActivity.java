package com.dn.gibloo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

public class AlbumSongsActivity extends AppCompatActivity {
    RecyclerView songList;
    long albumId;
    ArrayList<Song> songs;
    AlbumSongsAdapter adapter;
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
        albumId = getIntent().getLongExtra("album_id", 0L);
        setTitle(getIntent().getStringExtra("album_name"));
        songList = (RecyclerView)findViewById(R.id.song_list);
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
                adapter = new AlbumSongsAdapter(AlbumSongsActivity.this, songs);
                songList.setLayoutManager(new LinearLayoutManager(AlbumSongsActivity.this));
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
            service = ((MusicService.MusicBinder)iBinder).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public void collectSongs() {
        String[] projections = {
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.YEAR
        };
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projections, MediaStore.Audio.Media.ALBUM_ID+"=?", new String[] {Long.toString(albumId)}, MediaStore.Audio.Media.TITLE+" ASC");
        if (c != null && c.moveToFirst()) {
            int count = c.getCount();
            if (count > 0) {
                int nextCursor = 0;
                while (nextCursor < count) {
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    long albumId = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    long dateAdded = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
                    long dateModified = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED));
                    int year = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
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
                    song.setIndex(nextCursor);
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
        }
        return false;
    }
}