#!/usr/bin/env bash

set -e

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

mvn -f "$PROJECT_DIR"/pom.xml clean package -DskipTests -Pcli

# Clean up target folder
rm -rf "$SCRIPT_DIR"/target/*
mkdir -p "$SCRIPT_DIR"/target
# Copy target
cp -r "$PROJECT_DIR"/target/*.jar "$SCRIPT_DIR"/target/commons-ip-cits-siard.jar

docker build -t keeps/commons-ip-cits-siard:latest "$SCRIPT_DIR"
