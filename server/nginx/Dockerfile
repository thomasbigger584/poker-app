# syntax=docker/dockerfile:1

##
# Thin nginx image serving the static site:
#   - the Flutter web client (production build) at the site root
#   - shared branding assets at /branding (also used by the Keycloak login theme,
#     see server/keycloak/docker-compose.yml)
#   - the reverse-proxy / TLS configuration (conf.d)
#
# The Flutter web bundle is NOT compiled here. Flutter ships no official ARM64
# Linux SDK, so building it on the Raspberry Pi failed with "Exec format error".
# Instead it is built once on x86_64 by the `flutter-release` GitHub workflow,
# attached to a `flutter-<version>` pre-release, and downloaded prebuilt by the
# deploy script (server/raspberrypi/deployment.sh) into server/nginx/web-dist/web.
# This image therefore needs no Flutter SDK and builds natively on ARM.
#
# Build context is the repo root. See the Dockerfile-scoped .dockerignore.
##

FROM nginx:alpine

# Reverse-proxy / TLS configuration.
COPY server/nginx/conf.d/ /etc/nginx/conf.d/

# Shared branding assets, served at /branding (referenced by the Keycloak
# tailcloakify theme — see server/keycloak/docker-compose.yml).
COPY server/nginx/html/branding/ /usr/share/nginx/html/branding/

# Flutter web client (production build), served at the site root. Downloaded by
# the deploy script; its base href is the generic "/" so the same artifact runs
# anywhere (locally, here at root, etc.).
COPY server/nginx/web-dist/web/ /usr/share/nginx/html/
