package com.example.moviemachine.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviemachine.R;
import com.example.moviemachine.Model.Movie;
import java.util.List;

/**
 * Adapter Class (MVC - V)
 * EN: Handles the display of movie items in RecyclerViews across the app.
 * TR: Film öğelerinin RecyclerView içinde görüntülenmesini sağlar.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movies;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movies, OnItemClickListener listener) {
        this.context = context;
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() { return movies.size(); }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRating;
        ImageView imgPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textTitle);
            tvRating = itemView.findViewById(R.id.textRating);
            imgPoster = itemView.findViewById(R.id.imgPoster);
        }

        public void bind(Movie movie, OnItemClickListener listener) {
            tvTitle.setText(movie.getTitle());
            tvRating.setText(String.format("%.1f", movie.getVoteAverage()));
            
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgPoster);

            itemView.setOnClickListener(v -> listener.onItemClick(movie));
        }
    }
}
