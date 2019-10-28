var editProfileModule = angular.module('PlaygroundApp.editProfile', ['ngRoute']);
editProfileModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/edit_profile', {
    templateUrl: 'app/components/editProfile/editProfileView.ejs'
  })
}]);
editProfileModule.controller('EditProfileController', function($scope, $http, $location, UserService, AlertMessageService) {
  console.info('EditProfileController - entered');
  if(!UserService.isUserValid()) {
    $location.path('/login');
  }
  else {
    $scope.user = UserService.getUser();
    $scope.updateProfile = function() {
      var user = $scope.user;
      console.info('EditProfileController - updateProfile() - ' + JSON.stringify(user));
      var request = {
        method: 'POST',
        url:    'http://localhost:3000/api/users/edit_user',
        data:   { updatedUserForm: user}
      }
      $http(request).then(function success(response) {
        console.info('EditProfileController - $http(request) - success');
        console.info('EditProfileController - $http(request) - response ' + JSON.stringify(response));
        AlertMessageService.setAlertMessage({status: 'ok', response: 'Please, re-login.'});
        UserService.resetUser();
      }, function failure(response) {
        console.error('EditProfileController - $http(request) - failure');
        console.error('EditProfileController - $http(request) - statusText - ' + response.statusText);
        AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
      });
    }
  }
});