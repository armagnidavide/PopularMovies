package com.example.android.popularmovies;


public class MovieReview {
    private final String author;
    private final String content;
    public MovieReview(String author, String content){

        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
