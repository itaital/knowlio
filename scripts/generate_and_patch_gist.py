#!/usr/bin/env python
"""
* יוצר קובץ JSON יומי בשם daily_knowledge_YYYY_MM_DD.json
* מעדכן (או יוצר) את הקובץ ב-Gist שצוין במשתני-הסביבה
"""
import json, os, requests
from datetime import date
from random import choice

# ---------- 1. בניית התוכן ----------
today = date.today().strftime("%Y-%m-%d")

def random_quote() -> str:
    r = requests.get("https://api.quotable.io/random", timeout=10).json()
    return f"{r['content']} – {r['author']}"

bundle = {
    "date": today,
    "languages": {
        "en": {
            "quoteOfTheDay":       [random_quote()],
            "interestingKnowledge": [
                "Bananas are berries, but strawberries aren’t.",
                "The Eiffel Tower can be 15 cm taller on hot days."
            ],
            "whoWereThey": [
                "Victor Hugo (1802-1885) – French novelist & poet.",
                "Albert Einstein (1879-1955) – Physicist, theory of relativity."
            ],
        },
        # אפשר להוסיף שפות נוספות כאן…
    },
}

fname = f"daily_knowledge_{today.replace('-', '_')}.json"

# ---------- 2. העלאה ל-Gist ----------
gist_id   = os.getenv("GIST_ID")
token     = os.getenv("GH_TOKEN")

if not gist_id or not token:
    raise SystemExit("❌ Environment variables GIST_ID / GH_TOKEN are missing")

headers = {"Authorization": f"token {token}"}
url     = f"https://api.github.com/gists/{gist_id}"

payload = {
    "files": {
        fname: {
            "content": json.dumps(bundle, ensure_ascii=False, indent=2)
        }
    },
    "description": f"Daily Knowledge bundle ({today})"
}

resp = requests.patch(url, headers=headers, json=payload, timeout=30)
resp.raise_for_status()
print(f"✅ Updated gist {gist_id} with {fname}")
