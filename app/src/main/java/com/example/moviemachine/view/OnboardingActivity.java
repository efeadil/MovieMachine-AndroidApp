package com.example.moviemachine.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviemachine.R;

public class OnboardingActivity extends AppCompatActivity {

    private CheckBox cbAction, cbComedy, cbSciFi, cbHorror, cbDrama, cbRomance;
    private Button btnSavePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        cbAction = findViewById(R.id.cbAction);
        cbComedy = findViewById(R.id.cbComedy);
        cbSciFi = findViewById(R.id.cbSciFi);
        cbHorror = findViewById(R.id.cbHorror);
        cbDrama = findViewById(R.id.cbDrama);
        cbRomance = findViewById(R.id.cbRomance);
        btnSavePreferences = findViewById(R.id.btnSavePreferences);

        btnSavePreferences.setOnClickListener(v -> {
            // Seçimleri hafızaya kaydet
            SharedPreferences prefs = getSharedPreferences("MovieMachinePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("PREF_ACTION", cbAction.isChecked());
            editor.putBoolean("PREF_COMEDY", cbComedy.isChecked());
            editor.putBoolean("PREF_SCIFI", cbSciFi.isChecked());
            editor.putBoolean("PREF_HORROR", cbHorror.isChecked());
            editor.putBoolean("PREF_DRAMA", cbDrama.isChecked());
            editor.putBoolean("PREF_ROMANCE", cbRomance.isChecked());
            editor.apply();

            Toast.makeText(this, getString(R.string.preferences_saved), Toast.LENGTH_SHORT).show();

            // Ana ekrana git
            Intent intent = new Intent(OnboardingActivity.this, MovieListActivity.class);
            startActivity(intent);
            finish(); // Geri tuşuna basıldığında bu ekrana geri dönülmesini engelle
        });
    }
}