# CLAUDE.md — Assist v1.1

## Règles entreprise (inclure automatiquement)

@claude-rules/springboot-rules-v0.1.0.md
@claude-rules/springboot-test-rules-v0.1.0.md
@claude-rules/angular-rules-v0.1.0.md

---

## Présentation du projet

**Assist** est un assistant développeur intelligent full-stack propulsé par Claude AI.
Il offre une interface conversationnelle (chat) permettant la recherche de documentation,
la génération de code, la revue de PR, l'analyse de bugs et l'orchestration de tâches via des agents IA.

## Stack technique

| Couche      | Technologie                                        |
|-------------|----------------------------------------------------|
| Backend     | Java 21 · Spring Boot 3.x · Gradle                 |
| Frontend    | Angular 17+ · Angular Material · npm               |
| IA          | Claude API (Anthropic SDK Java)                    |
| Streaming   | Server-Sent Events (SSE)                           |
| Persistance | H2 in-memory (embarqué JVM — runtime + tests)      |
| Tests       | Cucumber (principal) · JUnit 5 (cas particuliers)  |

## Structure des répertoires

```
training_chat/
├── CLAUDE.md
├── claude-rules/                   ← Règles entreprise (lecture seule)
├── backend/                        ← Projet Spring Boot (Gradle)
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── .env.example
│   └── src/
│       ├── main/
│       │   ├── java/com/assist/
│       │   │   ├── AssistApplication.java
│       │   │   ├── config/         ← CorsConfig, ClaudeConfig
│       │   │   ├── controller/     ← @RestController (routing + validation uniquement)
│       │   │   ├── service/        ← Logique métier
│       │   │   ├── model/          ← Entités JPA + DTOs (records)
│       │   │   └── exception/      ← @ControllerAdvice global
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           ├── java/com/assist/
│           │   ├── steps/          ← Step definitions Cucumber
│           │   └── config/         ← CucumberSpringConfiguration
│           └── resources/
│               └── features/       ← Fichiers .feature (Gherkin)
└── frontend/                       ← Projet Angular CLI
    ├── angular.json
    ├── package.json
    ├── proxy.conf.json             ← Proxy → http://localhost:8080
    └── src/app/
        ├── components/             ← Composants Angular (un dossier par composant)
        ├── services/               ← Services Angular (HttpClient, SSE)
        └── models/                 ← Interfaces TypeScript (*.model.ts)
```

## Conventions de code — Backend

- **Injection** : toujours par constructeur ; si Lombok installé → `@RequiredArgsConstructor`
- **Controllers** : routing + validation uniquement ; `@GetMapping`/`@PostMapping`/etc. (jamais `@RequestMapping` sur les méthodes)
- **Validation** : `@Valid` sur `@RequestBody`, `@Validated` sur la classe contrôleur ; messages personnalisés
- **DTOs** : records Java obligatoires ; jamais exposer une entité JPA directement
- **Entités JPA** : `@Getter`/`@Setter`/`@NoArgsConstructor` si Lombok — jamais `@Data` ni `@ToString` sur les entités
- **Repositories** : `JpaRepository` ; derived queries en priorité, `@Query` uniquement si impossible autrement
- **Imports** : explicites uniquement — jamais de wildcard `import xxx.*`
- **Commentaires** : aucun sauf logique non évidente (algorithme complexe, workaround tiers)
- **Erreurs** : format uniforme via `@ControllerAdvice` → `{ error, message, details? }`

## Conventions de code — Frontend

- **Fichiers** : `kebab-case.component.ts`, `kebab-case.service.ts`, max **200 lignes**
- **Fonctions** : max **25 lignes** ; templates inline max **5 lignes** (sinon fichier `.html` séparé)
- **Signals** : privés préfixés `_` → `private readonly _users = signal([])`
- **Observables** : suffixe `$` obligatoire → `users$`, `isLoading$`
- **Barrel exports** : fichier `index.ts` dans chaque dossier feature/composant
- **Linting** : ESLint + Prettier (Husky pre-commit)
- **Pas d'appel direct à l'API Anthropic** depuis le frontend (tout passe par le backend)
- **CORS** : le backend autorise uniquement `http://localhost:4200`

## Tests — Stratégie

### Règle principale : Cucumber BDD

**Cucumber est le framework de test par défaut** pour le backend.
Les tests unitaires JUnit 5 sont réservés aux cas particuliers (voir ci-dessous).

### Structure des tests Cucumber

```
src/test/
├── java/com/assist/
│   ├── config/
│   │   └── CucumberSpringConfiguration.java   ← @CucumberContextConfiguration
│   └── steps/
│       ├── HealthSteps.java
│       ├── ConfigSteps.java
│       └── ChatSteps.java
└── resources/
    └── features/
        ├── m1-health.feature
        ├── m1-config.feature
        └── m2-chat.feature
```

### Dépendances Cucumber (build.gradle.kts)

```kotlin
testImplementation("io.cucumber:cucumber-java:7.20.1")
testImplementation("io.cucumber:cucumber-spring:7.20.1")
testImplementation("io.cucumber:cucumber-junit-platform-engine:7.20.1")
testImplementation("org.junit.platform:junit-platform-suite")
testImplementation("org.springframework.boot:spring-boot-starter-test")
```

### Configuration Cucumber

```java
// CucumberSpringConfiguration.java
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {}
```

```java
// CucumberTestSuite.java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.assist.steps,com.assist.config")
public class CucumberTestSuite {}
```

### Pattern obligatoire : Given → When → Then (API + BDD)

**Chaque scénario Cucumber doit suivre ce cycle :**
1. **Given** — insérer les données de test en base via les repositories Spring
2. **When** — déclencher l'API via `TestRestTemplate` ou `MockMvc`
3. **Then** — vérifier **à la fois** la réponse HTTP (statut + corps) **et** l'état en base de données

```gherkin
Feature: Historique de conversation — M2

  Scenario: Envoyer un message enregistre l'échange en base
    Given la session "sess-001" ne contient aucun message
    When je POST /chat avec { "message": "Bonjour", "session_id": "sess-001" }
    Then le statut HTTP est 200
    And la réponse contient un champ "response" non vide
    And la base contient 2 messages pour la session "sess-001"
    And le premier message a le rôle "USER" et le contenu "Bonjour"
    And le second message a le rôle "ASSISTANT"

  Scenario: Supprimer l'historique vide la base
    Given la session "sess-002" contient 3 messages en base
    When je DELETE /chat/history avec session_id "sess-002"
    Then le statut HTTP est 204
    And la base ne contient aucun message pour la session "sess-002"
```

```java
// Exemple de step definitions
@Component
public class ChatSteps {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ChatMessageRepository chatMessageRepository;

    @Given("la session {string} ne contient aucun message")
    public void givenEmptySession(String sessionId) {
        chatMessageRepository.deleteBySessionId(sessionId);
    }

    @Given("la session {string} contient {int} messages en base")
    public void givenSessionWithMessages(String sessionId, int count) {
        chatMessageRepository.deleteBySessionId(sessionId);
        IntStream.range(0, count).forEach(i ->
            chatMessageRepository.save(new ChatMessage(sessionId, Role.USER, "message " + i))
        );
    }

    @When("je POST /chat avec \\{ {string}: {string}, {string}: {string} \\}")
    public void whenPostChat(String k1, String v1, String k2, String v2) {
        var request = Map.of(k1, v1, k2, v2);
        lastResponse = restTemplate.postForEntity("/chat", request, String.class);
    }

    @Then("le statut HTTP est {int}")
    public void thenStatusIs(int status) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(status);
    }

    @Then("la base contient {int} messages pour la session {string}")
    public void thenDbContainsMessages(int count, String sessionId) {
        assertThat(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId))
            .hasSize(count);
    }
}
```

**Règles du pattern :**
- Le `Given` utilise **directement les repositories** pour insérer/nettoyer les données (pas d'appel API)
- Le `When` déclenche **un seul appel HTTP** par scénario
- Le `Then` vérifie systématiquement **les deux** : réponse HTTP **et** état BDD via repositories
- Chaque scénario est **autonome** : le `Given` remet la base dans un état connu avant d'agir
- Utiliser `AssertJ` pour toutes les assertions (`assertThat(...)`)

### Cas particuliers — Tests unitaires JUnit 5 autorisés

Tests unitaires (JUnit 5 + Mockito) **uniquement** pour :
- Algorithmes / logique pure sans Spring (parsers, scorers, utilitaires)
- Méthodes de mapping complexes
- Cas où l'instanciation du contexte Spring serait disproportionnée

Nommage : `{Classe}Test.java`, méthodes `should{Action}When{Condition}`.

### Tests Frontend

- **Jest** ou **Vitest** (pas Karma, déprécié depuis 2023)
- **Playwright** ou **Cypress** pour les tests E2E
- Minimum 1 test par composant critique : `ChatContainer`, `MessageInput`, `StreamingText`

### Commandes

```bash
./gradlew test          # Tous les tests (Cucumber + JUnit)
ng test                 # Tests frontend (Jest/Vitest)
```

## Base de données (H2 in-memory + JPA)

```properties
spring.datasource.url=jdbc:h2:mem:assist;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=false
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Entités JPA :**
- `ChatMessage` : `id`, `sessionId`, `role` (USER/ASSISTANT), `content`, `createdAt`
- `AssistConfig` : `id` (singleton = 1), `model`, `maxTokens`, `temperature`

## Variables d'environnement (backend)

```
ANTHROPIC_API_KEY=sk-ant-...
CLAUDE_MODEL=claude-sonnet-4-5-20250514
CLAUDE_MAX_TOKENS=4096
CLAUDE_TEMPERATURE=0.7
SERVER_PORT=8080
DOCS_PATH=./docs
TEST_COMMAND=./gradlew test
```

## Modules et endpoints

| Module | Endpoints                                                               |
|--------|-------------------------------------------------------------------------|
| M1     | GET /health · GET /config · POST /config                                |
| M2     | POST /chat · POST /chat/stream · GET /chat/history · DELETE /chat/history |
| M3     | POST /search                                                            |
| M4     | POST /chat (+ tools) · POST /analyze-screenshot                        |
| M5     | MCP Server (stdio)                                                      |
| M6     | POST /orchestrate                                                       |

## Format des erreurs API

```json
{
  "error": "ERROR_CODE",
  "message": "Description lisible",
  "details": {}
}
```

## Commits

Format Conventional Commits :
```
feat(m1): add GET /health endpoint
feat(m2): implement SSE streaming for /chat/stream
fix(m3): handle missing docs/ directory gracefully
test(m1): add Cucumber scenarios for health and config
```

## Roadmap de développement

```
Phase 1 : M1 backend (/health, /config) + scaffold Angular
Phase 2 : M3 backend (POST /search)
Phase 3 : Refactoring Clean Architecture
Phase 4 : M2 backend (chat + SSE) + frontend chat complet
Phase 5 : M4 backend (tool calling) + composants Angular tools
Phase 6 : M5 MCP Server
Phase 7 : M6 multi-agents + dashboard Angular
```

## Modèles Claude disponibles

| Alias       | Model ID                    |
|-------------|-----------------------------|
| Opus 4.6    | claude-opus-4-6             |
| Sonnet 4.5  | claude-sonnet-4-5-20250514  |
| Haiku 4.5   | claude-haiku-4-5-20251001   |

Modèle par défaut : `claude-sonnet-4-5-20250514`
