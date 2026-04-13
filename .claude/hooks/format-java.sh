#!/bin/bash
# Hook formatage — exécuté après chaque Edit/Write sur un fichier .java
# Lit le JSON du tool depuis stdin, extrait file_path, lance spotlessApply si .java

input=$(cat)
file=$(echo "$input" | python3 -c "
import json, sys
d = json.load(sys.stdin)
path = d.get('tool_input', {}).get('file_path', '')
print(path)
" 2>/dev/null)

if [[ "$file" == *.java ]]; then
    cd /home/pp6649/IdeaProjects/training/training_chat/backend
    echo "[format] Formatage de $file..."
    ./gradlew spotlessApply --quiet 2>&1 | grep -v "^$" | tail -5
fi
