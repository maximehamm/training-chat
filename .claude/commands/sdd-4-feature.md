---
name: sdd-4-feature
description: Étape 4/7 — Génère les scénarios Cucumber depuis le PDT. Usage: /sdd-4-feature <module>
---

## ÉTAPE 4 / 7 — ÉCRIRE les scénarios BDD

Prérequis : `specs/$ARGUMENTS-pdt.md` avec colonne Couverture remplie.

1. Lis `specs/$ARGUMENTS.md` et `specs/$ARGUMENTS-pdt.md`.
2. Génère `src/test/resources/features/$ARGUMENTS.feature` uniquement pour les tests marqués `✅ À créer`.

**Règles OBLIGATOIRES (CLAUDE.md) :**

- Pattern `Given → When → Then` strict sur chaque scénario
- `Given` : données insérées en base **via repositories Spring** (jamais via appel API)
- `When` : **un seul appel HTTP** par scénario (TestRestTemplate ou MockMvc)
- `Then` : vérification **réponse HTTP** (statut + corps) **ET état base de données**
- Assertions : AssertJ uniquement (`assertThat(...)`)
- Scénarios **autonomes** : chaque `Given` remet la base dans un état connu
- Nommage : description claire correspondant à l'ID PDT en commentaire

**Format attendu :**

```gherkin
Feature: [Titre module] — $ARGUMENTS

  # T01 — [description du test]
  Scenario: [intitulé explicite]
    Given [précondition base de données]
    When [appel HTTP unique]
    Then le statut HTTP est [code]
    And [vérification corps réponse]
    And [vérification état base de données si applicable]
```

3. Affiche le fichier `.feature` complet généré.

---

⛔ **GATE 4 — Validation humaine requise**

Affiche exactement ce message et ne continue PAS :

```
🥒 Scénarios générés : src/test/resources/features/$ARGUMENTS.feature

Vérifiez :
  □ Chaque scénario a un Given / When / Then
  □ Le When ne contient qu'un seul appel HTTP
  □ Le Then vérifie la réponse HTTP ET l'état BDD
  □ Les données de test sont réalistes et non ambiguës
  □ Chaque ID PDT est référencé en commentaire

Quand les scénarios sont validés → /sdd-5-implement $ARGUMENTS
```
