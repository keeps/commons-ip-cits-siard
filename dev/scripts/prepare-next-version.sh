#!/bin/bash

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

REGEX="([0-9]+).([0-9]+).([0-9]+)"

if [[ $VERSION =~ $REGEX ]]; then
    MAJOR="${BASH_REMATCH[1]}"
    MINOR="${BASH_REMATCH[2]}"
fi

MINOR=$((MINOR + 1))
BUILD="0"

SNAPSHOT_VERSION="${MAJOR}.${MINOR}.${BUILD}-SNAPSHOT"

cat <<EOF
################################
# Prepare for next version
################################
EOF

echo "Update version to $SNAPSHOT_VERSION"

mvn versions:set versions:commit -DnewVersion="$SNAPSHOT_VERSION"

git add pom.xml
git commit -S -m "Setting version $SNAPSHOT_VERSION"
git push
