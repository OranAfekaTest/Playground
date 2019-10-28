var loginModule = angular.module('PlaygroundApp.login', ['ngRoute']);
loginModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'app/components/login/loginView.ejs'
  })
}]);
loginModule.controller('LoginController', function($scope, $http, $location, UserService, AlertMessageService) {
  console.info('LoginController - entered');
  $scope.user = {};
  $scope.user.playground = '2019A.kariv';
  $scope.login = function() {
    $scope.showLoginFailureAlert = false;
    var user = {
      email:      $scope.user.email,
      playground: $scope.user.playground
    }
    if($scope.failedLogin) {
      user.verificationCode = $scope.user.verificationCode;
    }
    console.info('LoginController - login() - ' + JSON.stringify(user));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/users/login',
      data:   { userForm: user }
    }
    $http(request).then(function success(response) {
      console.info('LoginController - $http(request) - success');
      console.info('LoginController - $http(request) - response ' + JSON.stringify(response));
      $scope.failedLogin = false;
      UserService.setUserFields(response.data);
      $location.path('/playground');
    }, function failure(response) {
      console.error('LoginController - $http(request) - failure');
      console.error('LoginController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
      $scope.failedLogin           = true;
      $scope.showLoginFailureAlert = true;
    });
  }
});