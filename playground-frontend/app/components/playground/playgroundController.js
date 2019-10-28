var playgroundModule = angular.module('PlaygroundApp.playground', ['ngRoute']);
playgroundModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/playground', {
    templateUrl: 'app/components/playground/playgroundView.ejs',
  })
}]);
playgroundModule.controller('PlaygroundController', function($scope, $http, $location, $window, UserService, AlertMessageService) {
  console.info('PlaygroundController - entered');
  if (UserService.isUserValid()) {
    setValuesForUser();
    setDefaultValuesForNewElementForm();
    setPlaygroundTheme();
    $scope.resetElementsPaginationAndGetElements = false;
    $scope.$watch('resetElementsPaginationAndGetElements', function(newValue, oldValue) {
      $scope.resetElementsPaginationAndGetElements = false;
      setInitialPaginationValuesForElements();
      getNextPageOfElements();
    });
    UserService.registerCallback(function() {
      $location.path('/login');
    });
  }
  else {
    console.error('PlaygroundController - user is not valid, sign in first.');
    $location.path('/login');
  }
  // ##################################################
  // Helpers for Controller
  // ##################################################
  function setValuesForUser() {
    console.info('PlaygroundController - setValuesForUser()');
    $scope.playground = UserService.getPlayground();
    $scope.userRole   = UserService.getRole();
    $scope.userEmail  = UserService.getEmail();
  }
  function setDefaultValuesForNewElementForm() {
    console.info('PlaygroundController - setDefaultValuesForNewElementForm()');
    $scope.element                = {};
    $scope.element.expirationDate = '2019-12-01T09:15:00';
    $scope.element.type           = 'Animal';
    $scope.element.attributes     = '{"favoriteFood": ["apple", "banana"]}';
  }
  function setPlaygroundTheme() {
    console.info('PlaygroundController - setPlaygroundTheme()');
    switch($scope.playground) {
      case '2019A.kariv':
        $scope.playgroundTheme = 'animals';
        break;
      default:
        $scope.playgroundTheme = 'not defined yet';
    }
  }
  function setInitialPaginationValuesForElements() {
    console.info('PlaygroundController - setInitialPaginationValuesForElements()');
    // There is no 0 page.
    // Its value will be incremented to 1 once getNextPageOfElements() is called
    $scope.elementsPage     = 0;
    $scope.elementsPageSize = 3;
  }
  function getNextPageOfElements() {
    console.info('PlaygroundController - getNextPageOfElements()');
    $scope.elementsPage += 1;
    if ($scope.elementsPage < 1) {
      $scope.elementsPage = 1;
    }
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/elements/animal/next_page',
      data:   {
        userPlayground: $scope.playground,
        userEmail:      $scope.userEmail,
        page:           $scope.elementsPage,
        size:           $scope.elementsPageSize
      }
    }
    $http(request).then(function success(response) {
      console.info('PlaygroundController - $http(request) - success');
      console.info('PlaygroundController - $http(request) - response ' + JSON.stringify(response));
      $scope.elements = response.data;
      if($scope.elements.length == 0 && $scope.elementsPage != 1) {
        $scope.resetElementsPaginationAndGetElements = true;
      }
    }, function failure(response) {
      console.error('PlaygroundController - $http(request) - failure');
      console.error('PlaygroundController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  function reLogin() {
    console.info('PlaygroundController - reLogin()');
    var userLoginForm = {
      email:      $scope.userEmail,
      playground: $scope.playground
    }
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/users/login',
      data:   { userForm: userLoginForm }
    }
    console.info('PlaygroundController - reLogin() - ' + JSON.stringify(userLoginForm))
    $http(request).then(function success(response) {
      console.info('PlaygroundController - $http(request) - success');
      console.info('PlaygroundController - $http(request) - response ' + JSON.stringify(response));
      UserService.setUserFields(response.data);
    }, function failure(response) {
      console.error('PlaygroundController - $http(request) - failure');
      console.error('PlaygroundController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  // ##################################################
  // Functions to call from view template
  // ##################################################
  $scope.addNewElement = function() {
    var newElementForm = {
      playground: $scope.playground,
      location: {
        x: $scope.element.locationX,
        y: $scope.element.locationY
      },
      name:              $scope.element.name,
      expirationDate:    $scope.element.expirationDate,
      type:              $scope.element.type,
      attributes:        JSON.parse($scope.element.attributes),
      creatorPlayground: $scope.playground,
      creatorEmail:      $scope.userEmail
    };
    console.info('PlaygroundController - addNewElement() - ' + JSON.stringify(newElementForm));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/elements/new_element',
      data:   { newElementForm: newElementForm}
    }
    $http(request).then(function success(response) {
      console.info('PlaygroundController - $http(request) - success');
      console.info('PlaygroundController - $http(request) - response ' + JSON.stringify(response));
      $scope.elementsPage -= 1;
      getNextPageOfElements();
      $window.hideElementSubmissionForm();
    }, function failure(response) {
      console.error('PlaygroundController - $http(request) - failure');
      console.error('PlaygroundController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.editElement = function(existingElement) {
    $scope.editedElement = {
      id:             existingElement.id,
      playground:     existingElement.playground,
      locationX:      existingElement.location.x,
      locationY:      existingElement.location.y,
      name:           existingElement.name,
      expirationDate: existingElement.expirationDate,
      creationDate:   existingElement.creationDate,
      type:           existingElement.type,
      attributes:     JSON.stringify(existingElement.attributes)
    };
    console.info('PlaygroundController - editElement() - ' + JSON.stringify($scope.editedElement));
    $window.showEditElementForm();
  }
  $scope.submitEditedElement = function() {
    var editedElementForm = {
      id:         $scope.editedElement.id,
      playground: $scope.editedElement.playground,
      location: {
        x: $scope.editedElement.locationX,
        y: $scope.editedElement.locationY
      },
      name:              $scope.editedElement.name,
      expirationDate:    $scope.editedElement.expirationDate,
      creationDate:      $scope.editedElement.creationDate,
      type:              $scope.editedElement.type,
      attributes:        JSON.parse($scope.editedElement.attributes),
      creatorPlayground: $scope.playground,
      creatorEmail:      $scope.userEmail
    };
    console.info('PlaygroundController - submitEditedElement() - ' + JSON.stringify(editedElementForm));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/elements/edit_element',
      data:   { editedElementForm: editedElementForm}
    }
    $http(request).then(function success(response) {
      console.info('PlaygroundController - $http(request) - success');
      console.info('PlaygroundController - $http(request) - response ' + JSON.stringify(response));
      $scope.elementsPage -= 1;
      $window.hideEditElementForm();
      getNextPageOfElements();
    }, function failure(response) {
      console.error('PlaygroundController - $http(request) - failure');
      console.error('PlaygroundController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.feedAnimal = function(animal) {
    console.info('PlaygroundController - feedAnimal() - ' + animal.name);
    var activityData = {
      playground:        animal.playground,
      elementPlayground: animal.playground,
      elementId:         animal.id,
      type:              'FeedAnAnimal',
      playerPlayground:  $scope.playground,
      playerEmail:       $scope.userEmail,
      attributes:        {}
    }
    console.info('PlaygroundController - feedAnimal() - activityData : ' + JSON.stringify(activityData));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/activities/create_activity',
      data:   { activityData: activityData }
    }
    $http(request).then(function success(response) {
      console.info('PlaygroundController - $http(request) - success');
      console.info('PlaygroundController - $http(request) - response ' + JSON.stringify(response));
      var attributes = response.data.attributes;
      AlertMessageService.setAlertMessage({status: 'ok', response: JSON.stringify(attributes)});
      reLogin();
    }, function failure(response) {
      console.error('PlaygroundController - $http(request) - failure');
      console.error('PlaygroundController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.pokeAnimal = function (animal) {
    console.info('PlaygroundController - pokeAnimal() - ' + animal.name);
    var activityData = {
      playground:        animal.playground,
      elementPlayground: animal.playground,
      elementId:         animal.id,
      type:              'PokeAnAnimal',
      playerPlayground:  $scope.playground,
      playerEmail:       $scope.userEmail,
      attributes:        {}
    }
    console.info('PlaygroundController - pokeAnimal() - activityData : ' + JSON.stringify(activityData));
    var request = {
      method: 'POST',
      url:    'http://localhost:3000/api/activities/create_activity',
      data:   { activityData: activityData }
    }
    $http(request).then(function success(response) {
      console.info('PlaygroundController - $http(request) - success');
      console.info('PlaygroundController - $http(request) - response ' + JSON.stringify(response));
      var attributes = response.data.attributes;
      AlertMessageService.setAlertMessage({status: 'ok', response: JSON.stringify(attributes)});
      reLogin();
    }, function failure(response) {
      console.error('PlaygroundController - $http(request) - failure');
      console.error('PlaygroundController - $http(request) - statusText - ' + response.statusText);
      AlertMessageService.setAlertMessage({status: 'error', response: response.statusText});
    });
  }
  $scope.nextPage = function() {
    getNextPageOfElements();
  }
  $scope.previousPage = function() {
    // We have to get current $scope.elementsPage - 1.
    // Since getNextPageOfElements() adds 1 to $scope.elementsPage,
    // we have to set $scope.elementsPage -= 2
    $scope.elementsPage -= 2;
    getNextPageOfElements();
  }
});