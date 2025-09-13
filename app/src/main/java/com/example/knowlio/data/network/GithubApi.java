package com.example.knowlio.data.network;

import com.example.knowlio.data.models.GistResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/** Retrofit interface for GitHub API calls */
public interface GithubApi {

    /**
     * Fetches Gist metadata from GitHub API
     * URL: https://api.github.com/gists/{gistId}
     */
    @GET("gists/{gistId}")
    Call<GistResponse> getGist(@Path("gistId") String gistId);
}

