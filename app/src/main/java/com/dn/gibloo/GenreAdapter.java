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

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    Context context;
    ArrayList<Genre> items;

    public GenreAdapter(Context context, ArrayList<Genre> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.genre, container, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final Genre genre = items.get(position);
        vh.name.setText(genre.getName());
        vh.totalSongs.setText(Integer.toString(genre.getSongCount())+" "+context.getResources().getString(R.string.text4));
        vh.container01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, GenreSongsActivity.class);
                i.putExtra("genre_name", genre.getName());
                i.putExtra("genre_id", genre.getId());
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
