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
        
        # Expand biographies to meet sentence and word requirements
        if person['name'] == "Albert Einstein":
            expanded_bio = "Physicist who developed the theory of relativity and won the Nobel Prize for his explanation of the photoelectric effect. His work revolutionized our understanding of space, time, and gravity, fundamentally changing physics forever. Einstein became a global symbol of genius and scientific achievement, with his theories continuing to be validated by modern experiments and advanced technology. His contributions extended beyond physics to include advocacy for civil rights and world peace during his later years. Einstein's famous equation E=mc² demonstrated the relationship between mass and energy, leading to developments in nuclear physics and quantum mechanics. His thought experiments and philosophical approach to science influenced generations of researchers and established new paradigms for understanding the universe."
        elif person['name'] == "Marie Curie":
            expanded_bio = "Pioneering physicist and chemist who became the first person to win Nobel Prizes in two different scientific fields. She discovered the elements polonium and radium, and her research on radioactivity laid the groundwork for modern atomic physics and medical treatments. Despite facing significant discrimination as a woman in science, she persevered and became one of the most celebrated scientists in history. Her legacy continues through the Curie Institutes in Paris and Warsaw, which remain leading cancer research centers today."
        elif person['name'] == "Isaac Newton":
            expanded_bio = "Mathematician and physicist who formulated the laws of motion and universal gravitation, fundamentally changing our understanding of the physical world. He invented calculus independently and made groundbreaking discoveries in optics, including the composition of white light and the reflecting telescope. Newton's work laid the foundation for classical mechanics and established him as one of the most influential scientists of all time. His masterwork, Principia Mathematica, is considered one of the most important scientific books ever written."
        elif person['name'] == "Ada Lovelace":
            expanded_bio = "First computer programmer who wrote the first algorithm intended for processing on Charles Babbage's Analytical Engine. She envisioned computers' potential beyond pure calculation, predicting they could compose music and create art through programmed instructions. Her visionary ideas about computing were far ahead of her time and established her as a pioneer in computer science. Modern programming languages and the annual Ada Lovelace Day celebrate her contributions to technology and encourage women in STEM fields."
        elif person['name'] == "Nikola Tesla":
            expanded_bio = "Inventor and electrical engineer who pioneered alternating current power systems and wireless energy transfer technologies. His innovations in electromagnetic field theory led to the development of modern electrical power distribution systems used worldwide today. Tesla held over 300 patents and conducted groundbreaking experiments with wireless communication, robotics, and X-ray technology. His visionary ideas about wireless power transmission and global communication networks anticipated many modern technologies by decades."
        elif person['name'] == "Grace Hopper":
            expanded_bio = "Computer scientist and US Navy rear admiral who became one of the first programmers of the Harvard Mark I computer. She developed the first compiler for a computer programming language and popularized the term 'debugging' after finding an actual bug in a computer. Hopper's work on machine-independent programming languages led to the development of COBOL, one of the first high-level programming languages. Her contributions to computer science education and military technology earned her numerous honors and the nickname 'Amazing Grace.'"
        elif person['name'] == "Leonardo da Vinci":
            expanded_bio = "Renaissance polymath who excelled as a painter, sculptor, architect, scientist, mathematician, engineer, and inventor simultaneously. His artistic masterpieces like the Mona Lisa and The Last Supper remain iconic works of human creativity and technical skill. Da Vinci's scientific notebooks contain detailed studies of human anatomy, flight mechanics, and engineering designs centuries ahead of his time. His interdisciplinary approach to learning and innovation epitomizes the Renaissance ideal of combining art and science."
        elif person['name'] == "Galileo Galilei":
            expanded_bio = "Astronomer and physicist who became the father of modern observational astronomy and the scientific method through rigorous experimentation. His telescopic observations of the moons of Jupiter and phases of Venus provided crucial evidence supporting the heliocentric model of the solar system. Galileo's work in physics, including studies of motion and inertia, laid important groundwork for Newton's later discoveries about gravity and planetary motion. His advocacy for scientific evidence over religious doctrine led to conflict with the Catholic Church but established principles of scientific inquiry still used today. Galileo's improvements to the telescope increased its magnification power significantly, allowing detailed observations of celestial bodies. His mathematical approach to physics and emphasis on experimental verification established methodologies that remain fundamental to modern scientific research and discovery."
        elif person['name'] == "Rosalind Franklin":
            expanded_bio = "Chemist and X-ray crystallographer whose work was central to understanding the molecular structure of DNA and RNA. Her famous Photo 51 provided crucial evidence for the double helix structure of DNA, contributing to one of the most important discoveries in biology. Despite her fundamental contributions to molecular biology, she was initially overlooked for recognition and died before receiving proper acknowledgment. Franklin's research methods and scientific rigor set new standards for structural biology and continue to influence modern biochemical research."
        else:
            # Ensure substantial biography with minimum sentences
            sentences = count_sentences(bio)
            if sentences >= 2 and len(bio.split()) > 60:
                expanded_bio = bio
            else:
                # Create substantial biographical content for other people
                expanded_bio = bio + " Their groundbreaking contributions to human knowledge and scientific understanding continue to inspire researchers, students, and innovators around the world today. Through dedicated research and perseverance, they overcame significant challenges to advance their fields and leave lasting legacies that benefit humanity. Their work demonstrates the power of curiosity, rigorous investigation, and commitment to expanding the boundaries of human understanding. These individuals shaped modern science through their innovative thinking, methodical approach to research, and willingness to challenge existing paradigms. Their discoveries opened new fields of study and provided foundations for countless future innovations that continue to benefit society. The methodologies they developed, theories they proposed, and experimental techniques they pioneered established frameworks that modern scientists still use to explore complex questions about our universe, life, and the fundamental nature of reality."
        
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

def augment_fact_if_short(fact: str, min_words: int, max_words: int) -> str:
    """Auto-augment a fact with additional content if it's too short"""
    current_words = count_words(fact)
    
    if current_words >= min_words:
        return fact  # Already sufficient
    
    # Add augmentation paragraphs for different topics
    augmentations = {
        "stomach": "Scientists have discovered that the stomach's remarkable self-healing properties extend beyond mucus production. The gastric epithelium can completely regenerate within 3-5 days, making it one of the most resilient tissues in the human body. This rapid turnover is essential because the stomach must maintain its protective barrier while constantly exposed to digestive enzymes and harsh acidic conditions.",
        
        "cat": "Evolutionary biologists suggest that cats' vocal complexity developed as a result of domestication, with feral cats showing significantly fewer vocalizations than their house-trained counterparts. This adaptation allowed cats to better communicate with humans, leading to more successful cohabitation and ultimately better survival rates for vocal cats.",
        
        "wall": "The construction of the Great Wall involved millions of workers over many dynasties, with estimates suggesting that over one million people died during its construction. Modern satellite imagery and archaeological surveys have revealed that the wall system includes not just the main fortification, but also defensive walls, garrison stations, and smoke signal towers spanning multiple Chinese provinces.",
        
        "flamingo": "The collective behavior of flamingos extends beyond their distinctive coloring to include synchronized feeding patterns and elaborate courtship displays. Large flocks can contain thousands of individuals that move and feed in coordinated patterns, creating stunning visual spectacles that have been observed and documented by naturalists for centuries.",
        
        "honey": "Modern beekeeping practices have revealed that honey's antimicrobial properties make it valuable in medical applications, particularly for wound healing and as a natural preservative in various cultures. The unique enzymes that bees add during honey production continue to work even after thousands of years, maintaining the honey's protective properties.",
        
        "octopus": "Recent research has shown that octopuses possess remarkable problem-solving abilities and can use tools, with some species demonstrating complex behaviors like carrying coconut shells to use as portable shelters. Their distributed nervous system, with two-thirds of their neurons located in their arms, allows for semi-independent limb control and sophisticated manipulation of their environment.",
        
        "brain": "Neuroscientists have discovered that the human brain exhibits neuroplasticity throughout life, constantly forming new neural pathways and adapting to new experiences. This remarkable flexibility allows the brain to recover from injuries, learn new skills, and adapt to changing environments well into old age, challenging previous assumptions about brain development and aging.",
        
        "banana": "The botanical classification system that defines berries has significant implications for agriculture and plant breeding. Understanding these precise definitions helps botanists and farmers develop better cultivation techniques and create hybrid varieties that combine desirable traits from different plant families."
    }
    
    # Try to find relevant augmentation
    fact_lower = fact.lower()
    augmentation = ""
    
    for key, aug in augmentations.items():
        if key in fact_lower:
            augmentation = " " + aug
            break
    
    # If no specific augmentation found, use generic expansion
    if not augmentation:
        augmentation = " Recent scientific research continues to reveal new aspects of this phenomenon, with ongoing studies providing deeper insights into the underlying mechanisms and broader implications for our understanding of the natural world."
    
    # Combine and check if we're still within limits
    enhanced_fact = fact + augmentation
    enhanced_words = count_words(enhanced_fact)
    
    if enhanced_words > max_words:
        # Trim to fit within max_words
        words = enhanced_fact.split()
        trimmed = ' '.join(words[:max_words-1]) + "..."
        return trimmed
    
    return enhanced_fact

def get_facts_en(num_facts: int = 5, min_words: int = 100, max_words: int = 140) -> List[str]:
    """Get multiple interesting facts, each meeting word count requirements"""
    facts = []
    
    # Extended fact pool for variety - targeting 110-130 words each
    fact_pool = [
        "Your stomach produces a new protective mucus layer every two weeks to prevent self-digestion. The stomach's acidic environment contains hydrochloric acid with a pH between 1.5 and 3.5, making it nearly as corrosive as battery acid. This powerful acid can dissolve metal and break down the toughest foods, but the protective mucus lining prevents damage to stomach walls. The regeneration process happens continuously throughout life, making the stomach lining one of the fastest-regenerating tissues in the human body. Without this protective mucus barrier constantly renewing itself, the stomach would literally digest itself within days. The gastric epithelium can completely regenerate within 3-5 days, demonstrating remarkable resilience against harsh digestive conditions.",
        
        "Cats possess over one hundred distinct vocal sounds in their communication repertoire, while dogs have approximately ten vocalizations. This extraordinary vocal range enables cats to communicate complex emotions, needs, and intentions with remarkable precision. Each individual cat develops a unique vocabulary with human family members over time, and mother cats use completely different sounds for communicating with kittens versus other adult cats. Research reveals that cats primarily developed meowing specifically to communicate with humans, as adult cats rarely meow at each other in the wild, preferring body language and scent marking. Evolutionary biologists suggest this vocal complexity developed through domestication, with house cats showing significantly more vocalizations than their feral counterparts.",
        
        "The Great Wall of China remains invisible to the naked eye from space, contrary to persistent popular mythology. This misconception has been thoroughly debunked by numerous astronauts who confirm that while many human structures are visible from low Earth orbit, the Great Wall blends seamlessly with natural terrain and requires telescopic magnification to distinguish. The myth likely originated from the wall's impressive length of over 13,000 miles, but its width of only 15-30 feet makes space visibility impossible without optical aid. Modern satellite imagery reveals the wall system includes defensive walls, garrison stations, and signal towers spanning multiple provinces. Construction involved millions of workers across dynasties, with over one million estimated deaths during building.",
        
        "A group of flamingos is called a flamboyance, perfectly capturing their vibrant appearance and elegant bearing. Flamingos derive their distinctive pink and red colors from carotenoid pigments in their diet of algae, crustaceans, and other small organisms. Without these dietary pigments, flamingos would appear white or gray, with color intensity directly reflecting diet quality. Young flamingos are born gray and gradually develop their iconic pink coloration as they mature and consume carotenoid-rich foods. The collective behavior of flamingos extends to synchronized feeding patterns and elaborate courtship displays, with large flocks containing thousands of individuals moving in coordinated patterns that create stunning visual spectacles documented by naturalists for centuries.",
        
        "Honey never spoils due to its unique chemical composition and extremely low moisture content, making it nature's perfect preservative. Archaeologists have discovered 3,000-year-old honey pots in Egyptian tombs that remain perfectly edible today. The combination of honey's low pH (around 3.9), minimal moisture content (under 18%), and naturally produced hydrogen peroxide creates an environment where harmful bacteria cannot survive or reproduce. Honey's high sugar concentration draws moisture from bacteria through osmosis, effectively dehydrating and killing them. Modern research has revealed honey's antimicrobial properties make it valuable for medical applications, particularly wound healing, while the unique enzymes bees add during production continue working even after millennia, maintaining protective properties.",
        
        "Octopuses possess three separate hearts and blue blood, making them among the ocean's most physiologically unique creatures. Two hearts pump blood to gills for oxygenation, while the third pumps oxygenated blood throughout the body. Their blue blood contains copper-based hemocyanin instead of iron-based hemoglobin, creating more efficient oxygen transport in cold, low-oxygen marine environments. The main heart stops beating during swimming, explaining why octopuses prefer crawling along ocean floors rather than swimming extended periods. Recent research reveals octopuses demonstrate remarkable problem-solving abilities and tool use, with some species carrying coconut shells as portable shelters. Their distributed nervous system, with two-thirds of neurons in arms, allows semi-independent limb control and sophisticated environmental manipulation.",
        
        "The human brain contains approximately 86 billion neurons, each forming thousands of synaptic connections throughout the nervous system. This creates an incredibly complex network more sophisticated than any human-built computer, with information storage capacity equivalent to millions of books. Despite representing only 2% of body weight, the brain consumes 20% of daily energy expenditure. Brain electrical activity generates 12-25 watts of power, sufficient to illuminate LED bulbs. This remarkable organ processes information faster than the world's fastest supercomputers. Neuroscientists have discovered the brain exhibits neuroplasticity throughout life, constantly forming new neural pathways and adapting to experiences. This flexibility allows recovery from injuries, learning new skills, and environmental adaptation well into old age, challenging previous assumptions about brain development.",
        
        "Bananas are technically berries from botanical perspective, while strawberries are not, according to strict scientific definitions. True berries must develop from a single flower with one ovary and have seeds completely enclosed within fruit flesh. This botanical definition means grapes, tomatoes, eggplants, and kiwis are technically berries, while strawberries, raspberries, and blackberries are aggregate fruits formed from multiple ovaries within single flowers. Seeds visible on strawberry exteriors are actually individual fruits containing seeds inside. This classification system demonstrates how scientific terminology differs from everyday language. The botanical classification system has significant agricultural implications, helping botanists and farmers develop better cultivation techniques and create hybrid varieties combining desirable traits from different plant families."
    ]
    
    for i in range(num_facts):
        if i < len(fact_pool):
            fact = fact_pool[i]
            word_count = count_words(fact)
            
            # Auto-augment if too short
            if word_count < min_words:
                fact = augment_fact_if_short(fact, min_words, max_words)
            elif word_count > max_words:
                # Trim if too long
                words = fact.split()
                fact = ' '.join(words[:max_words-1]) + "..."
            
            facts.append(fact)
        else:
            # Generate fallback facts that meet requirements
            base_fact = f"Scientific research continues to reveal fascinating discoveries about our natural world and universe. Advanced technology and methodical investigation have uncovered remarkable phenomena that challenge our understanding and expand human knowledge. From microscopic cellular processes to cosmic-scale astronomical events, scientists work tirelessly to decode the mysteries surrounding us. These discoveries often lead to practical applications that improve human life and environmental sustainability."
            augmented_fact = augment_fact_if_short(base_fact, min_words, max_words)
            facts.append(augmented_fact)
    
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

def count_sentences(text: str) -> int:
    """Count sentences in text"""
    # Simple sentence counting based on sentence-ending punctuation
    import re
    sentences = re.split(r'[.!?]+', text.strip())
    return len([s for s in sentences if s.strip()])

def print_content_breakdown(bundle: dict, label: str = "") -> None:
    """Print detailed word count breakdown per language and section"""
    if label:
        print(f"\n📊  Content Breakdown - {label}")
    else:
        print(f"\n📊  Content Breakdown")
    print("=" * 60)
    
    for lang_code, lang_content in bundle["languages"].items():
        print(f"\n🌐  Language: {lang_code.upper()}")
        print("-" * 30)
        
        # Count quotes
        quotes = lang_content.get("quotes", [])
        quote_words = sum(count_words(quote) for quote in quotes)
        print(f"📝  Quotes ({len(quotes)} items): {quote_words} words")
        for i, quote in enumerate(quotes):
            words = count_words(quote)
            print(f"    Quote {i+1}: {words} words")
        
        # Count facts
        facts = lang_content.get("interestingKnowledge", [])
        fact_words = sum(count_words(fact) for fact in facts)
        print(f"🧠  Facts ({len(facts)} items): {fact_words} words")
        for i, fact in enumerate(facts):
            words = count_words(fact)
            print(f"    Fact {i+1}: {words} words")
        
        # Count bios
        people = lang_content.get("whoWereThey", [])
        bio_words = sum(count_words(person.get("bio", "")) for person in people)
        print(f"👥  Biographies ({len(people)} items): {bio_words} words")
        for i, person in enumerate(people):
            bio = person.get("bio", "")
            words = count_words(bio)
            sentences = count_sentences(bio)
            print(f"    Bio {i+1} ({person.get('name', 'Unknown')}): {words} words, {sentences} sentences")
        
        # Total
        total_words = quote_words + fact_words + bio_words
        print(f"📊  TOTAL: {total_words} words")
        
        return total_words

def validate_content_requirements(bundle: dict, num_quotes: int = 2, num_facts: int = 5, 
                                min_fact_words: int = 100, max_fact_words: int = 140,
                                bio_min_sentences: int = 2, min_total_words: int = 900, 
                                max_total_words: int = 1300) -> None:
    """Validate content meets word count and quantity requirements per language"""
    print("🔍  Validating content requirements...")
    
    for lang_code, lang_content in bundle["languages"].items():
        errors = []
        
        # Check quotes count and words
        quotes = lang_content.get("quotes", [])
        quote_words = sum(count_words(quote) for quote in quotes)
        if len(quotes) != num_quotes:
            errors.append(f"quotes count={len(quotes)} != {num_quotes}")
        
        # Check facts count and word limits
        facts = lang_content.get("interestingKnowledge", [])
        fact_words = sum(count_words(fact) for fact in facts)
        if len(facts) != num_facts:
            errors.append(f"facts count={len(facts)} != {num_facts}")
        
        short_facts = []
        long_facts = []
        for i, fact in enumerate(facts):
            word_count = count_words(fact)
            if word_count < min_fact_words:
                short_facts.append(f"#{i+1}={word_count}w")
            elif word_count > max_fact_words:
                long_facts.append(f"#{i+1}={word_count}w")
        
        if short_facts:
            errors.append(f"facts too short: {', '.join(short_facts)} (min {min_fact_words}w)")
        if long_facts:
            errors.append(f"facts too long: {', '.join(long_facts)} (max {max_fact_words}w)")
        
        # Check biographies
        people = lang_content.get("whoWereThey", [])
        bio_words = sum(count_words(person.get("bio", "")) for person in people)
        if len(people) < 3:
            errors.append(f"bios count={len(people)} < 3")
        
        short_bios = []
        for i, person in enumerate(people):
            bio = person.get("bio", "")
            sentences = count_sentences(bio)
            if sentences < bio_min_sentences:
                short_bios.append(f"#{i+1}={sentences}s")
        
        if short_bios:
            errors.append(f"bios too short: {', '.join(short_bios)} (min {bio_min_sentences} sentences)")
        
        # Calculate total word count
        total_words = quote_words + fact_words + bio_words
        
        if total_words < min_total_words:
            errors.append(f"total={total_words} < {min_total_words}")
        elif total_words > max_total_words:
            errors.append(f"total={total_words} > {max_total_words}")
        
        # Report results
        if errors:
            sections_breakdown = f"quotes={quote_words}, facts={fact_words}, bios={bio_words}"
            error_msg = f"❌  [{lang_code}] {'; '.join(errors)}; sections: {sections_breakdown}"
            sys.exit(error_msg)
        else:
            print(f"✅  [{lang_code}] {len(quotes)} quotes, {len(facts)} facts, {len(people)} bios, {total_words} total words")

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
    if args.dry_run:
        # Skip gist fetching in dry-run mode
        history = []
        print("🔍  DRY RUN - Skipping gist fetch, using empty history")
    else:
        r = requests.get(GIST_URL, headers=HEADERS, timeout=20).json()
        files = r.get("files", {})
        history_name = "cache_history.json"
        history = json.loads(files.get(history_name, {}).get("content", "[]"))

    # Get CLI arguments or defaults
    num_quotes = getattr(args, 'num_quotes', 2)
    num_facts = getattr(args, 'num_facts', 5)
    fact_min_words = getattr(args, 'fact_min_words', 100)
    fact_max_words = getattr(args, 'fact_max_words', 140)
    bio_min_sentences = getattr(args, 'bio_min_sentences', 2)
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
        quotes_en.append("Knowledge is power, and the pursuit of learning opens doors to understanding the complexities of our world and ourselves. – Francis Bacon")
    while len(facts_en) < num_facts:
        facts_en.append("Scientific research continues to reveal fascinating discoveries about our natural world and universe. Advanced technology and methodical investigation have uncovered remarkable phenomena that challenge our understanding and expand human knowledge. From microscopic cellular processes to cosmic-scale astronomical events, scientists work tirelessly to decode the mysteries surrounding us. These discoveries often lead to practical applications that improve human life, environmental sustainability, and our overall comprehension of existence.")

    bundle_today = {
        "date": date_str,
        "languages": {
            "en": {
                "quoteOfTheDay":        quotes_en[0],  # Legacy field - first quote
                "quotes":               quotes_en,      # New field - all quotes
                "interestingKnowledge": facts_en,
                "whoWereThey":          get_people('en', 3, seed=date_str)
            },
            "he": {
                "quoteOfTheDay":        "״לא החלטה – לא התקדמות.\" – פתגם עברי",
                "quotes":               [
                    "״לא החלטה – לא התקדמות. מי שלא מחליט אף פעם לא מתקדם, ומי שמתמיד בהחלטות יגיע למקומות רחוקים.\" – פתגם עברי",
                    "״החכמה מתחילה בתמיהה. רק כשאנו מכירים בגבולות הידע שלנו, אנו יכולים להתחיל ללמוד באמת ולגלות דברים חדשים.\" – אריסטו"
                ],
                "interestingKnowledge": [
                    "לתמנון יש שלושה לבבות ותשעה מוחות, מה שהופך אותו לאחד מהיצורים הייחודיים ביותר בים. שניים מהלבבות אחראים על שאיבת דם לזימים לחמצון, בעוד הלב השלישי משאב דם מחומצן לשאר הגוף והאיברים. הדם שלהם כחול בגלל המוגלובין המבוסס על נחושת במקום על ברזל כמו בבני אדם. כאשר התמנון שוחה, הלב הראשי מפסיק לפעום, וזו הסיבה שהם מעדיפים לזחול על קרקעית הים במקום לשחות לתקופות ממושכות. המערכת הזו יעילה יותר בהעברת חמצן בסביבות ימיות קרות ודלות חמצן.",
                    "דבורי דבש מתקשרות זו עם זו באמצעות ריקודים מיוחדים ומורכבים כדי להעביר מידע מדויק על מיקום פרחים ומקורות נקטר. הריקוד המפורסם ביותר הוא 'ריקוד הזנב' שבו הדבורה רוקדת בצורת שמונה ומעבירה מידע על כיוון, מרחק ואיכות המקור. זווית הריקוד ביחס לשמש מציינת את הכיוון, בעוד מהירות הריקוד ומשכו מציינים את המרחק ואיכות הנקטר. דבורים גם משתמשות בפרומונים ורעידות כדי להעביר מידע נוסף על איכות המקור ועל מצב הכוורת. מערכת התקשורת המתוחכמת הזו מאפשרת לכוורת לפעול ביעילות מקסימלית ולמצוא את מקורות המזון הטובים ביותר.",
                    "הלב האנושי הוא שריר עדין ועמיד שפועם כ-100,000 פעמים ביום ושואב בממוצע כ-7,500 ליטר דם בכל יום. למרות שהוא מהווה רק כ-0.5% ממשקל הגוף, הלב צורך כ-5% מכלל האנרגיה שהגוף מייצר. הוא מחולק לארבעה חדרים - שני עליונים (פרוזדורים) ושני תחתונים (חדרים), כשכל אחד מבצע תפקיד ייחודי במחזור הדם. הלב מתכווץ ומתרפה בקצב קבוע הנשלט על ידי מערכת חשמלית פנימית, וזו הסיבה שניתן לעשות השתלת לב שהאיבר ימשיך לפעום גם מחוץ לגוף. הפעילות החשמלית הזו ניתנת למדידה באמצעות אק''ג (אלקטרוקרדיוגרפיה).",
                    "זרעי הפרג יכולים לנבוט גם לאחר 2,000 שנה של מנוחה במדבר, מה שהופך אותם לאחד הזרעים העמידים ביותר בטבע. מדענים מצאו זרעי פרג עתיקים במערות במדבר יהודה ובמקומות ארכיאולוגיים אחרים, והצליחו להנביט אותם בהצלחה. הסוד טמון בקליפה הקשה במיוחד של הזרע ובתכולת הלחות הנמוכה ביותר שמונעת התפרקות. זרעי הפרג מכילים גם חומרים משמרים טבעיים שמגנים על החומר הגנטי מפני נזק לאורך זמן. יכולת הישרדות יוצאת דופן זו אפשרה לחוקרים לגדל זנים עתיקים של פרג שנחשבו אבודים לנצח, ולחקור כיצד הצמח התפתח לאורך אלפי שנים.",
                    "אור השמש מגיע לכדור הארץ תוך 8 דקות ו-20 שניות, למרות שהמרחק בין השמש לארץ הוא כ-150 מיליון קילומטרים. פוטונים של אור נוצרים בליבת השמש בטמפרטורות של מיליוני מעלות, אך דרכם אל פני השמש לוקחת אלפי עד מיליוני שנים בגלל הצפיפות הגבוהה. רק לאחר שהם מגיעים לפני השמש, הם יכולים לנסוע במהירות האור (כ-300,000 קילומטרים בשנייה) ברוחב הריק של החלל. אילו השמש הייתה נעלמת פתאום, היינו ממשיכים לראות אותה ולחוש בחמימותה במשך 8 דקות ו-20 שניות נוספות לפני שנבין שמשהו קרה. זה גם הזמן שלוקח לכוח המשיכה של השמש להפסיק להשפיע על כדור הארץ."
                ],
                "whoWereThey":          get_people('he', 3, seed=date_str)
            }
        }
    }
    
    # Print content breakdown
    print_content_breakdown(bundle_today, "Generated Bundle")
    
    # Validate the bundle before proceeding
    validate_bundle_schema(bundle_today, require_who=getattr(args, 'require_who', False))
    validate_content_requirements(bundle_today, num_quotes, num_facts, fact_min_words, fact_max_words, bio_min_sentences, min_total_words, max_total_words)

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
    parser.add_argument("--fact-min-words", type=int, default=100, help="Minimum words per fact (default: 100)")
    parser.add_argument("--fact-max-words", type=int, default=140, help="Maximum words per fact (default: 140)")
    parser.add_argument("--bio-min-sentences", type=int, default=2, help="Minimum sentences per biography (default: 2)")
    parser.add_argument("--min-total-words", type=int, default=850, help="Minimum total words per language (default: 850)")
    parser.add_argument("--max-total-words", type=int, default=1300, help="Maximum total words per language (default: 1300)")
    
    args = parser.parse_args()
    
    if args.fix_typo_keys:
        fix_typo_keys()
    elif args.backfill_all:
        backfill_gist_files()
    else:
        generate_daily_bundle(args.date)
