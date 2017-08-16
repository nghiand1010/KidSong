package com.example.nghia.kidsong;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;

import com.example.nghia.kidsong.Music.MusicController;
import com.example.nghia.kidsong.Services.MusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {


    private List<Song> songList;
    private ListView songView;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController controller;
    private boolean paused=false;
    private boolean playBackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songView=(ListView)findViewById(R.id.song_list);
        GetSongList();
        setController();
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
                musicService.setShuffle();
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

    private void setController(){
        controller=new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    public void playPrev(){

        musicService.playPrev();
        if (playBackPaused){
            setController();
            playBackPaused=false;
        }
        controller.show(0);
    }

    public void playNext(){
        musicService.playNext();
        if (playBackPaused){
            setController();
            playBackPaused=false;
        }
        controller.show(0);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService=null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        playBackPaused=true;
        musicService.pausePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    public int getDuration() {
        if (musicService!=null&&musicBound&&musicService.isPlaying()){
            return musicService.getDuration();
        }else{
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (musicService==null&&musicBound&&musicService.isPlaying()){
            return musicService.getPosn();
        }
        else {
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        musicService.go();
    }

    @Override
    public boolean isPlaying() {
        if (musicService!=null&&musicBound){
            return musicService.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
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
            musicService.setList(songList);
            songView.setAdapter(songAdapter);
            songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try{
                        musicService.setSong(i);
                        musicService.playSong();
                        if (playBackPaused){
                            setController();
                            playBackPaused=false;
                        }
                        controller.show(0);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.e("itemclick", e.getMessage());
                    }

                }
            });
        }
    }

}
