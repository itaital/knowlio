package com.example.knowlio.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Content for a single language – matches the JSON one-to-one. */
public class LanguageContent {
    public List<String> quoteOfTheDay;        // Legacy - single quote (backward compatibility)
    
    @SerializedName("quotes")
    public List<String> quotes;               // New - multiple quotes
    
    public List<String> interestingKnowledge; // עובדות מעניינות - now 5 multi-paragraph items
    
    @SerializedName(value = "whoWereThey", alternate = {"whowereThey"})
    public List<Person> whoWereThey;          // ביוגרפיות קצרות
}
