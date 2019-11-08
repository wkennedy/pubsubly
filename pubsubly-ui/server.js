const express = require('express');
const next = require('next');

const dev = process.env.NODE_ENV !== 'production';
const app = next({dev});
const handle = app.getRequestHandler();
const port = process.env.PORT || 8080;
const compression = require('compression');

const API_HOST_LOCAL = 'http://localhost:9000';
const API_HOST_DOCKER = 'http://pubsubly-service:9000';
const API_HOST = dev ? API_HOST_LOCAL : API_HOST_DOCKER;

const devProxy = {
    '/api': {
        target: API_HOST,
        pathRewrite: {'^/api': ''},
        changeOrigin: true
    }
};

app
    .prepare()
    .then(() => {
        const server = express();
        server.use(compression());
        server.use(require('express-status-monitor')());

        const proxyMiddleware = require('http-proxy-middleware');
        Object.keys(devProxy).forEach(function (context) {
            server.use(proxyMiddleware(context, devProxy[context]))
        });

        server.get('/t/:topic', (req, res) => {
            const actualPage = '/topic';
            const queryParams = {topic: req.params.topic};
            app.render(req, res, actualPage, queryParams)
        });

        server.get('/tag/:key/:value', (req, res) => {
            const actualPage = '/tags';
            const queryParams = {key: req.params.key, value: escape(req.params.value)};
            app.render(req, res, actualPage, queryParams)
        });

        server.get('*', (req, res) => {
            return handle(req, res)
        });

        server.listen(port, err => {
            if (err) throw err;
            console.log("process.env.NODE_ENV: " + JSON.stringify(process.env.NODE_ENV));
            console.log('> Ready on http://localhost:' + port + " process.env.PORT: " + process.env.PORT)
        })
    })
    .catch(ex => {
        console.error(ex.stack);
        process.exit(1)
    });