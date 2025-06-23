package com.example.knowlio.data.network;

import retrofit2.Call;
import retrofit2.http.GET;

/** Simple API for fetching random quotes from Quotable. */
public interface QuotableApi {
    @GET("random")
    Call<QuoteResponse> getRandom();
}
