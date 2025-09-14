package com.example.knowlio.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class GistResponse {
    
    @SerializedName("id")
    public String id;
    
    @SerializedName("files")
    public Map<String, GistFile> files;
    
    @SerializedName("updated_at")
    public String updatedAt;
    
    @SerializedName("commits")
    public GistCommit[] commits;
    
    public static class GistFile {
        @SerializedName("filename")
        public String filename;
        
        @SerializedName("raw_url")
        public String rawUrl;
        
        @SerializedName("size")
        public int size;
        
        @SerializedName("type")
        public String type;
    }
    
    public static class GistCommit {
        @SerializedName("version")
        public String version;
        
        @SerializedName("committed_at")
        public String committedAt;
    }
}




