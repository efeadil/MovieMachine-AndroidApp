package com.example.moviemachine.view;

/**
 * Bu sınıf, seçilen bir filmin veya dizinin detaylarını gösterir.
 * Oyuncu kadrosunu listeler ve izleme bağlantısını (IMDb) açar.
 */
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviemachine.Controller.MovieController;
import com.example.moviemachine.Model.CastMember;
import com.example.moviemachine.R;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imgDetailPoster;
    private TextView tvDetailTitle, tvDetailRating, tvDetailOverview;
    private Button btnWatch;
    private RecyclerView rvCast;
    private MovieController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Arayüz elemanlarını bağla
        imgDetailPoster = findViewById(R.id.imgDetailPoster);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailRating = findViewById(R.id.tvDetailRating);
        tvDetailOverview = findViewById(R.id.tvDetailOverview);
        btnWatch = findViewById(R.id.btnWatch);
        rvCast = findViewById(R.id.rvCast);

        controller = new MovieController();

        // 1. Intent ile gelen temel verileri al. 
        // Bu veriler bir önceki sayfadan hızlıca aktarılır.
        int movieId = getIntent().getIntExtra("MOVIE_ID", -1);
        String title = getIntent().getStringExtra("MOVIE_TITLE");
        String overview = getIntent().getStringExtra("MOVIE_OVERVIEW");
        String posterPath = getIntent().getStringExtra("MOVIE_POSTER");
        double rating = getIntent().getDoubleExtra("MOVIE_RATING", 0.0);

        // ÖNEMLİ: Tıklanan öğenin türünü (movie veya tv) alıyoruz.
        // Bu bilgi, doğru API uç noktasına (endpoint) gitmek için kritiktir.
        // IMPORTANT: Determining if it's a movie or a TV show to hit the correct API endpoint.
        String mediaType = getIntent().getStringExtra("MEDIA_TYPE");
        if (mediaType == null) {
            mediaType = "movie"; // Eğer boş gelirse varsayılan olarak film sayıyoruz
        }

        // Temel verileri ekrana yazdır
        tvDetailTitle.setText(title);
        tvDetailOverview.setText(overview);
        tvDetailRating.setText(getString(R.string.rating_format, rating));

        if (posterPath != null && !posterPath.equals("null")) {
            Glide.with(this).load("https://image.tmdb.org/t/p/w500" + posterPath).into(imgDetailPoster);
        }

        // 2. Film/Dizi ID'si varsa Gerçek Oyuncuları ve Doğru IMDb Linkini çek.
        // API'den detaylı bilgi almak için asenkron (arka plan) çağrı yapıyoruz.
        if (movieId != -1) {
            String apiKey = com.example.moviemachine.BuildConfig.TMDB_API_KEY;

            // Media type bilgisi sayesinde dizi ise oyuncuları ve IMDb ID'sini farklı yerden alıyoruz.
            controller.fetchMovieDetailsAndCast(movieId, mediaType, apiKey, new MovieController.MovieDetailCallback() {
                @Override
                public void onSuccess(String imdbId, List<CastMember> cast) {
                    runOnUiThread(() -> {
                        // Gerçek Oyuncuları Yatay Listeye Bas
                        rvCast.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        CastAdapter castAdapter = new CastAdapter(MovieDetailActivity.this, cast);
                        rvCast.setAdapter(castAdapter);

                        // İzle Butonuna tıklandığında video oynatıcıyı başlatıyoruz.
                        // Playback Logic: Constructing the streaming URL and launching our custom black-themed player.
                        btnWatch.setOnClickListener(v -> {
                            if (imdbId != null && !imdbId.isEmpty()) {
                                // Bulunan gerçek IMDb ID'si ile izleme linkini oluştur.
                                String videoUrl = "https://www.playimdb.com/title/" + imdbId + "/";

                                // Uygulamanın içindeki simsiyah video oynatıcı sayfasına git
                                Intent intent = new Intent(MovieDetailActivity.this, VideoPlayerActivity.class);
                                intent.putExtra("VIDEO_URL", videoUrl);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MovieDetailActivity.this, getString(R.string.error_no_stream_link), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(MovieDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}