package com.example.knowlio.data.network;

import com.example.knowlio.data.models.DailyQuoteBundle;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/** Retrofit interface – loads a single JSON file whose name מגיע בפרמטר. */
public interface FactsApi {

    /** Example URL שבסוף יוצא:
     *  https://gist.githubusercontent.com/…/raw/daily_knowledge_2025_07_02.json
     */
    @GET("{file}")                 // {file} מוחלף בזמן־ריצה
    Call<DailyQuoteBundle> getBundle(@Path("file") String file);
}
