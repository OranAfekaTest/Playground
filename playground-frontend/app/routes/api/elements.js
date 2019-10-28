var utilities      = require('util'),
    express        = require('express'),
    validateFields = require('../helpers/validateFields');

var elementsRouter = express.Router();

elementsRouter.post('/new_element', function(request, clientResponse) {
  console.info('POST /api/elements/new_element');
  var newElementForm = request.body.newElementForm;
  console.debug(utilities.format('newElementForm : %j', newElementForm));
  if (!validateFields.validateElementName(newElementForm.name)) {
    clientResponse.writeHead(500, 'Internal server error. Element name is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validateElementType(newElementForm.type)) {
    clientResponse.writeHead(500, 'Internal server error. Element type is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validatePlayground(newElementForm.playground)) {
    clientResponse.writeHead(500, 'Internal server error. Element playground is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validatePlayground(newElementForm.creatorPlayground)) {
    clientResponse.writeHead(500, 'Internal server error. Element creatorPlayground is illegal.');
    clientResponse.end();
    return;
  }
  var options = {
    protocol: 'http:',
    host:     'localhost',
    hostname: 'localhost',
    headers:  request.headers,
    port:     8085,
    method:   'POST',
    path:     '/playground/elements'+'/'+newElementForm.creatorPlayground+'/'+newElementForm.creatorEmail
  }
  sendHttpRequest(clientResponse, options, newElementForm);
});
elementsRouter.post('/animal/next_page', function(request, clientResponse) {
  console.info('POST /api/elements/animal/next_page');
  var paginationQuery = {
    userPlayground: request.body.userPlayground,
    userEmail:      request.body.userEmail,
    page:           request.body.page,
    pageSize:       request.body.size
  }
  console.debug(utilities.format('paginationQuery : %j', paginationQuery));
  var options = {
    protocol: 'http:',
    host:     'localhost',
    hostname: 'localhost',
    headers:  request.headers,
    port:     8085,
    method:   'GET',
    path:     utilities.format('/playground/elements/%s/%s/search/%s/%s?page=%s&size=%s',
                paginationQuery.userPlayground, paginationQuery.userEmail, 'type', 'Animal', paginationQuery.page, paginationQuery.pageSize
              )
  }
  sendHttpRequest(clientResponse, options, null);
});
elementsRouter.post('/message_board/next_page', function(request, clientResponse) {
  console.info('POST /api/elements/message_board/next_page');
  var paginationQuery = {
    userPlayground: request.body.userPlayground,
    userEmail:      request.body.userEmail,
    page:           request.body.page,
    pageSize:       request.body.size
  }
  console.debug(utilities.format('paginationQuery : %j', paginationQuery));
  var encodedPath = encodeURI(utilities.format("/playground/elements/%s/%s/search/%s/%s?page=%s&size=%s",
    paginationQuery.userPlayground, paginationQuery.userEmail, 'type', 'message board', paginationQuery.page, paginationQuery.pageSize
  ));
  var options = {
    protocol: 'http:',
    host:     'localhost',
    hostname: 'localhost',
    headers:  request.headers,
    port:     8085,
    method:   'GET',
    path:     encodedPath
  }
  sendHttpRequest(clientResponse, options, null);
});
elementsRouter.post('/edit_element', function(request, clientResponse) {
  console.info('POST /api/elements/edit_element');
  var editedElementForm = request.body.editedElementForm;
  console.debug(utilities.format('editedElementForm : %j', editedElementForm));
  if (!validateFields.validateElementName(editedElementForm.name)) {
    clientResponse.writeHead(500, 'Internal server error. Element name is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validatePlayground(editedElementForm.playground)) {
    clientResponse.writeHead(500, 'Internal server error. Element playground is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validatePlayground(editedElementForm.creatorPlayground)) {
    clientResponse.writeHead(500, 'Internal server error. Element creatorPlayground is illegal.');
    clientResponse.end();
    return;
  }
  var options = {
    protocol: 'http:',
    host:     'localhost',
    hostname: 'localhost',
    headers:  request.headers,
    port:     8085,
    method:   'PUT',
    path:     utilities.format('/playground/elements/%s/%s/%s/%s/',
               editedElementForm.creatorPlayground, editedElementForm.creatorEmail, editedElementForm.playground, editedElementForm.id
              )
  }
  sendHttpRequest(clientResponse, options, editedElementForm);
});

module.exports = elementsRouter;