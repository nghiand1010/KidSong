package com.example.nghia.kidsong;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nghia on 8/13/17.
 */

public class SongAdapter extends BaseAdapter {


    private List<Song> songs;
    private LayoutInflater songIfl;
    private Activity activity;


    public SongAdapter(Activity activity, List<Song> theSongs){
        this.activity = activity;
        songs=theSongs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = activity.getLayoutInflater().inflate(R.layout.songs_list, viewGroup, false);

        ImageView ivPlayPause=(ImageView)view.findViewById(R.id.buttonSong);
        TextView tvSong=(TextView)view.findViewById(R.id.tvSong);

        if( KSConstants.songIndex ==i){
            ivPlayPause.setImageResource(R.drawable.musicplayer);

        }else{
            ivPlayPause.setImageResource(R.drawable.play1);
        }


        Song currSong=songs.get(i);
        tvSong.setText(currSong.getName());

        return view;
    }


}
