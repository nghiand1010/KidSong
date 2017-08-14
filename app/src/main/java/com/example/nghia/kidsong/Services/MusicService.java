package com.example.nghia.kidsong.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.example.nghia.kidsong.Song;

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

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=0;
        player=new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer()
    {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playSong()
    {
        player.reset();
        Song playSong=songs.get(songPosn);

        String currSong=playSong.getUrl();
        Uri songUri = Uri.parse(currSong);
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
        songPosn=songIndex;
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

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    public void setList(List<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
