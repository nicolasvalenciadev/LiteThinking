FROM node:22-slim AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG API_URL=http://localhost:8080
ENV API_URL=$API_URL
RUN npm run build

FROM nginx:stable
COPY --from=builder /app/dist/spa /usr/share/nginx/html
COPY --from=builder /app/robots.txt /usr/share/nginx/html/robots.txt
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 9000
CMD ["nginx", "-g", "daemon off;"]
