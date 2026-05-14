package com.example.moviemachine.view;

/**
 * Bu sınıf, film listelerini (RecyclerView) doldurmak için kullanılır.
 * Her bir film kartının tasarımını ve tıklama olaylarını yönetir.
 */
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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Context context;
    private OnItemClickListener listener;

    // Tiklama islemleri icin Arayuz (Interface)
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
        holder.tvTitle.setText(movie.getTitle());
        holder.tvRating.setText(movie.getVoteAverage() + "%");

        // Glide kütüphanesi ile internetteki afişi (poster) ImageView içine yüklüyoruz.
        // Efficient Image Loading: Glide handles caching and memory management.
        String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imgPoster);

        // Karta tıklandığında listedeki o nesneye ait detay sayfasına gitmek için tetikleyici.
        // Click Listener: Triggering the callback with the selected movie object.
        holder.itemView.setOnClickListener(v -> listener.onItemClick(movie));
    }

    @Override
    public int getItemCount() { return movies.size(); }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRating;
        ImageView imgPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textTitle);
            tvRating = itemView.findViewById(R.id.textRating);
            imgPoster = itemView.findViewById(R.id.imgPoster);
        }
    }
}