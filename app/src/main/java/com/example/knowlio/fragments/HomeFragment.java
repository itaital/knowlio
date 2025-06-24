package com.example.knowlio.fragments;

import android.content.SharedPreferences;
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
import com.example.knowlio.data.models.KnowledgeItem;
import android.graphics.Typeface;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private LinearLayout quotesLayout;
    private LinearLayout knowledgeLayout;
    private LinearLayout peopleLayout;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        quotesLayout = v.findViewById(R.id.layoutQuotes);
        knowledgeLayout = v.findViewById(R.id.layoutKnowledge);
        peopleLayout = v.findViewById(R.id.layoutPeople);
        tvEmpty = v.findViewById(R.id.tvEmpty);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());

        FactsRepository repo = new FactsRepository(requireContext());
        repo.observeBundle(java.time.LocalDate.now()).observe(getViewLifecycleOwner(), bundle -> {
            LanguageContent content = null;
            if (bundle != null && bundle.languages != null) {
                content = bundle.languages.get(lang);
                if (content == null) content = bundle.languages.get("en");
            }
            showContent(content);
        });
    }

    private void showContent(@Nullable LanguageContent content) {
        if (content == null) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        quotesLayout.removeAllViews();
        if (content.quoteOfTheDay != null) {
            for (String q : content.quoteOfTheDay) {
                TextView t = new TextView(requireContext());
                t.setText("\u275D " + q + " \u275E");
                TextViewCompat.setTextAppearance(t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0, 0, 0, 12);
                quotesLayout.addView(t);
            }
        }

        knowledgeLayout.removeAllViews();
        if (content.interestingKnowledge != null) {
            for (KnowledgeItem item : content.interestingKnowledge) {
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
