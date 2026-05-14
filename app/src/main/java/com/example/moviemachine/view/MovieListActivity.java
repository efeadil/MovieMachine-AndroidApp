package com.example.moviemachine.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.Movie;
import com.example.moviemachine.R;
import java.util.List;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

public class MovieListActivity extends AppCompatActivity {

    private RecyclerView rvPopular, rvRecommended;
    private MovieAdapter adapterPopular, adapterRecommended;
    private MovieController controller;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);


        // Arayüz elemanlarını bağla
        rvPopular = findViewById(R.id.rvPopular);
        rvRecommended = findViewById(R.id.rvRecommended);
        pbLoading = findViewById(R.id.pbLoading);

        // Listeleri yatay (Horizontal) kaydırılabilir yapalım
        rvPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Arama butonu
        android.widget.TextView btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MovieListActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Yan Menü (Navigation Drawer) Ayarları
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        android.widget.TextView btnMenu = findViewById(R.id.btnMenu);

        // Menü butonuna basınca soldan (START) çekmeceyi aç
        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        controller = new MovieController();

        // Filmleri çekmeye başla
        loadAllMovies();


    }

    private void loadAllMovies() {
        pbLoading.setVisibility(View.VISIBLE);
        String apiKey = com.example.moviemachine.BuildConfig.TMDB_API_KEY;

        // 1. AŞAMA: POPÜLER FİLMLERİ GETİR (Tür filtresi yok)
        controller.fetchMovies(apiKey, "", new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    adapterPopular = new MovieAdapter(MovieListActivity.this, movies, movie -> openDetailActivity(movie));
                    rvPopular.setAdapter(adapterPopular);

                    // 2. AŞAMA: Popülerler gelince hemen ardından ÖNERİLENLERİ getir
                    loadRecommendedMovies(apiKey);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(MovieListActivity.this, getString(R.string.error_popular_movies), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadRecommendedMovies(String apiKey) {
        SharedPreferences prefs = getSharedPreferences("MovieMachinePrefs", MODE_PRIVATE);
        StringBuilder genreIds = new StringBuilder();
        
        if (prefs.getBoolean("PREF_ACTION", false)) genreIds.append("28,");
        if (prefs.getBoolean("PREF_COMEDY", false)) genreIds.append("35,");
        if (prefs.getBoolean("PREF_SCIFI", false)) genreIds.append("878,");
        if (prefs.getBoolean("PREF_HORROR", false)) genreIds.append("27,");
        if (prefs.getBoolean("PREF_DRAMA", false)) genreIds.append("18,");
        if (prefs.getBoolean("PREF_ROMANCE", false)) genreIds.append("10749,");

        controller.fetchMovies(apiKey, genreIds.toString(), new MovieController.MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    pbLoading.setVisibility(View.GONE);
                    adapterRecommended = new MovieAdapter(MovieListActivity.this, movies, movie -> openDetailActivity(movie));
                    rvRecommended.setAdapter(adapterRecommended);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> pbLoading.setVisibility(View.GONE));
            }
        });
    }

    // Tıklanan filmi detay sayfasına gönderen ortak metod
    private void openDetailActivity(Movie movie) {
        Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", movie.getId());
        intent.putExtra("MOVIE_TITLE", movie.getTitle());
        intent.putExtra("MOVIE_OVERVIEW", movie.getOverview());
        intent.putExtra("MOVIE_POSTER", movie.getPosterPath());
        intent.putExtra("MOVIE_RATING", movie.getVoteAverage());
        intent.putExtra("MEDIA_TYPE", "movie"); // Şimdilik hep "movie"
        startActivity(intent);
    }
}