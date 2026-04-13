# Règles d'adaptation pour projet Spring Boot existant

## Contexte

Règles pour intervenir sur une application Java d'entreprise Spring Boot existante en respectant son architecture, ses conventions et ses choix techniques.

## Objectif

Maintenir et faire évoluer des applications Spring Boot existantes en cohérence avec l'architecture en place, en évitant les ruptures et en maximisant la réutilisation de l'existant.

## Principes

- **Cohérence avant Innovation** : S'adapter aux conventions et patterns déjà en place dans le projet
- **Réutilisation Maximale** : Exploiter les dépendances, configurations et composants existants avant d'en ajouter de nouveaux
- **Évolution Incrémentale** : Privilégier les modifications progressives plutôt que les refactorisations massives
- **Non-régression** : Maintenir la compatibilité avec le code existant
- **Documentation de l'existant** : Comprendre l'architecture actuelle avant toute modification

## Lignes directrices

### 0. Analyse préalable **OBLIGATOIRE**

**Avant toute modification, analyser systématiquement :**

1. **Version Java** : Identifier la version utilisée dans `build.gradle` (propriété `sourceCompatibility` ou `java.toolchain`)
2. **Version Spring Boot** : Vérifier la version dans le bloc `plugins` ou `dependencies` de `build.gradle`
3. **Structure des packages** : Observer l'organisation existante
4. **Dépendances installées** : Lister les librairies dans `build.gradle` (sections `dependencies` et `implementation`)
5. **Patterns architecturaux** : Identifier les patterns déjà en place (services, repositories, DTOs, mappers, etc.)
6. **Configuration** : Examiner les fichiers `application.yml` / `application.properties`
7. **Gestion des exceptions** : Repérer les mécanismes existants (@ControllerAdvice, exceptions custom)
8. **Sécurité** : Identifier la configuration de sécurité en place
9. **Tests** : Observer la stratégie de test (frameworks utilisés, patterns de test)
10. **Commentaires** : Vérifier si le projet contient des commentaires et dans quelle langue
11. **Usage de Lombok** : Identifier si Lombok est installé dans les dépendances

### 1. Technologies

- **Langage** : Utiliser la version Java déjà configurée dans `build.gradle`
- **Framework** : Utiliser la version Spring Boot déjà installée
- **Système de build** : Gradle (tous les projets)
- **Base de données** : Utiliser la base de données déjà configurée
- **Lombok** : Si Lombok est installé dans les dépendances du projet, l'utiliser systématiquement sur toutes les classes où c'est pertinent selon les règles établies
- **Dépendances** : ⚠️ **NE PAS ajouter de nouvelles dépendances sans justification impérieuse**. Toujours chercher si une dépendance existante peut répondre au besoin.

### 2. Organisation des packages

- **Respecter la structure existante** : Ne pas créer de nouvelle organisation si une structure est déjà en place
- **Observer les conventions de nommage** : Reproduire les patterns de nommage utilisés (suffixes, préfixes, etc.)
- **Placement cohérent** : Placer les nouvelles classes aux côtés de classes similaires existantes

### 3. Dépôt de dépendances Gradle

Tous les projets utilisent le dépôt Artifactory GRDF.

⚠️ **Ne jamais ajouter de repositories publics** (mavenCentral, jCenter, etc.)

⚠️ **Ne modifier les repositories que si explicitement demandé**

### 4. Gestion des dépendances Gradle

- **Observer la structure** : Identifier si le projet utilise un fichier `gradle.properties`, un catalogue de versions, ou des versions directes
- **Cohérence des versions** : Maintenir le même système de gestion des versions
- **Configurations Gradle** : Utiliser les mêmes configurations (`implementation`, `api`, `compileOnly`, `runtimeOnly`, `testImplementation`) que le reste du projet
- **Spring Boot BOM** : Ne pas modifier la gestion du BOM Spring Boot si elle est déjà en place
- **Sécurité des dépendances** : ⚠️ **Lors de l'ajout ou de la mise à jour d'une dépendance, toujours vérifier qu'elle ne présente pas de CVE connues**

### 5. Propriétés de l'application

- **Conserver le format existant** : YAML ou Properties selon ce qui est utilisé
- **Réutiliser les variables d'environnement** : Ne pas en créer de nouvelles si des mécanismes existent
- **Actuator** : Si déjà configuré, ne pas modifier la configuration existante
- **Swagger** : Respecter la configuration en place (activé/désactivé selon environnements)
- **Logs** : Conserver le format et la configuration de logging existants

### 6. Contrôleurs REST

- **Reproduire les patterns existants** :
    - Observer comment les autres contrôleurs sont structurés
    - Appliquer les mêmes conventions de nommage pour les endpoints
    - Respecter le format de retour utilisé (ResponseEntity ou non)

- **Annotations de mapping** :
    - **Pour tous les nouveaux endpoints REST** : **Utiliser les annotations de mapping spécifiques au verbe HTTP** (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping`)
    - **Ne PAS utiliser** `@RequestMapping` pour les nouveaux endpoints (sauf au niveau de la classe pour définir le chemin de base)
    - Pour les endpoints existants : ne pas les modifier sauf demande explicite

- **Validation** :
    - **Si le projet n'utilise PAS de validation** : Mettre en place la validation Bean Validation (JSR-380) pour tous les nouveaux contrôleurs et/ou méthodes
        - Utiliser `@Valid` sur les paramètres annotés avec `@RequestBody`
        - Utiliser `@Validated` au niveau de la classe du contrôleur pour activer la validation sur les paramètres simples (`@PathVariable`, `@RequestParam`)
        - Appliquer les annotations de validation appropriées sur les DTOs et les paramètres
    - **Si le projet utilise déjà la validation** : Reproduire le style et les annotations utilisées

- **Pagination** :
    - **Si le projet utilise déjà la pagination** (Spring Data `Pageable`, paramètres personnalisés, etc.) : Mettre en place la pagination sur les nouveaux endpoints retournant des listes si cela est pertinent
    - Observer et reproduire le mécanisme de pagination utilisé (paramètres, format de réponse, etc.)

- **Documentation** : Si Swagger est utilisé, documenter de la même manière

### 7. Composants Spring (Services, Controllers, etc.)

- **Architecture existante** :
    - Si le projet utilise des interfaces + implémentations, reproduire ce pattern
    - Si le projet n'utilise que des classes concrètes, faire de même

- **Injection de dépendances** :
    - **Pour TOUS les NOUVEAUX composants Spring** (services, contrôleurs, configurations, etc.) : **TOUJOURS utiliser l'injection par constructeur**
    - Pour les modifications de composants existants : conserver le type d'injection déjà en place
    - Si Lombok est installé dans le projet, utiliser `@RequiredArgsConstructor` pour l'injection par constructeur

- **Transactions** : Si le projet utilise déjà la gestion transactionnelle explicite :
    - Observer les patterns utilisés (`@Transactional` au niveau classe ou méthode)
    - Reproduire l'usage de `@Transactional(readOnly = true)` pour les opérations de lecture
    - Appliquer `@Transactional` sur les méthodes d'écriture

- **Mappers** : Utiliser la librairie de mapping déjà présente (MapStruct, ModelMapper, ou mapping manuel)

### 8. Entités JPA

- **Conventions de nommage** : Respecter les conventions pour les tables et colonnes

- **Lombok** : Si Lombok est installé dans le projet, l'utiliser sur toutes les entités avec les restrictions suivantes :
    - **⚠️ INTERDICTION** : **Ne JAMAIS utiliser `@Data`** (combine des annotations problématiques pour JPA)
    - Utiliser le **minimum d'annotations** nécessaires :
        - `@Getter` / `@Setter` pour les accesseurs
        - `@NoArgsConstructor` pour le constructeur sans arguments (requis par JPA)
        - `@Builder` si des instances doivent être construites manuellement
        - `@AllArgsConstructor` uniquement si nécessaire (souvent avec `@Builder`)
    - **NE PAS utiliser** `@ToString` (risque de boucles infinies avec les relations)
    - **NE PAS utiliser** `@EqualsAndHashCode` (comportement problématique avec JPA)

- **Audit** : Si un mécanisme d'audit existe, l'appliquer aux nouvelles entités

- **Timestamps** : Si le projet utilise des annotations de timestamps (`@CreationTimestamp`, `@UpdateTimestamp`), les reproduire sur les nouvelles entités

- **Verrouillage optimiste** : Si le projet utilise `@Version` pour le verrouillage optimiste, l'appliquer aux nouvelles entités

### 9. Repository Spring Data

- **Type de repository** : Utiliser le même type que l'existant (JpaRepository, CrudRepository, etc.)

- **Méthodes de requête** :
    - **Si le projet utilise Spring Data JPA** : **Privilégier au maximum les derived query methods pour toute nouvelle méthode de repository**
    - Utiliser `@Query` uniquement pour les requêtes complexes qui ne peuvent absolument pas être exprimées avec les derived queries
    - Observer le style déjà utilisé pour rester cohérent avec les méthodes existantes

- **Projections** : Si le projet utilise des projections, reproduire ce pattern

- **Optimisations** : Si le projet utilise déjà ces techniques, les reproduire sur les nouveaux repositories :
    - `JOIN FETCH` pour éviter les problèmes N+1
    - Cache sur les méthodes fréquemment appelées

### 10. DTOs

- **Format** : **Privilégier les records pour les nouveaux DTOs** si la version Java le permet (Java 14+) et si cela est pertinent

- **Validation** :
    - **DTOs existants** : Conserver la validation en place
    - **Nouveaux DTOs** : Mettre en place la validation Bean Validation sur tous les champs nécessaires avec des messages personnalisés clairs

- **Lombok** : Si Lombok est installé dans le projet et que les records ne sont pas utilisés :
    - Privilégier `@Data` pour les DTOs mutables (afin de rendre le code le plus concis possible)
    - Privilégier `@Value` pour les DTOs immutables
    - Utiliser `@Builder` si des instances doivent être construites manuellement
    - Ne pas utiliser les features expérimentales

### 11. Gestion des exceptions

- **Réutiliser les exceptions existantes** : Ne créer de nouvelles exceptions que si nécessaire
- **ControllerAdvice** : Enrichir le(s) @ControllerAdvice existant(s) plutôt qu'en créer de nouveaux
- **Format de réponse** : Respecter le format des réponses d'erreur déjà établi
- **Énumération** : Si une énumération d'erreurs existe, l'étendre

### 12. Configuration de sécurité

- **⚠️ NE PAS MODIFIER** la configuration de sécurité sans demande explicite
- **Observer** : Comprendre le mécanisme en place (JWT, OAuth2, Basic Auth, etc.)
- **Étendre** : Si nécessaire, ajouter des règles cohérentes avec l'existant

### 13. Tests

- **Frameworks de test** : Utiliser les mêmes frameworks que les tests existants (JUnit 4/5, Mockito, etc.)
- **Patterns de test** : Reproduire la structure et les conventions des tests existants
- **Nomenclature** : Respecter les conventions de nommage des méthodes de test
- **Annotations** : Utiliser les mêmes annotations de test (@SpringBootTest, @WebMvcTest, etc.)

### 14. Gestion de versions et compatibilité

- **Fonctionnalités Java** : Utiliser uniquement les fonctionnalités compatibles avec la version Java du projet
- **APIs Spring** : Ne pas utiliser d'APIs dépréciées ou trop récentes par rapport à la version Spring Boot
- **Dépendances** : Vérifier la compatibilité avant toute mise à jour

### 15. Build Gradle

- **Tasks personnalisées** : Observer et comprendre les tasks Gradle existantes avant d'en ajouter
- **Plugins** : Ne pas ajouter de nouveaux plugins sans justification
- **Configuration du wrapper** : Ne pas modifier la version du wrapper Gradle sauf demande explicite

### 16. Commentaires dans le code

- **Règle générale** : **NE PAS ajouter de commentaires dans le code**
- **Exception** : Si le projet contient déjà des commentaires ailleurs :
    - Ajouter un commentaire **UNIQUEMENT** si le code nécessite absolument une explication
    - Utiliser la **même langue** que les autres commentaires du projet (français ou anglais)
    - Rester concis et pertinent

**Quand un commentaire est-il absolument nécessaire ?**
- Logique métier complexe non évidente
- Workaround temporaire pour un bug tiers
- Comportement contre-intuitif requis par des contraintes externes
- Algorithme complexe nécessitant une explication

## Règles prioritaires

1. **🔍 ANALYSER avant d'agir** : Toujours commencer par comprendre l'existant (notamment `build.gradle`)
2. **♻️ RÉUTILISER plutôt que créer** : Exploiter au maximum ce qui existe
3. **🎯 COHÉRENCE avant tout** : Rester aligné avec les patterns du projet
4. **⚠️ MINIMISER les changements** : Modifier uniquement ce qui est nécessaire
5. **📝 DOCUMENTER les écarts** : Si un choix différent de l'existant est nécessaire, l'expliquer

## Bonnes pratiques obligatoires pour le nouveau code

Même si l'existant ne les applique pas, appliquer systématiquement ces bonnes pratiques pour le nouveau code :

1. **Imports explicites** : Ne jamais utiliser les imports wildcard (`import xxx.*`). Toujours importer explicitement chaque classe utilisée.

2. **Annotations de mapping spécifiques** : Utiliser `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping` pour les nouveaux endpoints REST

3. **Validation** :
    - Mettre en place la validation Bean Validation sur les nouveaux contrôleurs/méthodes et DTOs
    - Utiliser `@Valid` sur les paramètres `@RequestBody`
    - Utiliser `@Validated` au niveau de la classe pour valider les paramètres simples
    - Fournir des messages de validation clairs et personnalisés

4. **Injection par constructeur** : Utiliser l'injection par constructeur pour tous les nouveaux composants Spring (services, contrôleurs, configurations, etc.)

5. **Derived queries** : Privilégier les derived query methods pour toute nouvelle méthode de repository Spring Data

6. **Lombok** : Si Lombok est installé dans le projet, l'utiliser systématiquement :
    - **INTERDICTION** : Ne jamais utiliser `@Data` sur les entités JPA
    - Privilégier `@Data` pour les classes mutables (hors entités JPA) et `@Value` pour les immutables
    - Utiliser `@RequiredArgsConstructor` pour l'injection par constructeur
    - Utiliser `@Builder` pour le pattern builder
    - Ne pas utiliser les features expérimentales

7. **Records pour les DTOs** : Privilégier les records pour les nouveaux DTOs si la version Java le permet

8. **DTOs obligatoires** : Toujours utiliser des DTOs pour les nouveaux endpoints, ne jamais exposer directement les entités JPA

9. **Pas de commentaires** : Éviter les commentaires sauf nécessité absolue et cohérence avec l'existant

## Anti-patterns

- ❌ Utiliser des imports wildcard (`import xxx.*`)
- ❌ Ajouter une nouvelle dépendance dans `build.gradle` sans vérifier si une existante peut faire l'affaire
- ❌ Ajouter ou mettre à jour une dépendance sans vérifier qu'elle ne présente pas de CVE connues
- ❌ Ajouter des repositories publics (le projet utilise uniquement Artifactory GRDF)
- ❌ Créer une nouvelle structure de packages sans raison valable
- ❌ Imposer des patterns différents de ceux déjà utilisés (sauf les bonnes pratiques obligatoires ci-dessus)
- ❌ Modifier la configuration de sécurité sans comprendre l'impact
- ❌ Changer le format des logs ou des réponses API
- ❌ Refactoriser du code existant qui n'est pas concerné par la modification
- ❌ Utiliser des fonctionnalités Java incompatibles avec la version du projet
- ❌ Modifier la structure ou les tasks du fichier `build.gradle` sans nécessité
- ❌ Utiliser `@RequestMapping` au lieu des annotations spécifiques pour les nouveaux endpoints REST
- ❌ Utiliser `@Data` de Lombok sur les entités JPA
- ❌ Utiliser des features expérimentales de Lombok
- ❌ Utiliser `@Query` quand une derived query method suffit
- ❌ Ajouter des commentaires inutiles ou redondants
- ❌ Ne pas valider les entrées des nouveaux contrôleurs et DTOs
- ❌ Ne pas utiliser de messages de validation clairs et personnalisés
- ❌ Exposer directement les entités JPA au lieu d'utiliser des DTOs
- ❌ Utiliser `SELECT *` dans les requêtes personnalisées

## Checklist d'intervention

- [ ] Fichier `build.gradle` analysé (versions, dépendances, plugins)
- [ ] Version Java identifiée et respectée
- [ ] Version Spring Boot identifiée et respectée
- [ ] Présence de Lombok identifiée dans les dépendances
- [ ] Structure des packages analysée et respectée
- [ ] Dépendances existantes réutilisées au maximum
- [ ] Nouvelles dépendances vérifiées pour l'absence de CVE connues
- [ ] Imports explicites utilisés (pas de wildcard `import xxx.*`)
- [ ] Patterns architecturaux reproduits (services, repositories, DTOs, etc.)
- [ ] Conventions de nommage respectées
- [ ] Annotations de mapping spécifiques utilisées pour les nouveaux endpoints REST
- [ ] Validation Bean Validation appliquée sur les nouveaux contrôleurs/méthodes et DTOs avec messages clairs
- [ ] `@Valid` utilisé sur les `@RequestBody` et `@Validated` au niveau de la classe
- [ ] Injection par constructeur utilisée pour tous les nouveaux composants Spring
- [ ] Derived queries privilégiées pour toute nouvelle méthode de repository Spring Data
- [ ] Lombok utilisé systématiquement si installé (selon les règles établies)
- [ ] `@Data` de Lombok NON utilisé sur les entités JPA
- [ ] DTOs utilisés pour tous les nouveaux endpoints (pas d'exposition directe des entités)
- [ ] Records privilégiés pour les nouveaux DTOs si la version Java le permet
- [ ] Gestion des exceptions cohérente avec l'existant
- [ ] Tests écrits dans le même style que les tests existants
- [ ] Configuration de sécurité non modifiée (sauf demande explicite)
- [ ] Aucun commentaire superflu ajouté
- [ ] Aucune régression introduite
- [ ] Code compatible avec la version Java du projet
- [ ] Repositories Gradle non modifiés (Artifactory uniquement)
- [ ] README mis à jour si les modifications le nécessitent

## Processus d'intervention recommandé

1. **Explorer** : Parcourir le code existant et le fichier `build.gradle` pour comprendre l'architecture
2. **Identifier** : Repérer les patterns, conventions et composants réutilisables
3. **Planifier** : Déterminer comment intégrer la modification de manière cohérente
4. **Implémenter** : Coder en suivant les conventions observées + les bonnes pratiques obligatoires
5. **Tester** : Vérifier la non-régression et le bon fonctionnement
6. **Réviser** : S'assurer que toutes les bonnes pratiques obligatoires sont appliquées

## Références

- [Documentation Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [Gradle Build Tool](https://docs.gradle.org/)

---

## Historique de versions
- v0.1.0 (24-12-2025): Version initiale