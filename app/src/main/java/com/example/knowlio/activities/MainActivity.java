package com.example.knowlio.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knowlio.R;
import com.example.knowlio.data.models.DailyFact;
import com.example.knowlio.data.network.FactsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) מצביעים ל-TextView-ים מה-layout
        TextView tvContent = findViewById(R.id.tvDailyContent);
        TextView tvTitle   = findViewById(R.id.tvDateTitle);

// --- במקום הקוד הישן: Retrofit retrofit = new Retrofit.Builder()...
// 1. בונים Gson רגוע
        Gson gson = new GsonBuilder()
                .setLenient()        // מאפשר תווים עודפים (BOM, רווח)
                .create();

// 2. בונים Retrofit עם ה-Gson הזה
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/itaital/734a548a728191c298d71e60afcd0dd2/")
                .addConverterFactory(GsonConverterFactory.create(gson))   // <-- משתמשים בגירסה lenient
                .build();

        FactsApi api = retrofit.create(FactsApi.class);

        // 3) קריאה אסינכרונית ל-JSON
        api.getFact().enqueue(new Callback<DailyFact>() {
            @Override
            public void onResponse(Call<DailyFact> call, Response<DailyFact> res) {
                if (res.isSuccessful() && res.body() != null) {
                    DailyFact fact = res.body();

                    // כותרת עם תאריך היום
                    tvTitle.setText(getString(R.string.daily_fact_title) + " – " + fact.date);

                    // בחירת שפה: עברית או אנגלית לפי העדפת המשתמש
                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());
                    boolean isHeb = lang.equals("he");
                    tvContent.setText(isHeb ? fact.he : fact.en);
                } else {
                    tvContent.setText("⚠️ ‎Error: empty body");
                }
            }

            @Override
            public void onFailure(Call<DailyFact> call, Throwable t) {
                // מדפיסים את שם החריגה והודעתה כדי שנדע מה קרה
                String msg = t.getClass().getSimpleName() + " : " + t.getMessage();
                TextView tvContent = findViewById(R.id.tvDailyContent);
                tvContent.setText("⚠️ " + msg);
                t.printStackTrace();   // גם בלוגקט
            }
        });
    }
}
