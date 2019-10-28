var utilities = require('util'),
    http = require('http');

module.exports = function(clientResponse, requestOptions, payload) {
  console.debug(utilities.format('request options : %j', requestOptions));
  var httpRequest = http.request(requestOptions, (backendResponse) => {
    console.debug(utilities.format('httpRequest - backendResponse - status : %s', backendResponse.statusCode));
    console.debug(utilities.format('httpRequest - backendResponse -  headers : %j', backendResponse.headers));
    if (backendResponse.statusCode == 500) {
      console.error('httpRequest - backendResponse - probably illegal field value');
      clientResponse.writeHead(backendResponse.statusCode, 'Internal server error at backend. Probably illegal field value');
      clientResponse.end();
    }
    else if (backendResponse.statusCode >= 400 && backendResponse.statusCode < 500) {
      console.error('httpRequest - backendResponse - probably illegal url or payload type');
      clientResponse.writeHead(backendResponse.statusCode, 'Fix request dispatch at api server');
      clientResponse.end();
    }
    else if (backendResponse.statusCode == 200) {
      console.debug('httpRequest - backendResponse - no backend errors.');
      clientResponse.writeHead(200, 'success', backendResponse.headers);
      backendResponse.setEncoding('utf8');
      backendResponse.on('data', (chunk) => {
        console.debug(`httpRequest - backendResponse - next chunk: ${chunk}`);
        clientResponse.write(chunk);
      });
      backendResponse.on('end', () => {
        console.debug('httpRequest - backendResponse - received everything.');
        clientResponse.end();
      });
    }
    else {
      console.error(utilities.format('Unexpected status code : %s', backendResponse.statusCode));
      clientResponse.writeHead(backendResponse.statusCode, 'unexpected status code from backend server');
      clientResponse.end();
    }
  });
  httpRequest.on('error', (error) => {
    console.error(`httpRequest - error : ${error}`);
    clientResponse.writeHead(500, utilities.format('Unexpected error at api server: %s', error));
    clientResponse.end();
  });
  if (requestOptions.method == 'POST' || requestOptions.method == 'PUT') {
    httpRequest.write(JSON.stringify(payload));
  }
  httpRequest.end();
}