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
import com.dn.gibloo.Song;
import java.util.ArrayList;

public class GenreSongsActivity extends AppCompatActivity {
    RecyclerView songList;
    long genreId;
    ArrayList<Song> songs;
    GenreSongsAdapter adapter;
    String artistName;
    MusicService service;
    boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_songs);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        artistName = getIntent().getStringExtra("genre_name");
        genreId = getIntent().getLongExtra("genre_id", 0L);
        setTitle(artistName);
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
                adapter = new GenreSongsAdapter(GenreSongsActivity.this, songs);
                songList.setLayoutManager(new LinearLayoutManager(GenreSongsActivity.this));
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
                MediaStore.Audio.Genres.Members._ID, MediaStore.Audio.Genres.Members.DATA, MediaStore.Audio.Genres.Members.TITLE, MediaStore.Audio.Genres.Members.ARTIST, MediaStore.Audio.Genres.Members.ALBUM, MediaStore.Audio.Genres.Members.ALBUM_ID, MediaStore.Audio.Genres.Members.DURATION, MediaStore.Audio.Genres.Members.SIZE,
                MediaStore.Audio.Genres.Members.DATE_ADDED, MediaStore.Audio.Genres.Members.DATE_MODIFIED, MediaStore.Audio.Genres.Members.YEAR
        };
        Cursor c = getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external", genreId), projections, MediaStore.Audio.Genres.Members.GENRE_ID+"=?", new String[] {Long.toString(genreId)}, MediaStore.Audio.Genres.Members.TITLE+" ASC");
        if (c != null && c.moveToFirst()) {
            int count = c.getCount();
            if (count > 0) {
                int nextCursor = 0;
                while (nextCursor < count) {
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members._ID));
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DATA));
                    String title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.TITLE));
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.ARTIST));
                    String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.ALBUM));
                    long albumId = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.ALBUM_ID));
                    long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DURATION));
                    long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.SIZE));
                    long dateAdded = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DATE_ADDED));
                    long dateModified = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.DATE_MODIFIED));
                    int year = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.YEAR));
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}