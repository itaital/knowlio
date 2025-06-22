package com.example.knowlio.data.network;

import com.example.knowlio.data.models.DailyQuoteBundle;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FactsApi {
    @GET
    Call<DailyQuoteBundle> getBundle(@Url String url);
}
