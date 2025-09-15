#!/usr/bin/env python
"""
Test content requirements for Knowlio daily bundles
"""

import sys
import os
import unittest
from datetime import date

# Add parent directory to path to import the main module
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from generate_and_patch_gist import (
    validate_content_requirements, 
    validate_bundle_schema,
    count_words, 
    count_sentences,
    print_content_breakdown
)

class TestContentRequirements(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures"""
        self.valid_bundle_en = {
            "date": "2025-09-14",
            "languages": {
                "en": {
                    "quoteOfTheDay": "Knowledge is power. – Francis Bacon",
                    "quotes": [
                        "Knowledge is power. – Francis Bacon",
                        "The only way to do great work is to love what you do. – Steve Jobs"
                    ],
                    "interestingKnowledge": [
                        "The human brain contains approximately 86 billion neurons, each forming thousands of synaptic connections throughout the nervous system. This creates an incredibly complex network more sophisticated than any human-built computer, with information storage capacity equivalent to millions of books. Despite representing only 2% of body weight, the brain consumes 20% of daily energy expenditure. Brain electrical activity generates 12-25 watts of power, sufficient to illuminate LED bulbs. This remarkable organ processes information faster than the world's fastest supercomputers. Neuroscientists have discovered the brain exhibits neuroplasticity throughout life, constantly forming new neural pathways and adapting to experiences. This flexibility allows recovery from injuries and learning new skills throughout life.",
                        "Honey never spoils due to its unique chemical composition and extremely low moisture content, making it nature's perfect preservative. Archaeologists have discovered 3,000-year-old honey pots in Egyptian tombs that remain perfectly edible today. The combination of honey's low pH (around 3.9), minimal moisture content (under 18%), and naturally produced hydrogen peroxide creates an environment where harmful bacteria cannot survive or reproduce. Honey's high sugar concentration draws moisture from bacteria through osmosis, effectively dehydrating and killing them. Modern research has revealed honey's antimicrobial properties make it valuable for medical applications, particularly wound healing, while the unique enzymes bees add during production continue working even after millennia.",
                        "Octopuses possess three separate hearts and blue blood, making them among the ocean's most physiologically unique creatures. Two hearts pump blood to gills for oxygenation, while the third pumps oxygenated blood throughout the body. Their blue blood contains copper-based hemocyanin instead of iron-based hemoglobin, creating more efficient oxygen transport in cold, low-oxygen marine environments. The main heart stops beating during swimming, explaining why octopuses prefer crawling along ocean floors rather than swimming extended periods. Recent research reveals octopuses demonstrate remarkable problem-solving abilities and tool use, with some species carrying coconut shells as portable shelters. Their distributed nervous system, with two-thirds of neurons in arms, allows semi-independent limb control.",
                        "Cats possess over one hundred distinct vocal sounds in their communication repertoire, while dogs have approximately ten vocalizations. This extraordinary vocal range enables cats to communicate complex emotions, needs, and intentions with remarkable precision. Each individual cat develops a unique vocabulary with human family members over time, and mother cats use completely different sounds for communicating with kittens versus other adult cats. Research reveals that cats primarily developed meowing specifically to communicate with humans, as adult cats rarely meow at each other in the wild, preferring body language and scent marking instead. Evolutionary biologists suggest this vocal complexity developed through domestication, with house cats showing significantly more vocalizations than their feral counterparts.",
                        "The Great Wall of China remains invisible to the naked eye from space, contrary to persistent popular mythology. This misconception has been thoroughly debunked by numerous astronauts who confirm that while many human structures are visible from low Earth orbit, the Great Wall blends seamlessly with natural terrain and requires telescopic magnification to distinguish. The myth likely originated from the wall's impressive length of over 13,000 miles, but its width of only 15-30 feet makes space visibility impossible without optical aid. Modern satellite imagery reveals the wall system includes defensive walls, garrison stations, and signal towers spanning multiple provinces. Construction involved millions of workers across dynasties, with over one million estimated deaths during building."
                    ],
                    "whoWereThey": [
                        {
                            "name": "Albert Einstein",
                            "bio": "Physicist who developed the theory of relativity and won the Nobel Prize for his explanation of the photoelectric effect. His work revolutionized our understanding of space, time, and gravity, fundamentally changing physics forever. Einstein became a global symbol of genius and scientific achievement, with his theories continuing to be validated by modern experiments. His contributions extended beyond physics to include advocacy for civil rights and world peace during his later years."
                        },
                        {
                            "name": "Marie Curie",
                            "bio": "Pioneering physicist and chemist who became the first person to win Nobel Prizes in two different scientific fields. She discovered the elements polonium and radium, and her research on radioactivity laid the groundwork for modern atomic physics and medical treatments. Despite facing significant discrimination as a woman in science, she persevered and became one of the most celebrated scientists in history. Her legacy continues through the Curie Institutes in Paris and Warsaw, which remain leading cancer research centers today."
                        }
                    ]
                }
            }
        }
        
        self.valid_bundle_he = {
            "date": "2025-09-14",
            "languages": {
                "he": {
                    "quoteOfTheDay": "״לא החלטה – לא התקדמות.\" – פתגם עברי",
                    "quotes": [
                        "״לא החלטה – לא התקדמות.\" – פתגם עברי",
                        "״החכמה מתחילה בתמיהה.\" – אריסטו"
                    ],
                    "interestingKnowledge": [
                        "לתמנון יש שלושה לבבות ותשעה מוחות, מה שהופך אותו לאחד מהיצורים הייחודיים ביותר בים. שניים מהלבבות אחראים על שאיבת דם לזימים לחמצון, בעוד הלב השלישי משאב דם מחומצן לשאר הגוף והאיברים. הדם שלהם כחול בגלל המוגלובין המבוסס על נחושת במקום על ברזל כמו בבני אדם. כאשר התמנון שוחה, הלב הראשי מפסיק לפעום, וזו הסיבה שהם מעדיפים לזחול על קרקעית הים במקום לשחות לתקופות ממושכות. המערכת הזו יעילה יותר בהעברת חמצן בסביבות ימיות קרות ודלות חמצן ומאפשרת לתמנון לשרוד בתנאים קשים.",
                        "דבורי דבש מתקשרות זו עם זו באמצעות ריקודים מיוחדים ומורכבים כדי להעביר מידע מדויק על מיקום פרחים ומקורות נקטר. הריקוד המפורסם ביותר הוא 'ריקוד הזנב' שבו הדבורה רוקדת בצורת שמונה ומעבירה מידע על כיוון, מרחק ואיכות המקור. זווית הריקוד ביחס לשמש מציינת את הכיוון, בעוד מהירות הריקוד ומשכו מציינים את המרחק ואיכות הנקטר. דבורים גם משתמשות בפרומונים ורעידות כדי להעביר מידע נוסף על איכות המקור ועל מצב הכוורת. מערכת התקשורת המתוחכמת הזו מאפשרת לכוורת לפעול ביעילות מקסימלית ולמצוא את מקורות המזון הטובים ביותר באזור.",
                        "הלב האנושי הוא שריר עדין ועמיד שפועם כ-100,000 פעמים ביום ושואב בממוצע כ-7,500 ליטר דם בכל יום. למרות שהוא מהווה רק כ-0.5% ממשקל הגוף, הלב צורך כ-5% מכלל האנרגיה שהגוף מייצר. הוא מחולק לארבעה חדרים - שני עליונים (פרוזדורים) ושני תחתונים (חדרים), כשכל אחד מבצע תפקיד ייחודי במחזור הדם. הלב מתכווץ ומתרפה בקצב קבוע הנשלט על ידי מערכת חשמלית פנימית, וזו הסיבה שניתן לעשות השתלת לב שהאיבר ימשיך לפעום גם מחוץ לגוף. הפעילות החשמלית הזו ניתנת למדידה באמצעות אק''ג (אלקטרוקרדיוגרפיה) ומספקת מידע חשוב על בריאות הלב.",
                        "זרעי הפרג יכולים לנבוט גם לאחר 2,000 שנה של מנוחה במדבר, מה שהופך אותם לאחד הזרעים העמידים ביותר בטבע. מדענים מצאו זרעי פרג עתיקים במערות במדבר יהודה ובמקומות ארכיאולוגיים אחרים, והצליחו להנביט אותם בהצלחה. הסוד טמון בקליפה הקשה במיוחד של הזרע ובתכולת הלחות הנמוכה ביותר שמונעת התפרקות. זרעי הפרג מכילים גם חומרים משמרים טבעיים שמגנים על החומר הגנטי מפני נזק לאורך זמן. יכולת הישרדות יוצאת דופן זו אפשרה לחוקרים לגדל זנים עתיקים של פרג שנחשבו אבודים לנצח, ולחקור כיצד הצמח התפתח לאורך אלפי שנים ברחבי האזור.",
                        "אור השמש מגיע לכדור הארץ תוך 8 דקות ו-20 שניות, למרות שהמרחק בין השמש לארץ הוא כ-150 מיליון קילומטרים. פוטונים של אור נוצרים בליבת השמש בטמפרטורות של מיליוני מעלות, אך דרכם אל פני השמש לוקחת אלפי עד מיליוני שנים בגלל הצפיפות הגבוהה. רק לאחר שהם מגיעים לפני השמש, הם יכולים לנסוע במהירות האור (כ-300,000 קילומטרים בשנייה) ברוחב הריק של החלל. אילו השמש הייתה נעלמת פתאום, היינו ממשיכים לראות אותה ולחוש בחמימותה במשך 8 דקות ו-20 שניות נוספות לפני שנבין שמשהו קרה. זה גם הזמן שלוקח לכוח המשיכה של השמש להפסיק להשפיע על כדור הארץ ולשנות את מסלול הפלנטות."
                    ],
                    "whoWereThey": [
                        {
                            "name": "אלברט איינשטיין",
                            "bio": "פיזיקאי שפיתח את תורת היחסות וזכה בפרס נובל על הסברו לאפקט הפוטואלקטרי. עבודתו חוללה מהפכה בהבנתנו את המרחב, הזמן והכבידה, ושינתה את הפיזיקה לנצח. איינשטיין הפך לסמל עולמי של גאונות והישג מדעי, כשהתיאוריות שלו ממשיכות להתאמת על ידי ניסויים מודרניים. תרומותיו התרחבו מעבר לפיזיקה וכללו הסברות למען זכויות אזרח ושלום עולמי בשנותיו המאוחרות."
                        },
                        {
                            "name": "מארי קירי",
                            "bio": "פיזיקאית וכימאית חלוצה שהפכה לאדם הראשון שזכה בפרס נובל בשני תחומים מדעיים שונים. היא גילתה את היסודות פולוניום ורדיום, והמחקר שלה על רדיואקטיביות הניח את היסודות לפיזיקה אטומית מודרנית וטיפולים רפואיים. למרות שהתמודדה עם אפליה משמעותית כאישה במדע, היא התמידה והפכה לאחת המדענים המהוללים בהיסטוריה. המורשת שלה נמשכת דרך מכוני קירי בפריז ובוורשה, שנותרים מרכזי מחקר מובילים בסרטן עד היום."
                        }
                    ]
                }
            }
        }
        
        # Bundle with insufficient word count (656 words) to test failure case
        self.failing_bundle = {
            "date": "2025-09-14",
            "languages": {
                "en": {
                    "quoteOfTheDay": "Short quote.",
                    "quotes": ["Short quote.", "Another short one."],
                    "interestingKnowledge": [
                        "Short fact one that doesn't meet the minimum word requirement set for this system.",
                        "Short fact two that also fails to meet the minimum word requirement for content validation.",
                        "Short fact three continuing the pattern of insufficient word count.",
                        "Short fact four with similarly inadequate length.",
                        "Short fact five completing the set of brief facts."
                    ],
                    "whoWereThey": [
                        {"name": "Person One", "bio": "Short bio."},
                        {"name": "Person Two", "bio": "Another short bio."}
                    ]
                }
            }
        }

    def test_word_counting(self):
        """Test word counting function"""
        self.assertEqual(count_words("Hello world"), 2)
        self.assertEqual(count_words(""), 0)
        self.assertEqual(count_words("Single"), 1)
        self.assertEqual(count_words("Multiple words in this sentence"), 5)

    def test_sentence_counting(self):
        """Test sentence counting function"""
        self.assertEqual(count_sentences("Hello world."), 1)
        self.assertEqual(count_sentences("First sentence. Second sentence!"), 2)
        self.assertEqual(count_sentences("Question? Answer. Exclamation!"), 3)
        self.assertEqual(count_sentences(""), 0)

    def test_valid_english_bundle(self):
        """Test that a valid English bundle passes validation"""
        try:
            validate_content_requirements(
                self.valid_bundle_en,
                num_quotes=2,
                num_facts=5,
                min_fact_words=100,
                max_fact_words=140,
                bio_min_sentences=2,
                min_total_words=900,
                max_total_words=1300
            )
            # Should not raise any exception
        except SystemExit:
            self.fail("Valid English bundle should pass validation")

    def test_valid_hebrew_bundle(self):
        """Test that a valid Hebrew bundle passes validation"""
        try:
            validate_content_requirements(
                self.valid_bundle_he,
                num_quotes=2,
                num_facts=5,
                min_fact_words=100,
                max_fact_words=140,
                bio_min_sentences=2,
                min_total_words=900,
                max_total_words=1300
            )
            # Should not raise any exception
        except SystemExit:
            self.fail("Valid Hebrew bundle should pass validation")

    def test_failing_bundle_shows_breakdown(self):
        """Test that failing bundle shows proper error breakdown"""
        with self.assertRaises(SystemExit) as cm:
            validate_content_requirements(
                self.failing_bundle,
                num_quotes=2,
                num_facts=5,
                min_fact_words=100,
                max_fact_words=140,
                bio_min_sentences=2,
                min_total_words=900,
                max_total_words=1300
            )
        
        # The error message should contain section breakdown
        error_message = str(cm.exception)
        self.assertIn("sections:", error_message)
        self.assertIn("quotes=", error_message)
        self.assertIn("facts=", error_message)
        self.assertIn("bios=", error_message)

    def test_schema_validation(self):
        """Test bundle schema validation"""
        try:
            validate_bundle_schema(self.valid_bundle_en)
            validate_bundle_schema(self.valid_bundle_he)
            # Should not raise any exception
        except SystemExit:
            self.fail("Valid bundles should pass schema validation")

    def test_content_breakdown_output(self):
        """Test that content breakdown prints without errors"""
        try:
            # Redirect stdout to capture output
            import io
            import contextlib
            
            f = io.StringIO()
            with contextlib.redirect_stdout(f):
                print_content_breakdown(self.valid_bundle_en, "Test Bundle")
            
            output = f.getvalue()
            self.assertIn("Content Breakdown", output)
            self.assertIn("EN", output)
            self.assertIn("Quotes", output)
            self.assertIn("Facts", output)
            self.assertIn("Biographies", output)
            
        except Exception as e:
            self.fail(f"Content breakdown should work without errors: {e}")

    def test_per_language_validation(self):
        """Test that validation is applied per language, not globally"""
        # Create a bundle with both languages where English is valid but Hebrew is not
        mixed_bundle = {
            "date": "2025-09-14",
            "languages": {
                "en": self.valid_bundle_en["languages"]["en"],
                "he": self.failing_bundle["languages"]["en"]  # Use failing content for Hebrew
            }
        }
        
        with self.assertRaises(SystemExit) as cm:
            validate_content_requirements(
                mixed_bundle,
                num_quotes=2,
                num_facts=5,
                min_fact_words=100,
                max_fact_words=140,
                bio_min_sentences=2,
                min_total_words=900,
                max_total_words=1300
            )
        
        error_message = str(cm.exception)
        # Should fail on Hebrew language validation
        self.assertIn("[he]", error_message)


if __name__ == '__main__':
    unittest.main()
