#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR" || { echo "Failed to change directory to $DIR"; exit 1; }

if [ -x "./stop.sh" ]; then
    ./stop.sh
else
    echo "'stop.sh' is not executable or not found. Exiting."
    exit 2
fi
if [ -x "./start-background.sh" ]; then
    ./start-background.sh "$@"
else
    echo "'start-background.sh' is not executable or not found. Exiting."
    exit 3
fi
