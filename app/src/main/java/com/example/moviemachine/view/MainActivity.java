package com.example.moviemachine.view;

/**
 * Bu sınıf, uygulamanın giriş (Login) ekranını yönetir.
 * Kullanıcıdan isim alır ve Onboarding ekranına yönlendirir.
 */
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moviemachine.R;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button btnLogin, btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnSkip = findViewById(R.id.btnSkip);

        btnLogin.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            // İsim alanı boş mu kontrol ediyoruz. Boşsa hata mesajı gösterilir.
            // Validation: Checking if name is empty before proceeding.
            if (name.isEmpty()) {
                etUsername.setError(getString(R.string.error_name_required));
            } else {
                startNextActivity(name);
            }
        });

        btnSkip.setOnClickListener(v -> startNextActivity(getString(R.string.guest_user)));
    }

    private void startNextActivity(String name) {
        // 1. Kullanıcıya hoş geldin mesajını strings.xml'den çekerek göster.
        // Localization: Showing a welcome toast with the user's name.
        Toast.makeText(this, getString(R.string.welcome_toast, name), Toast.LENGTH_SHORT).show();

        // 2. Bir sonraki ekran olan tür seçim sayfasına geçiş yapıyoruz.
        // Navigation: Moving to OnboardingActivity (Genre selection).
        Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);

        // 3. Kullanıcı adını 'Intent extra' olarak bir sonraki aktiviteye paslıyoruz.
        // Data Passing: Sending username to the next activity.
        intent.putExtra("USER_NAME", name);

        // 4. Yeni aktiviteyi (ekranı) başlat.
        startActivity(intent);
    }
}