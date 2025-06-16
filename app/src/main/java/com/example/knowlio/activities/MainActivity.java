package com.example.knowlio.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.knowlio.R;          //  ← שורה חדשה: זה הפתרון

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}