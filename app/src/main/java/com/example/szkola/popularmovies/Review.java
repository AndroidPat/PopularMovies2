package com.example.szkola.popularmovies;


public class Review {

    public Review(){

    }

    private String author;
    private String contents;


    public Review(String author, String contents) {
        this.author = author;
        this.contents = contents;
    }


    public String getAuthor() {
        return author;
    }

    public String getContents() {
        return contents;
    }
}
