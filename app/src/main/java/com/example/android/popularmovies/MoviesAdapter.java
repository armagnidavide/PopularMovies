package com.example.android.popularmovies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private ArrayList<MovieForGridItem> moviesForGrid;
    private int mItemsNumber;
    public MoviesAdapter(ArrayList<MovieForGridItem> movies){
        moviesForGrid=movies;
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
        return moviesForGrid.size();
    }
    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView movieThumbnail;
        public MovieViewHolder(View itemView) {
            super(itemView);
            movieThumbnail=(ImageView)itemView.findViewById(R.id.movie_thumbnail);
        }
        public void bind(int currentMovie){
            MovieForGridItem currentMovieForGridItem=moviesForGrid.get(currentMovie);
            String posterPath=currentMovieForGridItem.getPosterPath();
            String basicUrl="https://image.tmdb.org/t/p";
            String fixedSizeForPoster="/w150";
            String imageUrl=basicUrl+fixedSizeForPoster+posterPath;
            Picasso.with(movieThumbnail.getContext()).load(imageUrl).into(movieThumbnail);
        }
    }
    public void setMoviesData(ArrayList<MovieForGridItem> moviesData) {
        moviesForGrid = moviesData;
        notifyDataSetChanged();
    }
}
