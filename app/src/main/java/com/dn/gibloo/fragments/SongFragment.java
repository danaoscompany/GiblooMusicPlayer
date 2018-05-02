package com.dn.gibloo.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.MediaStore.Audio.Media;
import com.dn.gibloo.MainActivity;
import com.dn.gibloo.R;
import com.dn.gibloo.Song;
import com.dn.gibloo.SongAdapter;

import java.util.ArrayList;

public class SongFragment extends Fragment {
    View v;
    MainActivity activity;
    RecyclerView songList;
    ArrayList<Song> songs;
    SongAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        v = inflater.inflate(R.layout.fragment_song, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        activity = (MainActivity)getActivity();
        songList = v.findViewById(R.id.song_list);
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
                adapter = new SongAdapter(activity, songs);
                songList.setLayoutManager(new LinearLayoutManager(activity));
                songList.setItemAnimator(new DefaultItemAnimator());
                songList.setAdapter(adapter);
            }

        }.execute();
    }

    public void collectSongs() {
        String[] projections = {
                Media._ID, Media.DATA, Media.TITLE, Media.ARTIST, Media.ALBUM, Media.ALBUM_ID, Media.DURATION, Media.SIZE,
                Media.DATE_ADDED, Media.DATE_MODIFIED, Media.YEAR
        };
        Cursor c = activity.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projections, Media.IS_MUSIC+" != 0", null, Media.TITLE+" ASC");
        if (c != null && c.moveToFirst()) {
            int index = 0;
            while (index < c.getColumnCount()) {
                long id = c.getLong(c.getColumnIndexOrThrow(Media._ID));
                String path = c.getString(c.getColumnIndexOrThrow(Media.DATA));
                String title = c.getString(c.getColumnIndexOrThrow(Media.TITLE));
                String artist = c.getString(c.getColumnIndexOrThrow(Media.ARTIST));
                String album = c.getString(c.getColumnIndexOrThrow(Media.ALBUM));
                long albumId = c.getLong(c.getColumnIndexOrThrow(Media.ALBUM_ID));
                long duration = c.getLong(c.getColumnIndexOrThrow(Media.DURATION));
                long size = c.getLong(c.getColumnIndexOrThrow(Media.SIZE));
                long dateAdded = c.getLong(c.getColumnIndexOrThrow(Media.DATE_ADDED));
                long dateModified = c.getLong(c.getColumnIndexOrThrow(Media.DATE_MODIFIED));
                int year = c.getInt(c.getColumnIndexOrThrow(Media.YEAR));
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
                song.setIndex(index);
                songs.add(song);
                index++;
                c.moveToNext();
            }
            c.close();
        }
    }
}
