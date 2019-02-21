#!/usr/bin/env bash

# This script downloads github-release-notes, aka GREN and release notes are generated.
# GIT_TAG is sent in to this script from travis

REGEX=^[0-9]+\.[0-9]+\.[0-9]+$      # a regex matching git tags set by developers.

git fetch --tags -q
GIT_TAG=$(git describe --tags $TRAVIS_COMMIT)    # export the git tag pointing to the current commit


echo "Started script"
echo $GIT_TAG

# First we check if git tag is valid for release
if [[ $GIT_TAG =~ $REGEX ]]; then
    echo "Generating release notes ..."
    npm install github-release-notes -g
    gren release --debug --tags=$GIT_TAG --data-source=commits -T $GREN_GITHUB_TOKEN

    echo "Stepping versions in pom file to $GIT_TAG"
    mvn versions:set -DnewVersion=$GIT_TAG -DgenerateBackupPoms=false   # Step version in pom file
    git status
    git diff pom.xml;

else
    echo "Skipping release because a correct git tag was not found"
    travis_terminate 0;
fi
