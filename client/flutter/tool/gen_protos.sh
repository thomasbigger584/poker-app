#!/usr/bin/env bash
#
# Generates Dart protobuf types from the shared wire contract under proto/poker/.
#
# These generated files are the single source of truth for DTOs/enums on the
# Flutter client, mirroring how the server (protobuf-java) and Android
# (protobuf-javalite) generate from the same .proto files. Do not hand-edit the
# output; change the .proto and re-run this script.
#
# Requirements (one-time):
#   dart pub global activate protoc_plugin     # installs protoc-gen-dart
#   protoc binary                               # vendored under tool/protoc/bin
#
# Usage:
#   ./tool/gen_protos.sh        (run from client/flutter)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FLUTTER_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROTO_ROOT="$(cd "${FLUTTER_DIR}/../../proto" && pwd)"
OUT_DIR="${FLUTTER_DIR}/lib/core/proto/gen"

# Prefer the vendored protoc, fall back to one on PATH.
if [[ -x "${SCRIPT_DIR}/protoc/bin/protoc.exe" ]]; then
  PROTOC="${SCRIPT_DIR}/protoc/bin/protoc.exe"
elif command -v protoc >/dev/null 2>&1; then
  PROTOC="protoc"
else
  echo "error: protoc not found (expected tool/protoc/bin/protoc.exe or on PATH)" >&2
  exit 1
fi

# protoc discovers the Dart plugin via PATH; pub global installs it here.
PUB_BIN="${PUB_CACHE:-$HOME/.pub-cache}/bin"
export PATH="${PUB_BIN}:${PATH}"

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

echo "protoc:     ${PROTOC}"
echo "proto root: ${PROTO_ROOT}"
echo "out:        ${OUT_DIR}"

"${PROTOC}" \
  --proto_path="${PROTO_ROOT}" \
  --dart_out="${OUT_DIR}" \
  poker/enums.proto \
  poker/domain.proto \
  poker/validation.proto \
  poker/rest.proto \
  poker/websocket.proto

echo "done. generated:"
ls -1 "${OUT_DIR}/poker"
