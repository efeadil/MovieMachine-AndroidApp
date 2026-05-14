package com.example.moviemachine.view;

/**
 * Bu sınıf, kullanıcının sevdiği film türlerini seçtiği ekranı yönetir.
 * Seçilen tercihler SharedPreferences'a kaydedilir.
 */
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
            // Kullanıcının seçtiği türleri (preferences) telefonun hafızasına (SharedPreferences) kaydediyoruz.
            // Persistence: Storing user genre preferences locally.
            SharedPreferences prefs = getSharedPreferences("MovieMachinePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("PREF_ACTION", cbAction.isChecked());
            editor.putBoolean("PREF_COMEDY", cbComedy.isChecked());
            editor.putBoolean("PREF_SCIFI", cbSciFi.isChecked());
            editor.putBoolean("PREF_HORROR", cbHorror.isChecked());
            editor.putBoolean("PREF_DRAMA", cbDrama.isChecked());
            editor.putBoolean("PREF_ROMANCE", cbRomance.isChecked());
            editor.apply();

            // Başarıyla kaydedildiğine dair kullanıcıyı bilgilendiriyoruz.
            Toast.makeText(this, getString(R.string.preferences_saved), Toast.LENGTH_SHORT).show();

            // Kayıt sonrası asıl içerik sayfasına (Film Listesi) yönlendiriyoruz.
            // Flow: After saving, redirecting to the main Movie List activity.
            Intent intent = new Intent(OnboardingActivity.this, MovieListActivity.class);
            startActivity(intent);
            
            // Finish() komutu sayesinde bu ekran "back stack"ten silinir. Geri basınca login ekranına dönülmez.
            // Stack Management: Removing this activity from stack so user can't return via back button.
            finish();
        });
    }
}