# Setup MCP IntelliJ pour Claude Code (WSL2)

Ce guide explique comment connecter Claude Code (CLI sous WSL2) au plugin MCP d'IntelliJ IDEA,
afin que Claude puisse interagir directement avec le projet ouvert dans l'IDE.

## Prérequis

- IntelliJ IDEA installé sur Windows
- Claude Code installé dans WSL2
- WSL2 version ≥ 2.0.0 (vérifier avec `wsl --version` depuis PowerShell Windows)
- Windows 11 version 22H2 ou supérieure

## Étape 1 — Installer le plugin MCP dans IntelliJ

1. Dans IntelliJ : **File → Settings → Plugins**
2. Chercher **"MCP Server"** (ou "Claude MCP") dans le marketplace
3. Installer et redémarrer IntelliJ
4. Vérifier que le serveur SSE est actif (indicateur dans la barre d'état ou dans les logs IntelliJ)
5. Le plugin écoute par défaut sur `127.0.0.1:64342`

> Si le port est différent, adapter les étapes suivantes en conséquence.

## Étape 2 — Activer le mode réseau miroir dans WSL2

Par défaut, WSL2 utilise un réseau NAT isolé. Le mode miroir partage les interfaces réseau
de Windows avec WSL2, ce qui permet d'accéder à `localhost` Windows depuis WSL.

Depuis WSL, exécuter :

```bash
powershell.exe -Command "[System.IO.File]::WriteAllText([System.Environment]::ExpandEnvironmentVariables('%USERPROFILE%\.wslconfig'), \"[wsl2]\`nnetworkingMode=mirrored\`n\")"
```

Vérifier le contenu :

```bash
powershell.exe -Command "Get-Content ~\.wslconfig"
# Résultat attendu :
# [wsl2]
# networkingMode=mirrored
```

**Puis depuis PowerShell ou CMD Windows (pas depuis WSL), exécuter :**

```powershell
wsl --shutdown
```

Attendre quelques secondes, puis rouvrir WSL.

> ⚠️ Fermer le terminal WSL ne suffit pas. Il faut impérativement `wsl --shutdown` depuis Windows.

## Étape 3 — Vérifier que le mode miroir est actif

Dans WSL, après redémarrage :

```bash
ip addr show eth0
```

En mode miroir, l'IP sera celle du réseau de la machine Windows (ex: `192.168.x.x`),
et non une IP privée `172.x.x.x` (qui indique le mode NAT).

Tester l'accès à IntelliJ :

```bash
curl --max-time 3 --noproxy '*' http://localhost:64342/sse
```

La réponse doit commencer par `HTTP/1.1 200 OK` avec `Content-Type: text/event-stream`.

## Étape 4 — Configurer Claude Code

Ajouter le serveur MCP IntelliJ dans la configuration de Claude Code :

```bash
cat ~/.claude.json | python3 -c "
import json, sys
d = json.load(sys.stdin)
d['mcpServers'] = d.get('mcpServers', {})
d['mcpServers']['intellij'] = {
    'type': 'sse',
    'url': 'http://localhost:64342/sse'
}
print(json.dumps(d, indent=2))
" > /tmp/claude_new.json && mv /tmp/claude_new.json ~/.claude.json
```

## Étape 5 — Vérifier la connexion dans Claude Code

Redémarrer Claude Code, puis taper :

```
/mcp
```

Le serveur `intellij` doit apparaître comme connecté.

## Utilisation

Dans Claude Code, les outils IntelliJ sont disponibles sous le préfixe `mcp__intellij__*`.

Le paramètre `projectPath` à utiliser est le chemin UNC WSL du projet :

```
//wsl.localhost/Ubuntu-22.04/home/<user>/IdeaProjects/<projet>
```

Exemples de ce que Claude peut faire via ce MCP :
- Lire les fichiers ouverts dans l'IDE
- Chercher dans le code (par texte, regex, symbole)
- Obtenir les problèmes détectés par IntelliJ (erreurs, warnings)
- Exécuter des configurations de run/debug
- Naviguer dans l'arborescence du projet
- Reformater des fichiers, renommer des symboles

## Dépannage

### `/mcp` affiche "Failed to reconnect to intellij"

- Vérifier que IntelliJ est ouvert et que le plugin MCP est actif
- Vérifier que WSL est bien en mode miroir (`ip addr show eth0` → IP Windows attendue)
- Tester la connexion : `curl --noproxy '*' http://localhost:64342/sse`

### La connexion timeout sur `localhost:64342`

WSL n'est probablement pas en mode miroir. Refaire l'étape 2 et bien exécuter `wsl --shutdown` depuis Windows.

### Le mode miroir ne s'active pas après `wsl --shutdown`

Vérifier la version WSL :
```powershell
wsl --version
```
La version WSL doit être ≥ 2.0.0 et Windows ≥ 11 22H2 (build 22621).
Si WSL est trop ancien : `wsl --update` depuis Windows.
