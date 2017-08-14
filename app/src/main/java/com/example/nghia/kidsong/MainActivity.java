package com.example.nghia.kidsong;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nghia.kidsong.Services.MusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<Song> songList;
    private ListView songView;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songView=(ListView)findViewById(R.id.song_list);
        GetSongList();
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (playIntent==null){
            playIntent=new Intent(this,MusicService.class);
            bindService(playIntent,musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

    }

    private ServiceConnection musicConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder=(MusicService.MusicBinder)iBinder;
            musicService=binder.getService();
            musicService.setList(songList);
            musicBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound=false;
        }
    };

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
                stopService(playIntent);
                musicService=null;
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void GetSongList()
    {
        GetDataAsynTask getSongAsync=new GetDataAsynTask();
        getSongAsync.execute();

    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService=null;
        super.onDestroy();
    }

    class GetDataAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String url=KSConstants.JSON_URL;
            JSONObject jsonObj;
            try {
                jsonObj=JsonReader.readJsonFromUrl(url);
                JSONArray jsonArray=jsonObj.getJSONArray("content");
                songList=new ArrayList<Song>();

                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsSong= jsonArray.getJSONObject(i);
                    Song song=new Song();
                    song.setId(jsSong.getString("id"));
                    song.setName(jsSong.getString("name"));
                    song.setMp3(jsSong.getString("mp3"));
                    song.setUrl(jsSong.getString("url"));

                    songList.add(song);
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SongAdapter songAdapter=new SongAdapter(MainActivity.this,songList);
            songView.setAdapter(songAdapter);
            songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try{
                        musicService.setSong(i);
                        musicService.playSong();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

}
