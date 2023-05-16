FROM node:14-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 3000
CMD [ "npm", "start" ]

#docker build --tag random-number-api:latest .
#docker run --publish 3000:3000 --rm -d random-number-api:latest