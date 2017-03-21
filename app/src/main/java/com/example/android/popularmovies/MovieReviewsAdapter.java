package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.MovieReview;

import java.util.ArrayList;


public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {
    private ArrayList<MovieReview> reviews;
    private Context context;
    private TextView txtVwReviewAuthor;
    private TextView txtVwReviewContent;


    public MovieReviewsAdapter (ArrayList<MovieReview> reviews){
        this.reviews=reviews;
        Log.e("onbindviewholder","2222222");

    }


    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("onbindviewholder","333333333");
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
        MovieReview currentMovieReview=reviews.get(position);
        txtVwReviewAuthor.setText(currentMovieReview.getAuthor());
        txtVwReviewContent.setText(currentMovieReview.getContent());
        Log.e("onbindviewholder",currentMovieReview.getAuthor()+currentMovieReview.getContent());


    }

    @Override
    public int getItemCount() {
        if (reviews != null) {
            Log.e("onbindviewholder","444444");
            Log.e("onbindviewholder",""+reviews.size());

            return reviews.size();
        } else {
            Log.e("onbindviewholder","11111");
            return 0;
        }
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder  {

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            txtVwReviewAuthor=(TextView)itemView.findViewById(R.id.txtVw_review_author);
            txtVwReviewContent=(TextView)itemView.findViewById(R.id.txtVw_review_content);
        }
    }
    public void setMoviesData(ArrayList<MovieReview> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}
