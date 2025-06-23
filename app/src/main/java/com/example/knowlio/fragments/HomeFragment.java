package com.example.knowlio.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.knowlio.R;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.models.LanguageContent;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvQuote;
    private TextView tvKnowledge;
    private LinearLayout peopleLayout;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        tvQuote = v.findViewById(R.id.tvQuote);
        tvKnowledge = v.findViewById(R.id.tvKnowledge);
        peopleLayout = v.findViewById(R.id.layoutPeople);
        tvEmpty = v.findViewById(R.id.tvEmpty);

        Button btnHistory = v.findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(view -> {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new HistoryFragment());
            ft.addToBackStack(null);
            ft.commit();
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());

        FactsRepository repo = new FactsRepository(requireContext());
        LanguageContent content = repo.getTodayBundle(lang);

        if (content == null) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        if (content.quoteOfTheDay != null && !content.quoteOfTheDay.isEmpty()) {
            tvQuote.setText(content.quoteOfTheDay.get(0));
        }

        if (content.interestingKnowledge != null && !content.interestingKnowledge.isEmpty()) {
            tvKnowledge.setText(content.interestingKnowledge.get(0));
        } else {
            tvKnowledge.setText("");
        }

        peopleLayout.removeAllViews();
        if (content.whoWereThey != null) {
            for (String item : content.whoWereThey) {
                TextView t = new TextView(requireContext());
                t.setText("• " + item);
                TextViewCompat.setTextAppearance(t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                peopleLayout.addView(t);
            }
        }
    }
}
