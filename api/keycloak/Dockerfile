FROM node:20 as builder
WORKDIR /app

RUN corepack enable pnpm

COPY keywind .
RUN pnpm install
RUN pnpm build
RUN pnpm prune --prod

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /app/theme/keywind /opt/keycloak/themes/custom
