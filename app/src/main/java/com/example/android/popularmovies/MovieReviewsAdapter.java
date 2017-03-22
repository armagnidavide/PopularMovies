package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/*
This is the adapter for the Reviews in DetailsActivity.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {
    private ArrayList<MovieReview> reviews;
    private Context context;
    private TextView txtVwReviewAuthor;
    private TextView txtVwReviewContent;

    /**
     *
     * @param reviews Arraylist containing MovieReview instances, that are used to bind data to MovieReviewViewHolders
     */
    public MovieReviewsAdapter(ArrayList<MovieReview> reviews) {
        this.reviews = reviews;
    }


    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int LayoutIdForReview = R.layout.single_review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(LayoutIdForReview, parent, shouldAttachToParentImmediately);
        MovieReviewViewHolder movieReviewViewHolder = new MovieReviewViewHolder(view);
        return movieReviewViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        MovieReview currentMovieReview = reviews.get(position);
        txtVwReviewAuthor.setText(currentMovieReview.getAuthor());
        txtVwReviewContent.setText(currentMovieReview.getContent());
    }

    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        } else {
            return 0;
        }
    }

    public void setMoviesData(ArrayList<MovieReview> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            txtVwReviewAuthor = (TextView) itemView.findViewById(R.id.txtVw_review_author);
            txtVwReviewContent = (TextView) itemView.findViewById(R.id.txtVw_review_content);
        }
    }
}
