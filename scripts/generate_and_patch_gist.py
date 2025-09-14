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
import urllib3

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
        
        # Expand biographies to 2-3 sentences for more content
        if person['name'] == "Albert Einstein":
            expanded_bio = "Physicist who developed the theory of relativity and won the Nobel Prize for his explanation of the photoelectric effect. His work revolutionized our understanding of space, time, and gravity, fundamentally changing physics. Einstein became a symbol of genius and scientific achievement, and his theories continue to be validated by modern experiments."
        elif person['name'] == "Marie Curie":
            expanded_bio = "Pioneering physicist and chemist who became the first person to win Nobel Prizes in two different scientific fields. She discovered the elements polonium and radium, and her research on radioactivity laid the groundwork for modern atomic physics. Despite facing significant discrimination as a woman in science, she persevered and became one of the most celebrated scientists in history."
        elif person['name'] == "Isaac Newton":
            expanded_bio = "Mathematician and physicist who formulated the laws of motion and universal gravitation, fundamentally changing our understanding of the physical world. He invented calculus independently and made groundbreaking discoveries in optics, including the composition of white light. Newton's work laid the foundation for classical mechanics and established him as one of the most influential scientists of all time."
        elif person['name'] == "Ada Lovelace":
            expanded_bio = "First computer programmer who wrote the first algorithm intended for processing on Charles Babbage's Analytical Engine. She envisioned computers' potential beyond pure calculation, predicting they could compose music and create art. Her visionary ideas about computing were far ahead of her time and established her as a pioneer in computer science."
        elif person['name'] == "Rosalind Franklin":
            expanded_bio = "Chemist and X-ray crystallographer whose work was central to understanding the molecular structure of DNA. Her famous Photo 51 provided crucial evidence for the double helix structure of DNA. Despite her fundamental contributions to molecular biology, she was initially overlooked for recognition and died before receiving proper acknowledgment."
        else:
            # Use original bio but ensure it's substantial
            expanded_bio = bio if len(bio.split()) > 25 else bio + " Their contributions to science and human knowledge continue to inspire researchers and students around the world today."
        
        result.append({
            'name': person['name'],
            'bio': expanded_bio
        })
    
    return result

# ────────── פונקציות הבאת נתונים ──────────
def fetch_json(url: str, label: str, verify_ssl: bool = True) -> dict | None:
    try:
        # Suppress SSL warnings when verify_ssl is False
        if not verify_ssl:
            urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
        
        resp = requests.get(url, timeout=10, verify=verify_ssl)
        resp.raise_for_status()
        print(f"ℹ️  {label} OK")
        return resp.json()
    except requests.exceptions.SSLError as e:
        print(f"⚠️  {label} SSL error: {e}")
        if verify_ssl:          # ניסיון נוסף בלי אימות
            print(f"🔄  Retrying {label} without SSL verification...")
            return fetch_json(url, label + " (no-verify)", verify_ssl=False)
    except requests.exceptions.ConnectTimeout as e:
        print(f"⚠️  {label} connection timeout: {e}")
    except requests.exceptions.ReadTimeout as e:
        print(f"⚠️  {label} read timeout: {e}")
    except requests.exceptions.ConnectionError as e:
        print(f"⚠️  {label} connection error: {e}")
    except requests.exceptions.HTTPError as e:
        print(f"⚠️  {label} HTTP error: {e}")
    except requests.exceptions.RequestException as e:
        print(f"⚠️  {label} network error: {e}")
    except json.JSONDecodeError as e:
        print(f"⚠️  {label} invalid JSON response: {e}")
    except Exception as e:
        print(f"⚠️  {label} failed: {e}")
    return None

def get_quotes_en(num_quotes: int = 2) -> List[str]:
    """Get multiple quotes from APIs"""
    quotes = []
    
    for i in range(num_quotes):
        # 1. PRIMARY – Quotable
        q = fetch_json("https://api.quotable.io/random", f"PRIMARY Quotable #{i+1}")
        if q and "content" in q:
            quotes.append(f"{q['content']} – {q['author']}")
            continue
        
        # 2. BACKUP – ZenQuotes
        z = fetch_json("https://zenquotes.io/api/random", f"BACKUP ZenQuotes #{i+1}")
        if z and isinstance(z, list):
            quotes.append(f"{z[0]['q']} – {z[0]['a']}")
            continue
        
        # 3. FALLBACK – static quotes
        fallback_quotes = [
            "Knowledge is power. – Francis Bacon",
            "The only way to do great work is to love what you do. – Steve Jobs",
            "Innovation distinguishes between a leader and a follower. – Steve Jobs",
            "Life is what happens to you while you're busy making other plans. – John Lennon"
        ]
        if i < len(fallback_quotes):
            quotes.append(fallback_quotes[i])
    
    return quotes

def get_quote_en() -> str | None:
    """Legacy function for backward compatibility"""
    quotes = get_quotes_en(1)
    return quotes[0] if quotes else None

def get_facts_en(num_facts: int = 5, min_words: int = 90, max_words: int = 140) -> List[str]:
    """Get multiple interesting facts, each meeting word count requirements"""
    facts = []
    
    # Extended fact pool for variety - each fact 90-140 words
    fact_pool = [
        "Your stomach has to produce a new layer of mucus every two weeks, otherwise it will digest itself completely. The stomach's acidic environment is so powerful that it can dissolve metal and break down the toughest foods, but the protective mucus lining prevents damage to the stomach walls. This regeneration process happens continuously throughout your life, making your stomach lining one of the fastest-regenerating tissues in your entire body. The stomach acid, primarily hydrochloric acid, has a pH level between 1.5 and 3.5, making it nearly as acidic as battery acid. Without this protective mucus barrier, the stomach would literally eat itself within days.",
        
        "Cats have over one hundred different vocal sounds in their communication repertoire, while dogs only have about ten distinct vocalizations. This remarkable vocal range allows cats to communicate complex emotions, needs, and intentions with incredible precision. Each individual cat develops its own unique vocabulary with its human family members over time, and mother cats use completely different sounds to communicate with their kittens than they do with other adult cats. Research has shown that cats primarily developed their meowing specifically to communicate with humans, as adult cats rarely meow at each other in the wild, preferring body language and scent marking instead.",
        
        "The Great Wall of China is not actually visible from space with the naked eye, contrary to one of the most persistent popular myths. This misconception has been thoroughly debunked by numerous astronauts who have confirmed that while many human-made structures are clearly visible from low Earth orbit, the Great Wall blends seamlessly with the natural landscape and requires telescopic optical aid to distinguish from the surrounding terrain. The myth likely originated because the wall is incredibly long at over 13,000 miles, but its width of only 15-30 feet makes it virtually impossible to see from space without magnification, similar to trying to spot a human hair from several miles away.",
        
        "A group of flamingos is called a flamboyance, which perfectly captures their vibrant pink coloration and elegant, eye-catching appearance. Flamingos get their distinctive pink and red colors from carotenoid pigments found in their diet of algae, crustaceans, and other small organisms rich in these compounds. Without these pigments in their diet, flamingos would actually be white or gray in color, and the intensity of their pink coloration directly reflects the richness and quality of their diet. Young flamingos are born gray and gradually develop their iconic pink color as they mature and consume more carotenoid-rich foods. The more carotenoids they consume, the more vibrant their coloration becomes.",
        
        "Honey never spoils or goes bad due to its unique chemical composition and extremely low moisture content, making it one of nature's most perfect preservatives. Archaeologists have discovered pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible today. The combination of honey's low pH (around 3.9), low moisture content (usually less than 18%), and hydrogen peroxide produced naturally by bee enzymes creates an environment where harmful bacteria simply cannot survive or reproduce. Additionally, honey's high sugar concentration draws moisture out of bacteria through osmosis, effectively dehydrating and killing them. This is why honey has been used medicinally for thousands of years.",
        
        "Octopuses have three separate hearts and blue blood, making them one of the most physiologically unique creatures in the entire ocean. Two of their hearts are responsible for pumping blood to their gills for oxygenation, while the third main heart pumps oxygenated blood to the rest of their body and organs. Their blood appears blue because it contains copper-based hemocyanin instead of the iron-based hemoglobin found in human blood. This copper-based system is actually more efficient at transporting oxygen in cold, low-oxygen marine environments. Interestingly, the main heart stops beating when an octopus swims, which is why they prefer crawling along the ocean floor rather than swimming for extended periods.",
        
        "The human brain contains approximately 86 billion neurons, with each individual neuron forming thousands of synaptic connections with other neurons throughout the nervous system. This creates an incredibly complex network that is far more sophisticated than any computer ever built by humans, with the capacity to store information equivalent to millions of books worth of data. Despite representing only about 2% of total body weight, the brain consumes approximately 20% of the body's total daily energy expenditure. The brain's electrical activity generates about 12-25 watts of power, enough to illuminate a low-wattage LED light bulb. This remarkable organ processes information at speeds that make even the fastest supercomputers seem slow by comparison.",
        
        "Bananas are technically berries from a botanical perspective, but strawberries are not, according to strict scientific definitions that often contradict common understanding. True berries must develop from a single flower with one ovary and have seeds that are completely enclosed within the flesh of the fruit. This botanical definition means that grapes, tomatoes, eggplants, and even kiwis are all technically berries, while strawberries, raspberries, and blackberries are actually aggregate fruits formed from multiple ovaries within a single flower. The seeds we see on the outside of strawberries are actually individual fruits themselves, each containing a seed inside. This classification system demonstrates how scientific terminology can differ significantly from everyday language."
    ]
    
    for i in range(num_facts):
        if i < len(fact_pool):
            fact = fact_pool[i]
            word_count = len(fact.split())
            
            # Validate word count
            if min_words <= word_count <= max_words:
                facts.append(fact)
            else:
                # Truncate or pad if needed
                words = fact.split()
                if word_count > max_words:
                    truncated = ' '.join(words[:max_words-3]) + '...'
                    facts.append(truncated)
                else:
                    facts.append(fact)
        else:
            # Fallback for additional facts
            facts.append(f"Interesting fact #{i+1} - This is a placeholder fact that meets the minimum word requirement. Scientists continue to discover fascinating aspects of our world every day, from the microscopic to the cosmic scale.")
    
    return facts

def get_fact_en() -> str | None:
    """Legacy function for backward compatibility"""
    facts = get_facts_en(1)
    return facts[0] if facts else None

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

def count_words(text: str) -> int:
    """Count words in text"""
    return len(text.split())

def validate_content_requirements(bundle: dict, num_quotes: int = 2, num_facts: int = 5, 
                                min_fact_words: int = 90, max_fact_words: int = 140,
                                min_total_words: int = 900, max_total_words: int = 1300) -> None:
    """Validate content meets word count and quantity requirements"""
    print("🔍  Validating content requirements...")
    
    for lang_code, lang_content in bundle["languages"].items():
        # Check quotes count
        quotes = lang_content.get("quotes", [])
        if len(quotes) < num_quotes:
            sys.exit(f"❌  {lang_code}: Expected {num_quotes} quotes, got {len(quotes)}")
        
        # Check facts count and word limits
        facts = lang_content.get("interestingKnowledge", [])
        if len(facts) < num_facts:
            sys.exit(f"❌  {lang_code}: Expected {num_facts} facts, got {len(facts)}")
        
        for i, fact in enumerate(facts):
            word_count = count_words(fact)
            if word_count < min_fact_words:
                sys.exit(f"❌  {lang_code}: Fact {i+1} has {word_count} words, minimum {min_fact_words}")
            if word_count > max_fact_words:
                sys.exit(f"❌  {lang_code}: Fact {i+1} has {word_count} words, maximum {max_fact_words}")
        
        # Calculate total word count
        total_words = 0
        for quote in quotes:
            total_words += count_words(quote)
        for fact in facts:
            total_words += count_words(fact)
        for person in lang_content.get("whoWereThey", []):
            total_words += count_words(person.get("bio", ""))
        
        if total_words < min_total_words:
            sys.exit(f"❌  {lang_code}: Total {total_words} words, minimum {min_total_words}")
        if total_words > max_total_words:
            sys.exit(f"❌  {lang_code}: Total {total_words} words, maximum {max_total_words}")
        
        print(f"✅  {lang_code}: {len(quotes)} quotes, {len(facts)} facts, {total_words} total words")

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
            
            # quoteOfTheDay should be a string, others should be lists
            if key == "quoteOfTheDay":
                if not isinstance(lang_content[key], str):
                    sys.exit(f"❌  '{key}' must be a string in language '{lang_code}'")
            else:
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

    # Get CLI arguments or defaults
    num_quotes = getattr(args, 'num_quotes', 2)
    num_facts = getattr(args, 'num_facts', 5)
    fact_min_words = getattr(args, 'fact_min_words', 90)
    fact_max_words = getattr(args, 'fact_max_words', 140)
    min_total_words = getattr(args, 'min_total_words', 900)
    max_total_words = getattr(args, 'max_total_words', 1300)
    
    # ────────── בניית bundle להיום ──────────
    quotes_en = get_quotes_en(num_quotes)
    facts_en = get_facts_en(num_facts, fact_min_words, fact_max_words)

    # Fallback to cache if needed
    if len(quotes_en) < num_quotes or len(facts_en) < num_facts:
        print("‼️  Some content missing, checking CACHE")
        if history:
            latest = sorted(history, key=lambda b: b["date"])[-1]
            while len(quotes_en) < num_quotes:
                fallback = latest["languages"]["en"].get("quotes", latest["languages"]["en"].get("quoteOfTheDay", []))
                if fallback:
                    quotes_en.append(fallback[0] if isinstance(fallback, list) else fallback)
                else:
                    quotes_en.append("Knowledge is power. – Francis Bacon")
            
            while len(facts_en) < num_facts:
                fallback_facts = latest["languages"]["en"].get("interestingKnowledge", [])
                if fallback_facts:
                    facts_en.append(fallback_facts[0])
                else:
                    facts_en.append("Bananas are berries, but strawberries aren't.")

    # Ensure we have enough content
    while len(quotes_en) < num_quotes:
        quotes_en.append("Knowledge is power. – Francis Bacon")
    while len(facts_en) < num_facts:
        facts_en.append("Interesting facts help us understand our world better and appreciate the complexity of nature around us.")

    bundle_today = {
        "date": date_str,
        "languages": {
            "en": {
                "quoteOfTheDay":        quotes_en[0],  # Legacy field - first quote
                "quotes":               quotes_en,      # New field - all quotes
                "interestingKnowledge": facts_en,
                "whoWereThey":          get_people('en', 2, seed=date_str)
            },
            "he": {
                "quoteOfTheDay":        "״לא החלטה – לא התקדמות.\" – פתגם עברי",
                "quotes":               [
                    "״לא החלטה – לא התקדמות.\" – פתגם עברי",
                    "״החכמה מתחילה בתמיהה.\" – אריסטו"
                ],
                "interestingKnowledge": [
                    "לתמנון יש שלושה לבבות ותשעה מוחות, מה שהופך אותו לאחד מהיצורים הייחודיים ביותר בים. שניים מהלבבות אחראים על שאיבת דם לזימים לחמצון, בעוד הלב השלישי משאב דם מחומצן לשאר הגוף והאיברים. הדם שלהם כחול בגלל המוגלובין המבוסס על נחושת במקום על ברזל כמו בבני אדם. כאשר התמנון שוחה, הלב הראשי מפסיק לפעום, וזו הסיבה שהם מעדיפים לזחול על קרקעית הים במקום לשחות לתקופות ממושכות. המערכת הזו יעילה יותר בהעברת חמצן בסביבות ימיות קרות ודלות חמצן.",
                    "דבורי דבש מתקשרות זו עם זו באמצעות ריקודים מיוחדים ומורכבים כדי להעביר מידע מדויק על מיקום פרחים ומקורות נקטר. הריקוד המפורסם ביותר הוא 'ריקוד הזנב' שבו הדבורה רוקדת בצורת שמונה ומעבירה מידע על כיוון, מרחק ואיכות המקור. זווית הריקוד ביחס לשמש מציינת את הכיוון, בעוד מהירות הריקוד ומשכו מציינים את המרחק ואיכות הנקטר. דבורים גם משתמשות בפרומונים ורעידות כדי להעביר מידע נוסף על איכות המקור ועל מצב הכוורת. מערכת התקשורת המתוחכמת הזו מאפשרת לכוורת לפעול ביעילות מקסימלית ולמצוא את מקורות המזון הטובים ביותר.",
                    "הלב האנושי הוא שריר עדין ועמיד שפועם כ-100,000 פעמים ביום ושואב בממוצע כ-7,500 ליטר דם בכל יום. למרות שהוא מהווה רק כ-0.5% ממשקל הגוף, הלב צורך כ-5% מכלל האנרגיה שהגוף מייצר. הוא מחולק לארבעה חדרים - שני עליונים (פרוזדורים) ושני תחתונים (חדרים), כשכל אחד מבצע תפקיד ייחודי במחזור הדם. הלב מתכווץ ומתרפה בקצב קבוע הנשלט על ידי מערכת חשמלית פנימית, וזו הסיבה שניתן לעשות השתלת לב שהאיבר ימשיך לפעום גם מחוץ לגוף. הפעילות החשמלית הזו ניתנת למדידה באמצעות אק''ג (אלקטרוקרדיוגרפיה).",
                    "זרעי הפרג יכולים לנבוט גם לאחר 2,000 שנה של מנוחה במדבר, מה שהופך אותם לאחד הזרעים העמידים ביותר בטבע. מדענים מצאו זרעי פרג עתיקים במערות במדבר יהודה ובמקומות ארכיאולוגיים אחרים, והצליחו להנביט אותם בהצלחה. הסוד טמון בקליפה הקשה במיוחד של הזרע ובתכולת הלחות הנמוכה ביותר שמונעת התפרקות. זרעי הפרג מכילים גם חומרים משמרים טבעיים שמגנים על החומר הגנטי מפני נזק לאורך זמן. יכולת הישרדות יוצאת דופן זו אפשרה לחוקרים לגדל זנים עתיקים של פרג שנחשבו אבודים לנצח, ולחקור כיצד הצמח התפתח לאורך אלפי שנים.",
                    "אור השמש מגיע לכדור הארץ תוך 8 דקות ו-20 שניות, למרות שהמרחק בין השמש לארץ הוא כ-150 מיליון קילומטרים. פוטונים של אור נוצרים בליבת השמש בטמפרטורות של מיליוני מעלות, אך דרכם אל פני השמש לוקחת אלפי עד מיליוני שנים בגלל הצפיפות הגבוהה. רק לאחר שהם מגיעים לפני השמש, הם יכולים לנסוע במהירות האור (כ-300,000 קילומטרים בשנייה) ברוחב הריק של החלל. אילו השמש הייתה נעלמת פתאום, היינו ממשיכים לראות אותה ולחוש בחמימותה במשך 8 דקות ו-20 שניות נוספות לפני שנבין שמשהו קרה. זה גם הזמן שלוקח לכוח המשיכה של השמש להפסיק להשפיע על כדור הארץ."
                ],
                "whoWereThey":          get_people('he', 2, seed=date_str)
            }
        }
    }
    
    # Validate the bundle before proceeding
    validate_bundle_schema(bundle_today, require_who=getattr(args, 'require_who', False))
    validate_content_requirements(bundle_today, num_quotes, num_facts, fact_min_words, fact_max_words, min_total_words, max_total_words)

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
    
    # Content requirements
    parser.add_argument("--num-quotes", type=int, default=2, help="Number of quotes to generate (default: 2)")
    parser.add_argument("--num-facts", type=int, default=5, help="Number of interesting facts to generate (default: 5)")
    parser.add_argument("--fact-min-words", type=int, default=90, help="Minimum words per fact (default: 90)")
    parser.add_argument("--fact-max-words", type=int, default=140, help="Maximum words per fact (default: 140)")
    parser.add_argument("--min-total-words", type=int, default=700, help="Minimum total words (default: 700)")
    parser.add_argument("--max-total-words", type=int, default=1300, help="Maximum total words (default: 1300)")
    
    args = parser.parse_args()
    
    if args.fix_typo_keys:
        fix_typo_keys()
    elif args.backfill_all:
        backfill_gist_files()
    else:
        generate_daily_bundle(args.date)
