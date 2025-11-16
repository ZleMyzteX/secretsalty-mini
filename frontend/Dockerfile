# ---------- Build Stage ----------
FROM node:20 AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

ENV VITE_API_BASE_URL=''

RUN npm run build

# ---------- Runner Stage ----------
FROM node:20-alpine

WORKDIR /app

COPY --from=build /app/build ./build
COPY --from=build /app/package*.json ./

RUN npm install --production

EXPOSE 5173

ENV PORT=5173
ENV HOST=0.0.0.0

CMD ["node", "build"]
