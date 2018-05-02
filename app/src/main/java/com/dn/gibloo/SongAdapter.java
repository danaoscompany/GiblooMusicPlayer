package com.dn.gibloo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> items;

    public SongAdapter(Context ctx, ArrayList<Song> items) {
        context = ctx;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.song, container, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        final Song song = items.get(position);
        String albumArtPath = Tool.getAlbumCoverPath(context, song.getAlbumId());
        if (albumArtPath != null) {
            Picasso.with(context).load(new File(albumArtPath)).placeholder(R.drawable.song_icon).into(vh.albumCover);
        }
        vh.title.setText(song.getTitle());
        vh.artist.setText(song.getArtist());
        vh.container01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent("TRANSFER_LIST_OF_SONGS");
                i.putExtra("list_of_songs", items);
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
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout container01;
        public ImageView albumCover;
        public TextView title, artist;

        public ViewHolder(View v) {
            super(v);
            container01 = v.findViewById(R.id.container01);
            albumCover = v.findViewById(R.id.album_cover);
            title = v.findViewById(R.id.title);
            artist = v.findViewById(R.id.artist);
        }
    }
}
