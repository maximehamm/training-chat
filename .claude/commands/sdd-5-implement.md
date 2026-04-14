---
name: sdd-5-implement
description: Étapes 5-6/7 — Affinage des ambiguïtés puis implémentation. Usage: /sdd-5-implement <module>
---

## ÉTAPE 5 / 7 — AFFINER (L'entretien)

Prérequis : `specs/$ARGUMENTS.md` et `src/test/resources/features/$ARGUMENTS.feature` validés.

Lis les deux fichiers et pose **toutes** les questions dont la réponse pourrait causer
un échec d'implémentation. Regroupe-les par catégorie :

1. **Décisions de données** — types Java non spécifiés, formats (UUID ? Long ? String ?),
   valeurs par défaut, nullabilité

2. **Conflits de règles** — contradictions entre la spec et CLAUDE.md
   (ex : la spec dit X mais les conventions disent Y)

3. **Choix de pattern** — parmi les patterns déjà en place dans le projet,
   lequel appliquer ici (ex : quel type de repository, quelle exception custom)

4. **Gestion d'erreurs ambiguë** — comportement exact pour les cas limites
   non décrits dans la spec

5. **Frontières de scope** — ce qui est explicitement hors périmètre et
   ne doit PAS être implémenté

Affiche les questions regroupées et numérotées. Attends les réponses.

---

⛔ **GATE 5 — Validation humaine requise**

```
❓ Questions d'affinage posées.

Répondez à chaque question avant que l'implémentation démarre.
Tapez vos réponses, puis relancez /sdd-5-implement $ARGUMENTS pour continuer.
```

---

## ÉTAPE 6 / 7 — IMPLÉMENTER

*(Cette section s'exécute uniquement si toutes les questions de l'étape 5 ont été répondues
dans le contexte de la conversation courante.)*

Implémente le module `$ARGUMENTS` en respectant STRICTEMENT :

1. **`specs/$ARGUMENTS.md`** — contrat d'API, ni plus ni moins
   - Pas de champ supplémentaire dans la réponse
   - Pas d'endpoint non spécifié
   - Codes HTTP exactement ceux définis

2. **`src/test/resources/features/$ARGUMENTS.feature`** — les tests doivent passer
   - Implémente les step definitions manquantes dans `src/test/java/com/assist/steps/`
   - Respecte le pattern Given/When/Then de CLAUDE.md

3. **`CLAUDE.md`** — conventions obligatoires :
   - Injection par constructeur (`@RequiredArgsConstructor`)
   - DTOs = records Java
   - Annotations `@GetMapping`/`@PostMapping`/etc. (jamais `@RequestMapping` sur les méthodes)
   - `@Valid` sur les `@RequestBody`, `@Validated` sur la classe controller
   - Pas d'entité JPA exposée directement
   - Pas d'import wildcard

Lance `./gradlew test` à la fin. Si des tests sont rouges, corrige et relance.
N'avance pas à la revue tant que tous les tests ne sont pas **VERTS**.

Affiche le résumé final : nombre de tests passés / total.
