#!/bin/bash
#bash scripts/merge.sh "prod"

IFS=' '
read -a args <<< "$1"

mkdir -p target/classes/static
cp -R ../Elex-ui-"${args[0]}"/dist/* target/classes/static
cp -R ../Elex-ui-"${args[0]}"/src/version.ts target/classes