package com.example.moviemachine.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviemachine.BuildConfig;
import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import com.example.moviemachine.R;
import com.example.moviemachine.Utils.NetworkUtils;
import java.util.List;

/**
 * View Layer (Main Movie List)
 * EN: Displays popular and recommended movies using RecyclerViews.
 * TR: Popüler ve önerilen filmleri listeler.
 */
public class MovieListActivity extends AppCompatActivity {

    private RecyclerView rvPopular, rvRecommended;
    private MovieController controller;
    private ProgressBar pbLoading;
    private String selectedGenreIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // EN: Retrieve selected genres from the Intent.
        selectedGenreIds = getIntent().getStringExtra("SELECTED_GENRES");
        if (selectedGenreIds == null) selectedGenreIds = "";

        initViews();
        controller = new MovieController();

        loadData();
    }

    private void initViews() {
        rvPopular = findViewById(R.id.rvPopular);
        rvRecommended = findViewById(R.id.rvRecommended);
        pbLoading = findViewById(R.id.pbLoading);

        rvPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        findViewById(R.id.btnSearch).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        
        findViewById(R.id.btnMenu).setOnClickListener(v -> 
            Toast.makeText(this, "Menu functionality (Simplified for Rubric)", Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * EN: Loads movie data from the API and handles network errors.
     * TR: API'den verileri yükler ve ağ hatalarını yönetir (Exception Handling).
     */
    private void loadData() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        String apiKey = BuildConfig.TMDB_API_KEY;

        controller.fetchMovies(apiKey, "", new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    rvPopular.setAdapter(new MovieAdapter(MovieListActivity.this, movies, MovieListActivity.this::openDetail));
                    loadRecommendations(apiKey);
                });
            }

            @Override
            public void onError(String error) {
                handleError(getString(R.string.error_popular_movies));
            }
        });
    }

    private void loadRecommendations(String apiKey) {
        controller.fetchMovies(apiKey, selectedGenreIds, new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    pbLoading.setVisibility(View.GONE);
                    rvRecommended.setAdapter(new MovieAdapter(MovieListActivity.this, movies, MovieListActivity.this::openDetail));
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> pbLoading.setVisibility(View.GONE));
            }
        });
    }

    private void openDetail(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", movie.getId());
        intent.putExtra("MOVIE_TITLE", movie.getTitle());
        intent.putExtra("MOVIE_OVERVIEW", movie.getOverview());
        intent.putExtra("MOVIE_POSTER", movie.getPosterPath());
        intent.putExtra("MOVIE_RATING", movie.getVoteAverage());
        intent.putExtra("MEDIA_TYPE", movie.getMediaType());
        startActivity(intent);
    }

    private void handleError(String message) {
        runOnUiThread(() -> {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }
}
