package com.example.android.popularmovies;


/*
Its instances are bind to a ViewHolder for the RecyclerView in DetailsActivity.
 */
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
