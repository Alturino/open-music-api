FROM node:20.14.0-bullseye AS builder

WORKDIR /usr/src/app

COPY --chown=node:node ./package*.json .
RUN npm ci --omit=dev

USER node

FROM node:20.14.0-bullseye AS production
RUN apt-get update && apt-get install dumb-init -y

WORKDIR /usr/src/app

COPY --chown=node:node --from=builder /usr/src/app/node_modules ./node_modules
COPY --chown=node:node ./src ./src
COPY --chown=node:node ./migrations ./migrations
COPY --chown=node:node ./package*.json .

USER node
CMD ["dumb-init", "npm", "run", "start"]
