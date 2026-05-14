FROM node:22-slim AS build
WORKDIR /app

ENV NODE_ENV=production
ENV CI=true

COPY package.json package-lock.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

COPY . .
RUN npm run build

FROM nginx:1.27-alpine AS production
WORKDIR /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/build ./

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
