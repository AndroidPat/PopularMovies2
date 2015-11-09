package com.example.szkola.popularmovies;

public class Trailer {

    public Trailer(){

    }

    String name;
    String youtubeURL;

    public Trailer(String name, String youtubeURL) {
        this.name = name;
        this.youtubeURL = youtubeURL;
    }

    public String getName() {
        return name;
    }

    public String getYoutubeURL() {
        return youtubeURL;
    }
}
