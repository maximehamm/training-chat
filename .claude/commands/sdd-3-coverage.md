---
name: sdd-3-coverage
description: Étape 3/7 — Mappe le PDT vers les scénarios Cucumber. Usage: /sdd-3-coverage <module>
---

## ÉTAPE 3 / 7 — COUVRIR : mapping PDT → Cucumber

Prérequis : `specs/$ARGUMENTS-pdt.md` doit exister.

1. Lis `specs/$ARGUMENTS-pdt.md`.
2. Scanne tous les fichiers `src/test/resources/features/*.feature` existants.
3. Pour chaque ligne du PDT, détermine la couverture :

| Symbole | Signification |
|---------|--------------|
| `✅ [fichier.feature : Nom scénario]` | Couvert par un scénario Cucumber existant |
| `✅ À créer` | Doit être couvert par un nouveau scénario Cucumber |
| `🔷 JUnit` | Logique pure sans Spring — test unitaire suffisant |
| `⚠️ Manuel` | Non automatisable (performance, UI, timing...) |

4. Met à jour la colonne `Couverture` dans `specs/$ARGUMENTS-pdt.md`.

5. Affiche un résumé de couverture :

```
Couverture PDT — $ARGUMENTS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Tests P1 couverts     : X / Y
Tests P1 non couverts : Z

Gaps P1 (action requise) :
  • T0X — [description] → stratégie proposée

Nouveaux scénarios Cucumber à créer : N
Tests JUnit à créer : M
```

---

⛔ **GATE 3 — Validation humaine requise**

Affiche exactement ce message et ne continue PAS :

```
📊 Couverture calculée pour specs/$ARGUMENTS-pdt.md.

Vérifiez :
  □ Tous les tests P1 ont une couverture définie
  □ Les tests non-Cucumber ont une stratégie claire (JUnit / Manuel)
  □ Les scénarios "À créer" sont pertinents

Si des P1 restent sans couverture, décidez de la stratégie maintenant.
Quand la couverture est validée → /sdd-4-feature $ARGUMENTS
```
