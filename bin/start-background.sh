#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR" || { echo "Failed to change directory to $DIR"; exit 1; }

echo "JPro will be started in the background."
if [ -x "./start.sh" ]; then
    nohup ./start.sh "$@" > jpro_nohup.log 2>&1 &
else
    echo "'start.sh' is not executable or not found. Exiting."
    exit 2
fi
