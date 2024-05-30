FROM node:20.14.0-alpine3.20 AS builder

WORKDIR /usr/src/app

COPY --chown=node:node ./package*.json .
RUN npm ci --omit=dev

USER node

FROM node:22-alpine3.19 AS production
RUN apk add --no-cache dumb-init

WORKDIR /usr/src/app

COPY --chown=node:node --from=builder /usr/src/app/node_modules ./node_modules
COPY --chown=node:node ./src ./src
COPY --chown=node:node ./package*.json .

USER node
CMD ["dumb-init", "npm", "run", "start"]
