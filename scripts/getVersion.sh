#!/usr/bin/env bash

./gradlew properties --no-daemon --console=plain -q | grep "^version:" | awk '{printf $2}'
