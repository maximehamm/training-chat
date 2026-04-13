#!/usr/bin/env bash
set -euo pipefail

FRONTEND_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/frontend" && pwd)"

if ! node --version 2>/dev/null | grep -qE '^v(1[89]|[2-9][0-9])'; then
  export NVM_DIR="${NVM_DIR:-$HOME/.nvm}"
  # shellcheck source=/dev/null
  [ -s "$NVM_DIR/nvm.sh" ] && source "$NVM_DIR/nvm.sh"
  if command -v nvm &>/dev/null; then
    echo "🔄 Node.js v16 détecté — switch vers v20 via nvm..."
    nvm use 20
  else
    echo "❌ Node.js v18+ requis (installé : $(node --version 2>/dev/null || echo 'absent'))"
    echo "   → nvm install 20 && nvm use 20"
    exit 1
  fi
fi

cd "$FRONTEND_DIR"

if [ ! -d node_modules ]; then
  echo "📦 Installation des dépendances npm..."
  npm install
fi

echo "🚀 Démarrage du frontend Angular (http://localhost:4200)..."
npm start
