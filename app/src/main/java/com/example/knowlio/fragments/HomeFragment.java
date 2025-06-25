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
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;

import java.time.LocalDate;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private LinearLayout quotesLayout;
    private LinearLayout knowledgeLayout;
    private LinearLayout peopleLayout;
    private View cardQuote, cardKnowledge, cardPeople;
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
        cardQuote        = v.findViewById(R.id.cardQuote);
        cardKnowledge    = v.findViewById(R.id.cardKnowledge);
        cardPeople       = v.findViewById(R.id.cardPeople);
        tvEmpty          = v.findViewById(R.id.tvEmpty);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());

        FactsRepository repo = new FactsRepository(requireContext());
        repo.getBundleLive(LocalDate.now())
                .observe(getViewLifecycleOwner(),
                        bundle -> showBundle(bundle, lang));
    }

    private void showBundle(@Nullable DailyQuoteBundle bundle, String lang) {
        if (bundle == null) {
            tvEmpty.setVisibility(View.VISIBLE);
            cardQuote.setVisibility(View.GONE);
            cardKnowledge.setVisibility(View.GONE);
            cardPeople.setVisibility(View.GONE);
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        LanguageContent content = bundle.languages.get(lang);
        if (content == null) content = bundle.languages.get("en");

        /* ---------- Quotes ---------- */
        quotesLayout.removeAllViews();
        if (content != null && content.quoteOfTheDay != null &&
                !content.quoteOfTheDay.isEmpty()) {

            for (String q : content.quoteOfTheDay) {
                TextView t = new TextView(requireContext());
                t.setText("\u275D " + q + " \u275E");          // “ quote ”
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                quotesLayout.addView(t);
            }
            cardQuote.setVisibility(View.VISIBLE);
        } else {
            cardQuote.setVisibility(View.GONE);
        }

        /* ---------- Interesting Knowledge ---------- */
        knowledgeLayout.removeAllViews();
        if (content != null && content.interestingKnowledge != null &&
                !content.interestingKnowledge.isEmpty()) {

            for (String fact : content.interestingKnowledge) {
                TextView t = new TextView(requireContext());
                t.setText("• " + fact);
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                knowledgeLayout.addView(t);
            }
            cardKnowledge.setVisibility(View.VISIBLE);
        } else {
            cardKnowledge.setVisibility(View.GONE);
        }

        /* ---------- Who Were They ---------- */
        peopleLayout.removeAllViews();
        if (content != null && content.whoWereThey != null &&
                !content.whoWereThey.isEmpty()) {

            for (String bio : content.whoWereThey) {
                TextView t = new TextView(requireContext());
                t.setText("• " + bio);
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                peopleLayout.addView(t);
            }
            cardPeople.setVisibility(View.VISIBLE);
        } else {
            cardPeople.setVisibility(View.GONE);
        }
    }
}
