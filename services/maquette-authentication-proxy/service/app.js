var _ = require('lodash');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var cookieSession = require('cookie-session');
var logger = require('morgan');
var proxy = require('http-proxy-stream');

const multer = require('multer');
const submit = multer();

const config = require('./config.json');
const users = config.users;
const proxyUrl = process.env.PROXY_URL || config['proxy-url'];

var app = express();

app.use(logger('dev'));
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(cookieSession({
    name: 'session',
    keys: ['secret-key']
}));

app.post('/login', submit.none(), function (req, res) {
    var user = _.find(users, user => user.username === req.body.username) || { username: 'homer', name: 'Homer Simpson', roles: [] };
    req.session.user = user;
    res.redirect(req.body.redirect || '/');
});

app.post('/impersonate', express.json(), function (req, res) {
    console.log(req.body);
    var user = _.find(users, user => user.username === req.body.username) || { username: 'homer', name: 'Homer Simpson', roles: [] };
    console.log(user);
    req.session.user = user;
    res.sendStatus(200);
});

app.get('/logout', function (req, res) {
    req.session = null;
    res.redirect('/byebye');
});

app.get('/api/auth/user', function (req, res) {
    res.json(req.session.user);
});

app.get('/api/auth/users', function (req, res) {
    res.json(users);
});

app.use(function(req, res) {
    var forward = false;

    if (req.session.user) {
        req.headers[config['user-id-header']] = req.session.user.username;
        req.headers[config['user-roles-header']] = req.session.user.roles;
        req.headers[config['user-details-header']] = JSON.stringify(req.session.user);
        forward = true;
    } else if (!_.isEmpty(_.intersection(_.keys(req.headers), config['allowed-authorization-headers']))) {
        forward = true;
    }

    if (forward) {
        proxy(req, { url: `${proxyUrl}${req.originalUrl}` }, res);
    } else {
        req.next();
    }
})

app.use(express.static(path.join(__dirname, 'public')));

app.get('*', function (req, res) {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

console.log('Proxying requests to ' + proxyUrl);
module.exports = app;
