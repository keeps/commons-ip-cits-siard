#!/bin/bash

release() {

  cat <<EOF
################################
# Release version
################################
EOF
  RELEASE_VERSION=$1
  RELEASE_TAG="v$RELEASE_VERSION"
  # Ensure all classes have license header
  mvn license:format

  # Updating Maven modules
  mvn versions:set versions:commit -DnewVersion="$RELEASE_VERSION"

  # Commit Maven version update
  git add -u
  git commit -S -m "Release version $RELEASE_VERSION"

  # Create tag
  git tag -s -a "$RELEASE_TAG" -m "Version $RELEASE_VERSION"

  # Push tag
  git push origin "$RELEASE_TAG"

  return 0
}

# Check if an argument was passed
if [ $# -eq 0 ]; then
  echo "No arguments provided."
  echo "Usage: $0 <version>"
  echo "Example: $0 2.5.0"
  exit 1
else
  regex="([0-9]+).([0-9]+).([0-9]+)"
  if [[ $1 =~ $regex ]]; then
    read -p "Did you update the CHANGELOG.md? (yes/no): " answer
    case $answer in
    [Yy]*) release "$1" ;;
    [Nn]*)
      echo "Update the CHANGELOG.md before release a new version"
      exit 1
      ;;
    *) echo "Please answer yes or no." ;;
    esac
  else
    echo "Please provide a version that follows semantic versioning syntax"
    exit 1
  fi
fi
