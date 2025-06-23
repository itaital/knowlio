#!/usr/bin/env python
import json, requests, os
from datetime import date
from random import choice

# 1. בונים את שם הקובץ
today = date.today()
fname = f"daily_knowledge_{today:%Y_%m_%d}.json"

# 2. מייצרים נתונים לדוגמה
def quote_en():
    r = requests.get("https://api.quotable.io/random", timeout=10).json()
    return f"{r['content']} – {r['author']}"

fact_en = choice([
    "Bananas are berries, but strawberries aren’t.",
    "The Eiffel Tower can be 15 cm taller on hot days."
])

bundle = {
    "date": f"{today:%Y-%m-%d}",
    "languages": {
        "en": {
            "quoteOfTheDay":        [quote_en()],
            "interestingKnowledge": [fact_en],
            "whoWereThey":          []
        },
        "he": {
            "quoteOfTheDay":        ["״לא החלטה – לא התקדמות.\" – פתגם עברי"],
            "interestingKnowledge": ["לתמנון יש שלושה לבבות ותשעה מוחות."],
            "whoWereThey":          []
        }
    }
}

content_str = json.dumps(bundle, ensure_ascii=False, indent=2)

# 3. שולחים PATCH לגיסט
gist_id   = os.environ["GIST_ID"]
token     = os.environ["GIST_TOKEN"]
url       = f"https://api.github.com/gists/{gist_id}"
headers   = {
    "Authorization": f"Bearer {token}",
    "Accept": "application/vnd.github+json"
}
payload = {
    "files": {
        fname: { "content": content_str }
    },
    "description": f"Daily bundle for {today}"
}

resp = requests.patch(url, headers=headers, data=json.dumps(payload))
resp.raise_for_status()
print("✅ uploaded", fname, "→", resp.json().get("html_url"))
