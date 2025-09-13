#!/usr/bin/env python
"""
Tests for Knowlio daily bundle schema validation
"""

import unittest
import json
import sys
import os

# Add the scripts directory to the path to import the main module
sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..', '..', 'scripts'))

from generate_and_patch_gist import validate_bundle_schema, get_people


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
        except SystemExit:
            self.fail("Valid bundle should not raise SystemExit")
    
    def test_missing_date_fails(self):
        """Test that missing date fails validation"""
        bundle = self.valid_bundle.copy()
        del bundle["date"]
        
        with self.assertRaises(SystemExit):
            validate_bundle_schema(bundle)
    
    def test_missing_languages_fails(self):
        """Test that missing languages fails validation"""
        bundle = self.valid_bundle.copy()
        del bundle["languages"]
        
        with self.assertRaises(SystemExit):
            validate_bundle_schema(bundle)
    
    def test_missing_who_were_they_fails_with_require_who(self):
        """Test that missing whoWereThey fails with require_who=True"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"] = []
        
        with self.assertRaises(SystemExit):
            validate_bundle_schema(bundle, require_who=True)
    
    def test_missing_who_were_they_passes_without_require_who(self):
        """Test that missing whoWereThey passes with require_who=False"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"] = []
        
        try:
            validate_bundle_schema(bundle, require_who=False)
        except SystemExit:
            self.fail("Empty whoWereThey should pass when require_who=False")
    
    def test_malformed_person_object_fails(self):
        """Test that malformed person objects fail validation"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"][0] = {"name": "Test"}  # Missing bio
        
        with self.assertRaises(SystemExit):
            validate_bundle_schema(bundle)
    
    def test_person_with_non_string_fields_fails(self):
        """Test that person objects with non-string fields fail validation"""
        bundle = self.valid_bundle.copy()
        bundle["languages"]["en"]["whoWereThey"][0] = {"name": 123, "bio": "Test"}  # Non-string name
        
        with self.assertRaises(SystemExit):
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


class TestJSONStructure(unittest.TestCase):
    """Test the exact JSON structure that the app expects"""
    
    def test_sample_json_structure(self):
        """Test that our sample JSON matches expected structure"""
        sample_json = {
            "date": "2025-09-13",
            "languages": {
                "en": {
                    "quoteOfTheDay": [
                        "We may encounter many defeats, but we must not be defeated. – Maya Angelou"
                    ],
                    "interestingKnowledge": [
                        "Babies are most likely to be born on Tuesdays."
                    ],
                    "whoWereThey": [
                        {
                            "name": "Isaac Newton",
                            "bio": "Mathematician and physicist who formulated the laws of motion and universal gravitation. Founder of calculus."
                        },
                        {
                            "name": "Rosalind Franklin",
                            "bio": "Chemist and X-ray crystallographer. Her work was central to understanding the molecular structure of DNA."
                        }
                    ]
                },
                "he": {
                    "quoteOfTheDay": [
                        "״לא החלטה – לא התקדמות.\" – פתגם עברי"
                    ],
                    "interestingKnowledge": [
                        "לתמנון יש שלושה לבבות ותשעה מוחות."
                    ],
                    "whoWereThey": [
                        {
                            "name": "Isaac Newton",
                            "bio": "Mathematician and physicist who formulated the laws of motion and universal gravitation. Founder of calculus."
                        },
                        {
                            "name": "Rosalind Franklin",
                            "bio": "Chemist and X-ray crystallographer. Her work was central to understanding the molecular structure of DNA."
                        }
                    ]
                }
            }
        }
        
        # Validate the structure
        try:
            validate_bundle_schema(sample_json, require_who=True)
        except SystemExit:
            self.fail("Sample JSON should pass validation")
        
        # Ensure whoWereThey is not empty
        self.assertGreater(len(sample_json["languages"]["en"]["whoWereThey"]), 0)
        self.assertGreater(len(sample_json["languages"]["he"]["whoWereThey"]), 0)


if __name__ == '__main__':
    unittest.main()
