#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$REPO_ROOT/.env"

# ── Load .env ────────────────────────────────────────────────────────────────
if [[ ! -f "$ENV_FILE" ]]; then
  echo ""
  echo "ERROR: .env file not found."
  echo "  Copy .env.example to .env and set PRISM_MODS_PATH to your Prism"
  echo "  instance's mods folder, e.g.:"
  echo "    PRISM_MODS_PATH=/mnt/c/Users/<you>/AppData/Roaming/PrismLauncher/instances/create-colonies-dev/.minecraft/mods"
  echo ""
  exit 1
fi

# shellcheck source=/dev/null
source "$ENV_FILE"

if [[ -z "${PRISM_MODS_PATH:-}" ]]; then
  echo "ERROR: PRISM_MODS_PATH is not set in .env"
  exit 1
fi

if [[ ! -d "$PRISM_MODS_PATH" ]]; then
  echo "ERROR: PRISM_MODS_PATH does not exist or is not a directory:"
  echo "  $PRISM_MODS_PATH"
  echo ""
  echo "Make sure the Prism instance exists on Windows and the path is"
  echo "accessible from WSL (e.g. mounted under /mnt/c/...)."
  exit 1
fi

# ── Find the built JAR ───────────────────────────────────────────────────────
BUILD_LIBS="$REPO_ROOT/build/libs"
# Exclude -sources and -javadoc jars
JAR=$(find "$BUILD_LIBS" -maxdepth 1 -name "create_colonies-*.jar" \
      ! -name "*-sources.jar" ! -name "*-javadoc.jar" 2>/dev/null | head -n1)

if [[ -z "$JAR" ]]; then
  echo ""
  echo "ERROR: No built JAR found in build/libs/."
  echo "  Run './gradlew build' first, then re-run this script."
  echo ""
  exit 1
fi

JAR_NAME="$(basename "$JAR")"

# ── Remove old version, copy new ────────────────────────────────────────────
echo ""
echo "Removing old create_colonies-*.jar from Prism mods folder..."
find "$PRISM_MODS_PATH" -maxdepth 1 -name "create_colonies-*.jar" -delete

echo "Copying $JAR_NAME → $PRISM_MODS_PATH/"
cp "$JAR" "$PRISM_MODS_PATH/"

echo ""
echo "Done! Now launch your Prism 'create-colonies-dev' instance on Windows."
echo ""
