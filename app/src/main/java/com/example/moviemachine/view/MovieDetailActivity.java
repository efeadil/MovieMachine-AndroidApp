package com.example.moviemachine.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviemachine.BuildConfig;
import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.CastMember;
import com.example.moviemachine.R;
import java.util.List;

/**
 * View Layer (Movie Details Screen)
 * EN: Shows detailed information about a movie, including cast and trailers.
 * TR: Film detaylarını, oyuncu kadrosunu ve fragmanları gösterir.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imgDetailPoster;
    private TextView tvDetailTitle, tvDetailRating, tvDetailOverview, tvDetailReview;
    private Button btnWatch, btnOpenPrime;
    private RecyclerView rvCast;
    private MovieController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initViews();
        controller = new MovieController();
        displayBasicInfo();
        loadExtraDetails();
    }

    private void initViews() {
        imgDetailPoster = findViewById(R.id.imgDetailPoster);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailRating = findViewById(R.id.tvDetailRating);
        tvDetailOverview = findViewById(R.id.tvDetailOverview);
        tvDetailReview = findViewById(R.id.tvDetailReview);
        btnWatch = findViewById(R.id.btnWatch);
        btnOpenPrime = findViewById(R.id.btnOpenPrime);
        rvCast = findViewById(R.id.rvCast);
        rvCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void displayBasicInfo() {
        tvDetailTitle.setText(getIntent().getStringExtra("MOVIE_TITLE"));
        tvDetailOverview.setText(getIntent().getStringExtra("MOVIE_OVERVIEW"));
        tvDetailRating.setText(getString(R.string.rating_format, getIntent().getDoubleExtra("MOVIE_RATING", 0.0)));

        String posterPath = getIntent().getStringExtra("MOVIE_POSTER");
        if (posterPath != null) {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500" + posterPath).into(imgDetailPoster);
        }
    }

    /**
     * EN: Fetches cast and video information from the Controller.
     * TR: Oyuncu ve video bilgilerini Controller üzerinden çeker.
     */
    private void loadExtraDetails() {
        int movieId = getIntent().getIntExtra("MOVIE_ID", -1);
        String mediaType = getIntent().getStringExtra("MEDIA_TYPE");
        if (movieId == -1) return;

        controller.fetchMovieDetailsAndCast(movieId, mediaType, BuildConfig.TMDB_API_KEY, new MovieController.MovieDetailCallback() {
            @Override
            public void onSuccess(String videoUrl, String primeUrl, List<CastMember> cast, String review) {
                runOnUiThread(() -> {
                    rvCast.setAdapter(new CastAdapter(MovieDetailActivity.this, cast));
                    tvDetailReview.setText(review);
                    setupButtons(videoUrl, primeUrl);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvDetailReview.setText("Error loading reviews.");
                    Toast.makeText(MovieDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupButtons(String videoUrl, String primeUrl) {
        btnWatch.setOnClickListener(v -> openUrl(videoUrl, "Could not open trailer."));
        
        if (primeUrl != null && !primeUrl.isEmpty()) {
            btnOpenPrime.setVisibility(View.VISIBLE);
            btnOpenPrime.setOnClickListener(v -> openUrl(primeUrl, "Could not open Amazon Prime."));
        }
    }

    private void openUrl(String url, String errorMessage) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, R.string.error_no_stream_link, Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse(url);
        if (!isAllowedExternalUrl(uri)) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAllowedExternalUrl(Uri uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (!"https".equalsIgnoreCase(scheme) || host == null) {
            return false;
        }
        return "www.youtube.com".equalsIgnoreCase(host)
                || "youtube.com".equalsIgnoreCase(host)
                || "youtu.be".equalsIgnoreCase(host)
                || "www.primevideo.com".equalsIgnoreCase(host)
                || "primevideo.com".equalsIgnoreCase(host);
    }
}
