var menuBarModule = angular.module('PlaygroundApp.menuBar', [
  'ngRoute'
]);
menuBarModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/menu_bar', {
    templateUrl: 'app/components/menuBar/menuBarView.ejs'
  })
}]);
menuBarModule.controller('MenuBarController', function($scope, $http, $location, UserService) {
  console.info('MenuBarController - entered');
  if(UserService.isUserValid()) {
    $scope.showUserMenu = true;
    $scope.user         = UserService.getUser();
  }
  else {
    $scope.showUserMenu = false;
  }
  $scope.logout = function () {
    $scope.user         = null;
    $scope.showUserMenu = false;
    UserService.resetUser();
  }
});