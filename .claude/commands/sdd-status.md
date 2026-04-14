---
name: sdd-status
description: Affiche l'état SDD de tous les modules du projet (spec, PDT, features, tests verts)
---

## Tableau de bord SDD — Assist

1. Scanne `specs/` : liste tous les fichiers `m*.md` (specs et PDTs séparément)
2. Scanne `src/test/resources/features/` : liste tous les `.feature`
3. Lance `./gradlew test --quiet` et extrais les résultats par feature

Affiche ce tableau :

```
┌─────────┬────────────────┬──────────────────────┬───────────────────────────┬────────────┐
│ Module  │ specs/mX.md    │ specs/mX-pdt.md      │ features/mX.feature       │ Tests      │
├─────────┼────────────────┼──────────────────────┼───────────────────────────┼────────────┤
│ M1      │ ✅             │ ✅                   │ ✅                        │ 🟢  X/X   │
│ M2      │ ✅             │ ✅                   │ ✅                        │ 🔴  Y/Z   │
│ M3      │ ❌             │ ❌                   │ ❌                        │ ⬜  —     │
└─────────┴────────────────┴──────────────────────┴───────────────────────────┴────────────┘
```

Puis affiche les alertes :

```
⚠️  Alertes SDD
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 [module] : code présent mais spec manquante → /sdd <module>
🔴 [module] : controller sans .feature         → /sdd-feature <module>
🟡 [module] : tests rouges                     → /sdd-review <module>
🟡 [module] : PDT manquant                     → /sdd-pdt <module>
```

Enfin, affiche la commande à lancer pour le prochain module à traiter :

```
👉 Prochaine action recommandée : /sdd <module-prioritaire>
```
