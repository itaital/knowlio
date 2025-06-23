package com.example.knowlio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.knowlio.R;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.widget.TextViewCompat;
import com.example.knowlio.data.models.KnowledgeItem;
import android.graphics.Typeface;

import java.time.LocalDate;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private AutoCompleteTextView etDate;
    private LinearLayout quotesLayout;
    private LinearLayout knowledgeLayout;
    private LinearLayout peopleLayout;
    private View cardQuote, cardKnowledge, cardPeople;
    private FactsRepository repo;
    private String lang;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        etDate = v.findViewById(R.id.etDate);
        quotesLayout = v.findViewById(R.id.layoutQuotesHistory);
        knowledgeLayout = v.findViewById(R.id.layoutKnowledgeHistory);
        peopleLayout = v.findViewById(R.id.layoutPeopleHistory);
        cardQuote = v.findViewById(R.id.cardQuote);
        cardKnowledge = v.findViewById(R.id.cardKnowledge);
        cardPeople = v.findViewById(R.id.cardPeople);

        repo = new FactsRepository(requireContext());
        lang = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("pref_lang", Locale.getDefault().getLanguage());

        setupDropdown();

        return v;
    }

    private void setupDropdown() {
        LocalDate[] dates = repo.listDates();
        String[] ds = new String[dates.length];
        for (int i = 0; i < dates.length; i++) ds[i] = dates[i].toString();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, ds);
        etDate.setAdapter(adapter);
        etDate.setOnItemClickListener((p, v1, pos, id) -> {
            showBundle(LocalDate.parse(adapter.getItem(pos)));
        });
    }

    private void showBundle(LocalDate date) {
        DailyQuoteBundle bundle = repo.getBundle(date);
        if (bundle == null) {
            Snackbar.make(etDate, R.string.no_data, Snackbar.LENGTH_LONG).show();
            cardQuote.setVisibility(View.GONE);
            cardKnowledge.setVisibility(View.GONE);
            cardPeople.setVisibility(View.GONE);
            return;
        }

        LanguageContent c = bundle.languages.get(lang);
        if (c == null) c = bundle.languages.get("en");

        quotesLayout.removeAllViews();
        if (c != null && c.quoteOfTheDay != null && !c.quoteOfTheDay.isEmpty()) {
            for (String q : c.quoteOfTheDay) {
                TextView t = new TextView(requireContext());
                t.setText("\u275D " + q + " \u275E");
                TextViewCompat.setTextAppearance(t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                quotesLayout.addView(t);
            }
            cardQuote.setVisibility(View.VISIBLE);
        } else {
            cardQuote.setVisibility(View.GONE);
        }

        knowledgeLayout.removeAllViews();
        if (c != null && c.interestingKnowledge != null && !c.interestingKnowledge.isEmpty()) {
            for (KnowledgeItem item : c.interestingKnowledge) {
                TextView title = new TextView(requireContext());
                title.setText(item.title);
                title.setTypeface(null, Typeface.BOLD);
                TextViewCompat.setTextAppearance(title, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                TextView body = new TextView(requireContext());
                body.setText(item.text);
                TextViewCompat.setTextAppearance(body, com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
                body.setPadding(0, 0, 0, 16);
                knowledgeLayout.addView(title);
                knowledgeLayout.addView(body);
            }
            cardKnowledge.setVisibility(View.VISIBLE);
        } else {
            cardKnowledge.setVisibility(View.GONE);
        }

        peopleLayout.removeAllViews();
        if (c != null && c.whoWereThey != null && !c.whoWereThey.isEmpty()) {
            for (String item : c.whoWereThey) {
                TextView t = new TextView(requireContext());
                t.setText("• " + item);
                TextViewCompat.setTextAppearance(t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                peopleLayout.addView(t);
            }
            cardPeople.setVisibility(View.VISIBLE);
        } else {
            cardPeople.setVisibility(View.GONE);
        }
    }
}
