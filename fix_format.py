#!/usr/bin/env python
"""
Quick fix to convert whoWereThey from object format to string format
"""
import json
import os
import requests

# Environment setup
GIST_ID = os.getenv("GIST_ID")
TOKEN = os.getenv("GH_TOKEN")

HEADERS = {
    "Authorization": f"token {TOKEN}",
    "Accept": "application/vnd.github+json"
}
GIST_URL = f"https://api.github.com/gists/{GIST_ID}"

# Fixed data for today with correct string format
data = {
    "date": "2025-08-14",
    "languages": {
        "en": {
            "quoteOfTheDay": ["Life is about timing. – Carl Lewis"],
            "interestingKnowledge": ["Cats have over one hundred vocal sounds, while dogs only have about ten."],
            "whoWereThey": [
                "Grace Hopper: Computer scientist and US Navy rear admiral. One of the first programmers of the Harvard Mark I computer.",
                "Rosalind Franklin: Chemist and X-ray crystallographer. Her work was central to understanding the molecular structure of DNA."
            ]
        },
        "he": {
            "quoteOfTheDay": ["״לא החלטה – לא התקדמות.\" – פתגם עברי"],
            "interestingKnowledge": ["לתמנון יש שלושה לבבות ותשעה מוחות."],
            "whoWereThey": [
                "Grace Hopper: Computer scientist and US Navy rear admiral. One of the first programmers of the Harvard Mark I computer.",
                "Rosalind Franklin: Chemist and X-ray crystallographer. Her work was central to understanding the molecular structure of DNA."
            ]
        }
    }
}

print("🔧 Fixing whoWereThey format...")

# Upload the fixed version
payload = {
    "description": "Fix whoWereThey format to strings",
    "files": {
        "daily_knowledge_2025_08_14.json": {
            "content": json.dumps(data, ensure_ascii=False, indent=2)
        }
    }
}

r = requests.patch(GIST_URL, headers=HEADERS, json=payload, timeout=30)
r.raise_for_status()

print(f"✅ Fixed format successfully: {r.status_code}")
print("📱 Now try syncing your app - the 'Who Were They' section should appear!")

