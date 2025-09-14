# Symlink/copy of the main script for better organization
# This allows tools/gist/ to contain the gist generation logic
import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..', 'scripts'))
from generate_and_patch_gist import *

if __name__ == "__main__":
    # Re-run the main script
    exec(open(os.path.join(os.path.dirname(__file__), '..', '..', 'scripts', 'generate_and_patch_gist.py')).read())

