package com.example.moviemachine.view;

/**
 * Bu sınıf, uygulama içindeki bir WebView aracılığıyla film videolarını oynatır.
 * IMDb veya diğer video bağlantılarını tam ekran desteğiyle sunar.
 */
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviemachine.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private WebView webViewPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        webViewPlayer = findViewById(R.id.webViewPlayer);

        // Önceki sayfadan (Detay sayfasından) gelen linki al
        String videoUrl = getIntent().getStringExtra("VIDEO_URL");

        // Web sitesinin video oynatabilmesi için gerekli teknik ayarlar.
        // Technical settings to ensure the WebView can handle complex video players.
        WebSettings webSettings = webViewPlayer.getSettings();
        webSettings.setJavaScriptEnabled(true); // Kesinlikle açık olmalı. JavaScript is mandatory for players.
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false); // Videonun otomatik başlamasına izin ver.

        // Kullanıcıyı dışarı atmayıp uygulamanın içinde tutan ayarlar.
        // Keeping the user within the app context.
        webViewPlayer.setWebViewClient(new WebViewClient());
        webViewPlayer.setWebChromeClient(new WebChromeClient()); // Tam ekran video desteği için şart. Important for Fullscreen.

        // Linki WebView içinde yükle
        if (videoUrl != null) {
            webViewPlayer.loadUrl(videoUrl);
        }
    }

    // Kullanıcı geri tuşuna basarsa web sitesinde mi geri gitsin, sayfayı mı kapatsın?
    @Override
    public void onBackPressed() {
        if (webViewPlayer.canGoBack()) {
            webViewPlayer.goBack();
        } else {
            super.onBackPressed();
        }
    }
}