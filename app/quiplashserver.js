const WebSocket = require('ws');
const wss = new WebSocket.Server({port:8080});

wss.on('connection', function connection(ws) {

    console.log('connected')

    ws.on('error', function error(data) {
        console.log(data);
    });

    ws.on('open', function open() {
        console.log('open');
    });

    ws.on('close', function close() {
        console.log('disconnected');
    });

    ws.on('message', function incoming(gameid) {
    console.log(gameid)
         wss.clients.forEach(function each(client) {
            if (client.readyState === WebSocket.OPEN) {
                  client.send(gameid);
            }
        });
    });

});




