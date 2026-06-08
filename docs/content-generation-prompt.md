# Content Generation Prompt

Use this when generating the next Knowlio archive batch. Paste the result into a file and import it with `scripts/importContentArchive.mjs`.

```text
Act as a Senior Content Architect and Data Engineer. I have a React/TypeScript app called "Knowlio" that works offline. Generate a valid TypeScript array of objects containing daily content for dates November 28, 2025 to December 27, 2025.

For each day and for each language (English, Hebrew, Spanish, French, German, Turkish), generate:
1. quoteOfTheDay: exactly 3 unique inspiring quotes as strings formatted "Quote - Author".
2. whoWereThey: exactly 3 short biographies corresponding exactly to the quote authors above.
3. interestingKnowledge: exactly 5 distinct facts. Each fact must be { title, text }, and text must be a detailed 40-60 word paragraph.

Quality rules:
- No repeated quote across the 30 days.
- No repeated fact title across the 30 days.
- Hebrew must be native-level and grammatically correct.
- Return only TypeScript data, no explanations.
```
