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

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<Playlist> items;

    public PlaylistAdapter(Context context, ArrayList<Playlist> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.playlist, container, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final Playlist playlist = items.get(position);
        vh.name.setText(playlist.getName());
        vh.totalSongs.setText(""+playlist.getNumberOfSongs()+" "+context.getResources().getString(R.string.text4));
        vh.container01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PlaylistSongsActivity.class);
                i.putExtra("playlist_id", playlist.getId());
                i.putExtra("playlist_name", playlist.getName());
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
        public TextView name, totalSongs;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            totalSongs = v.findViewById(R.id.total_songs);
            container01 = v.findViewById(R.id.container01);
        }
    }
}
