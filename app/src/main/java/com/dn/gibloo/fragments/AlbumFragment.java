package com.dn.gibloo.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.MediaStore.Audio.Albums;
import com.dn.gibloo.Album;
import com.dn.gibloo.AlbumAdapter;
import com.dn.gibloo.GridAutofitLayoutManager;
import com.dn.gibloo.MainActivity;
import com.dn.gibloo.R;
import com.dn.gibloo.Tool;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    View v;
    MainActivity activity;
    RecyclerView albumList;
    ArrayList<Album> albums;
    AlbumAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        v = inflater.inflate(R.layout.fragment_album, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        activity = (MainActivity)getActivity();
        albumList = v.findViewById(R.id.album_list);
        albums = new ArrayList<>();
        collectAlbums();
        albumList.setLayoutManager(new GridAutofitLayoutManager(activity, Tool.dpToPx(activity, 200)));
        albumList.setItemAnimator(new DefaultItemAnimator());
        adapter = new AlbumAdapter(activity, albums);
        albumList.setAdapter(adapter);
    }

    public void collectAlbums() {
        String[] projections = {
                Albums._ID, Albums.ALBUM, Albums.NUMBER_OF_SONGS
        };
        Cursor c = activity.getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, projections, null, null, Albums.ALBUM+" ASC");
        if (c != null && c.moveToFirst()) {
            int index = 0;
            while (index < c.getColumnCount()) {
                Album album = new Album();
                album.setId(c.getLong(c.getColumnIndexOrThrow(Albums._ID)));
                album.setName(c.getString(c.getColumnIndexOrThrow(Albums.ALBUM)));
                album.setNumberOfSongs(c.getInt(c.getColumnIndexOrThrow(Albums.NUMBER_OF_SONGS)));
                albums.add(album);
                index++;
                c.moveToNext();
            }
            c.close();
        }
    }
}
