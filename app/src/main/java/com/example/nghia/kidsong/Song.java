package com.example.nghia.kidsong;

/**
 * Created by nghia on 8/8/17.
 */

public class Song {
    private String id;
    private String name;
    private String mp3;
    private String url;

    public Song()
    {

    }

    public Song(String id, String name, String mp3, String url) {
        this.id = id;
        this.name = name;
        this.mp3 = mp3;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}




