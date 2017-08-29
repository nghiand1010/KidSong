package com.example.nghia.kidsong.Services;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.widget.ListView;

import com.example.nghia.kidsong.FileControl.DownloadFile;
import com.example.nghia.kidsong.KSConstants;
import com.example.nghia.kidsong.MainActivity;
import com.example.nghia.kidsong.R;
import com.example.nghia.kidsong.Song;
import com.example.nghia.kidsong.SongAdapter;

import java.io.IOException;
import java.util.List;

/**
 * Created by nghia on 8/14/17.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{

    private MediaPlayer player;
    private List<Song> songs;
    private int songPosn;
    private final IBinder musicBind=new MusicBinder();
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;
    private ListView listView;

    @Override
    public void onCreate() {
        super.onCreate();
        player=new MediaPlayer();
        initMusicPlayer();
        rand=new Random();
    }

    public void initMusicPlayer()
    {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public boolean setShuffle(){
        if (shuffle){
            shuffle=false;
        }
        else {
            shuffle=true;
        }

        return shuffle;
    }



    public void playSong()
    {
        player.reset();
        Song playSong=songs.get(songPosn);
        songTitle=playSong.getName();

        DownloadFile downloadFile=new DownloadFile(playSong);
        String path=downloadFile.ReadFile();

        Uri songUri = Uri.parse(path);
        try {
            player.setDataSource(getApplicationContext(),songUri);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        player.prepareAsync();

    }

    public void setSong(int songIndex)
    {
        setSongPosn(songIndex);
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDuration()
    {
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void setSongPosn(int songPosn) {
        this.songPosn = songPosn;
        KSConstants.songIndex=songPosn;
        SongAdapter adapter= (SongAdapter) listView.getAdapter();

        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }

    }

    public void playPrev(){
        setSongPosn(songPosn-1);
        if (songPosn<=0)
            setSongPosn(songs.size()-1);
        playSong();
    }

    public void playNext(){

        if (shuffle){
            int newSong=songPosn;
            while (newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }

            setSongPosn(newSong);
        }
        else {
            setSongPosn(songPosn+1);
            if (songPosn>=songs.size()){
                setSongPosn(0);
            }
        }

        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (player.getCurrentPosition()>0){
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        MainActivity.controller.show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        Notification.Builder builder=new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);

        Notification not=builder.build();

        startForeground(NOTIFY_ID,not);

    }

    public void setList(List<Song> theSongs){
        songs=theSongs;
    }

    public void setListView(ListView _listView){
        listView=_listView;
        setSongPosn(0);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
