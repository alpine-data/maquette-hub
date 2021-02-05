var _ = require('lodash');
var express = require('express');
var logger = require('morgan');
var proxy = require('http-proxy-stream');

let routes = [];

var app = express();

app.use(logger('dev'));
app.use(express.urlencoded({ extended: false }));

app.get('/api/routes', function(_, res) {
    res.json(routes);
});

app.post('/api/routes', express.json(), function (req, res) {
    routes = _.concat(routes, [ _.pick(req.body, 'id', 'route', 'target') ])
    res.sendStatus(204);
});

app.delete('/api/routes/:id', function(req, res) {
    const id = req.params['id'];
    routes = _.filter(routes, r => r.id !== id);
    res.sendStatus(204);
})

app.use(function(req, res) {
    route = _.find(routes, route => req.originalUrl.indexOf(route.route) == 0);

    if (route) {
        proxy(req, { url: `${route.target}${req.originalUrl}` }, res);
    } else {
        req.next();
    }
});

module.exports = app;
