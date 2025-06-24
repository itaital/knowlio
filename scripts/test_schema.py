import json, pathlib, sys

data = json.load(open(sys.argv[1]))
en = data["languages"]["en"]
assert len(en["quoteOfTheDay"]) >= 2
assert len(en["interestingKnowledge"]) >= 2
assert all(isinstance(k, dict) and {"title", "text"} <= k.keys()
           for k in en["interestingKnowledge"])

