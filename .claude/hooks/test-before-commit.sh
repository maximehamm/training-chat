#!/usr/bin/env bash
set -euo pipefail

BACKEND_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)/backend"

echo "🧪 Lancement des tests Cucumber avant commit..."

cd "$BACKEND_DIR"
if ! ./gradlew test --quiet 2>&1; then
  echo "❌ Les tests ont échoué — commit annulé."
  exit 1
fi

echo "✅ Tous les tests passent."
