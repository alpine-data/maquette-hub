FROM node:14

WORKDIR /usr/src/app
COPY app/package*.json ./
RUN npm install

COPY app .
RUN ls -al && npm run build

WORKDIR /usr/src/service

COPY service/package*.json ./
RUN npm install

COPY service .
RUN rm -rf public && mv ../app/build ./public

EXPOSE 3030
CMD [ "node", "./bin/www" ]