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

import com.example.knowlio.R;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.DailyQuoteBundle;
import com.example.knowlio.data.models.LanguageContent;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.List;

public class HistoryFragment extends Fragment {

    private AutoCompleteTextView dateInput;
    private LinearLayout quoteLayout, knowledgeLayout, peopleLayout;
    private View cardQuote, cardKnowledge, cardPeople;
    private FactsRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);

        dateInput        = v.findViewById(R.id.etDate);
        quoteLayout      = v.findViewById(R.id.layoutQuotesHistory);
        knowledgeLayout  = v.findViewById(R.id.layoutKnowledgeHistory);
        peopleLayout     = v.findViewById(R.id.layoutPeopleHistory);

        cardQuote        = v.findViewById(R.id.cardQuote);
        cardKnowledge    = v.findViewById(R.id.cardKnowledge);
        cardPeople       = v.findViewById(R.id.cardPeople);

        repo = new FactsRepository(requireContext());
        initDateDropdown();

        return v;
    }

    /* -------- dropdown with available dates -------- */
    private void initDateDropdown() {
        List<LocalDate> dates = repo.getAllBundleDates();
        if (dates.isEmpty()) return;

        String[] items = dates.stream().map(LocalDate::toString).toArray(String[]::new);

        dateInput.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, items));

        dateInput.setOnItemClickListener((p, view, pos, id) ->
                renderBundle(repo.getBundle(dates.get(pos))));

        // show newest bundle by default
        dateInput.setText(items[0], false);
        renderBundle(repo.getBundle(dates.get(0)));
    }

    /* ------------------- render ------------------- */
    private void renderBundle(@Nullable DailyQuoteBundle bundle) {
        if (bundle == null) {
            cardQuote.setVisibility(View.GONE);
            cardKnowledge.setVisibility(View.GONE);
            cardPeople.setVisibility(View.GONE);
            return;
        }

        LanguageContent c = bundle.languages.getOrDefault("en", null);
        if (c == null) return;

        /* Quotes */
        quoteLayout.removeAllViews();
        if (c.quoteOfTheDay != null && !c.quoteOfTheDay.isEmpty()) {
            for (String q : c.quoteOfTheDay) {
                TextView tv = makeBody("❝ " + q + " ❞");
                tv.setPadding(0, 0, 0, 16);
                quoteLayout.addView(tv);
            }
            cardQuote.setVisibility(View.VISIBLE);
        } else cardQuote.setVisibility(View.GONE);

        /* Interesting knowledge */
        knowledgeLayout.removeAllViews();
        if (c.interestingKnowledge != null && !c.interestingKnowledge.isEmpty()) {
            for (String fact : c.interestingKnowledge) {
                TextView tv = makeBody("• " + fact);
                tv.setPadding(0, 0, 0, 12);
                knowledgeLayout.addView(tv);
            }
            cardKnowledge.setVisibility(View.VISIBLE);
        } else cardKnowledge.setVisibility(View.GONE);

        /* Who were they */
        peopleLayout.removeAllViews();
        if (c.whoWereThey != null && !c.whoWereThey.isEmpty()) {
            for (String bio : c.whoWereThey) {
                TextView tv = makeBody("• " + bio);
                tv.setPadding(0, 0, 0, 12);
                peopleLayout.addView(tv);
            }
            cardPeople.setVisibility(View.VISIBLE);
        } else cardPeople.setVisibility(View.GONE);
    }

    private TextView makeBody(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        TextViewCompat.setTextAppearance(tv,
                com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        return tv;
    }
}
