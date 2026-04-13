Feature: Health Check — M1

  Scenario: Le serveur répond 200 avec status ok quand la clé API est configurée
    When je GET /health
    Then le statut HTTP est 200
    And la réponse contient le champ "status" avec la valeur "ok"
    And la réponse contient le champ "version" non nul
    And la réponse contient le champ "uptime" non nul
