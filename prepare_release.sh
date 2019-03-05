#!/usr/bin/env bash


# Check if release should be performed, otherwise abort?
export TRAVIS_COMMIT_MESSAGE="This is my second [RELEASE MAJOR] commit. \n I will try to perform a release [RELEASE MINOR] with this tag."

echo "Evaluating commit message for release keywords ..."
echo $TRAVIS_COMMIT_MESSAGE

export REGEX_MINOR=^\[RELEASE\sMINOR\]$
export REGEX_MAJOR=^\[RELEASE\sMAJOR\]$

if [[ $TRAVIS_COMMIT_MESSAGE =~ *$REGEX_MINOR* ]]; then
    echo "Found keyword MINOR";
elif [[ $TRAVIS_COMMIT_MESSAGE =~ *$REGEX_MAJOR* ]]; then
    echo "Found keyword MAJOR";
else
    echo "Did not find matching keywords, will default to PATCH release";
fi

export changeVersion="minor"
# converting to uppercase in if statements
if [[ ${changeVersion^^} == "MINOR" ]]; then
    echo "MINOR version change";

elif [[ ${changeVersion^^} == "MAJOR" ]]; then
    echo "MAJOR version change";

elif [[ !${changeVersion} ]]; then
    echo "Version change type not found in commit message. Defaulting to PATCH";
fi;

## Step version in pom file
    echo "Stepping version in pom file to " + ${newVersion}
    # mvn versions ${newVersion}

## Create new git tag based on version
    echo "Creating git tag with new version " + $1
    # git tag ${newVersion}

## Push git tag and updated pom file to repository
    echo "Pushing git tags + updated pom file to repository"

## Generate release notes and attach to existing git tag
    echo "Generating release notes ..."
    # npm install github-release-notes -g
    # gren release --debug --tags=${newVersion} --data-source=commits -T $GREN_GITHUB_TOKEN