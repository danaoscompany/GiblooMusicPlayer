package com.dn.gibloo;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.Media;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> songs;

    public PlaylistSongsAdapter(Context ctx, ArrayList<Song> songs) {
        context = ctx;
        this.songs = songs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int type) {
        View v = LayoutInflater.from(context).inflate(R.layout.song, container, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, final int position) {
        final Song song = songs.get(position);
        String albumCoverPath = Tool.getAlbumCoverPath(context, song.getAlbumId());
        if (albumCoverPath != null) {
            Picasso.with(context).load(new File(albumCoverPath)).placeholder(R.drawable.song_icon).into(vh.albumArt);
        }
        vh.title.setText(song.getTitle());
        vh.artist.setText(song.getArtist());
        vh.container01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent("TRANSFER_LIST_OF_SONGS");
                i.putExtra("list_of_songs", songs);
                i.putExtra("song_index", position);
                context.sendBroadcast(i);
                i = new Intent(context, MusicPlayer.class);
                i.putExtra("song", song);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container01;
        ImageView albumArt;
        TextView title;
        TextView artist;

        public ViewHolder(View v) {
            super(v);
            container01 = v.findViewById(R.id.container01);
            albumArt = v.findViewById(R.id.album_cover);
            title = v.findViewById(R.id.title);
            artist = v.findViewById(R.id.artist);
        }
    }
}
