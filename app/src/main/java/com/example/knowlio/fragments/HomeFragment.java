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
import java.util.List;
import java.time.LocalDate;
import android.util.Log;

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
        
        // Check if we need to force refresh due to sync
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean forceRefresh = prefs.getBoolean("force_refresh_ui", false);
        String lastSyncedBundle = prefs.getString("last_synced_bundle_date", null);
        
        Log.d("HomeFragment", "🔄 onResume - forceRefresh: " + forceRefresh + ", lastSyncedBundle: " + lastSyncedBundle);
        
        if (forceRefresh) {
            Log.d("HomeFragment", "🔄 Force refresh detected, clearing cache and reloading...");
            // Clear the force refresh flag
            prefs.edit().putBoolean("force_refresh_ui", false).apply();
            
            // Clear any old cached data
            clearOldCachedData();
            
            // Force reload data
            loadData();
            
            // Show notification that refresh occurred
            android.widget.Toast.makeText(requireContext(), 
                "🔄 Content refreshed after sync!", 
                android.widget.Toast.LENGTH_SHORT).show();
        } else {
            // Check if we have a new synced bundle that we haven't displayed yet
            if (lastSyncedBundle != null) {
                FactsRepository repo = new FactsRepository(requireContext());
                LocalDate[] availableDates = repo.listDates();
                if (availableDates.length > 0) {
                    String currentDisplayedDate = availableDates[0].toString();
                    if (!currentDisplayedDate.equals(lastSyncedBundle)) {
                        Log.d("HomeFragment", "🔄 New synced bundle detected! Synced: " + lastSyncedBundle + ", Displayed: " + currentDisplayedDate);
                        // Clear cache and reload to show new content
                        clearOldCachedData();
                        loadData();
                        android.widget.Toast.makeText(requireContext(), 
                            "🔄 New content loaded: " + lastSyncedBundle, 
                            android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }
            
            loadData();
        }
        
        // Show current bundle date in a toast for debugging
        FactsRepository repo = new FactsRepository(requireContext());
        LocalDate[] availableDates = repo.listDates();
        if (availableDates.length > 0) {
            String currentDate = availableDates[0].toString();
            android.widget.Toast.makeText(requireContext(), 
                "📅 Current bundle: " + currentDate, 
                android.widget.Toast.LENGTH_LONG).show();
        }
    }

    /** Loads today’s bundle from SharedPreferences ➜ displays it. */
    public void loadData() {
        try {
            Log.d("HomeFragment", "🔄 Starting loadData()...");
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String lang = prefs.getString("pref_lang", Locale.getDefault().getLanguage());
            Log.d("HomeFragment", "🔄 Language: " + lang);

            FactsRepository repo = new FactsRepository(requireContext());
            LanguageContent c = repo.getTodayBundle(lang);
            Log.d("HomeFragment", "🔄 Retrieved content: " + (c != null ? "SUCCESS" : "NULL"));

            if (c == null) {
                Log.w("HomeFragment", "⚠️ No content available, showing empty state");
                tvEmpty.setVisibility(View.VISIBLE);
                quotesLayout.removeAllViews();
                knowledgeLayout.removeAllViews();
                peopleLayout.removeAllViews();
                return;
            }
            tvEmpty.setVisibility(View.GONE);

        /* ── Quote(s) - prefer new quotes field ──────── */
        quotesLayout.removeAllViews();
        List<String> quotesToDisplay = (c.quotes != null && !c.quotes.isEmpty()) ? c.quotes : c.quoteOfTheDay;
        
        if (quotesToDisplay != null && !quotesToDisplay.isEmpty()) {
            Log.d("HomeFragment", "📝 Displaying " + quotesToDisplay.size() + " quotes");
            for (String q : quotesToDisplay) {
                TextView t = new TextView(requireContext());
                t.setText("• " + q);
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0,0,0,16);
                t.setLineSpacing(4, 1.2f); // Improved line spacing
                quotesLayout.addView(t);
            }
        }

        /* ── Interesting knowledge (Multi-paragraph facts) ── */
        knowledgeLayout.removeAllViews();
        if (c.interestingKnowledge != null && !c.interestingKnowledge.isEmpty()) {
            Log.d("HomeFragment", "🧠 Displaying " + c.interestingKnowledge.size() + " interesting facts");
            for (int i = 0; i < c.interestingKnowledge.size(); i++) {
                String k = c.interestingKnowledge.get(i);
                TextView t = new TextView(requireContext());
                t.setText("• " + k);
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
                t.setPadding(0,0,0,24); // More padding between facts
                t.setLineSpacing(6, 1.3f); // Better line spacing for multi-line content
                t.setMaxLines(Integer.MAX_VALUE); // Remove any line limits
                knowledgeLayout.addView(t);
                
                // Add extra spacing between facts
                if (i < c.interestingKnowledge.size() - 1) {
                    View spacer = new View(requireContext());
                    spacer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 16));
                    knowledgeLayout.addView(spacer);
                }
            }
        }

        /* ── Who were they (Person objects) ────────── */
        peopleLayout.removeAllViews();
        if (c.whoWereThey != null && !c.whoWereThey.isEmpty()) {
            Log.d("HomeFragment", "📖 Displaying " + c.whoWereThey.size() + " people in whoWereThey");
            for (com.example.knowlio.data.models.Person p : c.whoWereThey) {
                TextView t = new TextView(requireContext());
                t.setText("• " + p.toString());
                TextViewCompat.setTextAppearance(
                        t, com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
                t.setPadding(0,0,0,12);
                peopleLayout.addView(t);
            }
        } else {
            // Show placeholder when whoWereThey is empty
            Log.w("HomeFragment", "⚠️ whoWereThey is empty or null");
            TextView placeholder = new TextView(requireContext());
            placeholder.setText("• No biographical information available today");
            placeholder.setTextColor(0xFF666666); // Gray color
            TextViewCompat.setTextAppearance(
                    placeholder, com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
            placeholder.setPadding(0,0,0,12);
            peopleLayout.addView(placeholder);
        }
            
            Log.d("HomeFragment", "✅ loadData() completed successfully");
            
        } catch (Exception e) {
            Log.e("HomeFragment", "❌ CRASH in loadData()!", e);
            // Show error to user
            try {
                android.widget.Toast.makeText(requireContext(), 
                    "❌ Error loading content: " + e.getMessage(), 
                    android.widget.Toast.LENGTH_LONG).show();
            } catch (Exception toastError) {
                Log.e("HomeFragment", "Even toast failed!", toastError);
            }
        }
    }
    
    /**
     * Clears old cached data to force fresh content display
     */
    private void clearOldCachedData() {
        try {
            Log.d("HomeFragment", "🧹 Clearing old cached data...");
            
            // Clear any old cached fact dates
            SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext());
            prefs.edit()
                    .remove("cached_fact_date")
                    .apply();
            
            Log.d("HomeFragment", "🧹 Cleared old cached fact dates");
            
        } catch (Exception e) {
            Log.e("HomeFragment", "Error clearing old cached data", e);
        }
    }
}
