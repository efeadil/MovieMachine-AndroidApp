package com.example.moviemachine.view;
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
            if (name.isEmpty()) {
                etUsername.setError(getString(R.string.error_name_required));
            } else {
                startNextActivity(name);
            }
        });

        btnSkip.setOnClickListener(v -> startNextActivity(getString(R.string.guest_user)));
    }

    private void startNextActivity(String name) {
        // 1. Kullaniciya karsilama mesaji goster
        Toast.makeText(this, getString(R.string.welcome_toast, name), Toast.LENGTH_SHORT).show();

        // 2. OnboardingActivity sayfasina gecisi baslat (Intent)
        Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);

        // 3. Kullanicinin girdigi ismi diger sayfaya tasi
        intent.putExtra("USER_NAME", name);

        // 4. Ikinci sayfayi ac
        startActivity(intent);
    }
}