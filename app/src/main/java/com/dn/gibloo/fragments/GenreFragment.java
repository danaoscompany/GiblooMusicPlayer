package com.dn.gibloo.fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.MediaStore.Audio.Genres;

import com.dn.gibloo.Genre;
import com.dn.gibloo.GenreAdapter;
import com.dn.gibloo.GridAutofitLayoutManager;
import com.dn.gibloo.MainActivity;
import com.dn.gibloo.R;
import com.dn.gibloo.Tool;

import java.util.ArrayList;

public class GenreFragment extends Fragment {
    View v;
    MainActivity activity;
    RecyclerView genreList;
    ArrayList<Genre> genres;
    GenreAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        v = inflater.inflate(R.layout.fragment_genre, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        activity = (MainActivity)getActivity();
        genreList = v.findViewById(R.id.genre_list);
        genres = new ArrayList<>();
        new AsyncTask<String, Void, String>() {

            @Override
            public String doInBackground(String... params) {
                collectGenres();
                return "";
            }

            @Override
            public void onPostExecute(String result) {
                genreList.setLayoutManager(new LinearLayoutManager(activity));
                genreList.setItemAnimator(new DefaultItemAnimator());
                adapter = new GenreAdapter(activity, genres);
                genreList.setAdapter(adapter);
            }

        }.execute();
    }

    private int getGenreSongCount(long genreId) {
        try {
            Cursor c = activity.getContentResolver().query(Genres.Members.getContentUri("external", genreId), null, null, null, null);
            c.moveToFirst();
            int songsCount = c.getCount();
            c.close();
            return songsCount;
        } catch (Exception exp) {}
        return 0;
    }

    public void collectGenres() {
        String[] projections = {
                Genres.NAME, Genres._ID
        };
        Cursor c = activity.getContentResolver().query(Genres.EXTERNAL_CONTENT_URI, projections, null, null, Genres.NAME+" ASC");
        if (c != null && c.moveToFirst()) {
            int dbCount = c.getCount();
            if (dbCount > 0) {
                int nextCursor = 0;
                while (nextCursor < dbCount) {
                    String name = c.getString(c.getColumnIndexOrThrow(Genres.NAME));
                    long id = c.getLong(c.getColumnIndexOrThrow(Genres._ID));
                    int songsCount = getGenreSongCount(id);
                    Genre genre = new Genre();
                    genre.setName(name);
                    genre.setSongCount(songsCount);
                    genre.setId(id);
                    genres.add(genre);
                    c.moveToNext();
                    nextCursor++;
                }
            }
            c.close();
        }
    }
}
