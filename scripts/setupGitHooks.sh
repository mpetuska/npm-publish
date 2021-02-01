#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )";
echo "$DIR";

mkdir -p "$DIR/../.git/hooks/";
echo "Configured pre-commit hook";
chmod +x "$DIR/../.git/hooks/pre-commit";
cp -f "$DIR/pre-commit" "$DIR/../.git/hooks/pre-commit";

echo "Configured pre-push hook";
cp -f "$DIR/pre-push" "$DIR/../.git/hooks/pre-push";
chmod +x "$DIR/../.git/hooks/pre-push";
