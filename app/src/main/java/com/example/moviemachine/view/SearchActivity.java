package com.example.moviemachine.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviemachine.BuildConfig;
import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import com.example.moviemachine.R;
import com.example.moviemachine.Utils.NetworkUtils;
import java.util.List;

/**
 * View Layer (Search Screen)
 * EN: Allows users to search for movies and TV shows via TMDB API.
 * TR: Kullanıcıların film ve dizi aramasına olanak tanır.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText etSearchQuery;
    private RecyclerView rvSearchResults;
    private ProgressBar pbSearchLoading;
    private MovieController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        controller = new MovieController();
    }

    private void initViews() {
        etSearchQuery = findViewById(R.id.etSearchQuery);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        pbSearchLoading = findViewById(R.id.pbSearchLoading);
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));

        findViewById(R.id.btnDoSearch).setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            if (!query.isEmpty()) performSearch(query);
            else etSearchQuery.setError(getString(R.string.please_enter_movie_name));
        });
    }

    /**
     * EN: Calls the controller to search for movies based on the query.
     * TR: Arama sorgusuna göre Controller'ı çağırarak sonuçları getirir.
     */
    private void performSearch(String query) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
            return;
        }

        pbSearchLoading.setVisibility(View.VISIBLE);
        controller.searchMovies(BuildConfig.TMDB_API_KEY, query, new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    pbSearchLoading.setVisibility(View.GONE);
                    if (movies.isEmpty()) Toast.makeText(SearchActivity.this, R.string.no_results_found, Toast.LENGTH_SHORT).show();
                    else rvSearchResults.setAdapter(new MovieAdapter(SearchActivity.this, movies, SearchActivity.this::openDetail));
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    pbSearchLoading.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                });
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
}
