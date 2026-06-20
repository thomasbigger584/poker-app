# syntax=docker/dockerfile:1

##
# Multi-stage build: compile the Flutter web client for production, then serve
# the static bundle from a thin nginx image. The Flutter SDK (multiple GB) lives
# only in the build stage and is discarded — the final image is nginx:alpine
# plus the compiled web output and this module's config.
#
# Build context is the repo root (so client/flutter is reachable). See the
# Dockerfile-scoped .dockerignore next to this file for what is sent.
##

# ---- Stage 1: build the Flutter web app (release) ----
# Pin the exact SDK from the official release channel for a reproducible build
# (matches the dev version, and doesn't depend on a third party publishing a tag).
FROM debian:bookworm-slim AS build

ARG FLUTTER_VERSION=3.44.2
ENV FLUTTER_HOME=/opt/flutter
ENV PATH="${FLUTTER_HOME}/bin:${PATH}"

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates curl git unzip xz-utils \
    && rm -rf /var/lib/apt/lists/*

# Install the SDK and pre-download the web engine artifacts. This whole layer is
# cached unless FLUTTER_VERSION changes, so it isn't redone on app code changes.
RUN curl -fsSL "https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_${FLUTTER_VERSION}-stable.tar.xz" \
      | tar -xJ -C /opt \
    && git config --global --add safe.directory "${FLUTTER_HOME}" \
    && flutter config --no-analytics --enable-web \
    && flutter precache --web \
    && flutter --version

WORKDIR /src

# Resolve dependencies first so this layer stays cached unless the manifest changes.
COPY client/flutter/pubspec.yaml client/flutter/pubspec.lock ./
RUN flutter pub get

# Bring in the client sources and compile a production web build. The app is
# served under /app behind nginx, so the base href must match.
COPY client/flutter/ ./
RUN flutter build web --release --base-href /app/

# Pre-compress text assets so nginx can serve them with gzip_static — no runtime
# CPU cost and a better ratio (-9) than on-the-fly compression. Originals are
# kept (-k) for clients that don't accept gzip.
RUN find build/web -type f \
      \( -name '*.js'  -o -name '*.wasm' -o -name '*.json' -o -name '*.css' \
      -o -name '*.html' -o -name '*.ttf'  -o -name '*.otf'  -o -name '*.svg' \) \
      -exec gzip -9 -k {} +

# ---- Stage 2: serve with nginx ----
FROM nginx:alpine

# Static dashboard + branding and the reverse-proxy / TLS configuration.
COPY server/nginx/html/   /usr/share/nginx/html/
COPY server/nginx/conf.d/ /etc/nginx/conf.d/

# Production Flutter web build, served at /app (see conf.d/default.conf).
COPY --from=build /src/build/web/ /usr/share/nginx/html/app/
