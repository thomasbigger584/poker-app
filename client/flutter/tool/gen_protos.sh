#!/usr/bin/env bash
#
# Generates Dart protobuf types from the shared wire contract under proto/poker/.
#
# These generated files are the single source of truth for DTOs/enums on the
# Flutter client, mirroring how the server (protobuf-java) and Android
# (protobuf-javalite) generate from the same .proto files. They are NOT checked
# into git — run this script once after cloning (and after any .proto change).
# Do not hand-edit the output.
#
# The script is self-contained: it downloads a vendored `protoc` into
# tool/protoc/ and activates the Dart plugin (`protoc-gen-dart`) if either is
# missing. Requires `dart`/`flutter`, `curl` and `unzip` on PATH.
#
# Usage:
#   ./tool/gen_protos.sh        (run from anywhere)
set -euo pipefail

PROTOC_VERSION="29.3"
# Pin the Dart plugin so generated code matches the `protobuf` runtime pinned in
# pubspec.yaml (protoc_plugin 25.x → protobuf 6.x). Bump both together.
PROTOC_PLUGIN_VERSION="25.0.0"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FLUTTER_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROTO_ROOT="$(cd "${FLUTTER_DIR}/../../proto" && pwd)"
OUT_DIR="${FLUTTER_DIR}/lib/core/proto/gen"

# protoc discovers the Dart plugin via PATH; `pub global` installs it under the
# pub cache, whose location varies by platform (and PUB_CACHE override). Add
# every known candidate so `protoc-gen-dart` is found on Linux/macOS/Windows CI.
for _cand in \
  "${PUB_CACHE:-}/bin" \
  "${HOME}/.pub-cache/bin" \
  "${LOCALAPPDATA:-}/Pub/Cache/bin" \
  "${APPDATA:-}/Pub/Cache/bin"; do
  if [[ "${_cand}" != "/bin" && ":${PATH}:" != *":${_cand}:"* ]]; then
    PATH="${_cand}:${PATH}"
  fi
done
export PATH

# --- Ensure the Dart plugin (protoc-gen-dart) is installed -------------------
ensure_dart_plugin() {
  if command -v protoc-gen-dart >/dev/null 2>&1; then return; fi
  echo "protoc-gen-dart not found — activating protoc_plugin ${PROTOC_PLUGIN_VERSION}..."
  dart pub global activate protoc_plugin "${PROTOC_PLUGIN_VERSION}" >/dev/null
}

# --- Ensure a protoc binary is available, downloading if needed --------------
ensure_protoc() {
  if [[ -x "${SCRIPT_DIR}/protoc/bin/protoc.exe" ]]; then
    PROTOC="${SCRIPT_DIR}/protoc/bin/protoc.exe"; return
  fi
  if [[ -x "${SCRIPT_DIR}/protoc/bin/protoc" ]]; then
    PROTOC="${SCRIPT_DIR}/protoc/bin/protoc"; return
  fi
  if command -v protoc >/dev/null 2>&1; then PROTOC="protoc"; return; fi

  local asset
  case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) asset="win64" ;;
    Darwin)               asset="osx-universal_binary" ;;
    Linux)
      case "$(uname -m)" in
        aarch64|arm64) asset="linux-aarch_64" ;;
        *)             asset="linux-x86_64" ;;
      esac ;;
    *) echo "error: unsupported OS '$(uname -s)' — install protoc manually" >&2; exit 1 ;;
  esac

  local url="https://github.com/protocolbuffers/protobuf/releases/download/v${PROTOC_VERSION}/protoc-${PROTOC_VERSION}-${asset}.zip"
  echo "Downloading protoc ${PROTOC_VERSION} (${asset})..."
  rm -rf "${SCRIPT_DIR}/protoc"
  curl -sSL -o "${SCRIPT_DIR}/protoc.zip" "${url}"
  unzip -o -q "${SCRIPT_DIR}/protoc.zip" -d "${SCRIPT_DIR}/protoc"
  rm -f "${SCRIPT_DIR}/protoc.zip"

  if [[ -x "${SCRIPT_DIR}/protoc/bin/protoc.exe" ]]; then
    PROTOC="${SCRIPT_DIR}/protoc/bin/protoc.exe"
  else
    chmod +x "${SCRIPT_DIR}/protoc/bin/protoc"
    PROTOC="${SCRIPT_DIR}/protoc/bin/protoc"
  fi
}

ensure_dart_plugin
ensure_protoc

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
