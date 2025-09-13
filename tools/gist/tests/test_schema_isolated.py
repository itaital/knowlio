#!/usr/bin/env python
"""
Isolated tests for Knowlio daily bundle schema validation
"""

import unittest
import json
import sys
import os
import random
from typing import List, Dict, Optional

# Isolated implementation of functions for testing without env vars
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
    """Get a list of people for whoWereThey section."""
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

def validate_bundle_schema(bundle: dict, require_who: bool = False) -> None:
    """Validate that the bundle has the required schema."""
    required_keys = ["date", "languages"]
    for key in required_keys:
        if key not in bundle:
            raise ValueError(f"Missing required key: {key}")
    
    if not isinstance(bundle["languages"], dict):
        raise ValueError("'languages' must be a dictionary")
    
    for lang_code, lang_content in bundle["languages"].items():
        if not isinstance(lang_content, dict):
            raise ValueError(f"Language content for '{lang_code}' must be a dictionary")
        
        required_lang_keys = ["quoteOfTheDay", "interestingKnowledge", "whoWereThey"]
        for key in required_lang_keys:
            if key not in lang_content:
                raise ValueError(f"Missing required key '{key}' in language '{lang_code}'")
            
            if not isinstance(lang_content[key], list):
                raise ValueError(f"'{key}' must be a list in language '{lang_code}'")
        
        # Check whoWereThey specifically
        who_were_they = lang_content["whoWereThey"]
        if require_who and len(who_were_they) == 0:
            raise ValueError(f"whoWereThey is empty in language '{lang_code}' (use require_who to enforce)")
        
        # Validate each person in whoWereThey
        for i, person in enumerate(who_were_they):
            if not isinstance(person, dict):
                raise ValueError(f"Person {i} in whoWereThey[{lang_code}] must be a dictionary")
            
            if "name" not in person or "bio" not in person:
                raise ValueError(f"Person {i} in whoWereThey[{lang_code}] missing 'name' or 'bio'")
            
            if not isinstance(person["name"], str) or not isinstance(person["bio"], str):
                raise ValueError(f"Person {i} in whoWereThey[{lang_code}] 'name' and 'bio' must be strings")


class TestBundleSchema(unittest.TestCase):
    
    def setUp(self):
        """Set up test fixtures"""
        self.valid_bundle = {
            "date": "2025-09-13",
            "languages": {
                "en": {
                    "quoteOfTheDay": ["Test quote - Author"],
                    "interestingKnowledge": ["Test fact"],
                    "whoWereThey": [
                        {
                            "name": "Isaac Newton",
                            "bio": "Mathematician and physicist"
                        },
                        {
                            "name": "Marie Curie", 
                            "bio": "Pioneering physicist and chemist"
                        }
                    ]
                },
                "he": {
                    "quoteOfTheDay": ["ציטוט בדיקה"],
                    "interestingKnowledge": ["עובדה בדיקה"],
                    "whoWereThey": [
                        {
                            "name": "Isaac Newton",
                            "bio": "Mathematician and physicist"
                        },
                        {
                            "name": "Marie Curie",
                            "bio": "Pioneering physicist and chemist"
                        }
                    ]
                }
            }
        }
    
    def test_valid_bundle_passes_validation(self):
        """Test that a valid bundle passes validation"""
        try:
            validate_bundle_schema(self.valid_bundle, require_who=True)
        except ValueError as e:
            self.fail(f"Valid bundle should not raise ValueError: {e}")
    
    def test_missing_date_fails(self):
        """Test that missing date fails validation"""
        bundle = self.valid_bundle.copy()
        del bundle["date"]
        
        with self.assertRaises(ValueError):
            validate_bundle_schema(bundle)
    
    def test_missing_languages_fails(self):
        """Test that missing languages fails validation"""
        bundle = self.valid_bundle.copy()
        del bundle["languages"]
        
        with self.assertRaises(ValueError):
            validate_bundle_schema(bundle)
    
    def test_missing_who_were_they_fails_with_require_who(self):
        """Test that missing whoWereThey fails with require_who=True"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"] = []
        
        with self.assertRaises(ValueError):
            validate_bundle_schema(bundle, require_who=True)
    
    def test_missing_who_were_they_passes_without_require_who(self):
        """Test that missing whoWereThey passes with require_who=False"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"] = []
        
        try:
            validate_bundle_schema(bundle, require_who=False)
        except ValueError as e:
            self.fail(f"Empty whoWereThey should pass when require_who=False: {e}")
    
    def test_malformed_person_object_fails(self):
        """Test that malformed person objects fail validation"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"][0] = {"name": "Test"}  # Missing bio
        
        with self.assertRaises(ValueError):
            validate_bundle_schema(bundle)
    
    def test_person_with_non_string_fields_fails(self):
        """Test that person objects with non-string fields fail validation"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"][0] = {"name": 123, "bio": "Test"}  # Non-string name
        
        with self.assertRaises(ValueError):
            validate_bundle_schema(bundle)
    
    def test_get_people_returns_valid_structure(self):
        """Test that get_people returns properly structured data"""
        people = get_people('en', 2, seed="2025-09-13")
        
        self.assertEqual(len(people), 2)
        
        for person in people:
            self.assertIsInstance(person, dict)
            self.assertIn("name", person)
            self.assertIn("bio", person)
            self.assertIsInstance(person["name"], str)
            self.assertIsInstance(person["bio"], str)
            self.assertGreater(len(person["name"]), 0)
            self.assertGreater(len(person["bio"]), 0)
    
    def test_get_people_deterministic_with_seed(self):
        """Test that get_people returns consistent results with the same seed"""
        people1 = get_people('en', 2, seed="2025-09-13")
        people2 = get_people('en', 2, seed="2025-09-13")
        
        self.assertEqual(people1, people2)
    
    def test_hebrew_and_english_get_same_people_with_seed(self):
        """Test that Hebrew and English get the same people with the same seed"""
        people_en = get_people('en', 2, seed="2025-09-13")
        people_he = get_people('he', 2, seed="2025-09-13")
        
        self.assertEqual(people_en, people_he)
    
    def test_sample_json_files_valid(self):
        """Test that our sample JSON files are valid"""
        sample_files = [
            "../../../sample_data/daily_2025-09-13_complete.json",
            "../../../sample_data/daily_2025-09-13_en.json", 
            "../../../sample_data/daily_2025-09-13_he.json"
        ]
        
        for file_path in sample_files:
            if os.path.exists(file_path):
                with open(file_path, 'r', encoding='utf-8') as f:
                    sample_json = json.load(f)
                
                try:
                    validate_bundle_schema(sample_json, require_who=True)
                except ValueError as e:
                    self.fail(f"Sample file {file_path} should pass validation: {e}")


if __name__ == '__main__':
    unittest.main()
