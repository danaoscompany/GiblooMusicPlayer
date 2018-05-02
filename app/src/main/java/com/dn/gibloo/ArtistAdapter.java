package com.dn.gibloo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    Context context;
    ArrayList<Artist> artists;

    public ArtistAdapter(Context ctx, ArrayList<Artist> artists) {
        context = ctx;
        this.artists = artists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int type) {
        View v = LayoutInflater.from(context).inflate(R.layout.artist, container, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final Artist artist = artists.get(position);
        vh.name.setText(artist.getName());
        vh.numberOfSongs.setText(Integer.toString(artist.getNumberOfTracks())+" "+context.getResources().getString(R.string.text4));
        vh.container01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ArtistSongsActivity.class);
                i.putExtra("artist_name", artist.getName());
                i.putExtra("artist_id", artist.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container01;
        TextView name;
        TextView numberOfSongs;

        public ViewHolder(View v) {
            super(v);
            container01 = v.findViewById(R.id.container01);
            name = v.findViewById(R.id.name);
            numberOfSongs = v.findViewById(R.id.total_songs);
        }
    }
}
