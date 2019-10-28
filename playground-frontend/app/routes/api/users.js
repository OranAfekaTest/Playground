var utilities       = require('util');
    express         = require('express'),
    sendHttpRequest = require('../helpers/sendHttpRequest'),
    validateFields = require('../helpers/validateFields');

var usersRouter = express.Router();

usersRouter.post('/signup', function(request, clientResponse) {
  console.info('POST /api/users/signup');
  var newUserForm = request.body.newUserForm;
  console.debug(utilities.format('newUserForm : %j', newUserForm));
  if (!validateFields.validateRole(newUserForm.role)) {
    clientResponse.writeHead(500, 'Internal server error. User role is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validateAvatar(newUserForm.avatar)) {
    clientResponse.writeHead(500, 'Internal server error. User avatar is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validateEmail(newUserForm.email)) {
    clientResponse.writeHead(500, 'Internal server error. User email is illegal.');
    clientResponse.end();
    return;
  }
  if(!validateFields.validatePlayground(newUserForm.playground)) {
    clientResponse.writeHead(500, 'Internal server error. User playground is illegal.');
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
    path:     '/playground/users'
  }
  sendHttpRequest(clientResponse, options, newUserForm);
});
usersRouter.post('/login', function(request, clientResponse) {
  console.info('POST /api/users/login');
  var userForm = request.body.userForm;
  console.debug(utilities.format('userForm : %j', userForm));
  if(!validateFields.validatePlayground(userForm.playground)) {
    clientResponse.writeHead(500, 'Internal server error. User playground is illegal.');
    clientResponse.end();
    return;
  }
  var options = {
    protocol: 'http:',
    host:     'localhost',
    hostname: 'localhost',
    headers:  request.headers,
    port:     8085,
    method:   'GET'
  }
  if(userForm.verificationCode) {
    options.path = '/playground/users/confirm/'+userForm.playground+'/'+userForm.email+'/'+userForm.verificationCode;
  }
  else {
    options.path = '/playground/users/login/'+userForm.playground+'/'+userForm.email;
  }
  sendHttpRequest(clientResponse, options, null);
});
usersRouter.post('/edit_user', function(request, clientResponse) {
  console.info('POST /api/users/edit_user');
  var updatedUserForm = request.body.updatedUserForm;
  console.debug(utilities.format('userForm : %j', updatedUserForm));
  if (!validateFields.validateRole(updatedUserForm.role)) {
    clientResponse.writeHead(500, 'Internal server error. User role is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validateAvatar(updatedUserForm.avatar)) {
    clientResponse.writeHead(500, 'Internal server error. User avatar is illegal.');
    clientResponse.end();
    return;
  }
  if (!validateFields.validateEmail(updatedUserForm.email)) {
    clientResponse.writeHead(500, 'Internal server error. User email is illegal.');
    clientResponse.end();
    return;
  }
  if(!validateFields.validatePlayground(updatedUserForm.playground)) {
    clientResponse.writeHead(500, 'Internal server error. User playground is illegal.');
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
    path:     '/playground/users/'+updatedUserForm.playground+'/'+updatedUserForm.email
  }
  sendHttpRequest(clientResponse, options, updatedUserForm);
});

module.exports = usersRouter;