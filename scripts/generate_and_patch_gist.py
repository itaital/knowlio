#!/usr/bin/env python
"""
יוצר daily_knowledge_YYYY_MM_DD.json ושומר ב-Gist.
סדר העדיפויות:
1. Quotable API                               (https://api.quotable.io/random)
2. Backup API (ZenQuotes)                     (https://zenquotes.io/api/random)
3. קאש – הקובץ האחרון מתוך cache_history.json (עד 5 ימים אחורה)

לוג: כל שלב מדפיס מקור (PRIMARY / BACKUP / CACHE).
"""

import json, os, requests, sys
from datetime import date

# ─────────────── הגדרות סביבתיות ───────────────
GIST_ID  = os.getenv("GIST_ID")
TOKEN    = os.getenv("GH_TOKEN")
if not GIST_ID or not TOKEN:
    sys.exit("❌  Missing GIST_ID / GH_TOKEN env vars")

HEADERS = {
    "Authorization": f"token {TOKEN}",
    "Accept": "application/vnd.github+json"
}
GIST_URL = f"https://api.github.com/gists/{GIST_ID}"

today = date.today()
fname = f"daily_knowledge_{today:%Y_%m_%d}.json"

# ────────── פונקציות הבאת נתונים ──────────
def fetch_json(url: str, label: str, verify_ssl: bool = True) -> dict | None:
    try:
        resp = requests.get(url, timeout=10, verify=verify_ssl)
        resp.raise_for_status()
        print(f"ℹ️  {label} OK")
        return resp.json()
    except requests.exceptions.SSLError as e:
        print(f"⚠️  {label} SSL error: {e}")
        if verify_ssl:          # ניסיון נוסף בלי אימות
            return fetch_json(url, label + " (no-verify)", verify_ssl=False)
    except Exception as e:
        print(f"⚠️  {label} failed: {e}")
    return None

def get_quote_en() -> str | None:
    # 1. PRIMARY – Quotable
    q = fetch_json("https://api.quotable.io/random", "PRIMARY Quotable")
    if q and "content" in q:
        print("✅  Using PRIMARY")
        return f"{q['content']} – {q['author']}"

    # 2. BACKUP – ZenQuotes
    z = fetch_json("https://zenquotes.io/api/random", "BACKUP ZenQuotes")
    if z and isinstance(z, list):
        print("✅  Using BACKUP")
        return f"{z[0]['q']} – {z[0]['a']}"

    return None  # יטופל בקאש

def get_fact_en() -> str | None:
    f = fetch_json("https://uselessfacts.jsph.pl/random.json?language=en",
                   "FACT API")
    if f and "text" in f:
        return f["text"]
    return None

# ────────── משיכת גיסט קיים והיסטוריה ──────────
r = requests.get(GIST_URL, headers=HEADERS, timeout=20).json()
files = r.get("files", {})
history_name = "cache_history.json"
history = json.loads(files.get(history_name, {}).get("content", "[]"))

# ────────── בניית bundle להיום ──────────
quote_en = get_quote_en()
fact_en  = get_fact_en()

if not quote_en or not fact_en:
    # 3. CACHE
    print("‼️  Falling back to CACHE")
    if history:
        latest = sorted(history, key=lambda b: b["date"])[-1]
        if not quote_en:
            quote_en = latest["languages"]["en"]["quoteOfTheDay"][0]
        if not fact_en:
            ik = latest["languages"]["en"]["interestingKnowledge"][0]
            fact_en = ik["text"] if isinstance(ik, dict) else ik
    else:
        quote_en = quote_en or "Knowledge is power. – Francis Bacon"
        fact_en  = fact_en  or "Bananas are berries, but strawberries aren’t."

quote_list = [quote_en, "Knowledge is power. – Francis Bacon"]
knowledge_list = [
    {"title": "Did You Know?", "text": fact_en},
    {
        "title": "Extra Fact",
        "text": "Bananas are berries, but strawberries aren’t."
    }
]

bundle_today = {
    "date": f"{today:%Y-%m-%d}",
    "languages": {
        "en": {
            "quoteOfTheDay":        quote_list,
            "interestingKnowledge": knowledge_list,
            "whoWereThey":          []
        },
        "he": {
            "quoteOfTheDay":        ["״לא החלטה – לא התקדמות.\" – פתגם עברי"],
            "interestingKnowledge": ["לתמנון יש שלושה לבבות ותשעה מוחות."],
            "whoWereThey":          []
        }
    }
}

# ────────── עדכון history (שומרים עד 5 ימים) ──────────
history.append(bundle_today)
history = sorted(history, key=lambda b: b["date"])[-5:]

payload = {
    "description": f"Daily bundle ({today}) by workflow",
    "files": {
        fname:         { "content": json.dumps(bundle_today, ensure_ascii=False, indent=2) },
        history_name:  { "content": json.dumps(history, ensure_ascii=False, indent=2) }
    }
}

with open(fname, "w", encoding="utf-8") as f:
    json.dump(bundle_today, f, ensure_ascii=False, indent=2)

print("⬆️  Uploading to Gist …")
requests.patch(GIST_URL, headers=HEADERS, json=payload, timeout=30).raise_for_status()
print("✅  Gist updated with", fname)
