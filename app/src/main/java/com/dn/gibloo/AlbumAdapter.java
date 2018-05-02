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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    Context context;
    ArrayList<Album> items;

    public AlbumAdapter(Context context, ArrayList<Album> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.album, container, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final Album album = items.get(position);
        vh.name.setText(album.getName());
        String albumCoverPath = Tool.getAlbumCoverPath(context, album.getId());
        if (albumCoverPath != null) {
            Picasso.with(context).load(new File(albumCoverPath)).placeholder(R.drawable.song_icon).into(vh.albumCover);
        }
        vh.container01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AlbumSongsActivity.class);
                i.putExtra("album_id", album.getId());
                i.putExtra("album_name", album.getName());
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
        public TextView name, numberOfSongs;

        public ViewHolder(View v) {
            super(v);
            container01 = v.findViewById(R.id.container01);
            albumCover = v.findViewById(R.id.album_cover);
            name = v.findViewById(R.id.album_name);
            numberOfSongs = v.findViewById(R.id.number_of_songs);
        }
    }
}
