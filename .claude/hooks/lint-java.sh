#!/bin/bash
# Hook linting — exécuté après chaque Edit/Write sur un fichier .java
# Lance compileJava pour détecter les erreurs de compilation immédiatement

input=$(cat)
file=$(echo "$input" | python3 -c "
import json, sys
d = json.load(sys.stdin)
path = d.get('tool_input', {}).get('file_path', '')
print(path)
" 2>/dev/null)

if [[ "$file" == *.java ]]; then
    cd /home/pp6649/IdeaProjects/training/training_chat/backend
    echo "[lint] Vérification compilation de $file..."
    output=$(./gradlew compileJava --quiet 2>&1)
    if [ $? -ne 0 ]; then
        echo "❌ Erreur de compilation détectée :"
        echo "$output" | grep -E "error:|warning:" | head -10
        exit 1
    else
        echo "✅ Compilation OK"
    fi
fi
