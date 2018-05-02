package com.dn.gibloo.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dn.gibloo.MainActivity;
import com.dn.gibloo.Playlist;
import com.dn.gibloo.PlaylistAdapter;
import com.dn.gibloo.R;
import java.util.ArrayList;
import android.provider.MediaStore.Audio.Playlists;

public class PlaylistFragment extends Fragment {
    View v;
    MainActivity activity;
    RecyclerView playlistList;
    ArrayList<Playlist> playlists;
    PlaylistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        v = inflater.inflate(R.layout.fragment_playlist, container, false);
        playlistList = v.findViewById(R.id.playlist_list);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        activity = (MainActivity)getActivity();
        playlists = new ArrayList<>();
        playlistList.setLayoutManager(new LinearLayoutManager(activity));
        playlistList.setItemAnimator(new DefaultItemAnimator());
        adapter = new PlaylistAdapter(activity, playlists);
        collectPlaylists();
        playlistList.setAdapter(adapter);
    }

    public void collectPlaylists() {
        String[] projections = {
                Playlists.DATA, Playlists.NAME, Playlists.DATE_ADDED, Playlists.DATE_MODIFIED, Playlists._ID
        };
        Cursor c = activity.getContentResolver().query(Playlists.EXTERNAL_CONTENT_URI, projections, null, null, Playlists.NAME+" ASC");
        if (c != null && c.moveToFirst()) {
            int count = c.getCount();
            if (count > 0) {
                int nextCursor = 0;
                while (nextCursor < count) {
                    String path = c.getString(c.getColumnIndexOrThrow(Playlists.DATA));
                    String name = c.getString(c.getColumnIndexOrThrow(Playlists.NAME));
                    long dateAdded = c.getLong(c.getColumnIndexOrThrow(Playlists.DATE_ADDED));
                    long dateModified = c.getLong(c.getColumnIndexOrThrow(Playlists.DATE_MODIFIED));
                    long id = c.getLong(c.getColumnIndexOrThrow(Playlists._ID));
                    Playlist playlist = new Playlist();
                    playlist.setPath(path);
                    playlist.setName(name);
                    playlist.setDateAdded(dateAdded);
                    playlist.setDateModified(dateModified);
                    playlist.setId(id);
                    playlist.setNumberOfSongs(getNumberOfTracks(id));
                    playlists.add(playlist);
                    c.moveToNext();
                    nextCursor++;
                }
            }
            c.close();
        }
    }

    private int getNumberOfTracks(long playlistId) {
        Uri uri = Playlists.Members.getContentUri("external", playlistId);
        Cursor c = activity.getContentResolver().query(uri, new String[] {Playlists.Members.AUDIO_ID}, null, null, null);
        try {
            int count = 0;
            if (c.moveToFirst()) {
                count = c.getCount();
            }
            c.close();
            return count;
        } catch (Exception exp) {}
        return 0;
    }
}
