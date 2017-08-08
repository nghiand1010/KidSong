package com.example.nghia.kidsong;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private long id;
    private String title;
    private String artist;

    private ArrayList<Song> songList;
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songView=(ListView)findViewById(R.id.song_list);
        songList=new ArrayList<Song>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:

                return true;
            case R.id.action_end:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void GetSongList()
    {
        ContentResolver musicResolver=getContentResolver();
        Uri musicUri=android.provider.
    }
}
