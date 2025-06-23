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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.widget.TextViewCompat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private TextInputEditText etDate;
    private TextView tvQuote;
    private TextView tvKnowledge;
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
        tvQuote = v.findViewById(R.id.tvQuoteHistory);
        tvKnowledge = v.findViewById(R.id.tvKnowledgeHistory);
        peopleLayout = v.findViewById(R.id.layoutPeopleHistory);
        cardQuote = v.findViewById(R.id.cardQuote);
        cardKnowledge = v.findViewById(R.id.cardKnowledge);
        cardPeople = v.findViewById(R.id.cardPeople);

        repo = new FactsRepository(requireContext());
        lang = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("pref_lang", Locale.getDefault().getLanguage());

        etDate.setOnClickListener(v1 -> openPicker());

        return v;
    }

    private void openPicker() {
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraints)
                .build();
        picker.addOnPositiveButtonClickListener(time -> {
            LocalDate date = Instant.ofEpochMilli(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            etDate.setText(date.toString());
            showBundle(date);
        });
        picker.show(getParentFragmentManager(), "picker");
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

        if (c != null && c.quoteOfTheDay != null && !c.quoteOfTheDay.isEmpty()) {
            tvQuote.setText(c.quoteOfTheDay.get(0));
            cardQuote.setVisibility(View.VISIBLE);
        } else {
            cardQuote.setVisibility(View.GONE);
        }

        if (c != null && c.interestingKnowledge != null && !c.interestingKnowledge.isEmpty()) {
            tvKnowledge.setText(c.interestingKnowledge.get(0));
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
