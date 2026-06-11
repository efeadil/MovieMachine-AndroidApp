package com.example.moviemachine.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviemachine.R;

/**
 * View Layer (Onboarding Screen)
 * EN: Collects user genre preferences and passes them to the main movie list.
 * TR: Kullanıcı tercihlerini (türleri) toplar ve film listesi ekranına iletir.
 */
public class OnboardingActivity extends AppCompatActivity {

    private CheckBox cbAction, cbComedy, cbSciFi, cbHorror, cbDrama, cbRomance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initViews();

        findViewById(R.id.btnSavePreferences).setOnClickListener(v -> continueToList());
    }

    private void initViews() {
        cbAction = findViewById(R.id.cbAction);
        cbComedy = findViewById(R.id.cbComedy);
        cbSciFi = findViewById(R.id.cbSciFi);
        cbHorror = findViewById(R.id.cbHorror);
        cbDrama = findViewById(R.id.cbDrama);
        cbRomance = findViewById(R.id.cbRomance);
    }

    /**
     * EN: Gathers selected genre IDs and navigates to MovieListActivity.
     * TR: Seçilen türlerin ID'lerini toplar ve MovieListActivity ekranına geçer.
     */
    private void continueToList() {
        StringBuilder ids = new StringBuilder();
        if (cbAction.isChecked()) ids.append("28,");
        if (cbComedy.isChecked()) ids.append("35,");
        if (cbSciFi.isChecked()) ids.append("878,");
        if (cbHorror.isChecked()) ids.append("27,");
        if (cbDrama.isChecked()) ids.append("18,");
        if (cbRomance.isChecked()) ids.append("10749,");

        Intent intent = new Intent(this, MovieListActivity.class);
        intent.putExtra("SELECTED_GENRES", ids.toString());
        startActivity(intent);
        finish();
    }
}
