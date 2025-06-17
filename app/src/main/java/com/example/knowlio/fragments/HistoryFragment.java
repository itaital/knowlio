package com.example.knowlio.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knowlio.R;
import com.example.knowlio.data.FactsRepository;
import com.example.knowlio.data.db.DailyFactEntity;

import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        RecyclerView rv = view.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HistoryAdapter();
        rv.setAdapter(adapter);
        loadData();
        return view;
    }

    private void loadData() {
        FactsRepository repo = new FactsRepository(requireContext());
        List<DailyFactEntity> history = repo.getHistory();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = prefs.getString("pref_lang", "en");
        adapter.setData(history, lang);
    }
}
