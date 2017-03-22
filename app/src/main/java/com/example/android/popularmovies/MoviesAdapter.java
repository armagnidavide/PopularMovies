package com.example.android.popularmovies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.DesignUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * provides new Views for the RecyclerView when it's needed and to bind movies' data from the network to the views
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private ArrayList<Movie> moviesForGrid;
    private GridItemClickListener currentGridItemClickListener;
    private Context context;

    public MoviesAdapter(ArrayList<Movie> movies, GridItemClickListener gridItemClickListener) {
        moviesForGrid = movies;
        currentGridItemClickListener = gridItemClickListener;
    }

    /**
     * create a View with the item-layout then pass it to the movieViewHolder constructor and return a movieViewHolder
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int LayoutIdForGridItem = R.layout.grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(LayoutIdForGridItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    /**
     * Bind Data to View objects
     */
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    /**
     * return the number of items to display
     */
    @Override
    public int getItemCount() {
        if (moviesForGrid != null) {
            return moviesForGrid.size();
        } else {
            return 0;
        }
    }

    /**
     * return the movie's poster size to download the movie's poster image
     */
    private String calculatePosterSize() {
        float density = context.getResources().getDisplayMetrics().density;
        return DesignUtils.calculatePosterSizeForGrid(density);
    }

    /**
     * notify that the data is changed
     * LayoutManagers will be forced to fully rebind and relayout all visible views
     */
    public void setMoviesData(ArrayList<Movie> moviesData) {
        moviesForGrid = moviesData;
        notifyDataSetChanged();
    }

    /**
     * implement this interface to override the onclick method for the movieAdapter
     */
    public interface GridItemClickListener {
        void goToMovieDetails(int position);
    }

    /**
     * the ViewHolder contains a reference to the root view object for the item,and we use it
     * to cache the view objects represented in the layout to make it less costly to update the views with new data.
     * In this way findViewById is called just when a new item is created and not every time the views in an item are populated with data.
     * Implement View.OnClickListener.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieThumbnail;

        /**
         * MovieViewHolder's constructor.
         * Initialize the View objects,
         * attach an onClickListener to the MovieViewHolder created
         */
        public MovieViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.movie_thumbnail);
            itemView.setOnClickListener(this);
        }

        public void bind(int currentMovie) {
            Movie currentMovieForGridItem = moviesForGrid.get(currentMovie);
            String posterPath = currentMovieForGridItem.getPosterPath();
            String basicUrl = "https://image.tmdb.org/t/p";
            String imageUrl = basicUrl + calculatePosterSize() + posterPath;
            Picasso.with(movieThumbnail.getContext()).load(imageUrl).into(movieThumbnail);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            currentGridItemClickListener.goToMovieDetails(position);
        }
    }


}
