package com.example.nghia.kidsong.FileControl;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.nghia.kidsong.Song;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.lang.Object;

/**
 * Created by nghia on 8/16/17.
 */

public class DownloadFile{

    private Song song;
    public DownloadFile(Song inputSong){
        song=inputSong;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }





    public Void DownloadFile() {
        int count;
        try {
            String fileName=song.getMp3();
            File extStore = Environment.getExternalStorageDirectory();
            String path = extStore.getAbsolutePath() + "/" + fileName;

            File myFile = new File(path);
            if (myFile.exists()){
                return null;
            }

            URL url1 = new URL(song.getUrl());
            URLConnection conexion = url1.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            InputStream input = new BufferedInputStream(url1.openStream());


            myFile.createNewFile();

            OutputStream output = new FileOutputStream(myFile);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("download file", "DownloadFile: ", e);
        }

        return null;
    }

    public String ReadFile()
    {
        File extStore = Environment.getExternalStorageDirectory();
        String path = extStore.getAbsolutePath() + "/" + song.getMp3();
        return path;
    }
}
