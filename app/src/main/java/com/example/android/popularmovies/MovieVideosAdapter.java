package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosViewHolder> {
    Button btnWatchTrailer;
    ArrayList<String> videosIds;
    private Context context;
    private goToYoutubeClickListener currentGoToYoutubeClickListener;

    public MovieVideosAdapter (ArrayList<String> videosIds,goToYoutubeClickListener goToYoutubeClickListener){
        this.videosIds=videosIds;
        currentGoToYoutubeClickListener=goToYoutubeClickListener;}
    @Override
    public MovieVideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int LayoutIdForVideoId = R.layout.single_video_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(LayoutIdForVideoId, parent, shouldAttachToParentImmediately);
        MovieVideosViewHolder movieVideosViewHolder = new MovieVideosViewHolder(view);
        return movieVideosViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieVideosViewHolder holder, int position) {
        String currentVideoId=videosIds.get(position);
        btnWatchTrailer.setText(currentVideoId);
    }

    @Override
    public int getItemCount() {
        if(videosIds!=null){
            return videosIds.size();
        }else return 0;
    }

    public class MovieVideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public MovieVideosViewHolder(View itemView) {
            super(itemView);
            btnWatchTrailer=(Button)itemView.findViewById(R.id.btn_watch_trailer);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Log.e("bbbbbb","dajeeee");
            int position=getAdapterPosition();
            String videoId=videosIds.get(position);
            currentGoToYoutubeClickListener.goToYoutube(videoId);
        }
    }
    public  void setMovieData(ArrayList<String> videosIds){
        this.videosIds=videosIds;
    }

    public interface goToYoutubeClickListener{
        void goToYoutube(String videoId);}
}
