package com.example.knowlio.data.network;

import com.example.knowlio.data.models.DailyFact;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FactsApi {
    @GET("raw")
    Call<DailyFact> getFact();
}
