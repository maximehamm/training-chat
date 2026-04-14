#!/usr/bin/env bash
# SDD compliance check — vérifie qu'une spec et des features existent avant de committer du code métier
# Déclenché par : PreToolUse sur git commit

STAGED_JAVA=$(git diff --cached --name-only 2>/dev/null \
  | grep -E 'src/main/java/.*(Controller|Service).*\.java$' || true)

# Pas de fichiers métier stagés → pas de vérification
if [ -z "$STAGED_JAVA" ]; then
  exit 0
fi

SEP="━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Vérification 1 : au moins une spec technique existe dans specs/
SPEC_COUNT=$(find specs/ -maxdepth 1 -name "*.md" ! -name "*-pdt.md" 2>/dev/null | wc -l | tr -d ' ')

if [ "$SPEC_COUNT" -eq 0 ]; then
  echo ""
  echo "⛔ SDD — Commit bloqué"
  echo "$SEP"
  echo "Du code métier est commité sans spec technique dans specs/."
  echo ""
  echo "Fichiers concernés :"
  echo "$STAGED_JAVA" | sed 's/^/  • /'
  echo ""
  echo "👉 Démarrez le workflow SDD : /sdd <module>"
  echo "   Exemple : /sdd m3-search"
  echo "$SEP"
  exit 1
fi

# Vérification 2 : les controllers ont des .feature associés
STAGED_CONTROLLERS=$(echo "$STAGED_JAVA" | grep -iE 'Controller\.java$' || true)

if [ -n "$STAGED_CONTROLLERS" ]; then
  FEATURE_COUNT=$(find src/test/resources/features/ -name "*.feature" 2>/dev/null | wc -l | tr -d ' ')

  if [ "$FEATURE_COUNT" -eq 0 ]; then
    echo ""
    echo "⛔ SDD — Commit bloqué"
    echo "$SEP"
    echo "Un Controller est commité sans scénarios Cucumber."
    echo ""
    echo "Fichiers concernés :"
    echo "$STAGED_CONTROLLERS" | sed 's/^/  • /'
    echo ""
    echo "👉 Générez les scénarios BDD : /sdd-feature <module>"
    echo "$SEP"
    exit 1
  fi
fi

echo "✅ SDD — Conformité vérifiée (specs/ et features/ présents)"
exit 0
