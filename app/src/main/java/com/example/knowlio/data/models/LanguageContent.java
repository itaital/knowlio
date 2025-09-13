package com.example.knowlio.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Content for a single language – matches the JSON one-to-one. */
public class LanguageContent {
    public List<String> quoteOfTheDay;        // ציטוט(ים)
    public List<String> interestingKnowledge; // עובדות מעניינות ← עכשיו String-ים
    
    @SerializedName(value = "whoWereThey", alternate = {"whowereThey"})
    public List<Person> whoWereThey;          // ביוגרפיות קצרות
}
