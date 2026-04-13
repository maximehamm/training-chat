# Règles de Développement Angular - Contexte Entreprise GRDF

> Ce document définit les règles spécifiques à appliquer lors de la génération de code Angular dans le contexte GRDF. Ces règles complètent les bonnes pratiques Angular officielles.

---

## 1. Design System Obligatoire

### Règle DS-01 : Utilisation d'ALCANE
- **OBLIGATOIRE** : Utiliser le Design System ALCANE pour tout nouveau projet si le socle technique est compatible
- Ne jamais créer de composants UI personnalisés sans vérifier leur existence dans ALCANE
- Les composants ALCANE garantissent la conformité RGAA et l'image de marque GRDF

### Règle DS-02 : Versions synchronisées
- La version Angular utilisée doit correspondre à celle supportée par ALCANE :
  - Angular 18 → ALCANE 4.X
  - Angular 19 → ALCANE 4.X
  - Angular 20 → ALCANE 5.X
  - Angular 21 → ALCANE 6.X

---

## 2. Gestion des Dépendances

### Règle DEP-01 : Registre NPM interne exclusif
- **INTERDIT** : Installation de paquets depuis des dépôts tiers ou npmjs.com directement
- **OBLIGATOIRE** : Utiliser uniquement le registre NPM interne de GRDF
- Verrouiller systématiquement le `package-lock.json` ou `pnpm-lock.yaml`

### Règle DEP-02 : Audit de sécurité
- Intégrer une analyse SCA (Software Composition Analysis) dans la CI/CD
- Outils recommandés : OWASP Dependency-Track, Snyk, Sonatype
- Appliquer les patchs de sécurité Angular via mise à jour des dépendances ET du Design System ALCANE

---

## 3. Structure et Architecture

### Règle ARCH-01 : Documentation obligatoire
- **OBLIGATOIRE** : Documenter les choix d'architecture dans le `README.md`
- Faire évoluer cette documentation en accord avec l'architecture
- Privilégier les évolutions architecturales au fil de l'eau (éviter l'effet "Big Bang")

### Règle ARCH-02 : Limites de structure
- Profondeur des dossiers : **7 niveaux maximum**
- Taille des fichiers : **200 lignes maximum**
- Taille des fonctions : **25 lignes maximum**
- Templates inline : **5 lignes maximum**, sinon externaliser en fichier `.html`

### Règle ARCH-03 : Barrel exports
- Utiliser des fichiers `index.ts` et/ou `public-api.ts` pour simplifier les imports dans chaque module/feature

---

## 4. Conventions de Nommage

### Règle NAMING-01 : Fichiers
```
users-list.html
users-list.spec.ts
users-list.ts → export class UserListComponent
users-list.scss
manage-users.ts → export class UserService
manage-users.spec.ts
```

### Règle NAMING-02 : Code
- **PascalCase** : Classes, Interfaces
- **camelCase** : Propriétés, méthodes, signals
- **kebab-case** : Sélecteurs de composants, noms de fichiers

### Règle NAMING-03 : Signals
- Noms explicites : `isLoading`, `userCount`
- Signals privés préfixés : `private readonly _users = signal([])`
- Effects nommés explicitement : `saveUserEffect = effect(...)`

### Règle NAMING-04 : Observables
- **Suffixe \$** obligatoire : `users$`, `isLoading$`
- Préfixes pour actions : `load`, `save`, `delete`
- Préfixes pour états booléens : `is`, `has`, `can`

---

## 5. Sécurité Spécifique

### Règle SEC-01 : Stockage des tokens
- **INTERDIT** : Stocker les tokens JWT dans `localStorage`
- **OBLIGATOIRE** : Utiliser des cookies `httpOnly + Secure + SameSite=Strict`

### Règle SEC-02 : Protection XSRF
- Utiliser `HttpClientXsrfModule` natif d'Angular

### Règle SEC-03 : Content Security Policy
- Implémenter une CSP restrictive
- Éviter `unsafe-inline`, préférer les nonces ou hashes
- Exemple : `script-src 'self' 'nonce-xyz'; object-src 'none'; base-uri 'self';`

### Règle SEC-04 : Conformité OWASP ASVS
- Aligner les contrôles frontend avec les niveaux ASVS 1 et 2
- Documenter les mesures de sécurité dans le README technique

---

## 6. Accessibilité (RGAA/WCAG)

### Règle A11Y-01 : Checklist minimale obligatoire
- Landmarks HTML sémantiques (`<main>`, `<nav>`, `<header>`, `<footer>`)
- Focus visible sur tous les éléments interactifs
- Contraste suffisant (ratios WCAG AA/AAA)
- Labels ARIA explicites
- Navigation clavier complète
- Skip links vers le contenu principal

### Règle A11Y-02 : Angular CDK a11y
- Utiliser `FocusTrap` pour les modales
- Utiliser `LiveAnnouncer` pour les messages dynamiques

### Règle A11Y-03 : Tests obligatoires
- Tester avec NVDA (disponible sur le portail d'entreprise)
- Utiliser les outils : Axe DevTools, HeadingsMap, WCAG Color contrast checker

---

## 7. Écoconception

### Règle ECO-01 : Budgets de performance
- TTI (Time to Interactive) : **< 3 secondes**
- LCP (Largest Contentful Paint) : **< 2,5 secondes**
- Taille JS initiale : **< 250 Ko** (hors lazy loading)
- Requêtes HTTP : **< 50**
- Polices externes : **< 2**

### Règle ECO-02 : Optimisations obligatoires
- Lazy loading des modules et images (`loading="lazy"`)
- Formats modernes d'images : WebP, AVIF
- Compression des assets : Gzip, Brotli
- Suppression des assets inutilisés

### Règle ECO-03 : Mesure
- Utiliser `source-map-explorer`, Lighthouse, WebPageTest
- Mesurer l'éco-index avec GreenIT-Analysis

---

## 8. Testing

### Règle TEST-01 : Framework de tests unitaires
- Utiliser **Jest** ou **Vitest** (Karma est déprécié depuis 2023)
- Fixer des seuils de couverture minimum

### Règle TEST-02 : Tests E2E
- Utiliser **Playwright** ou **Cypress**
- Automatiser les tests de régression sur les fonctionnalités critiques

---

## 9. Build et Déploiement

### Règle BUILD-01 : Builders
- Utiliser les builders préconisés : **esbuild**, **Vite**

### Règle BUILD-02 : Optimisations production
- AOT compilation obligatoire
- Tree-shaking activé
- Compression gzip/brotli configurée

### Règle BUILD-03 : CDN
- Utiliser des CDN pour les assets statiques (si validé par la sécurité)
- Activer SRI (Subresource Integrity) pour les ressources externes

---

## 10. Documentation du Code

### Règle DOC-01 : JSDoc obligatoire
- Documenter toutes les APIs publiques
- Annoter les computed signals avec leurs dépendances
- Documenter les effects et leurs side effects

### Règle DOC-02 : Commentaires
- Expliquer la logique métier complexe avec des commentaires inline
- Utiliser des commentaires descriptifs pour les signals complexes

---

## 11. Internationalisation

### Règle I18N-01 : Configuration
- Utiliser Angular i18n natif
- Externaliser tous les textes dans des fichiers de traduction
- Implémenter un fallback pour les traductions manquantes

### Règle I18N-02 : Formats localisés
- Gérer les formats de date, nombre et devise par locale
- Tester avec des langues RTL si nécessaire

---

## Résumé des Interdictions Absolues

| Catégorie | Interdit |
|-----------|----------|
| Dépendances | Installation depuis dépôts tiers |
| Sécurité | Tokens JWT dans localStorage |
| Sécurité | `unsafe-inline` dans CSP |
| Hébergement | Hors DC, AREA Administration/SIE |
| Code | innerHTML sans DomSanitizer |
| Code | Fichiers > 200 lignes |
| Code | Fonctions > 25 lignes |
| Code | Dossiers > 7 niveaux |

---

## Outils Obligatoires/Recommandés

| Catégorie | Outil | Statut |
|-----------|-------|--------|
| Linting | ESLint + Prettier | Obligatoire |
| Pre-commit | Husky | Obligatoire |
| Tests unitaires | Jest / Vitest | Obligatoire |
| Tests E2E | Playwright / Cypress | Obligatoire |
| Accessibilité | NVDA, Axe DevTools | Obligatoire |
| Écoconception | GreenIT-Analysis, Lighthouse | Recommandé |
| Sécurité | SonarQube (règles sécurité) | Obligatoire |

---

## Historique de versions
- v0.1.0 (24-12-2025): Version initiale