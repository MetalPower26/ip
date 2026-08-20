#!/usr/bin/env bash
#
# Compiles Emma and runs one UI test case against it.
#
# Usage: run-case.sh <inputs-file> <raw-out> <normalised-out>
#
#   <inputs-file>     one command per line, exactly as the user would type them
#   <raw-out>         receives the full console transcript (what the tester sees)
#   <normalised-out>  receives just Emma's responses, for comparing against
#                     the "Expected output" block in the test plan
#
# Normalisation removes, in order:
#   - ANSI colour codes
#   - carriage returns (Windows line endings)
#   - the "Emma" / "user" speaker labels
#   - blank lines
#   - the startup banner and greeting (everything before the 2nd "Emma" label)
#
# Trailing whitespace on each line is also stripped so that invisible
# differences never fail a test.

set -euo pipefail

if [ "$#" -ne 3 ]; then
    echo "usage: run-case.sh <inputs-file> <raw-out> <normalised-out>" >&2
    exit 2
fi

inputs="$1"
raw_out="$2"
norm_out="$3"

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../.." && pwd)"
classes="$(mktemp -d)"
trap 'rm -rf "$classes"' EXIT

javac -d "$classes" "$repo_root"/src/main/java/*.java

# The program may exit non-zero if it crashes; keep the transcript either way
# so the failure report can show what actually happened.
java -cp "$classes" Emma < "$inputs" > "$raw_out" 2>&1 || true

sed -e 's/\x1b\[[0-9;]*m//g' -e 's/[[:space:]]*$//' "$raw_out" \
    | tr -d '\r' \
    | awk '
        /^Emma$/ { emmaLabels++; next }
        /^user$/ { next }
        /^$/     { next }
        emmaLabels >= 2 { print }
    ' > "$norm_out"
