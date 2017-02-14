package com.example.android.popularmovies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private ArrayList<MovieForGridItem> moviesForGrid;
    private int mItemsNumber;
    public MoviesAdapter(ArrayList<MovieForGridItem> movies){
        moviesForGrid=movies;
        mItemsNumber=moviesForGrid.size();
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        int LayoutIdForGridItem=R.layout.grid_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately=false;

        View view=inflater.inflate(LayoutIdForGridItem,parent,shouldAttachToParentImmediately);
        MovieViewHolder movieViewHolder=new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mItemsNumber;
    }
    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView movieThumbnail;
        public MovieViewHolder(View itemView) {
            super(itemView);
            movieThumbnail=(ImageView)itemView.findViewById(R.id.movie_thumbnail);
        }
        public void bind(int currentMovie){
            MovieForGridItem currentMovieForGridItem=moviesForGrid.get(currentMovie);
            movieThumbnail.setImageBitmap(currentMovieForGridItem.getPosterPath());
        }
    }
}
