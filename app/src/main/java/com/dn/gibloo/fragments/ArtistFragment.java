package com.dn.gibloo.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.MediaStore.Audio.Artists;
import android.widget.EditText;
import com.dn.gibloo.Artist;
import com.dn.gibloo.ArtistAdapter;
import com.dn.gibloo.ArtistSongsActivity;
import com.dn.gibloo.MainActivity;
import com.dn.gibloo.R;

import java.util.ArrayList;

public class ArtistFragment extends Fragment {
    View v;
    MainActivity activity;
    RecyclerView artistList;
    ArrayList<Artist> artists;
    ArtistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        v = inflater.inflate(R.layout.fragment_artist, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        activity = (MainActivity)getActivity();
        artistList = v.findViewById(R.id.artist_list);
        artists = new ArrayList<>();
        new AsyncTask<String, Void, String>() {

            @Override
            public String doInBackground(String... params) {
                collectArtists();
                return "";
            }

            @Override
            public void onPostExecute(String result) {
                super.onPostExecute(result);
                artistList.setLayoutManager(new LinearLayoutManager(activity));
                artistList.setItemAnimator(new DefaultItemAnimator());
                adapter = new ArtistAdapter(activity, artists);
                artistList.setAdapter(adapter);
            }

        }.execute();
    }

    public void collectArtists() {
        String[] projection = {
                Artists.ARTIST, Artists._ID, Artists.NUMBER_OF_ALBUMS, Artists.NUMBER_OF_TRACKS
        };
        Cursor c = activity.getContentResolver().query(Artists.EXTERNAL_CONTENT_URI, projection, null, null, Artists.ARTIST+" ASC");
        if (c != null && c.moveToFirst()) {
            int count = c.getCount();
            if (count > 0) {
                int nextCursor = 0;
                while (nextCursor < count) {
                    String name = c.getString(c.getColumnIndexOrThrow(Artists.ARTIST));
                    long id = c.getLong(c.getColumnIndexOrThrow(Artists._ID));
                    int numberOfAlbums = c.getInt(c.getColumnIndexOrThrow(Artists.NUMBER_OF_ALBUMS));
                    int numberOfTracks = c.getInt(c.getColumnIndexOrThrow(Artists.NUMBER_OF_TRACKS));
                    Artist artist = new Artist();
                    artist.setName(name);
                    artist.setId(id);
                    artist.setNumberOfAlbums(numberOfAlbums);
                    artist.setNumberOfTracks(numberOfTracks);
                    artists.add(artist);
                    c.moveToNext();
                    nextCursor++;
                }
            }
            c.close();
        }
    }
}