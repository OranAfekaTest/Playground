var playgroundApp = angular.module('PlaygroundApp', [
  'ngRoute',
  'PlaygroundApp.userService',
  'PlaygroundApp.alertMessageService',
  'PlaygroundApp.alert',
  'PlaygroundApp.menuBar',
  'PlaygroundApp.login',
  'PlaygroundApp.register',
  'PlaygroundApp.editProfile',
  'PlaygroundApp.playground',
  'PlaygroundApp.messageBoard'
]);

playgroundApp.config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider){
  $locationProvider.hashPrefix('!');
  $routeProvider.otherwise({redirectTo: '/signup'});
}]);

playgroundApp.controller('MainController', function() {
  // Nothing for now
});