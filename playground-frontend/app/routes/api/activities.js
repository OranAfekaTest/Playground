var utilities      = require('util'),
    express        = require('express'),
    validateFields = require('../helpers/validateFields');

var activitiesRouter = express.Router();

activitiesRouter.post('/create_activity', function(request, clientResponse) {
  console.info('POST /api/activities/create_activity');
  var newActivityData = request.body.activityData;
  console.debug(utilities.format('newActivityData : %j', newActivityData));
  if (!validateFields.validateActivityType(newActivityData.type)) {
    clientResponse.writeHead(500, 'Internal server error. Activity type is illegal.');
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
    path:     '/playground/activities'+'/'+newActivityData.playerPlayground+'/'+newActivityData.playerEmail
  }
  sendHttpRequest(clientResponse, options, newActivityData);
});

module.exports = activitiesRouter;