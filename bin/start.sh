#!/bin/bash

# Get the directory of the current script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Change to that directory
cd "$DIR" || { echo "Failed to change directory to '$DIR'"; exit 1; }
# Change to the parent directory
cd .. || { echo "Failed to change directory to parent"; exit 2; }

# Set the application directory
APP_DIR=$(pwd)
echo "Application directory: '$APP_DIR'"

# Print arguments if any are provided
if [ "$#" -gt 0 ]; then\
  echo "Provided arguments: $@"
fi

# Determine the platform (OS)
case "${OSTYPE}" in
  darwin*) PLATFORM="mac" ;;
  linux*)
    if [ -z "$LC_CTYPE" ] || [ "$LC_CTYPE" = "UTF-8" ]; then
        export LC_CTYPE="C.UTF-8"
    fi
    PLATFORM="linux"
    if [ "$LC_CTYPE" = "UTF-8" ]; then
      export LC_CTYPE="C.UTF-8"
    fi
    ;;
  *) echo "Unsupported OS: $OSTYPE"; exit 3 ;;
esac
echo "Current platform: $PLATFORM"

# Determine the architecture
ARCH=""
UARCH="$(uname -m)"
# Map ARM architectures to aarch64
case "$UARCH" in
  *arm*|aarch64) ARCH="-aarch64" ;;
esac
echo "Current architecture: $ARCH"

# Create a classifier string based on platform and architecture
CLASSIFIER="$PLATFORM$ARCH"
echo "Classifier: $CLASSIFIER"

# Set the required directories
LIBS_DIR="$APP_DIR/libs"
JPRO_LIBS="$APP_DIR/jprolibs"
JFX_DIR="$APP_DIR/jfx"

# Check if the JavaFX builds directory exists for the classifier
if [ ! -d "$JFX_DIR/$CLASSIFIER" ]; then
    echo "Failed to find JavaFX builds for platform '$CLASSIFIER' in this release."
    exit 4
fi

# Set default working directory from environment variable if available
WORKING_DIR="${JPRO_WORKING_DIR:-}"

# Parse arguments to find the working directory if specified and filter out the argument
FILTERED_ARGS=()
while [[ "$#" -gt 0 ]]; do
    case "$1" in
        --working-dir) WORKING_DIR="$2"; shift 2 ;;
        *) FILTERED_ARGS+=("$1"); shift ;;
    esac
done

# Use the filtered arguments list for further processing
set -- "${FILTERED_ARGS[@]}"

# Change to the working directory if specified
if [ -n "$WORKING_DIR" ]; then
    if [ -d "$WORKING_DIR" ]; then
        echo "Working directory: '$WORKING_DIR'"
        cd "$WORKING_DIR" || { echo "Failed to change directory to '$WORKING_DIR'"; exit 5; }
    else
        echo "The working directory '$WORKING_DIR' does not exist."
        exit 6
    fi
fi

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Prepare the classpath and arguments
CLASSPATH=$(JARS=("$LIBS_DIR"/*.jar "$JFX_DIR/$CLASSIFIER"/*.jar); IFS=:; echo "${JARS[*]}")
JPROCLASSPATH=$(JARS=("$JPRO_LIBS"/*.jar); IFS=:; echo "${JARS[*]}")
JPRO_ARGS=("-Djpro.applications.default=bookstore.Main" "-Dfile.encoding=UTF-8" "-Dprism.useFontConfig=false" "-Dhttp.port=8080" "-Dquantum.renderonlysnapshots=true" "-Dglass.platform=Monocle" "-Dmonocle.platform=Headless" "-Dcom.sun.javafx.gestures.scroll=true" "-Djpro.deployment=MAVEN-Distribution" "-Dprism.fontdir=$APP_DIR/fonts/")
# Print the JPro arguments if any are provided
if (( ${#JPRO_ARGS[@]} )); then
    echo "JPro arguments: ${JPRO_ARGS[*]}"
fi

echo "JPro will be started"
# Execute the Java command with the constructed arguments
$JAVACMD "${JPRO_ARGS[@]}" "-Djprocp=$JPROCLASSPATH" "$@" -cp "$CLASSPATH" com.jpro.boot.JProBoot
