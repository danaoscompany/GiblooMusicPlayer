package com.dn.gibloo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dn.gibloo.fragments.AlbumFragment;
import com.dn.gibloo.fragments.ArtistFragment;
import com.dn.gibloo.fragments.GenreFragment;
import com.dn.gibloo.fragments.PlaylistFragment;
import com.dn.gibloo.fragments.SongFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SongFragment songFragment;
    AlbumFragment albumFragment;
    ArtistFragment artistFragment;
    GenreFragment genreFragment;
    PlaylistFragment playlistFragment;
    MusicService service;
    Intent musicServiceIntent;
    boolean bound = false;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        if (Tool.readObject(new File(getFilesDir(), "song")) == null) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MusicPlayer.class);
                startActivity(i);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        songFragment = new SongFragment();
        albumFragment = new AlbumFragment();
        artistFragment = new ArtistFragment();
        genreFragment = new GenreFragment();
        playlistFragment = new PlaylistFragment();
        selectFragment(songFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);
        bindService(musicServiceIntent, conn, BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY_SONG");
        registerReceiver(playSongReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(conn);
        unregisterReceiver(playSongReceiver);
    }

    public BroadcastReceiver playSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fab.setVisibility(View.VISIBLE);
        }
    };

    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((MusicService.MusicBinder)iBinder).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    public void selectFragment(Fragment fr) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fr).commit();
        if (fr instanceof SongFragment) {
            setTitle(R.string.text1);
        } else if (fr instanceof AlbumFragment) {
            setTitle(R.string.text3);
        } else if (fr instanceof ArtistFragment) {
            setTitle(R.string.text5);
        } else if (fr instanceof GenreFragment) {
            setTitle(R.string.text6);
        } else if (fr instanceof PlaylistFragment) {
            setTitle(R.string.text7);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.song_list) {
            selectFragment(songFragment);
        } else if (id == R.id.album_list) {
            selectFragment(albumFragment);
        } else if (id == R.id.artist_list) {
            selectFragment(artistFragment);
        } else if (id == R.id.genre_list) {
            selectFragment(genreFragment);
        } else if (id == R.id.playlist_list) {
            selectFragment(playlistFragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
