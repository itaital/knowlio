package com.example.knowlio.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import com.example.knowlio.data.models.KnowledgeItem;
import com.example.knowlio.data.models.LanguageContent;

import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView tvQuote1;
    private TextView tvQuote2;
    private LinearLayout knowledgeLayout;
    private LinearLayout peopleLayout;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        tvQuote1 = v.findViewById(R.id.tvQuote1);
        tvQuote2 = v.findViewById(R.id.tvQuote2);
        knowledgeLayout = v.findViewById(R.id.layoutKnowledge);
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

        if (content.quoteOfTheDay != null) {
            if (content.quoteOfTheDay.size() > 0) {
                tvQuote1.setText(content.quoteOfTheDay.get(0));
            }
            if (content.quoteOfTheDay.size() > 1) {
                tvQuote2.setText(content.quoteOfTheDay.get(1));
            }
        }

        knowledgeLayout.removeAllViews();
        if (content.interestingKnowledge != null) {
            for (KnowledgeItem item : content.interestingKnowledge) {
                TextView t1 = new TextView(requireContext());
                t1.setText(item.title);
                t1.setTypeface(null, Typeface.BOLD);
                TextViewCompat.setTextAppearance(t1, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                knowledgeLayout.addView(t1);

                TextView t2 = new TextView(requireContext());
                t2.setText(item.text);
                TextViewCompat.setTextAppearance(t2, com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
                t2.setPadding(0, 0, 0, 12);
                knowledgeLayout.addView(t2);
            }
        }

        peopleLayout.removeAllViews();
        if (content.whoWereThey != null) {
            for (Map.Entry<String, String> entry : content.whoWereThey.entrySet()) {
                TextView t = new TextView(requireContext());
                t.setText(entry.getKey() + " – " + entry.getValue());
                TextViewCompat.setTextAppearance(t, com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
                t.setPadding(0, 0, 0, 12);
                peopleLayout.addView(t);
            }
        }
    }
}
