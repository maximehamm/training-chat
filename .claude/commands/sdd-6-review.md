---
name: sdd-6-review
description: Étape 7/7 — Revue du code contre la spec et CLAUDE.md. Usage: /sdd-6-review <module>
---

## ÉTAPE 7 / 7 — RÉVISER

1. Récupère le diff depuis la branche principale :
   ```bash
   git diff main 2>/dev/null || git diff HEAD~1
   ```

2. Lis `specs/$ARGUMENTS.md` comme référence de conformité.

3. Analyse le diff selon ces 4 axes et produis un rapport structuré :

---

### Axe 1 — Conformité spec

- Le contrat d'API (endpoint, request body, response body) est-il respecté à 100% ?
- Les codes HTTP correspondent-ils exactement à la spec ?
- Des champs ont-ils été ajoutés ou omis sans justification ?
- Les messages d'erreur respectent-ils le format `{ error, message, details? }` ?

### Axe 2 — Sécurité

- `@Valid` présent sur tous les `@RequestBody` ?
- `@Validated` présent sur la classe controller ?
- Validation des paramètres de chemin et query params ?
- Path traversal bloqué si manipulation de fichiers système ?
- Aucune clé API ou secret dans le code ?

### Axe 3 — Conventions CLAUDE.md

- Injection par constructeur / `@RequiredArgsConstructor` ?
- DTOs = records Java (pas de classes mutables) ?
- Pas d'entité JPA exposée directement dans les réponses ?
- Annotations mapping spécifiques (`@GetMapping`, `@PostMapping`...) ?
- Imports explicites (pas de wildcard `import xxx.*`) ?
- Pas de commentaires superflus ?

### Axe 4 — Tests

- Tous les tests P1 du PDT (`specs/$ARGUMENTS-pdt.md`) sont verts ?
- Les scénarios Cucumber vérifient bien réponse HTTP **et** état BDD ?
- Les step definitions utilisent AssertJ (`assertThat`) ?
- Les scénarios sont autonomes (pas de dépendance d'ordre) ?

---

## Format du rapport

```
## Rapport de revue — $ARGUMENTS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

### ✅ Points conformes
...

### ⚠️ Points à améliorer (non bloquants)
...

### 🔴 Bloquants (à corriger avant merge)
...

### 📊 Résumé
Conformité spec : X/4 axes OK
Tests PDT P1    : X/Y verts
Prêt pour merge : OUI / NON
```
