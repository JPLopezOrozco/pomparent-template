#!/usr/bin/env bash
set -euo pipefail
if [ $# -lt 3 ]; then
  echo "Uso: scripts/stamp.sh <groupId> <artifactId> <version>"
  exit 1
fi
GROUP_ID="$1"; ARTIFACT_ID="$2"; VERSION="$3"

sed -i.bak "s|__GROUP_ID__|${GROUP_ID}|g" pom.xml
sed -i.bak "s|__ARTIFACT_ID__|${ARTIFACT_ID}|g" pom.xml
sed -i.bak "s|__VERSION__|${VERSION}|g" pom.xml
rm -f pom.xml.bak
echo "OK: pom.xml personalizado"