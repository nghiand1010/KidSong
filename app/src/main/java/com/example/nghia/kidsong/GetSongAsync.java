package com.example.nghia.kidsong;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by nghia on 8/13/17.
 */

public class GetSongAsync extends AsyncTask<List<Song>,Void,Void>{
    @Override
    protected Void doInBackground(List<Song>... lists) {
        String url=KSConstants.JSON_URL;
        List<Song> songs=lists[0];
        JSONObject jsonObj;
        try {
            jsonObj=JsonReader.readJsonFromUrl(url);
            JSONArray jsonArray=jsonObj.getJSONArray("content");

            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsSong= jsonArray.getJSONObject(i);
                Song song=new Song();
                song.setId(jsSong.getString("id"));
                song.setName(jsSong.getString("name"));
                song.setMp3(jsSong.getString("mp3"));
                song.setUrl(jsSong.getString("url"));

                songs.add(song);
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
}
