#!/usr/bin/env python
"""
יוצר daily_knowledge_YYYY_MM_DD.json ושומר ב-Gist.
סדר העדיפויות:
1. Quotable API                               (https://api.quotable.io/random)
2. Backup API (ZenQuotes)                     (https://zenquotes.io/api/random)
3. קאש – הקובץ האחרון מתוך cache_history.json (עד 5 ימים אחורה)

לוג: כל שלב מדפיס מקור (PRIMARY / BACKUP / CACHE).
"""

import json, os, requests, warnings, sys, argparse, random
from datetime import date
from typing import List, Dict, Optional

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

# ────────── People Pool for whoWereThey ──────────
PEOPLE_POOL = [
    {"name": "Albert Einstein", "bio": "Physicist who developed the theory of relativity. Nobel Prize winner for his explanation of the photoelectric effect."},
    {"name": "Marie Curie", "bio": "Pioneering physicist and chemist. First person to win Nobel Prizes in two different scientific fields."},
    {"name": "Isaac Newton", "bio": "Mathematician and physicist who formulated the laws of motion and universal gravitation. Founder of calculus."},
    {"name": "Ada Lovelace", "bio": "First computer programmer. Wrote the first algorithm intended for processing on Charles Babbage's Analytical Engine."},
    {"name": "Nikola Tesla", "bio": "Inventor and electrical engineer. Pioneered alternating current power systems and wireless energy transfer."},
    {"name": "Grace Hopper", "bio": "Computer scientist and US Navy rear admiral. One of the first programmers of the Harvard Mark I computer."},
    {"name": "Leonardo da Vinci", "bio": "Renaissance polymath. Painter, sculptor, architect, scientist, mathematician, engineer, and inventor."},
    {"name": "Galileo Galilei", "bio": "Astronomer and physicist. Father of modern observational astronomy and the scientific method."},
    {"name": "Jane Goodall", "bio": "Primatologist and anthropologist. World's foremost expert on chimpanzees and their behavior in the wild."},
    {"name": "Stephen Hawking", "bio": "Theoretical physicist and cosmologist. Made groundbreaking discoveries about black holes and the universe."},
    {"name": "Rosalind Franklin", "bio": "Chemist and X-ray crystallographer. Her work was central to understanding the molecular structure of DNA."},
    {"name": "Charles Darwin", "bio": "Naturalist and biologist. Developed the theory of evolution through natural selection."},
    {"name": "Hypatia", "bio": "Ancient Greek mathematician, astronomer, and philosopher. Head of the Neoplatonic school in Alexandria."},
    {"name": "Archimedes", "bio": "Ancient Greek mathematician, physicist, engineer, astronomer, and inventor. Discovered the principle of buoyancy."},
    {"name": "Srinivasa Ramanujan", "bio": "Mathematical genius. Made extraordinary contributions to mathematical analysis, number theory, and infinite series."}
]

def get_people(lang: str = 'en', n: int = 2, seed: Optional[str] = None) -> List[Dict[str, str]]:
    """
    Get a list of people for whoWereThey section.
    
    Args:
        lang: Language code ('en' or 'he')
        n: Number of people to return
        seed: Seed for deterministic randomness (usually date string)
    
    Returns:
        List of dictionaries with 'name' and 'bio' keys
    """
    if seed:
        random.seed(seed)
    
    # For Hebrew, reuse English names and bios for now
    if lang == 'he':
        lang = 'en'
    
    # Sample n people without replacement
    selected = random.sample(PEOPLE_POOL, min(n, len(PEOPLE_POOL)))
    
    result = []
    for person in selected:
        bio = person['bio']
        # Truncate bio to ≤140 chars at word boundary
        if len(bio) > 140:
            words = bio.split()
            truncated = ""
            for word in words:
                if len(truncated + " " + word) <= 137:  # Leave room for "..."
                    truncated += (" " + word) if truncated else word
                else:
                    break
            bio = truncated + "..."
        
        result.append({
            'name': person['name'],
            'bio': bio
        })
    
    return result

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

def backfill_gist_files() -> None:
    """
    Backfill all gist files that have empty or missing whoWereThey.
    """
    print("🔄  Starting backfill of gist files...")
    
    # Get current gist
    r = requests.get(GIST_URL, headers=HEADERS, timeout=20)
    r.raise_for_status()
    gist_data = r.json()
    files = gist_data.get("files", {})
    
    files_to_patch = {}
    total_files = 0
    files_needing_update = 0
    
    for filename, file_info in files.items():
        if not filename.startswith("daily_knowledge_") or not filename.endswith(".json"):
            continue
            
        total_files += 1
        try:
            content = json.loads(file_info.get("content", "{}"))
            languages = content.get("languages", {})
            
            needs_update = False
            for lang_code in ["en", "he"]:
                if lang_code in languages:
                    who_were_they = languages[lang_code].get("whoWereThey", [])
                    if not who_were_they:  # Empty or missing
                        needs_update = True
                        break
            
            if needs_update:
                files_needing_update += 1
                print(f"📝  Will update {filename}")
                
                # Extract date from filename for deterministic seeding
                date_str = filename.replace("daily_knowledge_", "").replace(".json", "")
                # Handle both YYYY_MM_DD and YYYY-MM-DD formats
                if "_" in date_str:
                    parts = date_str.split("_")
                else:
                    parts = date_str.split("-")
                
                if len(parts) == 3:
                    try:
                        date_obj = date(int(parts[0]), int(parts[1]), int(parts[2]))
                        date_formatted = f"{date_obj:%Y-%m-%d}"
                    except ValueError:
                        print(f"⚠️  Invalid date format in {filename}: {date_str}")
                        continue
                else:
                    print(f"⚠️  Unexpected filename format: {filename}")
                    continue
                
                # Update the content with whoWereThey
                for lang_code in ["en", "he"]:
                    if lang_code in languages:
                        languages[lang_code]["whoWereThey"] = get_people(lang_code, 2, seed=date_formatted)
                
                # Update the date if it was missing
                if "date" not in content:
                    content["date"] = date_formatted
                
                files_to_patch[filename] = {
                    "content": json.dumps(content, ensure_ascii=False, indent=2)
                }
        
        except (json.JSONDecodeError, KeyError) as e:
            print(f"⚠️  Error parsing {filename}: {e}")
            continue
    
    print(f"📊  Summary: {files_needing_update}/{total_files} files need updating")
    
    if not files_to_patch:
        print("✅  No files need updating")
        return
    
    if args.dry_run:
        print("🔍  DRY RUN - No changes made")
        return
    
    # Apply patches
    print("⬆️  Patching gist files...")
    payload = {
        "description": f"Backfill whoWereThey for {len(files_to_patch)} files",
        "files": files_to_patch
    }
    
    r = requests.patch(GIST_URL, headers=HEADERS, json=payload, timeout=30)
    r.raise_for_status()
    print(f"✅  Successfully patched {len(files_to_patch)} files")

def fix_typo_keys() -> None:
    """
    Fix typo keys in existing gist files - move "whowereThey" to "whoWereThey"
    """
    print("🔧  Fixing typo keys in gist files...")
    
    # Get current gist
    r = requests.get(GIST_URL, headers=HEADERS, timeout=20)
    r.raise_for_status()
    gist_data = r.json()
    files = gist_data.get("files", {})
    
    files_to_patch = {}
    total_files = 0
    files_with_typos = 0
    
    for filename, file_info in files.items():
        if not filename.startswith("daily_knowledge_") or not filename.endswith(".json"):
            continue
            
        total_files += 1
        try:
            content = json.loads(file_info.get("content", "{}"))
            languages = content.get("languages", {})
            
            file_modified = False
            for lang_code in languages:
                lang_content = languages[lang_code]
                
                # Check if we have the typo key and no correct key
                if "whowereThey" in lang_content and "whoWereThey" not in lang_content:
                    print(f"🔧  Fixing typo key in {filename} ({lang_code})")
                    # Move the array from typo key to correct key
                    lang_content["whoWereThey"] = lang_content["whowereThey"]
                    # Delete the typo key
                    del lang_content["whowereThey"]
                    file_modified = True
            
            if file_modified:
                files_with_typos += 1
                files_to_patch[filename] = {
                    "content": json.dumps(content, ensure_ascii=False, indent=2)
                }
                
        except (json.JSONDecodeError, KeyError) as e:
            print(f"⚠️  Error parsing {filename}: {e}")
            continue
    
    print(f"📊  Summary: {files_with_typos}/{total_files} files had typo keys")
    
    if not files_to_patch:
        print("✅  No typo keys found to fix")
        return
    
    if args.dry_run:
        print("🔍  DRY RUN - Would fix typo keys in files:")
        for filename in files_to_patch.keys():
            print(f"  - {filename}")
        return
    
    # Apply patches
    print("⬆️  Patching gist files to fix typo keys...")
    payload = {
        "description": f"Fix typo keys (whowereThey → whoWereThey) in {len(files_to_patch)} files",
        "files": files_to_patch
    }
    
    r = requests.patch(GIST_URL, headers=HEADERS, json=payload, timeout=30)
    r.raise_for_status()
    print(f"✅  Successfully fixed typo keys in {len(files_to_patch)} files")

def validate_bundle_schema(bundle: dict, require_who: bool = False) -> None:
    """
    Validate that the bundle has the required schema.
    """
    print("🔍  Validating bundle schema...")
    
    required_keys = ["date", "languages"]
    for key in required_keys:
        if key not in bundle:
            sys.exit(f"❌  Missing required key: {key}")
    
    if not isinstance(bundle["languages"], dict):
        sys.exit(f"❌  'languages' must be a dictionary")
    
    for lang_code, lang_content in bundle["languages"].items():
        if not isinstance(lang_content, dict):
            sys.exit(f"❌  Language content for '{lang_code}' must be a dictionary")
        
        required_lang_keys = ["quoteOfTheDay", "interestingKnowledge", "whoWereThey"]
        for key in required_lang_keys:
            if key not in lang_content:
                sys.exit(f"❌  Missing required key '{key}' in language '{lang_code}'")
            
            if not isinstance(lang_content[key], list):
                sys.exit(f"❌  '{key}' must be a list in language '{lang_code}'")
        
        # Check whoWereThey specifically
        who_were_they = lang_content["whoWereThey"]
        if require_who and len(who_were_they) == 0:
            sys.exit(f"❌  whoWereThey is empty in language '{lang_code}' (use --require-who to enforce)")
        
        # Validate each person in whoWereThey
        for i, person in enumerate(who_were_they):
            if not isinstance(person, dict):
                sys.exit(f"❌  Person {i} in whoWereThey[{lang_code}] must be a dictionary")
            
            if "name" not in person or "bio" not in person:
                sys.exit(f"❌  Person {i} in whoWereThey[{lang_code}] missing 'name' or 'bio'")
            
            if not isinstance(person["name"], str) or not isinstance(person["bio"], str):
                sys.exit(f"❌  Person {i} in whoWereThey[{lang_code}] 'name' and 'bio' must be strings")
    
    print("✅  Bundle schema validation passed")

def generate_daily_bundle(target_date: Optional[str] = None) -> None:
    """
    Generate daily bundle for the specified date or today.
    """
    if target_date:
        try:
            # Parse date from YYYY-MM-DD format
            bundle_date = date.fromisoformat(target_date)
        except ValueError:
            sys.exit(f"❌  Invalid date format: {target_date}. Use YYYY-MM-DD")
    else:
        bundle_date = date.today()
    
    fname = f"daily_knowledge_{bundle_date:%Y_%m_%d}.json"
    date_str = f"{bundle_date:%Y-%m-%d}"
    
    print(f"📅  Generating bundle for {date_str}")
    
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
            quote_en = quote_en or latest["languages"]["en"]["quoteOfTheDay"][0]
            fact_en  = fact_en  or latest["languages"]["en"]["interestingKnowledge"][0]
        else:
            quote_en = quote_en or "Knowledge is power. – Francis Bacon"
            fact_en  = fact_en  or "Bananas are berries, but strawberries aren't."

    bundle_today = {
        "date": date_str,
        "languages": {
            "en": {
                "quoteOfTheDay":        [quote_en],
                "interestingKnowledge": [fact_en],
                "whoWereThey":          get_people('en', 2, seed=date_str)
            },
            "he": {
                "quoteOfTheDay":        ["״לא החלטה – לא התקדמות.\" – פתגם עברי"],
                "interestingKnowledge": ["לתמנון יש שלושה לבבות ותשעה מוחות."],
                "whoWereThey":          get_people('he', 2, seed=date_str)
            }
        }
    }
    
    # Validate the bundle before proceeding
    validate_bundle_schema(bundle_today, require_who=getattr(args, 'require_who', False))

    # ────────── עדכון history (שומרים עד 5 ימים) ──────────
    history.append(bundle_today)
    history = sorted(history, key=lambda b: b["date"])[-5:]

    payload = {
        "description": f"Daily bundle ({date_str}) by workflow",
        "files": {
            fname:         { "content": json.dumps(bundle_today, ensure_ascii=False, indent=2) },
            history_name:  { "content": json.dumps(history, ensure_ascii=False, indent=2) }
        }
    }

    if args.dry_run:
        print("🔍  DRY RUN - Output would be:")
        print(json.dumps(bundle_today, ensure_ascii=False, indent=2))
        return

    print("⬆️  Uploading to Gist …")
    requests.patch(GIST_URL, headers=HEADERS, json=payload, timeout=30).raise_for_status()
    print("✅  Gist updated with", fname)

# ────────── Main execution ──────────
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate and patch Knowlio daily knowledge gist")
    parser.add_argument("--date", help="Generate bundle for specific date (YYYY-MM-DD)")
    parser.add_argument("--backfill-all", action="store_true", help="Backfill all historical files with whoWereThey")
    parser.add_argument("--fix-typo-keys", action="store_true", help="Fix typo keys (whowereThey → whoWereThey) in existing gist files")
    parser.add_argument("--require-who", action="store_true", help="Fail if whoWereThey field is empty (for CI validation)")
    parser.add_argument("--dry-run", action="store_true", help="Show what would be done without making changes")
    
    args = parser.parse_args()
    
    if args.fix_typo_keys:
        fix_typo_keys()
    elif args.backfill_all:
        backfill_gist_files()
    else:
        generate_daily_bundle(args.date)
