package com.example.knowlio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private AutoCompleteTextView etDate;
    private LinearLayout quotesLayout, knowledgeLayout, peopleLayout;
    private View cardQuote, cardKnowledge, cardPeople;
    private FactsRepository repo;
    private String lang;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        etDate           = v.findViewById(R.id.etDate);
        quotesLayout     = v.findViewById(R.id.layoutQuotesHistory);
        knowledgeLayout  = v.findViewById(R.id.layoutKnowledgeHistory);
        peopleLayout     = v.findViewById(R.id.layoutPeopleHistory);
        cardQuote        = v.findViewById(R.id.cardQuote);
        cardKnowledge    = v.findViewById(R.id.cardKnowledge);
        cardPeople       = v.findViewById(R.id.cardPeople);

        repo = new FactsRepository(requireContext());
        lang = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("pref_lang", Locale.getDefault().getLanguage());

        setupDropdown();
        return v;
    }

    /* Populates the dropdown with all saved dates */
    private void setupDropdown() {
        LocalDate[] dates = repo.listDates();
        String[] arr = new String[dates.length];
        for (int i = 0; i < dates.length; i++) arr[i] = dates[i].toString();

        ArrayAdapter<String> adp = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, arr);

        etDate.setAdapter(adp);
        etDate.setOnItemClickListener((p, v, pos, id) ->
                showBundle(LocalDate.parse(adp.getItem(pos))));
    }

    /* Shows a bundle for the selected date */
    private void showBundle(LocalDate date) {
        DailyQuoteBundle b = repo.getBundle(date);
        if (b == null) {
            Snackbar.make(etDate, R.string.no_data, Snackbar.LENGTH_LONG).show();
            cardQuote.setVisibility(View.GONE);
            cardKnowledge.setVisibility(View.GONE);
            cardPeople.setVisibility(View.GONE);
            return;
        }

        LanguageContent c = b.languages.getOrDefault(lang, b.languages.get("en"));

        /* Quotes */
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
            cardQuote.setVisibility(View.VISIBLE);
        } else cardQuote.setVisibility(View.GONE);

        /* Interesting knowledge (Strings) */
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
            cardKnowledge.setVisibility(View.VISIBLE);
        } else cardKnowledge.setVisibility(View.GONE);

        /* Who were they */
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
            cardPeople.setVisibility(View.VISIBLE);
        } else cardPeople.setVisibility(View.GONE);
    }
}
