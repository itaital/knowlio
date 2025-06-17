package com.example.knowlio.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.knowlio.R;

public class OnBoardLanguageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains("pref_lang")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_on_board_language);

        Button btnEnglish = findViewById(R.id.btnEnglish);
        Button btnHebrew = findViewById(R.id.btnHebrew);

        btnEnglish.setOnClickListener(v -> saveLangAndStart("en"));
        btnHebrew.setOnClickListener(v -> saveLangAndStart("he"));
    }

    private void saveLangAndStart(String lang) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("pref_lang", lang).apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
