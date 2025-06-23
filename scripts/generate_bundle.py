#!/usr/bin/env python
"""
Generates daily_knowledge_YYYY_MM_DD.json in repository root.
Structure follows Knowlio format (quote, knowledge, people) in 2 languages.
Extend as needed.
"""

import json, pathlib, requests
from datetime import date
from random import choice

# ---- 1. File name for today ----
today = date.today()
fname = f"daily_knowledge_{today:%Y_%m_%d}.json"

# Don't overwrite existing bundle
if pathlib.Path(fname).exists():
    print("Bundle already exists – skipping")
    exit(0)

# ---- 2. Very simple data sources ----
def random_quote_en():
    r = requests.get("https://api.quotable.io/random", timeout=10).json()
    return f"{r['content']} – {r['author']}"

facts_en = [
    "Bananas are berries, but strawberries aren’t.",
    "The Eiffel Tower can be 15 cm taller on hot days."
]

quote_en = random_quote_en()
fact_en  = choice(facts_en)

quote_he = "״מי שלא נכשל מעולם – לא ניסה דבר חדש.\" – אלברט איינשטיין"
fact_he  = "לתמנון יש שלושה לבבות ותשעה מוחות."

# ---- 3. Build JSON structure ----
bundle = {
    "date": f"{today:%Y-%m-%d}",
    "languages": {
        "en": {
            "quoteOfTheDay":        [quote_en],
            "interestingKnowledge": [fact_en],
            "whoWereThey":          []
        },
        "he": {
            "quoteOfTheDay":        [quote_he],
            "interestingKnowledge": [fact_he],
            "whoWereThey":          []
        }
    }
}

# ---- 4. Write file ----
with open(fname, "w", encoding="utf-8") as f:
    json.dump(bundle, f, ensure_ascii=False, indent=2)

print("✅  Created", fname)
