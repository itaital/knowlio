#!/usr/bin/env bash

# 📌 טוען את nvm אם קיים
export NVM_DIR="$HOME/.nvm"
if [ -s "$NVM_DIR/nvm.sh" ]; then
  source "$NVM_DIR/nvm.sh"
else
  echo "❌ NVM לא מותקן. התקן אותו קודם מהכתובת:"
  echo "   curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash"
  exit 1
fi

# 🔄 מתקין Node (אם לא מותקן)
if ! command -v node >/dev/null 2>&1; then
  echo "⬇️  Installing Node LTS ..."
  nvm install --lts
fi

# 📦 מתקין את Codex CLI
npm install -g @openai/codex

# 🧱 בונה את האפליקציה
./gradlew assembleDebug

# ✅ סיום
echo "✅ Environment ready!"
