---
name: sdd-2-pdt
description: Étape 2/7 — Élabore le Plan de Test depuis la spec. Usage: /sdd-2-pdt <module>
---

## ÉTAPE 2 / 7 — ÉLABORER le Plan de Test (PDT)

Prérequis : `specs/$ARGUMENTS.md` doit exister. Sinon lance d'abord `/sdd-1-start $ARGUMENTS`.

1. Lis `specs/$ARGUMENTS.md`.

2. Identifie TOUS les tests nécessaires en couvrant ces catégories :
   - **Fonctionnel** : chaque critère d'acceptation, chaque happy path
   - **Erreur** : chaque code HTTP d'erreur (400, 404, 409...), chaque validation
   - **Edge case** : valeurs limites, champs optionnels absents, listes vides, taille max
   - **Non-régression** : endpoints des modules déjà livrés susceptibles d'être impactés par ce module

3. Crée `specs/$ARGUMENTS-pdt.md` :

```markdown
# PDT — $ARGUMENTS

| ID  | Description                        | Type             | Priorité | Couverture |
|-----|------------------------------------|------------------|----------|------------|
| T01 | ...                                | Fonctionnel      | P1       | ?          |
| T02 | ...                                | Erreur           | P1       | ?          |
| T03 | ...                                | Edge case        | P2       | ?          |
| T04 | GET /health toujours OK            | Non-régression   | P1       | ?          |
```

**Types** : `Fonctionnel` / `Erreur` / `Edge case` / `Non-régression`
**Priorités** : `P1` bloquant / `P2` important / `P3` nice-to-have
**Couverture** : laisser `?` — sera rempli à l'étape `/sdd-3-coverage`

4. Affiche le tableau complet.

---

⛔ **GATE 2 — Validation humaine requise**

Affiche exactement ce message et ne continue PAS :

```
📋 PDT specs/$ARGUMENTS-pdt.md créé — XX tests identifiés.

Vérifiez :
  □ Tous les cas d'erreur de la spec sont couverts
  □ Les tests de non-régression concernent les bons modules
  □ Les priorités P1 sont correctement identifiées

Ajoutez / supprimez des lignes si besoin, puis → /sdd-3-coverage $ARGUMENTS
```
