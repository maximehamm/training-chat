Feature: Configuration — M1

  Scenario: Récupérer la configuration courante retourne 200 avec les champs attendus
    When je GET /config
    Then le statut HTTP est 200
    And la réponse contient le champ "model" non nul
    And la réponse contient le champ "max_tokens" non nul
    And la réponse contient le champ "temperature" non nul

  Scenario: Mettre à jour le modèle modifie la valeur en base
    Given la configuration est réinitialisée aux valeurs par défaut
    When je POST /config avec le body "{"model": "claude-opus-4-6"}"
    Then le statut HTTP est 200
    And la réponse contient le champ "model" avec la valeur "claude-opus-4-6"
    And la base contient le modèle "claude-opus-4-6"

  Scenario: Un modèle invalide retourne 400
    When je POST /config avec le body "{"model": "gpt-4"}"
    Then le statut HTTP est 400
    And la réponse contient une erreur "VALIDATION_ERROR"
