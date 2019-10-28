var path       = require('path'),
    express    = require('express'),
    http       = require('http'),
    bodyParser = require('body-parser');
// ##################################################
// Initiate Express Application and configure it
// ##################################################
var app = express();
app.set('views', path.join(__dirname, '/app'));
app.set('view engine', 'ejs');
app.use(express.static(__dirname));
app.use(function(request, response, next) {
  response.header('Access-Control-Allow-Origin', '*');
  response.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  next();
});
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
// ##################################################
// Route for serving HTML, CSS, JS and Angular content
// for client side. Shout have 1 route - we are using
// Angular after all.
// ##################################################
app.get('/', function(request, response) {
  response.render('appView');
});
// ##################################################
// Routes for backend calls, i.e. users, elements,
// activities controllers.
// ##################################################
app.use('/api/users', require(
  path.join(__dirname, 'app', 'routes', 'api', 'users')
));
app.use('/api/elements', require(
  path.join(__dirname, 'app', 'routes', 'api', 'elements')
));
app.use('/api/activities', require(
  path.join(__dirname, 'app', 'routes', 'api', 'activities')
));
// ##################################################
// Server is ready for initiation. We're using http
// connection.
// ##################################################
try {
  http.createServer(app).listen(3000, function() {
    console.log('Listening on port 3000');
  });
}
catch(error) {
  console.error('Error during server creation.');
  console.error(error.message);
  console.error(error.stack);
}