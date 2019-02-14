#!/usr/bin/env bash

# This script downloads github-release-notes, aka GREN and release notes are generated.
# GIT_TAG is sent in to this script from travis

REGEX=^[0-9]+\.[0-9]+\.[0-9]+$      # a regex matching git tags set by developers.

echo "Started script"

echo $GIT_TAG
echo $SHOULD_RELEASE

if [[ $GIT_TAG =~ $REGEX ]]; then
    export SHOULD_RELEASE=true
    echo $SHOULD_RELEASE
    echo "Generating release notes ..."
    npm install github-release-notes -g
    gren release --debug --tags=$GIT_TAG --data-source=commits -T $GREN_GITHUB_TOKEN;
else
    echo "Skipping release because a correct git tag was not found";
fi
