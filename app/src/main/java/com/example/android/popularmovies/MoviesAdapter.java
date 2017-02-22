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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private ArrayList<Movie> moviesForGrid;
    private GridItemClickListener currentGridItemClickListener;
    private Context context;

    public MoviesAdapter(ArrayList<Movie> movies, GridItemClickListener gridItemClickListener) {
        moviesForGrid = movies;
        currentGridItemClickListener = gridItemClickListener;
    }

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

    public interface GridItemClickListener {
        void goToMovieDetails(int position);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (moviesForGrid != null) {
            return moviesForGrid.size();
        } else {
            return 0;
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieThumbnail;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.movie_thumbnail);
            itemView.setOnClickListener(this);
        }

        public void bind(int currentMovie) {
            Movie currentMovieForGridItem = moviesForGrid.get(currentMovie);
            String posterPath = currentMovieForGridItem.getPosterPath();
            String basicUrl = "https://image.tmdb.org/t/p";
            String fixedSizeForPoster = calculatePosterSize();
            String imageUrl = basicUrl + fixedSizeForPoster + posterPath;
            Picasso.with(movieThumbnail.getContext()).load(imageUrl).into(movieThumbnail);
        }
        private String calculatePosterSize() {
            //According with Android device metrics I came out with this ranges for device's density
            float density= context.getResources().getDisplayMetrics().density;
           return DesignUtils.calculatePosterSizeForGrid(density);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            currentGridItemClickListener.goToMovieDetails(position);

        }
    }

    public void setMoviesData(ArrayList<Movie> moviesData) {
        moviesForGrid = moviesData;
        notifyDataSetChanged();
    }


}
