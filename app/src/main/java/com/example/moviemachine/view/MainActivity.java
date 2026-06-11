package com.example.moviemachine.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviemachine.R;

/**
 * View Layer (Login Screen)
 * EN: Handles user login and input validation.
 * TR: Kullanıcı girişini ve veri doğrulamayı yönetir.
 * MVC: This is a View that delegates logic to Controller if needed, 
 * but simple validation is kept here for UI responsiveness.
 */
public class MainActivity extends AppCompatActivity {

    private EditText etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);

        // EN: Set up click listeners for login and skip actions.
        findViewById(R.id.btnLogin).setOnClickListener(v -> validateLogin());
        findViewById(R.id.btnSkip).setOnClickListener(v -> startApp(getString(R.string.guest_user)));
    }

    /**
     * EN: Validates the username input field.
     * TR: Kullanıcı adı alanının boş olup olmadığını kontrol eder (Input Validation).
     */
    private void validateLogin() {
        String name = etUsername.getText().toString().trim();
        if (name.isEmpty()) {
            etUsername.setError(getString(R.string.error_name_required));
        } else {
            startApp(name);
        }
    }

    private void startApp(String name) {
        Toast.makeText(this, getString(R.string.welcome_toast, name), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, OnboardingActivity.class));
    }
}
