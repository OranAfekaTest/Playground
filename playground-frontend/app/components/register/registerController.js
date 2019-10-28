var registerModule = angular.module('PlaygroundApp.register', ['ngRoute']);
registerModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/signup', {
    templateUrl: 'app/components/register/registerView.ejs'
  })
}]);
registerModule.controller('RegisterController', function($scope, $http, $location, UserService, AlertMessageService) {
  console.info('RegisterController - entered');
  if(UserService.isUserValid()) {
    $location.path('/login');
  }
  else {
    $scope.user = {};
    $scope.user.playground = '2019A.kariv';
    $scope.signup = function() {
      var user = $scope.user;
      console.info('RegisterController - signup() - ' + JSON.stringify(user));
      var request = {
        method: 'POST',
        url:    'http://localhost:3000/api/users/signup',
        data:   { newUserForm: user }
      }
      $http(request).then(function success(response) {
        console.info('RegisterController - $http(request) - success');
        console.info('RegisterController - $http(request) - response ' + JSON.stringify(response));
        $location.path('/login');
      }, function failure(response) {
        console.error('RegisterController - $http(request) - failure');
        console.error('RegisterController - $http(request) - statusText - ' + response.statusText);
        AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
      });
    }
  }
});