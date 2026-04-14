---
name: sdd-1-start
description: Démarre le workflow SDD+BDD+PDT pour un module. Usage: /sdd-1-start <module> (ex: /sdd-1-start m3-search)
---

Lance le workflow SDD+BDD+PDT pour le module **$ARGUMENTS**.

## ÉTAPE 1 / 7 — EXTRAIRE la spec technique

1. Lis `spec/Assist_Spec_Fonctionnelle_v1.1_FullStack.pdf` et localise la section du module `$ARGUMENTS`.

2. Crée `specs/$ARGUMENTS.md` avec cette structure stricte :

```markdown
# Spec technique — $ARGUMENTS

## Endpoints
<!-- Méthode + path + description courte pour chaque endpoint -->

## Contrat d'API
### Request
<!-- Champs, types Java, contraintes (requis/optionnel, min/max, format) -->

### Response
<!-- Champs, types, codes HTTP possibles -->

## Contraintes
<!-- EXPLICITES uniquement — pas d'exigences vagues -->
<!-- Format : "Ne PAS faire X" ou "Retourner Y quand Z" -->

## Critères de complétion
<!-- Issus des critères d'acceptation — chaque ligne = un test vérifiable -->
<!-- Ces critères alimenteront le PDT à l'étape suivante -->
```

3. Affiche le contenu du fichier créé.

---

⛔ **GATE 1 — Validation humaine requise**

Affiche exactement ce message et ne continue PAS :

```
📋 Spec specs/$ARGUMENTS.md créée.

Vérifiez :
  □ Le contrat d'API est complet (tous les champs, tous les codes HTTP)
  □ Les contraintes sont explicites (pas de "gérer les erreurs", mais "retourner 400 si...")
  □ Les critères de complétion sont testables

Quand la spec est validée → /sdd-2-pdt $ARGUMENTS
```
