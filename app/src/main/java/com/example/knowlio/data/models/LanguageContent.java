package com.example.knowlio.data.models;

import java.util.List;

/** Content for a single language – matches the JSON one-to-one. */
public class LanguageContent {
    public List<String> quoteOfTheDay;        // ציטוט(ים)
    public List<String> interestingKnowledge; // עובדות מעניינות ← עכשיו String-ים
    public List<String> whoWereThey;          // ביוגרפיות קצרות
}
