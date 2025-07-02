package com.example.knowlio.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.knowlio.R;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.LanguageContent;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private LinearLayout quotesLayout, knowledgeLayout, peopleLayout;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        quotesLayout     = v.findViewById(R.id.layoutQuotes);
        knowledgeLayout  = v.findViewById(R.id.layoutKnowledge);
        peopleLayout     = v.findViewById(R.id.layoutPeople);
        tvEmpty          = v.findViewById(R.id.tvEmpty);

        return v;
    }

    @Override public void onResume() {
        super.onResume();
        loadData();
    }

    /** Loads today’s bundle from SharedPreferences ➜ displays it. */
    private void loadData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());

        FactsRepository repo = new FactsRepository(requireContext());
        LanguageContent c = repo.getTodayBundle(lang);

        if (c == null) {
            tvEmpty.setVisibility(View.VISIBLE);
            quotesLayout.removeAllViews();
            knowledgeLayout.removeAllViews();
            peopleLayout.removeAllViews();
            return;
        }
        tvEmpty.setVisibility(View.GONE);

        /* ── Quote(s) ───────────────────────── */
        quotesLayout.removeAllViews();
        if (c.quoteOfTheDay != null && !c.quoteOfTheDay.isEmpty()) {
            for (String q : c.quoteOfTheDay) {
                TextView t = new TextView(requireContext());
                t.setText("\u275D " + q + " \u275E");
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0,0,0,12);
                quotesLayout.addView(t);
            }
        }

        /* ── Interesting knowledge (Strings) ── */
        knowledgeLayout.removeAllViews();
        if (c.interestingKnowledge != null && !c.interestingKnowledge.isEmpty()) {
            for (String k : c.interestingKnowledge) {
                TextView t = new TextView(requireContext());
                t.setText("• " + k);
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
                t.setPadding(0,0,0,12);
                knowledgeLayout.addView(t);
            }
        }

        /* ── Who were they (Strings) ────────── */
        peopleLayout.removeAllViews();
        if (c.whoWereThey != null && !c.whoWereThey.isEmpty()) {
            for (String p : c.whoWereThey) {
                TextView t = new TextView(requireContext());
                t.setText("• " + p);
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0,0,0,12);
                peopleLayout.addView(t);
            }
        }
    }
}
