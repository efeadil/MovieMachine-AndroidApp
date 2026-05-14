package com.example.moviemachine.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import com.example.moviemachine.R;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchQuery;
    private Button btnDoSearch;
    private RecyclerView rvSearchResults;
    private ProgressBar pbSearchLoading;
    private MovieController controller;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnDoSearch = findViewById(R.id.btnDoSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        pbSearchLoading = findViewById(R.id.pbSearchLoading);

        // Sonuçları şık bir 2'li ızgara (Grid) şeklinde gösterelim
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        controller = new MovieController();

        btnDoSearch.setOnClickListener(v -> {
            String query = etSearchQuery.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            } else {
                Toast.makeText(this, getString(R.string.please_enter_movie_name), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        pbSearchLoading.setVisibility(View.VISIBLE);
        String apiKey = com.example.moviemachine.BuildConfig.TMDB_API_KEY;

        controller.searchMovies(apiKey, query, new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    pbSearchLoading.setVisibility(View.GONE);
                    if (movies.isEmpty()) {
                        Toast.makeText(SearchActivity.this, getString(R.string.no_results_found), Toast.LENGTH_SHORT).show();
                    } else {
                        // Tıklanınca doğrudan detay sayfasına gidiyoruz (Aynı ana sayfadaki gibi!)
                        adapter = new MovieAdapter(SearchActivity.this, movies, movie -> {
                            Intent intent = new Intent(SearchActivity.this, MovieDetailActivity.class);
                            intent.putExtra("MOVIE_ID", movie.getId());
                            intent.putExtra("MOVIE_TITLE", movie.getTitle());
                            intent.putExtra("MOVIE_OVERVIEW", movie.getOverview());
                            intent.putExtra("MOVIE_POSTER", movie.getPosterPath());
                            intent.putExtra("MOVIE_RATING", movie.getVoteAverage());
                            intent.putExtra("MEDIA_TYPE", movie.getMediaType()); // Bunu ekle
                            startActivity(intent);
                        });
                        rvSearchResults.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    pbSearchLoading.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}