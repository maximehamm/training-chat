# Règles de Test - Architecture en Couches

## Objectif

Définir une stratégie de test cohérente pour une architecture en couches Spring Boot : tests unitaires, tests d'intégration et tests fonctionnels.

## Pyramide des tests

```
           /\
          /  \     Tests Fonctionnels (~10%)
         /----\    Suffixe : *FT.java, *FuncTest.java
        /      \   
       /        \  Tests d'Intégration (~20%)
      /----------\ Suffixe : *IT.java, *IntegrationTest.java
     /            \
    /              \ Tests Unitaires (~70%)
   /----------------\ Suffixe : *Test.java
```

---

## Structure des répertoires

```
src/
├── main/
│   ├── java/
│   └── resources/
├── test/                          # Tests unitaires
│   ├── java/
│   │   └── com/example/
│   │       └── service/
│   │           └── OrderServiceTest.java
│   └── resources/
├── integrationTest/               # Tests d'intégration
│   ├── java/
│   │   └── com/example/
│   │       ├── repository/
│   │       │   └── OrderRepositoryIT.java
│   │       └── controller/
│   │           └── OrderControllerIT.java
│   └── resources/
│       └── application-integration.yml
└── funcTest/                      # Tests fonctionnels
    ├── java/
    │   └── com/example/
    │       └── workflow/
    │           └── OrderManagementFT.java
    └── resources/
        └── application-functional.yml
```

---

## 1. Tests Unitaires

### Caractéristiques

| Aspect | Valeur |
|--------|--------|
| Couverture cible | > 80% du code métier |
| Répertoire | `src/test/java` |
| Commande | `./gradlew test` |

### Quoi tester

- Couche Service : Règles métier, validations
- Utilitaires : Helpers, parsers, formatters
- Mappers : Transformations de données

### Structure

```java
@Test
void shouldActionWhenCondition() {
    // Given - Préparation
    
    // When - Exécution
    
    // Then - Vérification
}
```

### Conventions de nommage

| Élément | Convention | Exemple |
|---------|------------|---------|
| Fichier | `{Classe}Test.java` | `OrderServiceTest.java` |
| Méthode | `should{Action}When{Condition}` | `shouldSaveWhenValid` |

### Exemple

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @InjectMocks OrderService orderService;

    @Test
    void shouldSaveOrderWhenValid() {
        // Given
        Order order = new Order("CUST001", BigDecimal.TEN);
        when(orderRepository.save(any())).thenReturn(order);

        // When
        Order result = orderService.save(order);

        // Then
        assertThat(result).isNotNull();
        verify(orderRepository).save(order);
    }
}
```

---

## 2. Tests d'Intégration

### Caractéristiques

| Aspect | Valeur |
|--------|--------|
| Base de données | PostgreSQL embarquée (Zonky) |
| Répertoire | `src/integrationTest/java` |
| Commande | `./gradlew integrationTest` |

### Annotations réutilisables

```java
package com.example.test.annotation;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;

/**
 * Annotation pour les tests d'intégration avec contexte Spring complet.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(type = POSTGRES, provider = ZONKY)
@FlywayTest
@ActiveProfiles("integration")
public @interface IntegrationTest {
}
```

```java
package com.example.test.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Annotation pour les tests de Controller (slice test).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest
@ActiveProfiles("integration")
public @interface ControllerTest {
}
```

### Configuration application-integration.yml

```yaml
# src/integrationTest/resources/application-integration.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  flyway:
    enabled: true
    locations: classpath:db/migration

logging:
  level:
    org.springframework.test: INFO
    org.hibernate.SQL: DEBUG
```

### Test Repository

```java
@IntegrationTest
class OrderRepositoryIT {

    @Autowired OrderRepository repository;

    @Test
    void shouldSaveAndRetrieveOrder() {
        // Given
        Order order = new Order("CUST001", BigDecimal.TEN);

        // When
        Order saved = repository.save(order);

        // Then
        assertThat(repository.findById(saved.getId())).isPresent();
    }
}
```

### Test Controller

```java
@ControllerTest(OrderController.class)
class OrderControllerIT {

    @Autowired MockMvc mockMvc;
    @MockBean OrderService orderService;

    @Test
    void shouldReturn200WhenGetOrder() throws Exception {
        // Given
        when(orderService.findById("ORD001")).thenReturn(Optional.of(new Order()));

        // When & Then
        mockMvc.perform(get("/api/orders/ORD001"))
            .andExpect(status().isOk());
    }
}
```

---

## 3. Tests Fonctionnels

### Caractéristiques

| Aspect | Valeur |
|--------|--------|
| Scope | Flux métier complet via API REST |
| Répertoire | `src/funcTest/java` |
| Commande | `./gradlew funcTest` |

### Annotation dédiée

```java
package com.example.test.annotation;

import java.lang.annotation.*;

/**
 * Annotation pour les tests fonctionnels (end-to-end).
 * Hérite de @IntegrationTest pour la configuration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@IntegrationTest
public @interface FunctionalTest {
}
```

### Configuration application-functional.yml

```yaml
# src/funcTest/resources/application-functional.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  flyway:
    enabled: true
    locations: classpath:db/migration

logging:
  level:
    root: WARN
    com.example: INFO
```

### Exemple

```java
@FunctionalTest
class OrderManagementFT {

    @Autowired TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndRetrieveOrder() {
        // Given
        OrderRequest request = new OrderRequest("CUST001", BigDecimal.TEN);

        // When
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
            "/api/orders", request, OrderResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotBlank();
    }
}
```

---

## Tableau récapitulatif

| Critère | Unitaires | Intégration | Fonctionnels |
|---------|-----------|-------------|--------------|
| **Scope** | Classe/Méthode | Composant + Infra | Flux complet |
| **BDD** | Non | PostgreSQL | PostgreSQL |
| **Mocks** | Oui | Partiels | Non |
| **Quantité** | ~70% | ~20% | ~10% |
| **Suffixe** | `*Test.java` | `*IT.java` | `*FT.java` |
| **Répertoire** | `src/test` | `src/integrationTest` | `src/funcTest` |
| **Commande** | `./gradlew test` | `./gradlew integrationTest` | `./gradlew funcTest` |

---

## Commandes utiles

```bash
# Tests unitaires uniquement
./gradlew test

# Tests d'intégration uniquement
./gradlew integrationTest

# Tests fonctionnels uniquement
./gradlew funcTest

# Tous les tests
./gradlew allTests

# Tests avec rapport de couverture
./gradlew test jacocoTestReport

# Vérification des seuils de couverture
./gradlew jacocoTestCoverageVerification

# Tests en mode verbose
./gradlew test --info

# Exécuter un test spécifique
./gradlew test --tests "OrderServiceTest.shouldSaveOrderWhenValid"

# Exécuter les tests d'une classe
./gradlew test --tests "OrderServiceTest"
```

---

## Checklist avant commit

### Tests unitaires

- [ ] Chaque classe métier a ses tests
- [ ] Pattern Given-When-Then respecté
- [ ] Pas de dépendance à l'infrastructure
- [ ] Nommage `should...When...`
- [ ] Tests dans `src/test/java`

### Tests d'intégration

- [ ] Endpoints API testés (succès + erreurs)
- [ ] Repositories testés avec vraie BDD
- [ ] Suffixe `IT` utilisé
- [ ] Tests dans `src/integrationTest/java`

### Tests fonctionnels

- [ ] Scénarios métier critiques couverts
- [ ] Indépendance entre scénarios (ou ordre explicite)
- [ ] Suffixe `FT` utilisé
- [ ] Tests dans `src/funcTest/java`
- [ ] Timeouts définis

---

## Anti-patterns à éviter

| Anti-pattern | Problème | Solution |
|--------------|----------|----------|
| Test trop gros | Difficile à maintenir | Un test = un comportement |
| Mocks partout | Ne teste pas l'intégration | Mocker uniquement les frontières externes |
| Tests flaky | Résultats non reproductibles | Isolation, données déterministes, timeouts |
| Copier-coller | Duplication, maintenance | `@ParameterizedTest`, helpers, fixtures |
| Test sans assertion | Fausse confiance | Toujours asserter le comportement attendu |
| ID en dur dans les FT | Dépendance à l'ordre | Récupérer l'ID généré dynamiquement |
| Pas de cleanup | Pollution entre tests | `@BeforeEach` avec reset des données |

---

## Assertions

Exploiter au maximum les assertions **AssertJ** pour des tests lisibles et expressifs. Voir la [documentation AssertJ](https://assertj.github.io/doc/).

---

## Couverture de code

### Objectifs de couverture

| Métrique | Cible minimum | Cible idéale |
|----------|---------------|--------------|
| Couverture lignes | > 80% | > 90% |
| Couverture branches | > 70% | > 80% |
| Mutations tuées (PIT) | > 75% | > 85% |

### Génération et consultation du rapport

```bash
# Générer le rapport après les tests
./gradlew test integrationTest jacocoTestReport

# Le rapport HTML est disponible ici :
# build/reports/jacoco/test/html/index.html
```

### Interprétation

| Couleur | Couverture | Action |
|---------|------------|--------|
| 🟢 Vert | > 80% | Satisfaisante |
| 🟠 Orange | 60-80% | Amélioration nécessaire |
| 🔴 Rouge | < 60% | Insuffisante, prioriser |

> ⚠️ **Important** : La couverture mesure les lignes exécutées, pas la qualité des tests. Un test sans assertion peut donner 100% de couverture sans rien vérifier !

---


## Résumé des commandes

| Action | Commande |
|--------|----------|
| Tests unitaires | `./gradlew test` |
| Tests intégration | `./gradlew integrationTest` |
| Tests fonctionnels | `./gradlew funcTest` |
| Tous les tests | `./gradlew allTests` |
| Couverture | `./gradlew jacocoTestReport` |
| Vérification seuils | `./gradlew jacocoTestCoverageVerification` |
| Build complet | `./gradlew build` |

---

## Références

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html#page-title)
- [Zonky Embedded Database](https://github.com/zonkyio/embedded-database-spring-test)

---

## Historique de versions
- v0.1.0 (24-12-2025): Version initiale