package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;


public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosViewHolder> {
    TextView txtVwTrailerTitle;
    ArrayList<String> videosIds;
    private Context context;
    private goToYoutubeClickListener currentGoToYoutubeClickListener;

    public MovieVideosAdapter(ArrayList<String> videosIds, goToYoutubeClickListener goToYoutubeClickListener) {
        this.videosIds = videosIds;
        currentGoToYoutubeClickListener = goToYoutubeClickListener;
    }

    @Override
    public MovieVideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int LayoutIdForVideoId = R.layout.single_video_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(LayoutIdForVideoId, parent, shouldAttachToParentImmediately);
        return new MovieVideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideosViewHolder holder, int position) {
        txtVwTrailerTitle.setText("Trailer N°"+(position+1));
    }

    @Override
    public int getItemCount() {
        if (videosIds != null) {
            return videosIds.size();
        } else return 0;
    }

    public void setMovieData(ArrayList<String> videosIds) {
        this.videosIds = videosIds;
    }

    public interface goToYoutubeClickListener {
        void goToYoutube(String videoId);
    }

    public class MovieVideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MovieVideosViewHolder(View itemView) {
            super(itemView);
            txtVwTrailerTitle = (TextView) itemView.findViewById(R.id.txtVw_trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(NetworkUtils.checkNetworkConnection(context)){
            int position = getAdapterPosition();
            String videoId = videosIds.get(position);
            currentGoToYoutubeClickListener.goToYoutube(videoId);}
            else{
                Toast.makeText(context,"No connection,check your network state and try again",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
